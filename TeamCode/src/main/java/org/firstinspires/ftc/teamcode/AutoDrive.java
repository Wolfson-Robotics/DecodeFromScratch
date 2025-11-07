package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.debug.util.Async;

@Autonomous(name = "AutoDrive")
public abstract class AutoDrive extends RobotBase {

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

    @Override
    public void start() {
        moveBot(10, 1, 0, 0);
        Async.sleep(200);
        moveBot(20, 1, 0, 0);
    }


    boolean toggle = false;


}
