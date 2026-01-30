package org.firstinspires.ftc.teamcode.testing;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.RobotBase;
import org.firstinspires.ftc.teamcode.debug.util.Async;
import org.firstinspires.ftc.teamcode.util.ControllerNumberInput;
import org.firstinspires.ftc.teamcode.util.PersistentTelemetry;

@TeleOp(name = "TurnTuner", group = "Testing")
public class TurnTuner extends RobotBase {

    private PersistentTelemetry pTelem;
    private ControllerNumberInput input;
    private double currentDegConv = 0.51;

    @Override
    public void init() {
        super.init();
        pTelem = new PersistentTelemetry(telemetry);
        input = new ControllerNumberInput(gamepad1, pTelem);
        
        driveSystem.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        
        pTelem.addLine("--- TURN TUNER ---");
        pTelem.setData("Current degConv", () -> String.format("%.4f", currentDegConv));
        pTelem.addLine("Press B to run 90-degree test");
        pTelem.update();
    }

    @Override
    public void loop() {
        double val = input.getInput();
        if (!Double.isNaN(val)) {
            currentDegConv = val;
        }

        if (gamepad1.bWasPressed()) {
            runTest();
        }

        pTelem.update();
    }

    private void runTest() {
        pTelem.addLine("Running test...");
        pTelem.update();
        
        turnBotCustom(0.5, 90, currentDegConv);
        Async.sleep(1000);
        turnBotCustom(0.5, -90, currentDegConv);
        
        pTelem.addLine("Test complete.");
        pTelem.update();
    }

    /**
     * Copy-pasted from MecanumDrive.java and modified to use customDegConv
     */
    public void turnBotCustom(double power, double degrees, double customDegConv) {
        // 13.62 inches is default robot length
        double distUnit = (driveSystem.ROBOT_LENGTH_IN) / (Math.cos(Math.toRadians(45)));
        double distIN = (Math.abs((distUnit * ((degrees * 1.75))) / 90)) * customDegConv;
        int motorTics;
        int pivot = (degrees >= 0) ? 1 : -1;

        driveSystem.rf.setPower(power * (-pivot));
        driveSystem.rb.setPower(power * (-pivot));
        driveSystem.lf.setPower(power * (pivot));
        driveSystem.lb.setPower(power * (pivot));
        
        motorTics = driveSystem.lf.getCurrentPosition() + (int) Math.round((distIN * driveSystem.ticsPerInch) * pivot);
        
        if (pivot == 1) {
            while (opModeIsActive() && (driveSystem.lf.getCurrentPosition() < motorTics)) {
                // Thread.yield(); is usually handled in LinearOpMode, in OpMode we just wait
            }
        }
        if (pivot == -1) {
            while (opModeIsActive() && (driveSystem.lf.getCurrentPosition() > motorTics)) {
                // Thread.yield();
            }
        }

        driveSystem.lf.setPower(0);
        driveSystem.lb.setPower(0);
        driveSystem.rf.setPower(0);
        driveSystem.rb.setPower(0);
    }
    
    // Helper to mimic LinearOpMode behavior in iterative OpMode for the while loop
    private boolean opModeIsActive() {
        return true; // Simple stub for iterative OpMode
    }
}
