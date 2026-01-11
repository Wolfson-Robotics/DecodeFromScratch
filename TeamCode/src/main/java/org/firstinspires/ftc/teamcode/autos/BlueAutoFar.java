package org.firstinspires.ftc.teamcode.autos;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.AutoBase;
import org.firstinspires.ftc.teamcode.debug.util.Async;

@Autonomous(name = "BlueAutoFar", group = "Auto")
public class BlueAutoFar extends AutoBase {

    @Override
    public void start() {
        launcher.applyVelocity(FAR_VELOCITY);
        moveBot(9, 0.2, 0, 0);
        Async.sleep(250);
        turnBot(0.2,  -24);
        Async.sleep(500);
        shootBetter(FAR_VELOCITY);
        //moveBot(16, 0.2, 0, 0);

        turnBot(0.2, -69);
        Async.sleep(400);
        runFeed();
        moveBot(16, 0, 0, 0.4);
        Async.sleep(400);
        moveBot(52, 0.4, 0, 0);
        Async.sleep(1000);
        launcher.applyVelocity(FAR_VELOCITY);
        stopFeed();
        //Go back
        moveBot(49, -0.4, 0, 0);
        Async.sleep(400);
        moveBot(18, 0, 0, -0.4);
        Async.sleep(250);
        turnBot(0.2, 68);
        shootBetter(FAR_VELOCITY);
        moveBot(16, 0.2, 0, 0);
    }

    @Override
    public void stop() {
        Async.stopAll();
    }

}
