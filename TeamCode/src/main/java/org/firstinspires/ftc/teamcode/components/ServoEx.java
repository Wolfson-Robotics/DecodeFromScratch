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
        //TODO: return if it has already reached position or is stuck
        //^^^^^: Seems impossible as of 12/6/25
        position = Math.min(position, MAX_POSITION);
        position = Math.max(position, MIN_POSITION);
        servo.setPosition(position);
    }

/*    public void applyAngle(double angle) {
        //TODO: Map position to angle
        position = Math.min(position, MAX_POSITION);
        position = Math.max(position, MIN_POSITION);
        servo.setPosition(position);
    }*/

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


    private boolean maxElseMin = false;
    /**
     Switch between min and max position
     */
    public void switchPosition() {
        if (maxElseMin) {
            applyPosition(MIN_POSITION);
        } else {
            applyPosition(MAX_POSITION);
        }
        maxElseMin = !maxElseMin;
    }


}
