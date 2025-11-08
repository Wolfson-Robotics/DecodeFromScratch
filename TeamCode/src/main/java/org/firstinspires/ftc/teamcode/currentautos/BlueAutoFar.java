package org.firstinspires.ftc.teamcode.currentautos;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.RobotBase;
import org.firstinspires.ftc.teamcode.debug.util.Async;

@Autonomous(name = "BlueAutoFar", group = "Auto")
public class BlueAutoFar extends RobotBase {


    @Override
    public void init() {
        super.init();
        lf.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rf.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        lb.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rb.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        driveSystem.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        launcher.motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    @Override
    public void start() {
        /*
        moveBot(9, 0.2, 0, 0);
//        Async.sleep(500);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
        }
        turnBot(0.2,  -20);
//        turnBot()
//        shoot(1610);
//        shoot(1610);
//        shoot(1610);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {}
        telemetry.addData("current vel", launcher.motor.getVelocity(AngleUnit.DEGREES));
        telemetry.update();
        shootBetter(1610);
        launcher.motor.setPower(0);
        stopShoot();
         */
//        moveBot(16, 1, 0, 0);
/*

        moveBot(9, 0.2, 0, 0);
//        Async.sleep(500);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
        }
        turnBot(0.2,  23.5);
//        turnBot()
//        shoot(1610);
//        shoot(1610);
//        shoot(1610);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {}
        shootBetter(1610);
        launcher.motor.setPower(0);
        stopShoot();
        moveBot(10, 0.2, 0, 0);*/
        moveBot(9, 0.2, 0, 0);
//        Async.sleep(500);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
        }
        turnBot(0.2,  -21.6);
//        turnBot()
//        shoot(1610);
//        shoot(1610);
//        shoot(1610);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {}
        shootBetter(1570);
        launcher.motor.setPower(0);
        stopShoot();
        moveBot(16, 0.2, 0, 0);
    }

    @Override
    public void loop() {

    }


}
