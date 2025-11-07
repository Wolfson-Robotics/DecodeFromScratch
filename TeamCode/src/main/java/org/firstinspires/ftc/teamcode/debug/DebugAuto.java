package org.firstinspires.ftc.teamcode.debug;


import org.firstinspires.ftc.teamcode.debug.instructions.DebugInstruction;
public abstract class DebugAuto extends TranslationRobotBase {

    // TODO: Make room for other movements in a different thread
    @Override
    public void start() {
        super.start();
        DebugInstruction[] instructions = instructions();
        for (DebugInstruction interval : instructions) {
            interval.run(this);
        }
    }

    @Override
    public void loop() {}

    public abstract DebugInstruction[] instructions();

}
