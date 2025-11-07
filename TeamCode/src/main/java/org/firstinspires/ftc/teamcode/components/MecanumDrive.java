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

    public MecanumDrive(DcMotorEx lf, DcMotorEx lb, DcMotorEx rf, DcMotorEx rb) {
        this.lf = lf;
        this.lb = lb;
        this.rf = rf;
        this.rb = rb;
        this.lf.setDirection(DcMotorSimple.Direction.REVERSE);
        this.lb.setDirection(DcMotorSimple.Direction.REVERSE);
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

    /** Drive the mecanum system relative to the field.
     *  Meaning the movement of the mecanum system won't be dependent on the robot's rotation
     * @param y Forward/Backwards
     * @param x Left/Right
     * @param rotation Rotate
     */
    public void driveFieldCentric(float y, float x, float rotation) {
        if (imu == null) {
            throw new IllegalStateException(
                    "IMU was not provided, cannot use driveFieldCentric. Please provide an IMU."
            );
        }

        double theta = Math.atan2(y, x);
        double r = Math.hypot(x, y);

        theta = AngleUnit.normalizeRadians(
                theta - imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES)
        );

        double newX = r * Math.sin(theta);
        double newY = r * Math.cos(theta);

        this.drive((float) newY, (float) newX, rotation);
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
}
