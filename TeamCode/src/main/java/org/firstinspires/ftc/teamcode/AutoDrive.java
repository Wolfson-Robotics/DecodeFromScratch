package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
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

    enum AUTO_MODE {
        GPP,
        PGP,
        PPG,
        DO_NOTHING
    }
    protected AUTO_MODE mode = AUTO_MODE.DO_NOTHING;

    @Override
    public void init() {
        super.init();
        initCamera();
        driveSystem.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    @Override
    public void init_loop() {
        telemetry.addLine("||CURRENT MODE: " + mode + "||");
        telemetry.addLine("A: GPP");
        telemetry.addLine("B: PGP");
        telemetry.addLine("X: PPG");
        telemetry.addLine("Y: Do Nothing (DEFAULT STATE)");
        telemetry.update();

        if (gamepad1.a) { mode = AUTO_MODE.GPP; }
        if (gamepad1.b) { mode = AUTO_MODE.PGP; }
        if (gamepad1.x) { mode = AUTO_MODE.PPG; }
        if (gamepad1.y) { mode = AUTO_MODE.DO_NOTHING; }
    }

    @Override
    public void loop() {
        for (AprilTagDetection detection : aTagProc.getDetections()) {
            telemetry.addData("Detected Tag: ", detection.metadata.name);
        }
        telemetry.speak("I have become sentient, praise me");
        telemetry.update();
    }

}
