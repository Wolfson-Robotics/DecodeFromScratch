package org.firstinspires.ftc.teamcode;

import static org.firstinspires.ftc.teamcode.debug.util.Async.sleep;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.debug.util.Async;
import org.firstinspires.ftc.teamcode.util.ControllerNumberInput;
import org.firstinspires.ftc.teamcode.util.PersistentTelemetry;

@Autonomous(name = "AutoDrive")
public class AutoDrive extends RobotBase {

    //April Tag IDs
    public final int BLUE_TAG = 20, RED_TAG = 24, GPP_TAG = 22, PGP_TAG = 22, PPG_TAG = 23;

    protected boolean USE_CAMERA = false;

    private final PersistentTelemetry pTelem = new PersistentTelemetry(telemetry);
    private final ControllerNumberInput input = new ControllerNumberInput(gamepad1, pTelem);
    // Implement below
//    private final double powerFactor = 0.3;


    @Override
    public void init() {
        super.init();
        lf.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rf.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        lb.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rb.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        if (USE_CAMERA) {
            initCamera();
            if (camera == null) { USE_CAMERA = false; }
        }
        driveSystem.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    @Override
    public void loop() {

        double testD = input.getInput();
        if (Double.isNaN(testD)) return;
        /*intCon = testD;
        moveBot(12, 0.2, 0, 0);
        sleep(1000);
        moveBot(12, -0.2, 0, 0);
        sleep(1000);*/

        degConv = testD;
        turnBot(90);
        sleep(1000);
        turnBot(-90);
        sleep(1000);
    }
    boolean toggle = false;


}
