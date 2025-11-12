package org.firstinspires.ftc.teamcode.autos;

import org.firstinspires.ftc.teamcode.AutoBase;

//@Autonomous(name = "BlueAutoClose", group = "Auto")
public class BlueAutoClose extends AutoBase {

    @Override
    public void init() {
        super.init();
    }

    @Override
    public void start() {
        moveBot(9, 0.2, 0, 0);
//        Async.sleep(500);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
        }
        turnBot(0.2,  -29);
//        turnBot()
        shootBetter(1610);
        shootBetter(1610);
        shootBetter(1610);
        launcher.motor.setPower(0);
        stopShoot();
    }

    @Override
    public void loop() {
        Thread.yield();
    }

}
