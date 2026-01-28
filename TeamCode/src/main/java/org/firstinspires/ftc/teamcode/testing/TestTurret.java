package org.firstinspires.ftc.teamcode.testing;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.AutoBase;
import org.firstinspires.ftc.teamcode.components.Turret;
import org.firstinspires.ftc.teamcode.components.camera.VisionPortalCamera;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;

@TeleOp(name = "TestAimer")
public class TestTurret extends AutoBase {

    @Override
    public void init() {
        super.init();
        initCamera();

        turret.curTurretState = Turret.TurretState.GO_TO_GLOBAL_TARGET;
    }

    final double ticsPerRev = 537.7;
    final double gearRatio = 92.0 / 200.0;

    boolean isAiming = false;
    @Override
    public void loop() {
        driveSystem.drive(-gamepad1.left_stick_y, gamepad1.left_stick_x, gamepad1.right_stick_x);

        AprilTagDetection tag = VisionPortalCamera.getTargetTag(aTagProc, BLUE_TAG);

        double range = -1;
        double yaw = -1;
        double rangeX = -1;
        double rangeY = -1;

        //TAKEN FROM Aimer.java
        if (tag != null) {
            range = tag.ftcPose.range;
            yaw = tag.ftcPose.bearing;
            rangeX = range * Math.sin(Math.toRadians(turret.globalYaw));
            rangeY = range * Math.cos(Math.toRadians(turret.globalYaw));
        }
        double distX = pinpoint.getPosX(DistanceUnit.INCH);
        double distY = pinpoint.getPosY(DistanceUnit.INCH);

        turret.TARGET_TAG = tag;
        if (gamepad1.xWasPressed()) {
            turret.curTurretState = Turret.TurretState.GO_TO_ZERO;
        }
        if (gamepad1.yWasPressed()) {
            turret.curTurretState = Turret.TurretState.FOLLOW_TAG;
        }
        //turret.loop();
        turret.loop();

        telemetry.addLine("|---POINT AT BLUE TAG---|");
        telemetry.addData("Range", range);
        telemetry.addData("Bearing", yaw);
        telemetry.addData("DistX", distX);
        telemetry.addData("DistY", distY);
        telemetry.addData("robotHeading", turret.robotYaw);
        telemetry.addData("rangeX", rangeX);
        telemetry.addData("rangeY", rangeY);
        telemetry.addData("isAiming", isAiming);
        telemetry.addData("encoderPos", turret.encoderPos);
        telemetry.addData("localAngle", turret.turretYaw);
        telemetry.addData("targetPos", turret.targetPos);
        telemetry.addData("targetAngle", turret.TARGET_TURRET_YAW);
        telemetry.addData("currentPos", turret.motor.getCurrentPosition());
        telemetry.addData("globalAngle", turret.globalYaw);
        telemetry.addData("targetGlobalYaw", turret.TARGET_GLOBAL_YAW);
        telemetry.update();
    }

}
