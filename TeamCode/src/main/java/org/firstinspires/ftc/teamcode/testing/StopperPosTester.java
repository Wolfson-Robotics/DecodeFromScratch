package org.firstinspires.ftc.teamcode.testing;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.components.ServoEx;

@TeleOp(name = "StopperPosTester")
public class StopperPosTester extends OpMode {

    public ServoEx stopper;

    @Override
    public void init() {
        stopper = new ServoEx(hardwareMap, "stopper");
        stopper.servo.setDirection(Servo.Direction.REVERSE);
    }

    double pos = 0;
    @Override
    public void loop() {
        if (gamepad1.yWasReleased()) {
            pos += 0.01;
            stopper.applyPosition(pos);
        } else if (gamepad1.aWasReleased()) {
            pos -= 0.01;
            stopper.applyPosition(pos);
        }

        telemetry.addData("Pos", pos);
        telemetry.addData("True Pos", stopper.servo.getPosition());
        telemetry.update();
    }
}
