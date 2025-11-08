package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.debug.util.Async;
import org.firstinspires.ftc.teamcode.util.ControllerNumberInput;
import org.firstinspires.ftc.teamcode.util.PersistentTelemetry;

@TeleOp(name = "LauncherPowerInput")
public class LauncherPowerInput extends RobotBase {

    private final PersistentTelemetry pTelem = new PersistentTelemetry(telemetry);

    private final ControllerNumberInput input = new ControllerNumberInput(gamepad1, pTelem);


    @Override
    public void loop() {
        double testD = input.getInput();
        if (Double.isNaN(testD)) return;
        launcher.motor.setPower(testD);
        pTelem.addLine("Reaching power");
        pTelem.update();
        Async.sleep(6000);
        pTelem.addLine("Reached power");
        pTelem.update();
        leftSpinner.motor.setPower(1);
        centerSpinner.motor.setPower(1);
        Async.sleep(5000);
        launcher.motor.setPower(0);
        Async.sleep(6000);
    }
}
