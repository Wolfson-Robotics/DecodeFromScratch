package org.firstinspires.ftc.teamcode.debug.instructions;

import org.firstinspires.ftc.teamcode.debug.DebugAuto;
import org.firstinspires.ftc.teamcode.debug.util.CompassDirection;

public class OrdinalMovement implements DebugInstruction {

    private final CompassDirection direction;
    private final double horizIN, vertIN;

    public OrdinalMovement(CompassDirection direction, double horizIN, double vertIN) {
        if (!direction.intercardinal()) {
            throw new IllegalArgumentException("OrdinalMovement requires a diagonal direction (NE, SE, SW, NW).");
        }
        this.direction = direction;
        this.horizIN = horizIN;
        this.vertIN = vertIN;
    }

    @Override
    public void run(DebugAuto instance) {
        switch (direction) {
            case NE:
                instance.moveBotDiagonal(horizIN, vertIN);
                break;
            case SE:
                instance.moveBotDiagonal(horizIN, -vertIN);
                break;
            case SW:
                instance.moveBotDiagonal(-horizIN, -vertIN);
                break;
            case NW:
                instance.moveBotDiagonal(-horizIN, vertIN);
                break;
            default:
                throw new IllegalStateException("Non-diagonal direction for OrdinalMovement: " + direction);
        }
    }
}