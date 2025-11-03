package org.firstinspires.ftc.teamcode;

import android.os.Build;
import android.os.Environment;

import androidx.annotation.RequiresApi;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.CRServoImpl;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.Servo;

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
    public Roller leftSpinner;
    public Roller centerSpinner;

    public VisionPortalCamera camera;
    public AprilTagProcessor aTagProc;

    public IMU imu;

    //Storage/Files
    protected final String storagePath = Environment.getExternalStorageDirectory().getPath();
    protected final String logsPath = storagePath + "/Logs/";

    //If overriding init(), make to sure call super.init();
    @Override
    public void init() {
        imu = (IMU) hardwareMap.get("imu");
        driveSystem = new MecanumDrive(
                hardwareMap,
                "lf_drive", "lb_drive", "rf_drive", "rb_drive"
        );
        driveSystem.imu = imu;
        launcher = new Roller(hardwareMap, "launcher");
        intake = new Roller(hardwareMap, "intake");
        intake.SWAP_DIRECTION = true;
        leftSpinner = new Roller(hardwareMap, "left_spin");
        leftSpinner.SWAP_DIRECTION = true;
        centerSpinner = new Roller(hardwareMap, "center_spin");
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
