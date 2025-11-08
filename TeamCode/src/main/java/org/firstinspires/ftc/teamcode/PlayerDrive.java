package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

@TeleOp(name = "PlayerDrive")
public class PlayerDrive extends RobotBase {

    private int prevPos = 0;
    @Override
    public void init() {
        super.init();
        this.prevPos = lf.getCurrentPosition();

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
        double res2 = 0.7 * res;

        launcher.MAX_POWER = PRINCIPAL_POWER * res;
        launcher.togglePower(ACTIVE_LAUNCHER, launcher.MAX_POWER);
        if (gamepad1.dpad_down) {
            launcher.motor.setVelocity(480, AngleUnit.DEGREES);
        } else if (gamepad1.dpad_up) {
            launcher.motor.setVelocity(0, AngleUnit.DEGREES);
        }

        double MIN_POS = 0.2D;
        double MAX_POS = 1D;
        if (gamepad1.y) {
            //TODO: Elijah fix this it sucks and didn't work
            //0 is hitting the bar, 1 is not hitting the bar
            double pos = 1D;
            if (tongue.getPosition() >= MAX_POS - 0.05) {
                pos = MIN_POS;
            } else {
                pos = MAX_POS;
            }
            tongue.setPosition(pos);
        }

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
        telemetry.addData("lf traveled", lf.getCurrentPosition() - prevPos);
        telemetry.addData("lf power", lf.getPower());
        telemetry.addData("rf power", rf.getPower());
        telemetry.addData("lb power", lb.getPower());
        telemetry.addData("rb power", rb.getPower());
        telemetry.update();
    }

    private void gamepad2Loop() {}

}
