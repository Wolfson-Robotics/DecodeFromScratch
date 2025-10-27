package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.components.Roller;
import org.firstinspires.ftc.teamcode.components.MecanumDrive;
import org.firstinspires.ftc.teamcode.components.camera.VisionPortalCamera;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

public class Base extends OpMode {

    //Components
    public MecanumDrive driveSystem;
    public Roller launcher;
    public Roller intake;

    public VisionPortalCamera camera;
    public AprilTagProcessor aTagProc;

    //If overriding init(), make to sure call super.init();
    @Override
    public void init() {
        driveSystem = new MecanumDrive(
                hardwareMap,
                "lf_drive", "lb_drive", "rf_drive", "rb_drive");
        launcher = new Roller(hardwareMap, "launcher");
        intake = new Roller(hardwareMap, "intake");
    }

    //By default this will not be called in init() of base, so extending classes have to call it to use it
    public void initCamera() {
        aTagProc = AprilTagProcessor.easyCreateWithDefaults();
        camera = VisionPortalCamera.createVisionPortalCamera(
                hardwareMap.get(WebcamName.class, "Webcam 1"),
                aTagProc
        );
    }

    @Override
    public void loop() {}
}
