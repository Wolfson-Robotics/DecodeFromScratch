package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.debug.util.Async;
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

        stopper.applyPosition(stopper.MIN_POSITION);
    }

    @Override
    public void stop() {
        super.stop();
        this.input.reset();
        this.input = null;
    }

    @Override
    public void loop() {
        double vel = launcher.motor.getVelocity();

        telemetry.addData("USE CAMERA", USE_CAMERA);
        telemetry.addData("Vel" + count, vel);
        //Don't add telemetry.update(). Don't want extending classes to overwrite ^^^
    }


    /*
    ----------------------------
    Autonomous Utility Functions
    ----------------------------
     */
    int count = 0;
    protected void setLauncher(double velocity) {
        launcher.applyVelocity(velocity);

        ElapsedTime time = new ElapsedTime();
        time.reset();
        while (time.seconds() < 6 || !launcher.reachedVelocity()) {
            Thread.yield();
        }

        count++;
    }

    protected void preventStuck() {
        feedDirectionForward(false);
        runFeed();
        Async.sleep(200);
        stopFeed();
        feedDirectionForward(true);
    }

    protected void shootBetter(double velocity) {
        for (int i = 0; i < 3; i++) {
            setLauncher(velocity); //Wait till the velocity gets to its target
            if (i > 0) {
                preventStuck(); //Move backwards, Move forwards, (feed)
            }
            runFeed(); //Runs the feed

            //Pause for different lengths based on current ball
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

            stopFeed(); //Stops the feed
            Async.sleep(3000); //Pauses before next launch/run
        }

        stopShoot();
    }

    //Stop all the shooting
    public void stopShoot() {
        launcher.applyPower(0);
        stopFeed();
    }

    //Run the feed
    protected void runFeed() {
        transfer.applyPower(transfer.MAX_POWER);
        transport.applyPower(transport.MAX_POWER);
        intake.applyPower(intake.MAX_POWER);
    }

    protected void feedDirectionForward(boolean swap) {
        swap = !swap; //So it matches the name
        transfer.swapDirection(swap);
        transport.swapDirection(swap);
        intake.swapDirection(swap);
    }

    //Stop the feed
    protected void stopFeed() {
        transfer.applyPower(0);
        transport.applyPower(0);
        intake.applyPower(0);
    }

    /*
    --------
    Wrappers
    --------
    For old auto code
     */
    public void moveBot(double distIN, double vertical, double pivot, double horizontal) {
        driveSystem.moveBotInches(distIN, vertical, pivot, horizontal);
    }

    public void turnBot(double power, double degrees) {
        driveSystem.turnBot(power, degrees);
    }

}