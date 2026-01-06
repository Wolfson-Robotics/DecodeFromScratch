package org.firstinspires.ftc.teamcode.components;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

/** Aimer - 360 Degrees Auto Aimer Towards Target
 *  ---------------------------------------------
 *  The Target will be an April Tag which has positional data
 *  The Aimer will face itself towards the target to the best of its ability
 *  The Aimer will account for rotational constraints due to wires and prevent tangling
 */
public class Aimer extends RollerEx {

    //public double targetYaw;
    //private double currentYaw;
    public double yawFacingRange = 1; //Default range is 1 to -1

    public Aimer(DcMotorEx motor) {
        super(motor);
        this.motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        this.motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    public Aimer(HardwareMap map, String motorName) {
        this((DcMotorEx) map.get(motorName));
    }

    public void aim(double yaw) {
        swapDirection(yaw > 0);
        if (yaw < yawFacingRange || yaw > -yawFacingRange) { applyPower(0); }
        applyPower(MAX_POWER);
    }

}
