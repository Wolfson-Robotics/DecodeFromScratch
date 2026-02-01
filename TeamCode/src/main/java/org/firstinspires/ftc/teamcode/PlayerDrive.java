package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.components.Turret;
import org.firstinspires.ftc.teamcode.components.camera.VisionPortalCamera;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Instructions to connect: adb connect 192.168.43.1:5555
// cd %localappdata%/android/sdk/platform-tools
//@TeleOp(name = "PlayerDrive")
public class PlayerDrive extends RobotBase {

    private static final Logger log = LoggerFactory.getLogger(PlayerDrive.class);

    // CONFIGURABLE VELOCITY VALUES
    public double LAUNCHER_MAX_VELOCITY = 1625;
    public double LAUNCHER_MIN_VELOCITY = 1375;

    int targetTag = BLUE_TAG;

    @Override
    public void init() {
        super.init();
        initCamera();

        // Ensure RobotBase velocities match these configurable ones if they are used elsewhere
        FAR_VELOCITY = LAUNCHER_MAX_VELOCITY;
        CLOSE_VELOCITY = LAUNCHER_MIN_VELOCITY;

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
    double curVelTarget = 1350; // Default to min
    boolean stableVelocity = false;
    //boolean firstMode = true;
    AprilTagDetection tag = null;

    boolean didFirst = false;
    boolean appliedVelocityDirectly = false;
    private void gamepad2Loop() {
        if (gamepad2.xWasPressed()) { launcher.MAX_POWER -= .05; }
        if (gamepad2.bWasPressed()) { launcher.MAX_POWER += .05; }
        
        // Updated dpad up/down for fixed velocity values
        if (gamepad2.dpadUpWasPressed()) { curVelTarget = LAUNCHER_MAX_VELOCITY; }
        if (gamepad2.dpadDownWasPressed()) { curVelTarget = LAUNCHER_MIN_VELOCITY; }
        
        if (gamepad2.dpadLeftWasPressed()) {
            curVelTarget = curVelTarget != LAUNCHER_MIN_VELOCITY ? LAUNCHER_MIN_VELOCITY : LAUNCHER_MAX_VELOCITY;
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
            if (launcher.targetVelocity == curVelTarget && !didFirst) {
                launcher.applyVelocity(curVelTarget);
                appliedVelocityDirectly = true;
                didFirst = true;
            }else{
            launcher.switchVelocity(curVelTarget);
            didFirst =true;}
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
        telemetry.addData("Launcher Target Velocity (Supposed)", curVelTarget);
        telemetry.addData("Launcher Target Velocity (ACTUAL)", launcher.targetVelocity);
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
        telemetry.addData("Switch Launcher Velocity", "Dpad Up (Max) / Down (Min)");
        telemetry.addLine("|||-------------------------|||");
        telemetry.addLine();
        telemetry.addLine();
        telemetry.addLine();
        telemetry.addLine();
        telemetry.addData("didFirst", didFirst);
        telemetry.addData("appliedVelocityDirectly", appliedVelocityDirectly);
    }
}
