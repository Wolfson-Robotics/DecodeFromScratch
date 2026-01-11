package org.firstinspires.ftc.teamcode.components;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class MecanumDrive {

    public DcMotorEx lf, lb, rf, rb;
    public double powerFactor = 1D; //Lower to slow down the speed

    public IMU imu;

    public double ROBOT_LENGTH_IN = 13.62;

    public MecanumDrive(DcMotorEx lf, DcMotorEx lb, DcMotorEx rf, DcMotorEx rb) {
        this.lf = lf;
        this.lb = lb;
        this.rf = rf;
        this.rb = rb;
        this.lf.setDirection(DcMotorSimple.Direction.REVERSE);
        this.lb.setDirection(DcMotorSimple.Direction.REVERSE);
        this.lf.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        this.lb.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        this.rf.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        this.rb.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    public MecanumDrive(HardwareMap map, String lf, String lb, String rf, String rb) {
        this(
            (DcMotorEx) map.get(lf),
            (DcMotorEx) map.get(lb),
            (DcMotorEx) map.get(rf),
            (DcMotorEx) map.get(rb)
        );
    }


    /**
    Drive the mecanum system (WARNING: Not based on any units!)
     @param y Move forward and backward
     @param x Move left and right
     @param rotation Rotate
     */
    public void drive(float y, float x, float rotation) {
        //Based on an equation that determines proper power in each wheel for mecanum drive
        double rightFrontPower = powerFactor * (-rotation + (y - x));
        double rightBackPower = powerFactor * (-rotation + y + x);
        double leftFrontPower = powerFactor * (rotation + y + x);
        double leftBackPower = powerFactor * (rotation + (y - x));

        // Normalize wheel powers to be less than 1.0
        double max = Math.max(Math.abs(rightFrontPower), Math.abs(rightFrontPower));
        max = Math.max(max, Math.abs(leftBackPower));
        max = Math.max(max, Math.abs(rightBackPower));
        if (max > 1.0) {
            leftFrontPower /= max;
            rightFrontPower /= max;
            leftBackPower /= max;
            rightBackPower /= max;
        }

        rf.setPower(rightFrontPower);
        rb.setPower(rightBackPower);
        lf.setPower(leftFrontPower);
        lb.setPower(leftBackPower);
    }

    /**
     * Drive a certain amount of time <br>
     * ||WARNING: WILL PAUSE THREAD OF EXECUTION FOR THE TIME||
     * @param y Forward/Backwards
     * @param x Left/Right
     * @param time Time to drive in seconds
     */
    public void driveForSeconds(float y, float x, double time) {
        ElapsedTime timer = new ElapsedTime();
        while(timer.seconds() < time) {
            drive(y, x, 0);
        }
        drive(0, 0, 0);
    }

    /*
    Returns an array of all the motors: lf, lb, rf, rb (in that order) <br>
    Use for getAllMotors.forEach(n -> n.someDcMotorExMethod());
     */
    public Stream<DcMotorEx> getAllMotors() {
        return Arrays.stream(new DcMotorEx[]{lf, lb, rf, rb});
    }

    /**
     * Sets the DcMotor.RunMode on all the mecanum motors
     * @param mode
     */
    public void setMode(DcMotor.RunMode mode) {
        lb.setMode(mode);
        lf.setMode(mode);
        rb.setMode(mode);
        rf.setMode(mode);
    }





    /*
    --------------------------------------
    Autonomous Specific Movement Functions
    --------------------------------------
     */

    private double ticsPerInch = 38;
    public void moveBotInches(double distIN, double vertical, double pivot, double horizontal) {

        // 23 motor tics = 1 IN
        int motorTics;
        int posNeg = (vertical >= 0) ? 1 : -1;

        rf.setPower(powerFactor * (-pivot + (vertical - horizontal)));
        rb.setPower(powerFactor * (-pivot + vertical + horizontal));
        lf.setPower(powerFactor * (pivot + vertical + horizontal));
        lb.setPower(powerFactor * (pivot + (vertical - horizontal)));

        if (horizontal != 0) {
            posNeg = (horizontal > 0) ? 1 : -1;
            motorTics = lf.getCurrentPosition() + (int) ((distIN * ticsPerInch) * (posNeg));
            if (posNeg == 1) {
                // right goes negative
                while ((lf.getCurrentPosition() < motorTics)) {
                    Thread.yield();
                }
            } else {
                // left goes positive
                while ((lf.getCurrentPosition() > motorTics)) {
                    Thread.yield();
                }
            }
        } else {
            posNeg = vertical >= 0 ? 1 : -1;
            motorTics = rf.getCurrentPosition() + (int) ((distIN * ticsPerInch) * posNeg);
            if (posNeg == -1) {
                while (rf.getCurrentPosition() > motorTics) {
                    Thread.yield();
                }
            } else {
                while ((rf.getCurrentPosition() < motorTics)) {
                    Thread.yield();
                }
            }

        }
//        removePower();
        lf.setPower(0);
        lb.setPower(0);
        rf.setPower(0);
        rb.setPower(0);

    }

    private double degConv = 0.51;
    public void turnBot(double power, double degrees) {
        // 13.62 inches is default robot length
        double distUnit = (ROBOT_LENGTH_IN) / (Math.cos(45));
        double distIN = (Math.abs((distUnit * ((degrees*1.75))) / 90))*degConv;
        int motorTics;
        int pivot = (degrees >= 0) ? 1 : -1;
//        rf.setPower(powerFactor * (-pivot));
//        rb.setPower(powerFactor * (-pivot));
//        lf.setPower(powerFactor * (pivot));
//        lb.setPower(powerFactor * (pivot));
        rf.setPower(power * (-pivot));
        rb.setPower(power * (-pivot));
        lf.setPower(power * (pivot));
        lb.setPower(power * (pivot));
        motorTics = lf.getCurrentPosition() + (int) Math.round((distIN * ticsPerInch)* pivot);
        if (pivot == 1) {
            while ((lf.getCurrentPosition() < motorTics)) {
                Thread.yield();
            }
        }
        if (pivot == -1) {
            while ((lf.getCurrentPosition() > motorTics)) {
                Thread.yield();
            }
        }
//        removePower();
        lf.setPower(0);
        lb.setPower(0);
        rf.setPower(0);
        rb.setPower(0);


    }



}
