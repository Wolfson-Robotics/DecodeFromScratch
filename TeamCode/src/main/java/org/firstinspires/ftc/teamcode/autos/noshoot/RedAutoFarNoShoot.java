package org.firstinspires.ftc.teamcode.autos.noshoot;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.AutoBase;
import org.firstinspires.ftc.teamcode.debug.util.Async;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Autonomous(name = "RedAutoFarNoShoot", group = "AutoNoShoot")
public class RedAutoFarNoShoot extends AutoBase {

    private static final Logger log = LoggerFactory.getLogger(RedAutoFarNoShoot.class);

    @Override
    public void start() {
        moveBot(5, 0.2, 0, 0);
        Async.sleep(1000);
        turnBot(0.2,  17.5);
        Async.sleep(1000);
        moveBot(16, 0.2, 0, 0);

        turnBot(0.2, 58.0);
        Async.sleep(1000);
        // New instructions
        moveBot(6.81, 0, 0, -0.5); // New instruction
        stopper.togglePosition(true);
        Async.sleep(1000);
        runFeed();
        Async.sleep(1500);
        /*
        moveBot(20.76, 0.4, 0, 0);
        Async.sleep(1000);
        moveBot(5.83, 0.4, 0, 0);
        Async.sleep(1000);
        moveBot(7.39, 0.4, 0, 0);
         */
        moveBot(33.98, 0.19, 0, 0);
        Async.sleep(1000);
        launcher.applyVelocity(FAR_VELOCITY);
        //Async.sleep(1000);
        stopFeed();
        moveBot(15, 0.8, 0, 0);
        Async.sleep(500);
        moveBot(50.2, -0.5, 0, 0);
        Async.sleep(1000);
        turnBot(0.5, -56.75);
        Async.sleep(500);
        moveBot(15.39, -1, 0, 0);
        shootBetter(FAR_VELOCITY);
        moveBot(16, 0.2, 0, 0);
        //Async.sleep(250);
        //moveBot(10, 0.7, 0, 0);

        /*
        moveBot(8, 0.1, 0, -0.4);
        Async.sleep(1000);
        moveBot(52, 0.2, 0, 0);
        Async.sleep(1000);
        stopFeed();
        //Go back
        moveBot(50, -0.4, 0, 0);
        Async.sleep(1000);
        moveBot(30, 0, 0, 0.4);
        Async.sleep(1000);
        turnBot(0.2, -57.3);
        Async.sleep(1000);
        moveBot(16, 0.2, 0, 0);*/
    }

}
