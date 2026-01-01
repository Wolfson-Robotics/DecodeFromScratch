package org.firstinspires.ftc.teamcode.testing;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.components.RollerEx;
import org.firstinspires.ftc.teamcode.components.camera.VisionPortalCamera;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

@Autonomous(name = "CalibrateAimer")
public class CalibrateAimer extends OpMode {

    public final int BLUE_TAG = 20, RED_TAG = 24, GPP_TAG = 22, PGP_TAG = 22, PPG_TAG = 23;

    public RollerEx aimer;

    public VisionPortalCamera camera;
    public AprilTagProcessor aTagProc;

    @Override
    public void init() {
        aimer = new RollerEx(hardwareMap, "aimer");
        aimer.MAX_POWER = 0.1;
        aimer.motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        aTagProc = AprilTagProcessor.easyCreateWithDefaults();
       /* camera = VisionPortalCamera.createVisionPortalCamera(
                ,
                aTagProc
        );*/

        VisionPortal vp = VisionPortalCamera.createCustomVisionPortal((WebcamName) hardwareMap.get("Webcam 1"), aTagProc);
        camera = new VisionPortalCamera(vp);
    }

    @Override
    public void loop() {
        AprilTagDetection tag = null;
        for (AprilTagDetection dTag : aTagProc.getDetections()) {
            if (dTag.id == BLUE_TAG) { tag = dTag; }
        }
        if (tag == null) {
            aimer.applyPower(0);
            return;
        }

        double yaw = tag.ftcPose.yaw;
        telemetry.addData("Yaw", yaw);

        adjustRobot(tag);
    }

    public void adjustRobot(AprilTagDetection tag) {
        double yaw = tag.ftcPose.yaw;

        aimer.SWAP_DIRECTION = yaw > 0;
        aimer.applyPower(aimer.MAX_POWER);
    }


}
