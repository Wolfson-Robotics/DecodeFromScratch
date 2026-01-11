package org.firstinspires.ftc.teamcode.components;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

/** Aimer - 360 Degrees Auto Aimer Towards Target
 *  ---------------------------------------------
 *  The Target will be an April Tag which has positional data
 *  The Aimer will face itself towards the target to the best of its ability
 *  The Aimer will account for rotational constraints due to wires and prevent tangling
 */
public class Aimer extends RollerEx {

    //public double targetYaw;
    //private double currentYaw;
    IMU imu;

    double TICKS_PER_REV = 437;
    double GEAR_RATIO = 1;

    public Aimer(DcMotorEx motor, IMU imu) {
        super(motor);
        this.motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        this.motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    public Aimer(HardwareMap map, String motorName, String imuName) {
        this((DcMotorEx) map.get(motorName), (IMU) map.get(imuName));
    }

    public void aim(double yaw) {
        motor.getCurrentPosition();
        Orientation robotOrientation = imu.getRobotOrientation(AxesReference.EXTRINSIC, AxesOrder.XYZ, AngleUnit.DEGREES);
        double robotHeading = robotOrientation.secondAngle;
        double curAngle = (motor.getCurrentPosition() / TICKS_PER_REV) * GEAR_RATIO + robotHeading;

    }
    //current angle (motorpositiion / ticksperrev) * gearRatio + botHeading
    //FTCLib PID Controller
}
