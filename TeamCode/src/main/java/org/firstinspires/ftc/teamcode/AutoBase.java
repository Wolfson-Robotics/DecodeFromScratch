package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.util.ControllerNumberInput;
import org.firstinspires.ftc.teamcode.util.PersistentTelemetry;

@Autonomous(name = "AutoDrive")
public abstract class AutoBase extends RobotBase {

    //April Tag IDs
    public final int BLUE_TAG = 20, RED_TAG = 24, GPP_TAG = 22, PGP_TAG = 22, PPG_TAG = 23;
    protected boolean USE_CAMERA = false;

    private final PersistentTelemetry pTelem = new PersistentTelemetry(telemetry);
    private ControllerNumberInput input = new ControllerNumberInput(gamepad1, pTelem);

    @Override
    public void init() {
        super.init();
        if (USE_CAMERA) {
            initCamera();
            if (camera == null) {
                USE_CAMERA = false;
            }
        }

        driveSystem.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        driveSystem.getAllMotors().forEach(
                n -> n.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE)
        );
        launcher.motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    @Override
    public void stop() {
        super.stop();
        this.input.reset();
        this.input = null;
    }


    /*
    ----------------------------
    Autonomous Utility Functions
    ----------------------------
     */

    int count = 0;
    protected void setLauncher(double velocity) {
        launcher.motor.setVelocity(velocity);
        double vel = launcher.motor.getVelocity();

        while (Math.abs(vel - velocity) > 20) {
            vel = launcher.motor.getVelocity();
            telemetry.addData("vel" + count, vel);
            telemetry.update();
            try {Thread.sleep(110); } catch (Exception e) {}
            Thread.yield();
        }

        telemetry.addData("vel" + count, vel);
        telemetry.update();
        count++;
    }

    protected void prepareShoot() {
        leftSpinner.motor.setPower(-1);
        centerSpinner.motor.setPower(1);
    }

    protected void shootBetter(double velocity) {
        for (int i = 0; i < 2; i++) {
            setLauncher(velocity);
            prepareShoot();
            try {
                Thread.sleep(2000);
            } catch (Exception e) {}

            leftSpinner.motor.setPower(0);
            centerSpinner.motor.setPower(0);
        }

        stopShoot();
        launcher.motor.setPower(0);
    }

    public void stopShoot() {
        launcher.motor.setPower(0);
        leftSpinner.motor.setPower(0);
        centerSpinner.motor.setPower(0);
    }

    //Wrappers
    public void moveBot(double distIN, double vertical, double pivot, double horizontal) {
        driveSystem.moveBotInches(distIN, vertical, pivot, horizontal);
    }

    public void turnBot(double power, double degrees) {
        driveSystem.turnBot(power, degrees);
    }

}