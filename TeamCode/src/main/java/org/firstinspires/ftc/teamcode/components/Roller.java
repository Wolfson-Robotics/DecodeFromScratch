package org.firstinspires.ftc.teamcode.components;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareDevice;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Roller<T extends DcMotorSimple> {

    public T motor;
    public double POWER_INCREMENT = 0.1;
    public boolean SWAP_DIRECTION = false;
    public double MAX_POWER = 1.;

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
        if (SWAP_DIRECTION) { power = -power; }
        power = Math.min(power, MAX_POWER);
        motor.setPower(power);
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
        power = Math.max(power, 0);
        power = Math.min(power, 1);

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


}
