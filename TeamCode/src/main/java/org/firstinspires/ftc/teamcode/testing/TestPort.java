package org.firstinspires.ftc.teamcode.testing;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.teamcode.components.Roller;

@TeleOp(name = "TestPort")
public class TestPort extends OpMode  {

    public Roller<DcMotorEx> motorPort;


    @Override
    public void init() {
        motorPort = new Roller<>(hardwareMap, "launcher");
    }

    @Override
    public void loop() {
        if (gamepad1.dpadDownWasPressed()) { motorPort.SWAP_DIRECTION = !motorPort.SWAP_DIRECTION; }

        motorPort.togglePower(gamepad1.x, motorPort.MAX_POWER);

        telemetry.addData("motorPort Velocity:", motorPort.motor.getVelocity());
    }

}
