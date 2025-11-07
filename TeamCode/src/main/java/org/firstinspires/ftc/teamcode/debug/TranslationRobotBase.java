package org.firstinspires.ftc.teamcode.debug;

import static org.firstinspires.ftc.teamcode.debug.util.GeneralUtils.signClamp;

import org.firstinspires.ftc.teamcode.RobotBase;
import org.firstinspires.ftc.teamcode.debug.handlers.DcMotorExHandler;
import org.firstinspires.ftc.teamcode.debug.util.Async;

import java.util.concurrent.atomic.AtomicBoolean;

/*
This class was made to translate the code from the old code base to the new component based
one.

 */
public abstract class TranslationRobotBase extends RobotBase {

    /*
    ---------
    CONSTANTS
    ---------
     */

    public DcMotorExHandler lf_drive = new DcMotorExHandler(driveSystem.lf);
    public DcMotorExHandler lb_drive = new DcMotorExHandler(driveSystem.lb);
    public DcMotorExHandler rf_drive = new DcMotorExHandler(driveSystem.rf);
    public DcMotorExHandler rb_drive = new DcMotorExHandler(driveSystem.rb);
    public Double powerFactor = driveSystem.powerFactor;
    public static final double ROBOT_LENGTH = 13.62;
    public static final double TICS_PER_INCH = 0d, DEG_CONV = 0;

    /*
    -----------------------------------
    TRANSLATIONS FROM OLD AutoBase CODE
    -----------------------------------
     */



    private final AtomicBoolean stop = new AtomicBoolean(false);

    // notes:
    // rf_drive, when going forward, goes into the negative direction positionally
    // 12 inches (or a foot) is 73.6770894730908 robot inches
    /**
     * @param distIN distance in inches to move
     * @param vertical positive value goes forward, negative goes backward
     * @param pivot positive pivot is clockwise, negative pivot is counterclockwise
     * @param horizontal positive horizontal is right, negative horizontal is left
     */
    public void moveBot(double distIN, double vertical, double pivot, double horizontal) {
        // 23 motor tics = 1 IN
        int targetPos;

        // Drive power assignments
        rf_drive.setPower(powerFactor * (-pivot + (vertical - horizontal)));
        rb_drive.setPower(powerFactor * (-pivot + vertical + horizontal));
        lf_drive.setPower(powerFactor * (pivot + vertical + horizontal));
        lb_drive.setPower(powerFactor * (pivot + (vertical - horizontal)));

        // Horizontal strafe
        if (horizontal != 0) {
            targetPos = lf_drive.getCurrentPosition() + (int) ((distIN * TICS_PER_INCH) * signClamp(horizontal));
            while (!lf_drive.reachedPos(targetPos) && opModeIsActive()) {
                idle();
            }
            /*
            if (posNeg == 1) {
                // Right goes negative
                while (lf_drive.getCurrentPosition() < motorTics && opModeIsActive()) {
                    idle();
                }
            } else {
                // Left goes positive
                while (lf_drive.getCurrentPosition() > motorTics && opModeIsActive()) {
                    idle();
                }
            }*/
        }
        // Forward/backward motion
        else {
            targetPos = rf_drive.getCurrentPosition() + (int) ((distIN * TICS_PER_INCH) * signClamp(vertical));
            while (!rf_drive.reachedPos(targetPos) && opModeIsActive()) {
                idle();
            }
/*
            if (posNeg == -1) {
                while (rf_drive.getCurrentPosition() > motorTics && opModeIsActive()) {
                    idle();
                }
            } else {
                while (rf_drive.getCurrentPosition() < motorTics && opModeIsActive()) {
                    idle();
                }
            }*/
        }

        removeWheelPower();
    }

