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
        launcher.applyVelocity(FAR_VELOCITY);
        moveBot(5, 0.2, 0, 0);
        Async.sleep(250);
        turnBot(0.2,  25);
        Async.sleep(250);
        shootBetter(FAR_VELOCITY);
        moveBot(16, 0.2, 0, 0);

        turnBot(0.2, 83);
        Async.sleep(400);
        runFeed();
        moveBot(8, 0.1, 0, -0.4);
        Async.sleep(400);
        moveBot(52, 0.2, 0, 0);
        Async.sleep(1000);
        launcher.applyVelocity(FAR_VELOCITY);
        stopFeed();
        //Go back
        moveBot(50, -0.4, 0, 0);
        Async.sleep(400);
        moveBot(30, 0, 0, 0.4);
        Async.sleep(250);
        turnBot(0.2, -82);
        shootBetter(FAR_VELOCITY);
        moveBot(16, 0.2, 0, 0);
    }


}
