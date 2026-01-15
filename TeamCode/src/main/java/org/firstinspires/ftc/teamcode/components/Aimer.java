package org.firstinspires.ftc.teamcode.components;

import static java.lang.Math.sin;

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;

/** Aimer - 360 Degrees Auto Aimer Towards Target
 *  ---------------------------------------------
 *  The Target will be an April Tag which has positional data
 *  The Aimer will face itself towards the target to the best of its ability
 *  The Aimer will account for rotational constraints due to wires and prevent tangling
 */
public class Aimer extends RollerEx {

    public IMU imu = null;
    public GoBildaPinpointDriver pinpoint = null;

    public double TICKS_PER_REV = 537.7;
    public double GEAR_RATIO = 1.0 / 4.0;

    public double encoderPos;
    public double robotHeading;
    public double localAngle;
    public double globalAngle;

    public Aimer(DcMotorEx motor) {
        super(motor);
        this.motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        this.motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    public Aimer(HardwareMap map, String motorName) {
        this((DcMotorEx) map.get(motorName));
    }

    public double targetX = 999;
    public double targetY = 999;
    public void aim(AprilTagDetection tag) {
        if (imu == null || pinpoint == null) { return; }

        encoderPos = motor.getCurrentPosition();
        localAngle = (encoderPos / (TICKS_PER_REV * GEAR_RATIO)) * 360;
        if (imu != null) {
            robotHeading = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES);
            globalAngle = localAngle + robotHeading;
        }

        if (tag == null) {
            aimToTarget();
            return;
        }

        //Get the tag in global space with the imu
        double range = tag.ftcPose.range;
        double yaw = tag.ftcPose.yaw;
        double distX = pinpoint.getPosX(DistanceUnit.INCH);
        double distY = pinpoint.getPosY(DistanceUnit.INCH);
        double rangeX = range * Math.sin(Math.toRadians(globalAngle));
        double rangeY = range * Math.cos(Math.toRadians(globalAngle));
        targetX = rangeX + distX;
        targetY = rangeY + distY;

        //Aim at the tag since its in view
        aimLocalDegrees(yaw); //supposedly the yaw is in degrees?
    }

    public void aimToTarget() {
        if (targetX == 999 || targetY == 999) { return; }

        //Use that global space to figure out the angle needed/record it
        double robotX = pinpoint.getPosX(DistanceUnit.INCH);
        double robotY = pinpoint.getPosY(DistanceUnit.INCH);
        double neededGlobalAngle = Math.toDegrees(Math.atan2(targetY - robotY, targetX - robotX));
        //Set aimer motor position to aim at the tag
        aimGlobalDegrees(neededGlobalAngle);
    }

    public void aimGlobalDegrees(double globalDegrees) {
        double neededLocalAngle = globalDegrees - robotHeading;
        aimLocalDegrees(neededLocalAngle);
    }

    public void aimLocalDegrees(double localDegrees) {
        int neededPosition = (int)((localDegrees / 360.0) * (TICKS_PER_REV * GEAR_RATIO));
        motor.setTargetPosition(neededPosition);
        motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motor.setPower(0.5);
    }

}
