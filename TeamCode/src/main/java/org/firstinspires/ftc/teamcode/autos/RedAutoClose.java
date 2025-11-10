package org.firstinspires.ftc.teamcode.autos;

import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.AutoBase;
import org.firstinspires.ftc.teamcode.RobotBase;
import org.firstinspires.ftc.teamcode.debug.util.Async;

//@Autonomous(name = "RedAutoClose", group = "Auto")
public class RedAutoClose extends AutoBase {

@Override
public void init() {
    super.init();
}

@Override
public void start() {
    moveBot(9, 1, 0, 0);
    Async.sleep(500);
//        turnBot()
    shoot(1610);
    shoot(1610);
    shoot(1610);
    launcher.motor.setPower(0);
    stopShoot();
}

@Override
public void loop() {

}


}
