package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

@TeleOp(name = "PlayerDrive")
public class PlayerDrive extends RobotBase {

    private int prevPos = 0;
    private PIDFCoefficients pidfCoefficients;
    @Override
    public void init() {
        super.init();
        this.pidfCoefficients = launcher.motor.getPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER);
        this.prevPos = driveSystem.lf.getCurrentPosition();
    }

    @Override
    public void loop() {
        gamepad1Loop();
        gamepad2Loop();
        commonPrintData();
    }

    boolean settingVelocity = false;
    private void gamepad1Loop() {
        //Driving
        driveSystem.drive(-gamepad1.left_stick_y, gamepad1.left_stick_x, gamepad1.right_stick_x);

        //Slow down when holding left or right bumper
        if (gamepad1.right_bumper || gamepad1.left_bumper) { driveSystem.powerFactor = 0.2; }
        else { driveSystem.powerFactor = 1.; }
    }

    /* Intake, Launcher, Transfer
       --------------------------
    */
    private void gamepad2Loop() {
        //TEMP: Increase launcher speed values
        if (gamepad2.xWasPressed()) { launcher.MAX_POWER -= .05; }
        if (gamepad2.bWasPressed()) { launcher.MAX_POWER += .05; }

        launcher.togglePower(gamepad2.left_trigger > 0.1, launcher.MAX_POWER);

        boolean ACTIVE_INTAKE = gamepad2.right_trigger > 0.1;
        if (gamepad2.dpad_right) { intake.SWAP_DIRECTION = !intake.SWAP_DIRECTION; }
        intake.togglePower(ACTIVE_INTAKE, intake.MAX_POWER);
        transfer.togglePower(ACTIVE_INTAKE, transfer.MAX_POWER);
        transport.togglePower(ACTIVE_INTAKE, transport.MAX_POWER);

        stopper.togglePosition(gamepad2.y);
    }

    //Telemetry + Debug Data
    private void commonPrintData() {
        double PRINCIPAL_POWER = 0.61;
        double voltage = getVoltage();
        double res = scaleVoltPF(voltage);
        double res2 = 0.7 * res;

        telemetry.addData("scale/res", res);
        telemetry.addData("res 2", res2);
        telemetry.addData("voltage", voltage);
        telemetry.addData("Launcher Power: ", launcher.MAX_POWER);
        telemetry.addData("Launcher ACTUAL Power: ", launcher.motor.getPower());
        telemetry.addData("Vel", launcher.motor.getVelocity());
        telemetry.addData("Trying to maintain vel", settingVelocity);
        telemetry.addData("lf traveled", driveSystem.lf.getCurrentPosition() - prevPos);
        telemetry.addData("lf power", driveSystem.lf.getPower());
        telemetry.addData("rf power", driveSystem.rf.getPower());
        telemetry.addData("lb power", driveSystem.lb.getPower());
        telemetry.addData("rb power", driveSystem.rb.getPower());
        telemetry.addLine("");
        telemetry.addLine("");
        telemetry.addLine("--- PlayerDrive Controls (Gamepad 1) ---");
        telemetry.addLine();
        telemetry.addLine("DRIVING:");
        telemetry.addData("Left Stick", "Forward/Backward & Strafe");
        telemetry.addData("Right Stick", "Pivot/Turn");
        telemetry.addData("Left/Right Bumper", "Hold for Slow Mode");
        telemetry.addLine();
        telemetry.addLine("--- PlayerDrive Controls (Gamepad 2) ---");
        telemetry.addLine();
        telemetry.addLine("SYSTEMS:");
        telemetry.addData("Right Trigger", "Hold to run Intake & Spinners");
        telemetry.addData("Left Trigger", "Hold to run Launcher");
        telemetry.addData("DPad Right", "Swap Intake Direction");
        telemetry.addData("DPad Up/Down", "Set Launcher Velocity (Up=0)");
        telemetry.addLine();
        telemetry.addLine("TUNING:");
        telemetry.addData("'X' / 'B' Button", "Decrease/Increase Launcher Power");
        telemetry.addLine("------------------------------------");
        telemetry.update();
    }




    /*       if (gamepad2.dpad_down) {
            settingVelocity = true;
            launcher.motor.setVelocity(1600);
        } else if (gamepad2.dpad_up) {
            settingVelocity = false;
            launcher.motor.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidfCoefficients);
            launcher.motor.setVelocity(0);
        }
        if (settingVelocity) {
            launcher.motor.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(pidfCoefficients.p, pidfCoefficients.i, pidfCoefficients.d, (32767d / 1600d)));
            launcher.motor.setVelocity(1600);
        }*/
}
