package org.firstinspires.ftc.teamcode.testing;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

@TeleOp(name = "TestLauncher")
public class TestLauncher extends OpMode {

    public DcMotorEx flywheelMotor;
    public double targetVelocity = 1500;

    @Override
    public void init() {
        flywheelMotor = hardwareMap.get(DcMotorEx.class, "launcher");
        flywheelMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        flywheelMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        
        telemetry.addLine("Init Complete");
        telemetry.update();
    }

    @Override
    public void loop() {
        flywheelMotor.setVelocity(targetVelocity, AngleUnit.DEGREES);

        telemetry.addData("Target Velocity", targetVelocity);
        telemetry.addData("Current Velocity", flywheelMotor.getVelocity(AngleUnit.DEGREES));
        telemetry.addData("Error", targetVelocity - flywheelMotor.getVelocity(AngleUnit.DEGREES));
        telemetry.addData("Launcher Power", flywheelMotor.getPower());
        telemetry.update();
    }

    @Override
    public void stop() {
        flywheelMotor.setVelocity(0);
    }
}
