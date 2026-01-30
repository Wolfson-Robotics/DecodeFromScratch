package org.firstinspires.ftc.teamcode.autos.noshoot;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.AutoBase;
import org.firstinspires.ftc.teamcode.debug.util.Async;

@Autonomous(name = "BlueAutoFarNoShoot", group = "AutoNoShoot")
public class BlueAutoFarNoShoot extends AutoBase {

    @Override
    public void start() {
        moveBot(9, 0.2, 0, 0);
        Async.sleep(1000);
        turnBot(0.2,  -16.7);
        Async.sleep(1000);

        turnBot(0.2, -48.2);
        Async.sleep(1000);
        runFeed();
        moveBot(16, 0, 0, 0.4);
        Async.sleep(1000);
        moveBot(52, 0.4, 0, 0);
        Async.sleep(1000);
        stopFeed();
        //Go back
        moveBot(49, -0.4, 0, 0);
        Async.sleep(1000);
        moveBot(18, 0, 0, -0.4);
        Async.sleep(1000);
        turnBot(0.2, 47.5);
        Async.sleep(1000);
        moveBot(16, 0.2, 0, 0);
    }

    @Override
    public void stop() {
        Async.stopAll();
    }

}
