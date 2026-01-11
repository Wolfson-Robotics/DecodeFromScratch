package org.firstinspires.ftc.teamcode.components;

import com.qualcomm.robotcore.hardware.DcMotorSimple;
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

    /** Replacement for SWAP_DIRECTION
     Will reverse servo if swap is true else it goes forwards
     @param swap reverse servo if true
     */
    public void swapDirection(boolean swap) {
        if (swap) {
            servo.setDirection(Servo.Direction.REVERSE);
        } else {
            servo.setDirection(Servo.Direction.FORWARD);
        }
    }

    /**
     * Swaps the direction (if its going reverse it swaps forward, etc.)
     */
    public void swapDirection() {
        switch (servo.getDirection()) {
            case FORWARD:
                swapDirection(true);
                break;
            case REVERSE:
                swapDirection(false);
                break;
        }
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
