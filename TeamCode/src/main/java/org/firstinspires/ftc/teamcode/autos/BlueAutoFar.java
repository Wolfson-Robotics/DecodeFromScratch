package org.firstinspires.ftc.teamcode.autos;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.AutoBase;

@Autonomous(name = "BlueAutoFar", group = "Auto")
public class BlueAutoFar extends AutoBase {

    @Override
    public void start() {
        moveBot(9, 0.2, 0, 0);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
        }
        turnBot(0.2,  -21.6);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {}
        shoot(1570);
        launcher.motor.setPower(0);
        stopShoot();
        moveBot(16, 0.2, 0, 0);
    }

    @Override
    public void loop() {

    }


}
