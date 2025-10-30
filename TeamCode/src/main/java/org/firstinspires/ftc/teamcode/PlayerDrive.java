package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name = "PlayerDrive")
public class PlayerDrive extends Base {

    @Override
    public void init() {
        super.init();
    }

    @Override
    public void loop() {
        gamepad1Loop();
        gamepad2Loop();
    }

    double i = 0D;
    private void gamepad1Loop() {
        driveSystem.drive(-gamepad1.left_stick_y, gamepad1.left_stick_x, gamepad1.right_stick_x);

        //TEMP: Increase launcher speed values
        if (gamepad1.xWasPressed()) { launcher.MAX_POWER -= .1; }
        if (gamepad1.bWasPressed()) { launcher.MAX_POWER += .1; }
        telemetry.addData("Launcher Power: ", launcher.MAX_POWER);

        launcher.adjustPower(gamepad1.left_trigger > 0.1);
        intake.adjustPower(gamepad1.right_trigger > 0.1);
        leftSpinner.togglePower(gamepad1.a, leftSpinner.MAX_POWER);
        centerSpinner.togglePower(gamepad1.a, centerSpinner.MAX_POWER);
    }

    private void gamepad2Loop() {}

}
