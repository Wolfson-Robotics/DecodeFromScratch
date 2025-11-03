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

    private void gamepad1Loop() {
        driveSystem.drive(-gamepad1.left_stick_y, gamepad1.left_stick_x, gamepad1.right_stick_x);

        //TEMP: Increase launcher speed values
        if (gamepad1.xWasPressed()) { launcher.MAX_POWER -= .1; }
        if (gamepad1.bWasPressed()) { launcher.MAX_POWER += .1; }
        telemetry.addData("Launcher Power: ", launcher.MAX_POWER);
        telemetry.update();

        //Slow down when holding left or right bumper
        if (gamepad1.right_bumper || gamepad1.left_bumper) { driveSystem.powerFactor = 0.2; }
        else { driveSystem.powerFactor = 1.; }

        /* Intake, Launcher, Spinners
           --------------------------
        * If only intake is active spin center inwards and left outwards
        * If launcher is active at all spin the center inwards and left inwards
         */
        boolean ACTIVE_LAUNCHER = gamepad1.left_trigger > 0.1;
        boolean ACTIVE_INTAKE = gamepad1.right_trigger > 0.1;
        launcher.adjustPower(ACTIVE_LAUNCHER);
        intake.adjustPower(ACTIVE_INTAKE);

        leftSpinner.SWAP_DIRECTION = ACTIVE_LAUNCHER;
        boolean SPIN_SPINNERS = ACTIVE_INTAKE || gamepad1.a;
        leftSpinner.togglePower(SPIN_SPINNERS, leftSpinner.MAX_POWER);
        centerSpinner.togglePower(SPIN_SPINNERS, centerSpinner.MAX_POWER);
    }

    private void gamepad2Loop() {}

}
