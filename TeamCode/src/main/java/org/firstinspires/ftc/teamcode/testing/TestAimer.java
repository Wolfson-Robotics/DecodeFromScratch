package org.firstinspires.ftc.teamcode.testing;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.RobotBase;
import org.firstinspires.ftc.teamcode.components.RollerEx;
import org.firstinspires.ftc.teamcode.components.camera.VisionPortalCamera;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

@Autonomous(name = "CalibrateAimer")
public class TestAimer extends RobotBase {

    public final int BLUE_TAG = 20, RED_TAG = 24, GPP_TAG = 22, PGP_TAG = 22, PPG_TAG = 23;

    @Override
    public void init() {
//        aTagProc = AprilTagProcessor.easyCreateWithDefaults();
//       /* camera = VisionPortalCamera.createVisionPortalCamera(
//                ,
//                aTagProc
//        );*/
//
//        VisionPortal vp = VisionPortalCamera.createCustomVisionPortal((WebcamName) hardwareMap.get("Webcam 1"), aTagProc);
//        camera = new VisionPortalCamera(vp);
        initCamera();
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

        aimer.aim(yaw);
    }

}
