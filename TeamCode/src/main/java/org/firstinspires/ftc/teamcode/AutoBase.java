package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.debug.util.Async;
import org.firstinspires.ftc.teamcode.util.ControllerNumberInput;
import org.firstinspires.ftc.teamcode.util.PersistentTelemetry;

@Autonomous(name = "AutoDrive")
public abstract class AutoBase extends RobotBase {

    //Close dist: 1440
    //

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

        stopper.applyPosition(stopper.MIN_POSITION);
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

    protected void preventStuck() {
        transfer.SWAP_DIRECTION = true;
        transport.SWAP_DIRECTION = true;
        intake.SWAP_DIRECTION = true;
        transfer.applyPower(transfer.MAX_POWER);
        transport.applyPower(transport.MAX_POWER);
        intake.applyPower(intake.MAX_POWER);
        Async.sleep(200);
        transfer.applyPower(0);
        transport.applyPower(0);
        intake.applyPower(0);
        transfer.SWAP_DIRECTION = false;
        transport.SWAP_DIRECTION = false;
        intake.SWAP_DIRECTION = false;
    }

    //Run the feed
    protected void runFeed() {
        transfer.applyPower(transfer.MAX_POWER);
        transport.applyPower(transport.MAX_POWER);
        intake.applyPower(intake.MAX_POWER);
    }

    protected void shootBetter(double velocity) {
        for (int i = 0; i < 3; i++) {
            setLauncher(velocity); //Wait till the velocity gets to its target
            if (i > 0) {
                preventStuck(); //Move backwards, Move forwards, (feed)
                //Async.sleep(300); //Pause for 0.3s
            }
            runFeed(); //Runs the feed
            switch(i) {
                case 0:
                    Async.sleep(500);
                    break;
                case 1:
                    Async.sleep(850);
                    break;
                case 2:
                    Async.sleep(1500);
                    stopper.applyPosition(stopper.MAX_POSITION);
                    Async.sleep(1500);
                    break;
            }

            //Pause for 0.4s
            stopFeed(); //Stops the feed
            Async.sleep(3000); //Pauses before next launch/run
        }

        stopShoot();
    }

    //Stop all the shooting
    public void stopShoot() {
        launcher.motor.setPower(0);
        stopFeed();
    }

    //Stop the feed
    public void stopFeed() {
        transfer.applyPower(0);
        transport.applyPower(0);
        intake.applyPower(0);
    }

    //Wrappers
    public void moveBot(double distIN, double vertical, double pivot, double horizontal) {
        driveSystem.moveBotInches(distIN, vertical, pivot, horizontal);
    }

    public void turnBot(double power, double degrees) {
        driveSystem.turnBot(power, degrees);
    }

}