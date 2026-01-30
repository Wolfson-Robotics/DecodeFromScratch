package org.firstinspires.ftc.teamcode.testing;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.teamcode.RobotBase;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;

import java.util.List;

@TeleOp(name = "TestAprilTagDetection", group = "Testing")
public class TestAprilTagDetection extends RobotBase {

    @Override
    public void init() {
        super.init();
        initCamera();
    }

    @Override
    public void loop() {
        if (aTagProc != null) {
            List<AprilTagDetection> detections = aTagProc.getDetections();
            telemetry.addData("# Tags Detected", detections.size());

            for (AprilTagDetection detection : detections) {
                String customName = getTagName(detection.id);
                
                if (detection.metadata != null) {
                    telemetry.addLine(String.format("\n==== (ID %d) %s | %s", detection.id, detection.metadata.name, customName));
                    telemetry.addLine(String.format("XYZ %6.1f %6.1f %6.1f  (inch)", detection.ftcPose.x, detection.ftcPose.y, detection.ftcPose.z));
                    telemetry.addLine(String.format("PRY %6.1f %6.1f %6.1f  (deg)", detection.ftcPose.pitch, detection.ftcPose.roll, detection.ftcPose.yaw));
                    telemetry.addLine(String.format("RBE %6.1f %6.1f %6.1f  (inch/deg)", detection.ftcPose.range, detection.ftcPose.bearing, detection.ftcPose.elevation));
                } else {
                    telemetry.addLine(String.format("\n==== (ID %d) Unknown | %s", detection.id, customName));
                    telemetry.addLine(String.format("Center %6.0f %6.0f (pixels)", detection.center.x, detection.center.y));
                }
            }
        } else {
            telemetry.addLine("AprilTag Processor not initialized.");
        }
        telemetry.update();
    }

    private String getTagName(int id) {
        if (id == BLUE_TAG) return "BLUE_TAG";
        if (id == RED_TAG) return "RED_TAG";
        if (id == GPP_TAG) return "GPP/PGP_TAG"; // Both are ID 22 in RobotBase
        if (id == PPG_TAG) return "PPG_TAG";
        return "Not a game tag";
    }
}
