package org.firstinspires.ftc.teamcode.autos;

import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.AutoBase;
import org.firstinspires.ftc.teamcode.RobotBase;

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
        shoot(1610);
        shoot(1610);
        shoot(1610);
        launcher.motor.setPower(0);
        stopShoot();
    }

    @Override
    public void loop() {
        Thread.yield();
    }

}
