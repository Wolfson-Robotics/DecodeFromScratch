package org.firstinspires.ftc.teamcode.testing;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.AutoBase;
import org.firstinspires.ftc.teamcode.RobotBase;
import org.firstinspires.ftc.teamcode.components.RollerEx;
import org.firstinspires.ftc.teamcode.components.camera.VisionPortalCamera;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

@TeleOp(name = "TestAimer")
public class TestAimer extends AutoBase {

    @Override
    public void init() {
        super.init();
        initCamera();
    }

    final double ticsPerRev = 537.7;
    final double gearRatio = 92.0 / 200.0;

    boolean isAiming = false;
    @Override
    public void loop() {
        AprilTagDetection tag = null;
        for (AprilTagDetection dTag : aTagProc.getDetections()) {
            if (dTag.id == BLUE_TAG) { tag = dTag; }
        }


        double range = -1;
        double yaw = -1;
        double rangeX = -1;
        double rangeY = -1;

        //TAKEN FROM Aimer.java
        if (tag != null) {
            range = tag.ftcPose.range;
            yaw = tag.ftcPose.yaw;
            rangeX = range * Math.sin(Math.toRadians(aimer.globalAngle));
            rangeY = range * Math.cos(Math.toRadians(aimer.globalAngle));
        }
        double distX = pinpoint.getPosX(DistanceUnit.INCH);
        double distY = pinpoint.getPosY(DistanceUnit.INCH);

        if (gamepad1.x) {
            aimer.aim(30);
        }

        telemetry.addLine("|---POINT AT BLUE TAG---|");
        telemetry.addData("Range", range);
        telemetry.addData("Yaw", yaw);
        telemetry.addData("DistX", distX);
        telemetry.addData("DistY", distY);
        telemetry.addData("IMU", imu.getRobotYawPitchRollAngles());
        telemetry.addData("rangeX", rangeX);
        telemetry.addData("rangeY", rangeY);
        telemetry.addData("isAiming", isAiming);
        telemetry.addData("encoderPos", aimer.encoderPos);
        telemetry.addData("targetPos", aimer.targetPos);
        telemetry.addData("currentPos", aimer.motor.getCurrentPosition());
        telemetry.addData("globalAngle", aimer.globalAngle);
        telemetry.addData("targetAngle", aimer.targetAngle);
        telemetry.addData("Power", aimer.power);
        telemetry.update();
    }

}
