package org.firstinspires.ftc.teamcode.components;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Roller {

    public DcMotorEx motor;

    private static Double POWER_INCREMENT = 0.1;

    public Roller(DcMotorEx motor) {
        this.motor = motor;
    }

    public Roller(HardwareMap map, String motorName) {
        this.motor = (DcMotorEx) map.get(motorName);
    }





    /*
    Functionality Methods
    -------
     */

    /**
    Adjusts the power to a boolean phrase. Useful for speeding up the rolelr
     @increase - Increases or decreases the power by POWER_INCREMENT
     */
    public void adjustPower(Boolean increase) {
        double power = motor.getPower();
        power += increase ? POWER_INCREMENT : -POWER_INCREMENT;
        power = Math.max(power, 0);
        power = Math.min(power, 1);
        motor.setPower(power);
    }

}
