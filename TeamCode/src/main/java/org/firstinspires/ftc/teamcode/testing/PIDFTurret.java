package org.firstinspires.ftc.teamcode.testing;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.PIDCoefficients;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

@TeleOp(name = "PIDFTurret")
public class PIDFTurret extends OpMode {

    public int LOW_POSITION = 0;
    public int HIGH_POSITION = 250;
    public int targetPos = LOW_POSITION;

    public DcMotorEx aimer;

    public double p = 0;
    public double i = 0;
    public double d = 0;
    public double f = 0;
    public PIDFCoefficients co = new PIDFCoefficients(p, i, d, f);

    @Override
    public void init() {
        aimer = (DcMotorEx) hardwareMap.get("aimer");
        //aimer.setPIDFCoefficients(DcMotor.RunMode.RUN_TO_POSITION, co);
    }

    int dir = 1;
    @Override
    public void loop() {
        if (gamepad1.yWasPressed()) {
            targetPos = targetPos == HIGH_POSITION ? LOW_POSITION : HIGH_POSITION;
        }

        if (gamepad1.leftBumperWasPressed()) { dir *= -1; }
        if (gamepad1.aWasPressed()) { dir *= 0.1; }
        if (gamepad1.bWasPressed()) { dir *= 10; }

        if (gamepad1.dpadLeftWasPressed()) { p += dir; }
        if (gamepad1.dpadUpWasPressed()) { i += dir; }
        if (gamepad1.dpadRightWasPressed()) { d += dir; }
        if (gamepad1.dpadDownWasPressed()) { f += dir; }

        if (gamepad1.xWasPressed()) {
            aimer.setTargetPosition(targetPos);
            //aimer.setPIDFCoefficients(DcMotor.RunMode.RUN_TO_POSITION, new PIDFCoefficients(p, i, d, f));
            aimer.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            aimer.setPower(0.2);
        }

        telemetry.addLine("Y - Switch position");
        telemetry.addLine("X - Use coefficients go to Position");
        telemetry.addLine("A/B - Decrease/Increase DIR");
        telemetry.addLine("LB - Switch DIR direction");
        telemetry.addData("Aimer Pos", aimer.getCurrentPosition());
        telemetry.addData("Motor Target Pos", targetPos);
        telemetry.addLine();
        telemetry.addData("DIR", dir);
        telemetry.addData("P (DLEFT)", p);
        telemetry.addData("I (DUP)", i);
        telemetry.addData("D (DRIGHT)", d);
        telemetry.addData("F (DDOWN)", f);
        telemetry.update();
    }
}
