package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.components.MecanumDrive;
import org.firstinspires.ftc.teamcode.components.Roller;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;

@Autonomous(name = "AutoDrive")
public class AutoDrive extends Base {

    //April Tag IDs
    public final int BLUE_TAG = 20;
    public final int RED_TAG = 24;
    public final int GPP_TAG = 21;
    public final int PGP_TAG = 22;
    public final int PPG_TAG = 23;

    public void init() {
        super.init();
        initCamera();
    }

    @Override
    public void loop() {
        for (AprilTagDetection detection : aTagProc.getDetections()) {
            telemetry.addData("Detected Tag: ", detection.metadata.name);
        }
        telemetry.update();
        telemetry.speak("I have become sentient, praise me");
    }

}
