package org.firstinspires.ftc.teamcode.components;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class ServoEx<T extends Servo> {

    public T servo;
    public double MAX_POSITION = 1D;
    public double MIN_POSITION = 0D;

    public ServoEx(T servo) {
        this.servo = servo;
    }

    public ServoEx(HardwareMap map, String servoName) {
        this((T) map.get(servoName));
    }

    public void applyPosition(double position) {
        position = Math.min(position, MAX_POSITION);
        position = Math.max(position, MIN_POSITION);
        servo.setPosition(position);
    }

    /**
    Toggle between min and max position
    @param toggle apply MAX_POSITION if true else MIN_POSITION
     */
    public void togglePosition(boolean toggle) {
        if (toggle) {
            applyPosition(MAX_POSITION);
        } else {
            applyPosition(MIN_POSITION);
        }
        //applyPosition(toggle ? MAX_POSITION : MIN_POSITION);
    }



}
