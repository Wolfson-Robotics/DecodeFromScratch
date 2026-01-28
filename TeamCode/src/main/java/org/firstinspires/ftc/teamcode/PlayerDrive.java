package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.components.Turret;
import org.firstinspires.ftc.teamcode.components.camera.VisionPortalCamera;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@TeleOp(name = "PlayerDrive")
public class PlayerDrive extends RobotBase {

    private static final Logger log = LoggerFactory.getLogger(PlayerDrive.class);

    int targetTag = BLUE_TAG;

    @Override
    public void init() {
        super.init();
        initCamera();

        if (gamepad2.dpadLeftWasPressed()) {
            targetTag = BLUE_TAG;
        }
        if (gamepad2.dpadRightWasPressed()) {
            targetTag = RED_TAG;
        }
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
    double curVelTarget = CLOSE_VELOCITY;
    boolean stableVelocity = false;
    //boolean firstMode = true;
    AprilTagDetection tag = null;
    private void gamepad2Loop() {
        if (gamepad2.xWasPressed()) { launcher.MAX_POWER -= .05; }
        if (gamepad2.bWasPressed()) { launcher.MAX_POWER += .05; }
        if (gamepad2.dpadUpWasPressed()) { curVelTarget += 25; }
        if (gamepad2.dpadDownWasPressed()) { curVelTarget -= 25; }
        if (gamepad2.dpadLeftWasPressed()) {
            curVelTarget = curVelTarget != CLOSE_VELOCITY ? CLOSE_VELOCITY : FAR_VELOCITY;
        }

        tag = VisionPortalCamera.getTargetTag(aTagProc, targetTag);
        turret.TARGET_TAG = tag;
        /*if (gamepad2.leftBumperWasPressed()) {
            if (firstMode) {
                turret.curTurretState = Turret.TurretState.FOLLOW_TAG;
                firstMode = false;
            } else {
                turret.curTurretState = Turret.TurretState.GO_TO_ZERO;
                firstMode = true;
            }
        }*/
        if (gamepad2.leftBumperWasPressed()) {
            turret.switchTurretState(Turret.TurretState.FOLLOW_TAG);
        }
        turret.loop(); //NECESSARY FOR TURRET TO WORK

        if (gamepad2.rightBumperWasReleased()) {
            launcher.switchVelocity(curVelTarget);
        } else if (launcher.targetVelocity == 0) {
            launcher.togglePower(gamepad2.left_trigger > 0.1, launcher.MAX_POWER);
        }

        stableVelocity = launcher.reachedVelocity() && launcher.motor.getVelocity() > 0;

        if (gamepad2.dpadRightWasPressed()) {
            intake.swapDirection();
            transfer.swapDirection();
            lTransport.swapDirection();
            rTransport.swapDirection();
        }
        boolean ACTIVE_INTAKE = gamepad2.right_trigger > 0.1;
        intake.togglePower(ACTIVE_INTAKE, intake.MAX_POWER);
        transfer.togglePower(ACTIVE_INTAKE, transfer.MAX_POWER);
        lTransport.togglePower(ACTIVE_INTAKE, lTransport.MAX_POWER);
        rTransport.togglePower(ACTIVE_INTAKE, rTransport.MAX_POWER);

        stopper.togglePosition(!gamepad2.y);
    }

    //Telemetry + Debug Data
    private void commonPrintData() {
        telemetry.addLine("|||--IMPORTANT INFORMATION--|||");
        telemetry.addData("Launcher Velocity", launcher.motor.getVelocity());
        telemetry.addData("Launcher Target Velocity", curVelTarget);
        telemetry.addData("Launcher Velocity Stable", stableVelocity);
        telemetry.addData("Launcher Power", launcher.motor.getPower());
        telemetry.addData("April Tag Visible", tag != null);
        telemetry.addData("Turret State", turret.curTurretState);
        telemetry.addData("Turret Target Global Yaw", turret.TARGET_GLOBAL_YAW);
        telemetry.addLine("|||-------------------------|||");
        telemetry.addLine();
        telemetry.addLine();
        telemetry.addLine();
        telemetry.addLine();
        telemetry.addLine("|||----GAMEPAD1 CONTROLS----|||");
        telemetry.addData("Moving (Forward & Strafe)", "Left Stick");
        telemetry.addData("Rotating", "Right Stick");
        telemetry.addData("Slow Down", "Left/Right Bumper");
        telemetry.addLine("|||-------------------------|||");
        telemetry.addLine("|||----GAMEPAD2 CONTROLS----|||");
        telemetry.addData("Launcher", "Right Bumper (Toggle)");
        telemetry.addData("Turret State Toggle", "Left Bumper (Toggle)");
        telemetry.addData("Intake", "Right Trigger (Hold)");
        telemetry.addData("Open Stopper", "Y (Hold)");
        telemetry.addData("Swap Intake Direction", "Dpad Right (Toggle)");
        telemetry.addData("Switch Launcher Velocity", "Dpad Left (Toggle)");
        telemetry.addData("Change Launcher Velocity", "Dpad Up/Down");
        telemetry.addLine("|||-------------------------|||");
    }

    /*
        double PRINCIPAL_POWER = 0.61;
        double voltage = getVoltage();
        double res = scaleVoltPF(voltage);
        double res2 = 0.7 * res;


        telemetry.addData("scale/res", res);
        telemetry.addData("stopper pos", stopper.servo.getPosition());
        telemetry.addData("res 2", res2);
        telemetry.addData("voltage", voltage);
        telemetry.addData("Launcher Power: ", launcher.MAX_POWER);
        telemetry.addData("Launcher ACTUAL Power: ", launcher.motor.getPower());
        telemetry.addData("Vel", launcher.motor.getVelocity());
        telemetry.addData("Trying to maintain vel", settingVelocity);
        telemetry.addData("Reached Stable Velocity", stableVelocity);
        telemetry.addData("Target Vel", curVelTarget);
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
        telemetry.update();*/

}
