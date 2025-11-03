package org.firstinspires.ftc.teamcode.debug;


import static org.firstinspires.ftc.teamcode.debug.util.GeneralUtils.signClamp;

import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.teamcode.Base;
import org.firstinspires.ftc.teamcode.debug.handlers.DcMotorExHandler;
import org.firstinspires.ftc.teamcode.debug.instructions.DebugInstruction;
public abstract class DebugAuto extends TranslationBase {

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
