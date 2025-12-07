package org.firstinspires.ftc.teamcode.testing;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

import org.firstinspires.ftc.teamcode.AutoBase;
import org.firstinspires.ftc.teamcode.RobotBase;
import org.firstinspires.ftc.teamcode.debug.util.Async;

import java.util.ArrayList;
import java.util.List;

@TeleOp(name = "LauncherAutoTune")
public class LauncherAutoTune extends AutoBase {

    /*
     * Standalone OpMode: launcher characterization + simple human-in-loop P autotune.
     *
     * Controls:
     *   - gamepad2.a : start characterization (open-loop power sweep)
     *   - gamepad2.b : start autotune sweep (uses characterization result kF if available)
     *   - during autotune:
     *       Y = accept this P (applies PIDF and holds)
     *       X = reject -> test next P
     *       B = abort autotune (stop motor)
     *   - while DONE:
     *       A = re-run characterization
     *       B = reset to IDLE
     *       Y = apply last-computed kF as feed-forward (P=0)
     *
     * Notes:
     *   - Assumes RobotBase.init() initializes `launcher` and `launcher.motor` (DcMotorEx).
     *   - This file intentionally stands alone; it does not modify other classes.
     */

    private enum State { IDLE, CHARACTERIZING, AUTOTUNE_TEST, AUTOTUNE_MEASURE, DONE }
    private State state = State.IDLE;

    // characterization parameters
    private final double[] CHAR_POWERS = new double[] { 0.2, 0.4, 0.6, 0.8 };
    private final long CHAR_SETTLE_MS = 3000;
    private final int CHAR_SAMPLES = 20;
    private final long CHAR_SAMPLE_INTERVAL_MS = 250;
    private final List<Double> charVels = new ArrayList<>();
    private final List<Double> charPwr = new ArrayList<>();
    private int charIndex = 0;
    private long charPhaseStart = 0L;

    // autotune parameters
    private double suggestedKF = 0.0;
    private double suggestedKS = 0.0;
    private double autotuneTargetVel = 1600.0; // change to your preferred RPM/ticks/sec
    private double startP = 0.0002;
    private double stepP = 0.0005;
    private double maxP = 0.010;
    private double currentP = 0.0;
    private long autotuneStartMs = 0L;
    private final long AUTOTUNE_SETTLE_MS = 3000;
    private final long AUTOTUNE_MEASURE_MS = 800;
    private final int AUTOTUNE_SAMPLES = 12;

    // button edge detection
    private boolean prevA = false;
    private boolean prevB = false;
    private boolean prevX = false;
    private boolean prevY = false;

