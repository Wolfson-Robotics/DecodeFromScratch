package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name = "PlayerDrive")
public class PlayerDrive extends RobotBase {

    @Override
    public void loop() {
        driveSystem.drive(-gamepad1.left_stick_y, gamepad1.left_stick_x, gamepad1.right_stick_x);

        //TEMP: Increase launcher speed values
        if (gamepad1.xWasPressed()) { launcher.MAX_POWER -= .1; }
        if (gamepad1.bWasPressed()) { launcher.MAX_POWER += .1; }

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
        double PRINCIPAL_POWER = 0.61;
        double voltage = getVoltage();
        double res = scaleVoltPF(voltage);
        double res2 = 0.7*res;

        launcher.MAX_POWER = PRINCIPAL_POWER*res;
        launcher.togglePower(ACTIVE_LAUNCHER, launcher.MAX_POWER);
//        launcher.

        intake.togglePower(ACTIVE_INTAKE, intake.MAX_POWER);

        leftSpinner.SWAP_DIRECTION = gamepad1.a;
        boolean SPIN_SPINNERS = ACTIVE_INTAKE || gamepad1.a;
        leftSpinner.togglePower(SPIN_SPINNERS, leftSpinner.MAX_POWER);
        centerSpinner.togglePower(SPIN_SPINNERS, centerSpinner.MAX_POWER);

        telemetry.addData("leftSpinner power", leftSpinner.motor.getPower());
        telemetry.addData("rightSpinner power", leftSpinner.motor.getPower());
        telemetry.addData("scale/res", res);
        telemetry.addData("res 2", res2);
        telemetry.addData("voltage", voltage);
        telemetry.addData("Launcher Power: ", launcher.MAX_POWER);
        telemetry.addData("Launcher ACTUAL Power: ", launcher.motor.getPower());
        telemetry.update();
    }

}
