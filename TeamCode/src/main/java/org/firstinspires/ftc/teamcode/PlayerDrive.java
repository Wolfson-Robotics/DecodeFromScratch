package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

@TeleOp(name = "PlayerDrive")
public class PlayerDrive extends RobotBase {

    private int prevPos = 0;
    private PIDFCoefficients pidfCoefficients;
    @Override
    public void init() {
        super.init();
        this.pidfCoefficients = launcher.motor.getPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER);
        this.prevPos = lf.getCurrentPosition();
    }

    @Override
    public void loop() {
        gamepad1Loop();
        gamepad2Loop();
    }

    boolean settingVelocity = false;
    private void gamepad1Loop() {
        driveSystem.drive(-gamepad1.left_stick_y, gamepad1.left_stick_x, gamepad1.right_stick_x);

        //TEMP: Increase launcher speed values
        if (gamepad1.xWasPressed()) { launcher.MAX_POWER -= .05; }
        if (gamepad1.bWasPressed()) { launcher.MAX_POWER += .05; }

        //Slow down when holding left or right bumper
        if (gamepad1.right_bumper || gamepad1.left_bumper) { driveSystem.powerFactor = 0.2; }
        else { driveSystem.powerFactor = 1.; }

        double PRINCIPAL_POWER = 0.61;
        double voltage = getVoltage();
        double res = scaleVoltPF(voltage);
        double res2 = 0.7 * res;

        /* Intake, Launcher, Spinners
           --------------------------
        * If only intake is active spin center inwards and left outwards
        * If launcher is active at all spin the center inwards and left inwards
         */
        boolean ACTIVE_LAUNCHER = gamepad1.left_trigger > 0.1;
        boolean ACTIVE_INTAKE = gamepad1.right_trigger > 0.1;

        launcher.togglePower(ACTIVE_LAUNCHER, launcher.MAX_POWER);
        if (gamepad1.dpad_down) {
            settingVelocity = true;
            launcher.motor.setVelocity(1600);
        } else if (gamepad1.dpad_up) {
            settingVelocity = false;
            launcher.motor.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidfCoefficients);
            launcher.motor.setVelocity(0);
        }
        if (settingVelocity) {
            launcher.motor.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(pidfCoefficients.p, pidfCoefficients.i, pidfCoefficients.d, (32767d / 1600d)));
            launcher.motor.setVelocity(1600);
        }

        tongue.togglePosition(gamepad1.y);

        intake.SWAP_DIRECTION = !gamepad1.dpad_down;
        intake.togglePower(ACTIVE_INTAKE, intake.MAX_POWER);

        leftSpinner.SWAP_DIRECTION = gamepad1.a;
        boolean SPIN_SPINNERS = ACTIVE_INTAKE || gamepad1.a;
        leftSpinner.togglePower(SPIN_SPINNERS, leftSpinner.MAX_POWER);
        centerSpinner.togglePower(SPIN_SPINNERS, centerSpinner.MAX_POWER);



        //Telemetry + Debug Data
        telemetry.addData("leftSpinner power", leftSpinner.motor.getPower());
        telemetry.addData("rightSpinner power", leftSpinner.motor.getPower());
        telemetry.addData("scale/res", res);
        telemetry.addData("res 2", res2);
        telemetry.addData("voltage", voltage);
        telemetry.addData("Launcher Power: ", launcher.MAX_POWER);
        telemetry.addData("Launcher ACTUAL Power: ", launcher.motor.getPower());
        telemetry.addData("Vel", launcher.motor.getVelocity());
        telemetry.addData("Trying to maintain vel", settingVelocity);
        telemetry.addData("lf traveled", lf.getCurrentPosition() - prevPos);
        telemetry.addData("lf power", lf.getPower());
        telemetry.addData("rf power", rf.getPower());
        telemetry.addData("lb power", lb.getPower());
        telemetry.addData("rb power", rb.getPower());
        telemetry.addLine("");
        telemetry.addLine("");
        telemetry.addLine("--- PlayerDrive Controls (Gamepad 1) ---");
        telemetry.addLine();
        telemetry.addLine("DRIVING:");
        telemetry.addData("Left Stick", "Forward/Backward & Strafe");
        telemetry.addData("Right Stick", "Pivot/Turn");
        telemetry.addData("Left/Right Bumper", "Hold for Slow Mode (20% power)");
        telemetry.addLine();
        telemetry.addLine("SYSTEMS:");
        telemetry.addData("Right Trigger", "Hold to run Intake & Spinners");
        telemetry.addData("Left Trigger", "Hold to run Launcher");
        telemetry.addData("DPad Up/Down", "Set Launcher Velocity (Up=0)");
        telemetry.addData("'A' Button", "Hold to run Spinners (reverses left)");
        telemetry.addData("'Y' Button", "Press to toggle Tongue position");
        telemetry.addLine();
        telemetry.addLine("TUNING:");
        telemetry.addData("'X' / 'B' Button", "Decrease/Increase Launcher Power");
        telemetry.addLine("------------------------------------");
        telemetry.update();
    }

    private void gamepad2Loop() {}

}
