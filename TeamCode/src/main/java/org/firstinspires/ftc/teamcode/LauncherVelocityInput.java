package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.debug.util.Async;
import org.firstinspires.ftc.teamcode.util.ControllerNumberInput;
import org.firstinspires.ftc.teamcode.util.PersistentTelemetry;

@TeleOp(name = "LauncherVelocityInput")
public class LauncherVelocityInput extends RobotBase {

    private final PersistentTelemetry pTelem = new PersistentTelemetry(telemetry);

    private final ControllerNumberInput input = new ControllerNumberInput(gamepad1, pTelem);


    @Override
    public void loop() {
            double testD = input.getInput();
            if (Double.isNaN(testD)) return;
            launcher.motor.setVelocity(testD, AngleUnit.DEGREES);
            pTelem.addLine("Reaching Velocity");
            pTelem.update();
            while (Math.abs(launcher.motor.getVelocity(AngleUnit.DEGREES) - testD) <= 10) {
                Thread.yield();
            }
            pTelem.addLine("Reached Velocity");
            pTelem.update();
            leftSpinner.motor.setPower(1);
            centerSpinner.motor.setPower(1);
            Async.sleep(5000);
            launcher.motor.setVelocity(0);
            Async.sleep(5000);
    }
}
