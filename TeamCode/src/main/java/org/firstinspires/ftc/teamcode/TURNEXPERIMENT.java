package org.firstinspires.ftc.teamcode;

import static org.firstinspires.ftc.teamcode.debug.util.Async.sleep;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.util.ControllerNumberInput;
import org.firstinspires.ftc.teamcode.util.PersistentTelemetry;

@Autonomous(name = "TURNEXPERIMENT")
public class TURNEXPERIMENT extends RobotBase {

    //April Tag IDs
    public final int BLUE_TAG = 20, RED_TAG = 24, GPP_TAG = 22, PGP_TAG = 22, PPG_TAG = 23;

    protected boolean USE_CAMERA = false;

    private final PersistentTelemetry pTelem = new PersistentTelemetry(telemetry);
    private ControllerNumberInput input;
    // Implement below
//    private final double powerFactor = 0.3;

    @Override
    public void start() {
        moveBot(9, 0.2, 0, 0);
    }

    @Override
    public void init() {
        super.init();
        this.input = new ControllerNumberInput(gamepad1, pTelem);
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

//        degConv = testD;
        turnBot(0.2, testD);
        sleep(1000);
        turnBot(0.2, -testD);
        sleep(1000);
    }

    @Override
            public void stop() {
        super.stop();
        this.input.reset();
        this.input = null;
    }
    boolean toggle = false;


}
