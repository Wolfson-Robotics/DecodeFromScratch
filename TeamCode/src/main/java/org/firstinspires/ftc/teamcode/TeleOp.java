package org.firstinspires.ftc.teamcode;

public class TeleOp extends Base {

    @Override
    public void loop() {
        gamepad1Loop();
        gamepad2Loop();
    }

    private void gamepad1Loop() {
        launcher.adjustPower(gamepad1.left_trigger > 0.1);
        intake.adjustPower(gamepad1.right_trigger > 0.1);
    }

    private void gamepad2Loop() {
        driveSystem.drive(gamepad1.left_stick_y, gamepad2.left_stick_x, gamepad2.right_stick_x);
    }

}