    protected void moveBotDiagonal(double horizIN, double vertIN, double vertical, double horizontal) {
        rf_drive.setPower(powerFactor * ((vertical - horizontal)));
        rb_drive.setPower(powerFactor * (vertical + horizontal));
        lf_drive.setPower(powerFactor * (vertical + horizontal));
        lb_drive.setPower(powerFactor * ((vertical - horizontal)));

        int horizDiff = (int) (horizIN * TICS_PER_INCH), vertDiff = (int) (vertIN * TICS_PER_INCH);
        int prevHorizPos = rf_drive.getCurrentPosition(), prevVertPos = lf_drive.getCurrentPosition();
        if (horizDiff > vertDiff) {
            moveBot(lf_drive.getCurrentPosition() + (vertDiff * signClamp(horizontal)), vertical, 0, horizontal);
            moveBot(horizDiff - (rf_drive.getCurrentPosition() - prevHorizPos), 0, 0, horizontal);
        } else {
            moveBot(rf_drive.getCurrentPosition() + (horizDiff * signClamp(vertical)), vertical, 0, horizontal);
            moveBot(vertDiff - (lf_drive.getCurrentPosition() - prevVertPos), vertical, 0, horizontal);
        }
    }
    public void moveBotDiagonal(double horizIN, double vertIN) {
        double angle = Math.toDegrees(Math.atan(vertIN/horizIN));
        moveBot(Math.sqrt(Math.pow(horizIN, 2) + Math.pow(vertIN, 2)), signClamp(vertIN), 0, signClamp(horizIN)*((double)1/45)*(angle-45));
    }

    public void moveBot(double lfPower, double lbPower, double rfPower, double rbPower, DcMotorExHandler cmp, double cmpPos) {
        rf_drive.setPower(powerFactor * rfPower);
        rb_drive.setPower(powerFactor * rbPower);
        lf_drive.setPower(powerFactor * lfPower);
        lb_drive.setPower(powerFactor * lbPower);
        while (!cmp.reachedPos(cmpPos) && opModeIsActive()) {
            idle();
        }
        removeWheelPower();
    }


    protected void turnBot(double degrees) {
        // 13.62 inches is default robot length
        double distUnit = ROBOT_LENGTH/Math.cos(45);
        double distIN = (Math.abs((distUnit * ((degrees*1.75)))/90))*DEG_CONV;
        int motorTics;
        int pivot = signClamp(degrees);
        rf_drive.setPower(powerFactor * (-pivot));
        rb_drive.setPower(powerFactor * (-pivot));
        lf_drive.setPower(powerFactor * (pivot));
        lb_drive.setPower(powerFactor * (pivot));
        motorTics = lf_drive.getCurrentPosition() + (int) Math.round((distIN * TICS_PER_INCH)* pivot);
        while (!lf_drive.reachedPos(motorTics) && opModeIsActive()) {
            idle();
        }
        /*
        if (pivot == 1) {
            while ((lf_drive.getCurrentPosition() < motorTics) && opModeIsActive()) {
                idle();
            }
        }
        if (pivot == -1) {
            while ((lf_drive.getCurrentPosition() > motorTics) && opModeIsActive()) {
                idle();
            }
        }*/
        removeWheelPower();

    }


    protected double ticsToInches(double tics) {
        return tics/TICS_PER_INCH;
    }


    /*
    --------------
    From RobotBase
    --------------
     */

    public void enableBrakes() {
        this.lf_drive.enableBrake();
        this.lb_drive.enableBrake();
        this.rf_drive.enableBrake();
        this.rb_drive.enableBrake();
    }
    public void disableBrakes() {
        this.lf_drive.disableBrake();
        this.lb_drive.disableBrake();
        this.rf_drive.disableBrake();
        this.rb_drive.disableBrake();
    }
    public void removeWheelPower() {
        this.lf_drive.setPower(0);
        this.lb_drive.setPower(0);
        this.rf_drive.setPower(0);
        this.rb_drive.setPower(0);
    }

    protected boolean opModeIsActive() {
        return !stop.get();
    }
    protected void sleep(long ms) {
        Async.sleep(ms);
    }
    protected void idle() {
        Thread.yield();
    }

    @Override
    public void stop() {
        stop.set(true);
        Async.stopAll();
    }

}
