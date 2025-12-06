package org.firstinspires.ftc.teamcode.testing;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.teamcode.components.Roller;

@TeleOp(name = "TestPort")
public class TestPort extends OpMode  {

    public Roller<DcMotorEx> launcher;
    public Roller<DcMotorEx> intake;
    public Roller<DcMotorEx> transfer;


    @Override
    public void init() {
        launcher = new Roller<>(hardwareMap, "launcher");
        intake = new Roller<>(hardwareMap, "intake");
        transfer = new Roller<>(hardwareMap, "transfer");

        transfer.MAX_POWER = 0.8;
    }

    @Override
    public void loop() {
        if (gamepad1.dpadDownWasPressed()) { launcher.SWAP_DIRECTION = !launcher.SWAP_DIRECTION; }
        if (gamepad1.dpadUpWasPressed()) { intake.SWAP_DIRECTION = !intake.SWAP_DIRECTION; }
        if (gamepad1.dpadLeftWasPressed()) { transfer.SWAP_DIRECTION = !transfer.SWAP_DIRECTION; }

        launcher.togglePower(gamepad1.x, launcher.MAX_POWER);
        intake.togglePower(gamepad1.y, intake.MAX_POWER);
        transfer.togglePower(gamepad1.y, transfer.MAX_POWER);

        telemetry.addData("Launcher Velocity:", launcher.motor.getVelocity());
    }

}
