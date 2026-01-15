package org.firstinspires.ftc.teamcode.testing;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
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

@Autonomous(name = "CalibrateAimer")
public class TestAimer extends AutoBase {

    @Override
    public void init() {
        super.init();
        initCamera();
    }

    final double ticsPerRev = 537.7; //TODO: find
    final double gearRatio = 1.0 / 4.0; //TODO: figure out if this is accurate

    boolean isAiming = false;
    @Override
    public void loop() {
        AprilTagDetection tag = null;
        for (AprilTagDetection dTag : aTagProc.getDetections()) {
            if (dTag.id == BLUE_TAG) { tag = dTag; }
        }

        //TAKEN FROM Aimer.java
        double range = tag.ftcPose.range;
        double yaw = tag.ftcPose.yaw;
        double distX = pinpoint.getPosX(DistanceUnit.INCH);
        double distY = pinpoint.getPosY(DistanceUnit.INCH);
        double rangeX = range * Math.sin(Math.toRadians(aimer.globalAngle));
        double rangeY = range * Math.cos(Math.toRadians(aimer.globalAngle));

        if (gamepad1.yWasPressed()) { isAiming = !isAiming; }
        if (isAiming) { aimer.aim(tag); }

        telemetry.addLine("|---POINT AT BLUE TAG---|");
        telemetry.addData("Range", range);
        telemetry.addData("Yaw", yaw);
        telemetry.addData("DistX", distX);
        telemetry.addData("DistY", distY);
        telemetry.addData("rangeX", rangeX);
        telemetry.addData("rangeY", rangeY);
        telemetry.addData("encoderPos", aimer.encoderPos);
        telemetry.addData("globalAngle", aimer.globalAngle);
        telemetry.addData("targetX", aimer.targetX);
        telemetry.addData("targetY", aimer.targetY);
        telemetry.update();
    }

}
