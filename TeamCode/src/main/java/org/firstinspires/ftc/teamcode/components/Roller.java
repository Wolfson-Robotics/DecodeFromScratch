package org.firstinspires.ftc.teamcode.components;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareDevice;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

public class Roller<T extends DcMotorEx> {

    public T motor;
    public double POWER_INCREMENT = 0.1;
    public boolean SWAP_DIRECTION = false;
    public double MAX_POWER = 0.7;

    public double MAX_VELOCITY = 1500;
    public AngleUnit VELOCITY_UNIT = null;
    public int CHECKS = 50;  //Checks in a row to determine if reached target velocity
    private double targetVelocity = 0;
    private double curChecks = 0;

    public Roller(T motor) {
        this.motor = motor;
    }

    public Roller(HardwareMap map, String motorName) {
        this((T) map.get(motorName));
    }

    /*
    Applies SWAP_DIRECTION and sets power
     */
    public void applyPower(double power) {
        power = Math.min(power, MAX_POWER);
        power = Math.max(power, -1);
        if (SWAP_DIRECTION) { power = -power; }
        motor.setPower(power);
    }

    /*
    Applies SWAP_DIRECTION and sets velocity
     */
    public void applyVelocity(double velocity) {
        velocity = Math.min(velocity, MAX_VELOCITY);
        if (SWAP_DIRECTION) { velocity = -velocity; }
        targetVelocity = velocity;
        curChecks = 0;
        if (VELOCITY_UNIT != null) {
            motor.setVelocity(velocity, AngleUnit.DEGREES);
        } else {
            motor.setVelocity(velocity);
        }

    }

    /*
    Functionality Methods
    -------
     */

    /**
    Adjusts the power to a boolean phrase. Useful for speeding up the roller
     @param increase Increases or decreases the power by POWER_INCREMENT
     */
    public void adjustPower(boolean increase) {
        double power = Math.abs(motor.getPower());
        power += increase ? POWER_INCREMENT : -POWER_INCREMENT;
        applyPower(power);
    }

    /**
    Toggles power of roller
     @param toggle true applies power, false applies power of 0
     @param power power value to be applied if toggle is true
     */
    public void togglePower(boolean toggle, double power) {
        if (!toggle) { power = 0.; }
        applyPower(power);
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
        if (curChecks >= CHECKS) { return true; }

        return false;
    }

}
