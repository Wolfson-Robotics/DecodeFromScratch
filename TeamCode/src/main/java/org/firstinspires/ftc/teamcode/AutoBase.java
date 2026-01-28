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
        int count = 0;
        while (time.seconds() < 5 && !launcher.reachedVelocity()) {
            count++;
            telemetry.addData("Seconds", time.seconds());
            telemetry.addData("Launcher Vel", launcher.motor.getVelocity());
            telemetry.addData("Reached Stable Vel", launcher.reachedVelocity());
            telemetry.addData("Target Vel", velocity);
            telemetry.addData("Count", count);
            telemetry.update();
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
        stopper.applyPosition(stopper.MIN_POSITION);
        for (int i = 0; i < 3; i++) {
            setLauncher(velocity); //Wait till the velocity gets to its target
            runFeed(); //Runs the feed

            //Pause for different lengths based on current ball
            switch(i) {
                case 0:
                    Async.sleep(500); //NEEDED
                    break;
                case 1:
                    Async.sleep(850); //NEEDED
                    break;
                case 2:
                    Async.sleep(1500);
                    stopper.applyPosition(stopper.MAX_POSITION);
                    Async.sleep(500);
                    break;
            }

            stopFeed(); //Stops the feed
            //Async.sleep(150); //Pauses before next launch/run
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
        lTransport.applyPower(lTransport.MAX_POWER);
        rTransport.applyPower(rTransport.MAX_POWER);
        intake.applyPower(intake.MAX_POWER);
    }

    protected void feedDirectionForward(boolean swap) {
        swap = !swap; //So it matches the name
        transfer.swapDirection(swap);
        lTransport.swapDirection(swap);
        rTransport.swapDirection(swap);
        intake.swapDirection(swap);
    }

    //Stop the feed
    protected void stopFeed() {
        transfer.applyPower(0);
        lTransport.applyPower(0);
        rTransport.applyPower(0);
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