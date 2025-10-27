package org.firstinspires.ftc.teamcode.components;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class MecanumDrive {

    public DcMotorEx lf, lb, rf, rb;
    double powerFactor = 1D; //Lower to slow down the speed

    public MecanumDrive(DcMotorEx lf, DcMotorEx lb, DcMotorEx rf, DcMotorEx rb) {
        this.lf = lf;
        this.lb = lb;
        this.rf = rf;
        this.rb = rb;
        this.lf.setDirection(DcMotorSimple.Direction.REVERSE);
        this.lb.setDirection(DcMotorSimple.Direction.REVERSE);
    }
    public MecanumDrive(HardwareMap map, String lf, String lb, String rf, String rb) {
        this.lf = (DcMotorEx) map.get(lf);
        this.lb = (DcMotorEx) map.get(lb);
        this.rf = (DcMotorEx) map.get(rf);
        this.rb = (DcMotorEx) map.get(rb);
        this.lf.setDirection(DcMotorSimple.Direction.REVERSE);
        this.lb.setDirection(DcMotorSimple.Direction.REVERSE);
    }


    /**
    Drive the mecanum system (WARNING: Not based on any units!)
     @y - move forward and backward
     @x - move left and right
     @rotation - rotate
     */
    public void drive(float y, float x, float rotation) {
        //Based on an equation that determines proper power in each wheel for mecanum drive
        double leftFrontPower = y + x + rotation;
        double rightFrontPower = y - x - rotation;
        double leftBackPower = y - x + rotation;
        double rightBackPower = y + x - rotation;

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

        leftFrontPower *= powerFactor;
        rightFrontPower *= powerFactor;
        leftBackPower *= powerFactor;
        rightBackPower *= powerFactor;

        rf.setPower(rightFrontPower);
        rb.setPower(rightBackPower);
        lf.setPower(leftFrontPower);
        lb.setPower(leftBackPower);
    }

}
