package org.firstinspires.ftc.teamcode.autos;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.AutoBase;
import org.firstinspires.ftc.teamcode.debug.util.Async;

@Autonomous(name = "BlueAutoFar", group = "Auto")
public class BlueAutoFar extends AutoBase {

    @Override
    public void init() {
        super.init();
    }
    @Override
    public void start() {

        launcher.applyVelocity(FAR_VELOCITY);
        moveBot(5, 0.3, 0, 0);
        Async.sleep(250);
        turnBot(0.3,  -15.5);
        Async.sleep(250);
        shootBetter(FAR_VELOCITY);
        moveBot(16, 0.4, 0, 0);

        turnBot(0.3, -37.0);
        //Async.sleep(400);
        runFeed();
        moveBot(2, 0, 0, 0.4);
        Async.sleep(300);
        moveBot(45, 0.2, 0, 0);
        //Async.sleep(100);
        launcher.applyVelocity(FAR_VELOCITY);
        stopFeed();
        moveBot(7, 0.4, 0, 0);
//        stopFeed();
        //Go back
        Async.sleep(200);
        moveBot(50, -0.4, 0, 0);
        Async.sleep(400);
        moveBot(34, 0, 0, -0.4);
        //moveBotDiagonal(30, 50, -0.4, 0.4);
        Async.sleep(250);
        moveBot(7.5, 0, 0, 0.4);
        //turnBot(0.2, -57.3);
        turnBot(0.3, 53.1);
        shootBetter(FAR_VELOCITY - 25);
        moveBot(16, 1, 0, 0);
        /*
        launcher.applyVelocity(FAR_VELOCITY);
        moveBot(9, 0.2, 0, 0);
        Async.sleep(250);
        turnBot(0.2,  -14);
        Async.sleep(500);
        shootBetter(FAR_VELOCITY);
        moveBot(16, 0.2, 0, 0);*/
/*
        turnBot(0.2, -48.2);
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
        turnBot(0.2, 47.5);
        shootBetter(FAR_VELOCITY);
        moveBot(16, 0.2, 0, 0);*/
    }

    @Override
    public void stop() {
        Async.stopAll();
    }

}
