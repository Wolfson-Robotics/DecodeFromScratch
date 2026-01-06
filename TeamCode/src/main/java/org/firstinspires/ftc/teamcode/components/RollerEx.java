package org.firstinspires.ftc.teamcode.components;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

public class RollerEx extends Roller<DcMotorEx> {

    public double MAX_VELOCITY = 1500;
    public AngleUnit VELOCITY_UNIT = null;
    public int CHECKS = 50;  //Checks in a row to determine if reached target velocity
    public double targetVelocity = 0;
    private double curChecks = 0;

    public RollerEx(DcMotorEx motor) {
        super(motor);
    }

    public RollerEx(HardwareMap map, String motorName) {
        super(map, motorName);
    }

    /**
    Applies SWAP_DIRECTION and sets velocity
    */
    public void applyVelocity(double velocity) {
        velocity = Math.min(velocity, MAX_VELOCITY);
        targetVelocity = velocity;
        curChecks = 0;
        if (VELOCITY_UNIT != null) {
            motor.setVelocity(velocity, AngleUnit.DEGREES);
        } else {
            motor.setVelocity(velocity);
        }
    }


    /**
     Toggles velocity of roller
     @param toggle true applies velocity, false applies velocity of 0
     @param velocity velocity value to be applied if toggle is true
     */
    public void toggleVelocity(boolean toggle, double velocity) {
        if (!toggle) { velocity = 0; }
        applyVelocity(velocity);
    }


    /**
     * Switches velocity of roller
     * @param velocity velocity value to be applied if its off
     */
    public void switchVelocity(double velocity) {
        if (targetVelocity != 0) {
            applyVelocity(0); //Turn it off if its on
        } else {
            applyVelocity(velocity); //Turn it on if its off
        }
    }


    /**
     * Will check if the velocity has reached the stable target and return true if so
     * Use CHECKS to configure the amount of times the function needs to reach the target in a row
     * in order to return true
     */
    public boolean reachedVelocity() {
        double velocity = motor.getVelocity();
        if (targetVelocity > (velocity - 30) && targetVelocity < (velocity + 30)) {
            curChecks++;
        } else {
            curChecks = 0;
        }
        return curChecks >= CHECKS;
    }




}