    @Override
    public void init() {
        super.init();
        // ensure motor is in velocity mode candidate
        launcher.motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    @Override
    public void loop() {
        // simple edge detection for gamepad2 buttons
        boolean aPressed = gamepad2.a && !prevA;
        boolean bPressed = gamepad2.b && !prevB;
        boolean xPressed = gamepad2.x && !prevX;
        boolean yPressed = gamepad2.y && !prevY;
        prevA = gamepad2.a;
        prevB = gamepad2.b;
        prevX = gamepad2.x;
        prevY = gamepad2.y;

        switch (state) {
            case IDLE:
                telemetry.addLine("Tuner: IDLE");
                telemetry.addData("Press A", "Characterize (open-loop sweep)");
                telemetry.addData("Press B", "Autotune (P sweep; requires char or will use kF=0)");
                if (aPressed) {
                    // start characterization
                    charVels.clear();
                    charPwr.clear();
                    charIndex = 0;
                    launcher.motor.setPower(CHAR_POWERS[charIndex]);
                    charPhaseStart = System.currentTimeMillis();
                    state = State.CHARACTERIZING;
                    telemetry.addLine("Tuner: starting characterization");
                } else if (bPressed) {
                    // compute suggested kF/kS from any prior characterization
                    computeCharFit();
                    currentP = startP;
                    state = State.AUTOTUNE_TEST;
                    telemetry.addLine("Tuner: starting autotune sweep");
                    telemetry.addData("suggested kF", suggestedKF);
                    telemetry.addData("suggested kS", suggestedKS);
                }
                telemetry.update();
                break;

            case CHARACTERIZING:
                // wait settle then sample, advance powers, finish
                long now = System.currentTimeMillis();
                if (now - charPhaseStart >= CHAR_SETTLE_MS) {
                    // measure average velocity (blocking short period)
                    double avg = sampleAverageVelocity(CHAR_SAMPLES, CHAR_SAMPLE_INTERVAL_MS);
                    charVels.add(avg);
                    charPwr.add(CHAR_POWERS[charIndex]);
                    telemetry.addData("char power", CHAR_POWERS[charIndex]);
                    telemetry.addData("measured vel", avg);
                    telemetry.update();

                    charIndex++;
                    if (charIndex < CHAR_POWERS.length) {
                        launcher.motor.setPower(0);
                        while (Math.abs(launcher.motor.getVelocity()) > 40) {
                            Thread.yield();
                        }
                        Async.sleep(2000);
                        launcher.motor.setPower(CHAR_POWERS[charIndex]);
                        charPhaseStart = System.currentTimeMillis();
                    } else {
                        // finished characterization
                        launcher.motor.setPower(0.0);
                        computeCharFit();
                        state = State.DONE;
                        telemetry.addLine("Tuner: characterization complete");
                        telemetry.addData("kF", suggestedKF);
                        telemetry.addData("kS", suggestedKS);
                        telemetry.update();
                    }
                } else {
                    // still settling; show countdown
                    telemetry.addLine("Tuner: characterizing ...");
                    telemetry.addData("next power", CHAR_POWERS[charIndex]);
                    telemetry.update();
                }
                break;

            case AUTOTUNE_TEST:
                // apply PIDF and request velocity
                try {
                    PIDFCoefficients trial = new PIDFCoefficients(currentP, 0.0, 0.0, suggestedKF);
                    launcher.motor.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, trial);
                    launcher.motor.setVelocity(autotuneTargetVel);
                } catch (Exception e) {
                    // fallback: use feed-forward + P correction as open-loop power
                    double ff = suggestedKF * autotuneTargetVel + suggestedKS;
                    double out = ff + currentP * (autotuneTargetVel - launcher.motor.getVelocity());
                    launcher.motor.setPower(Math.max(-1.0, Math.min(1.0, out)));
                }
                autotuneStartMs = System.currentTimeMillis();
                state = State.AUTOTUNE_MEASURE;
                telemetry.addData("Tuner", "testing P=" + currentP);
                telemetry.update();
                break;

            case AUTOTUNE_MEASURE:
                // wait settle + measure window, then present operator with options
                long now2 = System.currentTimeMillis();
                if (now2 - autotuneStartMs >= AUTOTUNE_SETTLE_MS + AUTOTUNE_MEASURE_MS) {
                    double measured = sampleAverageVelocity(AUTOTUNE_SAMPLES, Math.max(1, (int)(AUTOTUNE_MEASURE_MS / ((double) AUTOTUNE_SAMPLES))));
                    double err = autotuneTargetVel - measured;
                    telemetry.addData("Tuner test P", currentP);
                    telemetry.addData("Measured", measured);
                    telemetry.addData("Error", err);
                    telemetry.addLine("Y=accept, X=reject->next, B=abort");
                    telemetry.update();

                    if (yPressed) {
                        // accept
                        PIDFCoefficients accepted = new PIDFCoefficients(currentP, 0.0, 0.0, suggestedKF);
                        launcher.motor.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, accepted);
                        launcher.motor.setVelocity(autotuneTargetVel);
                        state = State.DONE;
                        telemetry.addData("Tuner", "Accepted P=" + currentP);
                        telemetry.update();
                    } else if (xPressed) {
                        // reject -> next
                        currentP += stepP;
                        if (currentP > maxP) {
                            launcher.motor.setPower(0.0);
                            state = State.DONE;
                            telemetry.addLine("Tuner: sweep complete, no accept");
                            telemetry.update();
                        } else {
                            state = State.AUTOTUNE_TEST;
                        }
                    } else if (bPressed) {
                        // abort
                        launcher.motor.setPower(0.0);
                        state = State.DONE;
                        telemetry.addLine("Tuner: autotune aborted");
                        telemetry.update();
                    } else {
                        // wait: operator hasn't pressed yet; remain in this state until button pressed
                    }
                } else {
                    // still settling/measuring
                    telemetry.addLine("Tuner: measuring...");
                    telemetry.update();
                }
                break;

            case DONE:
                telemetry.addLine("Tuner: DONE");
                telemetry.addData("kF", suggestedKF);
                telemetry.addData("kS", suggestedKS);
                telemetry.addLine("A=re-run char  |  B=reset  |  Y=apply kF");
                telemetry.update();

                if (aPressed) {
                    // restart characterization
                    charVels.clear();
                    charPwr.clear();
                    charIndex = 0;
                    launcher.motor.setPower(CHAR_POWERS[charIndex]);
                    charPhaseStart = System.currentTimeMillis();
                    state = State.CHARACTERIZING;
                    telemetry.addLine("Tuner: re-running characterization");
                    telemetry.update();
                } else if (bPressed) {
                    state = State.IDLE;
                    launcher.motor.setPower(0.0);
                } else if (yPressed) {
                    // apply kF as feed-forward (P=0)
                    PIDFCoefficients applied = new PIDFCoefficients(0.0, 0.0, 0.0, suggestedKF);
                    launcher.motor.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, applied);
                    telemetry.addData("Tuner", "Applied kF=" + suggestedKF);
                    telemetry.update();
                }
                break;
        }

        // always show some useful telemetry
        telemetry.addData("State", state.name());
        telemetry.addData("Launcher Vel", launcher.motor.getVelocity());
//        telemetry.update();

    }

    // blocking sample average helper (short blocking window)
    private double sampleAverageVelocity(int samples, long intervalMs) {
        double sum = 0.0;
        for (int i = 0; i < samples; i++) {
            sum += launcher.motor.getVelocity();
            telemetry.addData("Launcher Vel", launcher.motor.getVelocity());
            telemetry.update();
            try {
                Thread.sleep(intervalMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        return sum / ((double) samples);
    }

    // compute linear fit power = kF * vel + kS using characterization lists
    private void computeCharFit() {
        if (charVels.isEmpty() || charVels.size() != charPwr.size()) {
            suggestedKF = 0.0;
            suggestedKS = 0.0;
            return;
        }
        int n = charVels.size();
        double sumX = 0.0, sumY = 0.0, sumXX = 0.0, sumXY = 0.0;
        for (int i = 0; i < n; i++) {
            double x = charVels.get(i);
            double y = charPwr.get(i);
            sumX += x; sumY += y; sumXX += x * x; sumXY += x * y;
        }
        double denom = n * sumXX - sumX * sumX;
        if (Math.abs(denom) > 1e-9) {
            suggestedKF = (n * sumXY - sumX * sumY) / denom;
            suggestedKS = (sumY - suggestedKF * sumX) / n;
        } else {
            suggestedKF = 0.0;
            suggestedKS = 0.0;
        }
    }

}
