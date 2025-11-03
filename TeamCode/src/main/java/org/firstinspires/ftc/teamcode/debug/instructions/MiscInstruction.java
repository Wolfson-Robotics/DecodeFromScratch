package org.firstinspires.ftc.teamcode.debug.instructions;

import org.firstinspires.ftc.teamcode.debug.DebugAuto;

public class MiscInstruction implements DebugInstruction {

    private final Runnable fn;

    public MiscInstruction(Runnable fn) {
        this.fn = fn;
    }

    @Override
    public void run(DebugAuto instance) {
        fn.run();
    }

}