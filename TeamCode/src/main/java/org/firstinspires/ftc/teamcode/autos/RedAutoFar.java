package org.firstinspires.ftc.teamcode.autos;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.AutoBase;
import org.firstinspires.ftc.teamcode.debug.util.Async;

@Autonomous(name = "RedAutoFar", group = "Auto")
public class RedAutoFar extends AutoBase {

    @Override
    public void init() {
        super.init();
    }

    @Override
    public void start() {
        moveBot(9, 0.2, 0, 0);
        Async.sleep(500);
        turnBot(0.2,  24);
        Async.sleep(500);
        shootBetter(1700);
        moveBot(16, 0.2, 0, 0);
    }

    @Override
    public void loop() {
        super.loop();
    }

}
