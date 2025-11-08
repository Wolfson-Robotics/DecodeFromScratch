package org.firstinspires.ftc.teamcode.currentautos;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.RobotBase;
import org.firstinspires.ftc.teamcode.debug.util.Async;

//@Autonomous(name = "RedAutoClose", group = "Auto")
public class RedAutoClose extends RobotBase {

@Override
public void init() {
    super.init();
    lf.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    rf.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    lb.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    rb.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    driveSystem.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
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
