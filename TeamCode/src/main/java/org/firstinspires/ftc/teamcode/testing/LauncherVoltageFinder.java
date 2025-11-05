package org.firstinspires.ftc.teamcode.testing;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Base;
import org.firstinspires.ftc.teamcode.debug.util.Async;

@TeleOp(name = "LauncherVoltageFinder")
public class LauncherVoltageFinder extends Base {

    @Override
    public void init() {
        super.init();
    }

    @Override
    public void loop() {
        gamepad1Loop();
    }

    private void gamepad1Loop() {

        // Sweep power from 0.1 to 1.0 in 0.1 increments
        for (double power = 0.1; power <= 1.0; power += 0.1) {

            // Clamp power to avoid floating issues (1.099999 -> 1.1)
            power = Math.round(power * 10) / 10.0;

            // Run each power value twice
            for (int i = 0; i < 2; i++) {

                launcher.motor.setPower(power);

                telemetry.addData("Current Power", power);
                telemetry.addData("Run #", i + 1);
                telemetry.update();

                Async.sleep(3000);     // motor on for 3 seconds
                launcher.motor.setPower(0);
                Async.sleep(5000);     // motor off for 5 seconds
            }
        }

        // When done, turn motor off
        launcher.motor.setPower(0);

        telemetry.addData("Status", "Finished. Motor Off.");
        telemetry.update();
    }

}
