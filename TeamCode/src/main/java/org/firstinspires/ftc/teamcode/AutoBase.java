package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.components.Turret;
import org.firstinspires.ftc.teamcode.debug.util.Async;
import org.firstinspires.ftc.teamcode.util.ControllerNumberInput;
import org.firstinspires.ftc.teamcode.util.PersistentTelemetry;

@Autonomous(name = "AutoDrive")
public abstract class AutoBase extends RobotBase {

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
        // Inside your start() method or init block
        turret.switchTurretState(Turret.TurretState.GO_TO_ZERO);

        FAR_VELOCITY = 1555;
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
        //Don't add telemetry.update(). Don't want extending classes to be overwritten ^^^
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
        while (!isStopRequested() && time.seconds() < 5 && !launcher.reachedVelocity()) {
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

    int AFTER_SHOOT_VELOCITY_DECREASE = -150;
    protected void waitForBallShoot(double initVelocity, double ms) {
        ElapsedTime time = new ElapsedTime();
        time.reset();
        int count = 0;
        while (!isStopRequested() && time.milliseconds() < ms && (launcher.motor.getVelocity() - initVelocity) <= AFTER_SHOOT_VELOCITY_DECREASE) {
            telemetry.addData("Seconds", time.seconds());
            telemetry.addData("Launcher Vel", launcher.motor.getVelocity());
            telemetry.addData("Reached Lower Vel", (launcher.motor.getVelocity() - initVelocity) <= AFTER_SHOOT_VELOCITY_DECREASE);
            telemetry.addData("Count", count);
            telemetry.update();
            count++;
        }
    }

    protected void shootEvenBetter(double velocity) {
        for (int i = 0; i < 3; i++) {
            Async.runTasksAsync(
                    () -> setLauncher(velocity),
                    this::consolidateFeed
            );
            Async.sleep(100);
            stopper.applyPosition(stopper.MIN_POSITION);
            runFeed();
            Async.sleep(500);
            stopper.applyPosition(stopper.MIN_POSITION);
            stopFeed();
            //reverseFeed();
            Async.sleep(150);
        }
        stopShoot();
    }

    protected void shootBetter(double velocity) {
        stopper.applyPosition(stopper.MIN_POSITION);
        for (int i = 0; i < 3; i++) {
            telemetry.addLine("Beginning ball " + i);
            telemetry.update();
            setLauncher(velocity); //Wait till the velocity gets to its target
            Async.sleep(600);
            runFeed(); //Runs the feed

            telemetry.addLine("Performing ball " + i);
            //Pause for different lengths based on current ball
            switch(i) {
                case 0:
                    //waitForBallShoot(velocity, 10000);
                    Async.sleep(600); //NEEDED
                    break;
                case 1:
                    //waitForBallShoot(velocity, 10000);
                    Async.sleep(450); //NEEDED
                    stopper.applyPosition(stopper.MAX_POSITION);
                    break;
                case 2:
                    Async.sleep(1500);
                    stopper.applyPosition(stopper.MAX_POSITION);
                    Async.sleep(500);
                    break;
            }

            telemetry.addLine("Ending ball " + i);
            telemetry.update();
            stopFeed(); //Stops the feed
            //Async.sleep(150); //Pauses before next launch/run
            if (i == 1) {
                telemetry.addLine("Ending ball " + i);
                telemetry.addLine("On 1");
                telemetry.update();
                reverseFeed();
                Async.sleep(150);
                stopper.applyPosition(stopper.MIN_POSITION);
            }
            //stopper.applyPosition(stopper);
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

    protected void reverseFeed() {
        transfer.applyPower(-transfer.MAX_POWER * 0.3);
        lTransport.applyPower(-lTransport.MAX_POWER * 0.3);
        rTransport.applyPower(-rTransport.MAX_POWER * 0.3);
        intake.applyPower(-intake.MAX_POWER * 0.3);
        Async.sleep(100);
        stopFeed();
    }

    protected void consolidateFeed() {
        stopFeed();
        stopper.applyPosition(stopper.MAX_POSITION);
        runFeed();
        Async.sleep(1000);
        stopFeed();
        //stopper.applyPosition(stopper.MIN_POSITION);
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

    public void moveBotDiagonal(double horizIN, double vertIN) {
        driveSystem.moveBotDiagonal(horizIN, vertIN);
    }
    public void moveBotDiagonal(double horizIN, double vertIN, double vertical, double horizontal) {
        driveSystem.moveBotDiagonal(horizIN, vertIN, vertical, horizontal);
    }

    public void turnBot(double power, double degrees) {
        driveSystem.turnBot(power, degrees);
    }

}
