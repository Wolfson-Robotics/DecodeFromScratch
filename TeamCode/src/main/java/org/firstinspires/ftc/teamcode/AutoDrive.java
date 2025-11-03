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

    protected boolean USE_CAMERA = false;


    @Override
    public void init() {
        super.init();

        if (USE_CAMERA) {
            initCamera();
            if (camera == null) { USE_CAMERA = false; }
        }
        driveSystem.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    boolean toggle = false;
    @Override
    public void loop() {
    }

}
