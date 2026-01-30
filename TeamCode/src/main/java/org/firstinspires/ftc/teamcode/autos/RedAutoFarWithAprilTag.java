package org.firstinspires.ftc.teamcode.autos;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import org.firstinspires.ftc.teamcode.AutoBase;
import org.firstinspires.ftc.teamcode.debug.util.Async;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;

import java.util.List;

@Autonomous(name = "RedAutoFarWithAprilTag", group = "Auto")
public class RedAutoFarWithAprilTag extends AutoBase {

    private enum TagDetected {
        NONE, GPP, PGP, PPG
    }

    private TagDetected detectedTag = TagDetected.NONE;

    @Override
    public void init() {
        USE_CAMERA = true;
        super.init();
    }

    @Override
    public void init_loop() {
        detectTag();
        telemetry.addData("Detected Tag", detectedTag);
        telemetry.update();
    }

    @Override
    public void start() {
        // --- PRE-DETECTION INSTRUCTIONS ---
        // Placeholder for initial movement/setup
        moveBot(5, 0.2, 0, 0);

        // --- BRANCHED INSTRUCTIONS ---
        switch (detectedTag) {
            case GPP:
                // Instructions for Tag 22 (GPP)
                telemetry.addLine("Executing GPP (22) Logic");
                break;
            case PGP:
                // Instructions for Tag 22 (PGP)
                telemetry.addLine("Executing PGP (22) Logic");
                break;
            case PPG:
                // Instructions for Tag 23 (PPG)
                telemetry.addLine("Executing PPG (23) Logic");
                break;
            case NONE:
            default:
                // Fallback instructions
                telemetry.addLine("No target tag detected, running default");
                break;
        }
        telemetry.update();

        // --- POST-BRANCH INSTRUCTIONS ---
        // Placeholder for final movements
        moveBot(16, 0.2, 0, 0);
    }

    private void detectTag() {
        if (aTagProc == null) return;
        
        List<AprilTagDetection> detections = aTagProc.getDetections();
        for (AprilTagDetection detection : detections) {
            if (detection.id == GPP_TAG) { // 22
                detectedTag = TagDetected.GPP;
                break;
            } else if (detection.id == PGP_TAG) { // 22
                detectedTag = TagDetected.PGP;
                break;
            } else if (detection.id == PPG_TAG) { // 23
                detectedTag = TagDetected.PPG;
                break;
            }
        }
    }
}
