package org.firstinspires.ftc.teamcode.testing;

import com.arcrobotics.ftclib.controller.PIDFController;
import com.bylazar.telemetry.JoinedTelemetry;
import com.bylazar.telemetry.PanelsTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import java.util.Arrays;

@TeleOp(name = "PIDFLauncher")
public class PIDFLauncher extends OpMode {

    PIDFController controller;
    //JoinedTelemetry telem = new JoinedTelemetry(PanelsTelemetry.INSTANCE.getFtcTelemetry(), telemetry);

    public static double p, i, d = 0;
    public static double f = 0;

    public static double target = 1500; //1500 ticks per second

    private final double ticks_in_degree = 700 / 180.0; //TODO: lookup values from motor

    private DcMotorEx launcher;

    @Override
    public void init() {
        controller = new PIDFController(p, i, d, f);
        launcher = (DcMotorEx) hardwareMap.get("launcher");
    }

    @Override
    public void loop() {
        double vel = launcher.getVelocity();
        controller.setPIDF(p, i, d, f);
        controller.calculate(vel, target);
        telemetry.addData("PIDF Co", Arrays.stream(controller.getCoefficients()).toArray());
        telemetry.addData("Vel", vel);
    }

}
