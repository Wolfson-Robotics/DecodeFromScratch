package org.firstinspires.ftc.teamcode.debug;

import org.firstinspires.ftc.teamcode.debug.instructions.CardinalMovement;
import org.firstinspires.ftc.teamcode.debug.instructions.DebugInstruction;
import org.firstinspires.ftc.teamcode.debug.instructions.MiscInstruction;
import org.firstinspires.ftc.teamcode.debug.util.Async;
import org.firstinspires.ftc.teamcode.debug.util.CompassDirection;

public class ExampleDebug extends DebugAuto {
    @Override
    public DebugInstruction[] instructions() {
        return new DebugInstruction[] {
                new CardinalMovement(CompassDirection.S, 15),
                new MiscInstruction(() -> Async.sleep(500)),
                new CardinalMovement(CompassDirection.E, 15),
                new MiscInstruction(() -> Async.sleep(500)),
                new CardinalMovement(CompassDirection.N, 15),
                new MiscInstruction(() -> Async.sleep(500)),
                new CardinalMovement(CompassDirection.W, 15),
        };
    }
}
