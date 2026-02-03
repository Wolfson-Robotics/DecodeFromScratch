package org.firstinspires.ftc.teamcode.components;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.util.Rotation;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;

/** Aimer - 360 Degrees Auto Aimer Towards Target
 *  ---------------------------------------------
 *  The Target will be an April Tag which has positional data
 *  The Aimer will face itself towards the target to the best of its ability
 *  The Aimer will account for rotational constraints due to wires and prevent tangling
 */
public class Turret extends RollerEx {

    public IMU imu = null;

    public double TICKS_PER_REV = 537.7;
    public double GEAR_RATIO = 200.0 / 92.0;
    public double FULL_TICKS_PER_REV = TICKS_PER_REV * GEAR_RATIO;

    public double MAX_TURRET_YAW = 70;
    public double MIN_TURRET_YAW = -70;

    public double TARGET_GLOBAL_YAW = 0;
    public double TARGET_TURRET_YAW = 0;
    public AprilTagDetection TARGET_TAG = null;

    //Updated every loop call
    public double encoderPos;
    public double targetPos;
    public double robotYaw;
    public double turretYaw;
    public double globalYaw;

    public enum TurretState {
        GO_TO_ZERO,
        GO_TO_GLOBAL_TARGET,
        FOLLOW_TAG,
    }

    public TurretState curTurretState = TurretState.GO_TO_ZERO;

    public Turret(DcMotorEx motor) {
        super(motor);
        this.motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        this.motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        this.motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    public Turret(HardwareMap map, String motorName) {
        this((DcMotorEx) map.get(motorName));
    }

    /**
     * The turret loop method, needs to be called every instance the turret needs to updated (usually every frame)
     * Requires imu to != null, set it by accessing and setting the property of the object
     */
    public void loop() {
        if (imu == null) { return; }

        //Update values
        encoderPos = motor.getCurrentPosition();
        robotYaw = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES);
        turretYaw = ((360.0 * encoderPos) / FULL_TICKS_PER_REV);
        globalYaw = turretYaw + robotYaw;
        globalYaw = Rotation.wrap180(globalYaw);

        double targetAbsPos = 0;
        switch (curTurretState) {
            case GO_TO_ZERO:
                TARGET_TURRET_YAW = 0;
                break;
            case FOLLOW_TAG:
                getAprilTagYaw();
                break;
            case GO_TO_GLOBAL_TARGET:
                //Get the target position needed from the global yaw
                adjustTurretToGlobal();
                break;
        }
        targetAbsPos = TARGET_TURRET_YAW / 360.0 * FULL_TICKS_PER_REV;

        //Magic code that somehow makes the robot not skip or go the long way
        double deltaPos = targetAbsPos - encoderPos;
        deltaPos = ((deltaPos + (FULL_TICKS_PER_REV / 2)) % FULL_TICKS_PER_REV) - FULL_TICKS_PER_REV / 2;
        targetPos = encoderPos + deltaPos;

        motor.setTargetPosition((int) targetPos);
        motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        //HMath.lerp(TARGET_TURRET_YAW, 180 - TARGET_TURRET_YAW, curGlobalYaw); //maybe
        motor.setPower(MAX_POWER);
    }

    /**
     * Will toggle based on the boolean between the provided state and GO_TO_ZERO
     */
    public void switchTurretState(TurretState state) {
        if (curTurretState == TurretState.GO_TO_ZERO) {
            curTurretState = state;
        } else {
            curTurretState = TurretState.GO_TO_ZERO;
        }
    }




    double lastRecordedGlobalYaw = Double.NaN;
    private void getAprilTagYaw() {
        if (TARGET_TAG != null) {
            double offYaw = TARGET_TAG.ftcPose.bearing;

            TARGET_TURRET_YAW = Rotation.limitAndRound(
                    Rotation.wrap180(turretYaw + offYaw),
                    MIN_TURRET_YAW,
                    MAX_TURRET_YAW
            );
            lastRecordedGlobalYaw = Rotation.wrap180(robotYaw + TARGET_TURRET_YAW);
            return;
        }

        if (!Double.isNaN(lastRecordedGlobalYaw)) {
            TARGET_GLOBAL_YAW = lastRecordedGlobalYaw;
            adjustTurretToGlobal();
            return;
        }
    }

    private void adjustTurretToGlobal() {
        TARGET_TURRET_YAW = Rotation.wrap180(TARGET_GLOBAL_YAW - robotYaw);
        TARGET_TURRET_YAW = Rotation.limitAndRound(TARGET_TURRET_YAW, MIN_TURRET_YAW, MAX_TURRET_YAW);
    }





}
