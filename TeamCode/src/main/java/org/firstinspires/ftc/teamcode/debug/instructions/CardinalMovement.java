package org.firstinspires.ftc.teamcode.debug.instructions;

import org.firstinspires.ftc.teamcode.debug.DebugAuto;
import org.firstinspires.ftc.teamcode.debug.util.CompassDirection;
public class CardinalMovement implements DebugInstruction {

    private final CompassDirection direction;
    private final double distIN, power;

    public CardinalMovement(CompassDirection direction, double distIN, double power) {
        this.direction = direction;
        this.distIN = distIN;
        this.power = power;
    }
    public CardinalMovement(CompassDirection direction, double distIN) {
        this(direction, distIN, 1);
    }

    /**
     * NOTE: For ordinal movement, it moves that distance *diagonally*, not moving that
     * distance horizontally and vertically.
     */
    @Override
    public void run(DebugAuto instance) {
        switch (direction) {
            case N:
                instance.moveBot(distIN, power, 0, 0);
                break;
            case NE:
                instance.moveBot(distIN, power, 0, power);
                break;
            case E:
                instance.moveBot(distIN, 0, 0, power);
                break;
            case SE:
                instance.moveBot(distIN, -power, 0, power);
                break;
            case S:
                instance.moveBot(distIN, -power, 0, 0);
                break;
            case SW:
                instance.moveBot(distIN, -power, 0, -power);
                break;
            case W:
                instance.moveBot(distIN, 0, 0, -power);
                break;
            case NW:
                instance.moveBot(distIN, power, 0, -power);
                break;
        }
    }


}
