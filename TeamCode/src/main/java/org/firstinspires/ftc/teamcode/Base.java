package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.teamcode.components.Roller;
import org.firstinspires.ftc.teamcode.components.MecanumDrive;

public class Base extends OpMode {

    public MecanumDrive driveSystem;
    public Roller launcher;
    public Roller intake;

    //If overriding init(), make to sure call super.init();
    @Override
    public void init() {
        driveSystem = new MecanumDrive(
                hardwareMap,
                "lf_drive", "lb_drive", "rf_drive", "rb_drive");
        launcher = new Roller(hardwareMap, "launcher");
        intake = new Roller(hardwareMap, "intake");
    }

    @Override
    public void loop() {

    }
}
