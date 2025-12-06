package org.firstinspires.ftc.teamcode;

import android.os.Environment;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.VoltageSensor;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.components.Roller;
import org.firstinspires.ftc.teamcode.components.MecanumDrive;
import org.firstinspires.ftc.teamcode.components.ServoEx;
import org.firstinspires.ftc.teamcode.components.camera.VisionPortalCamera;
import org.firstinspires.ftc.teamcode.debug.util.Async;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.Optional;

public abstract class RobotBase extends OpMode {

    //Components
    public MecanumDrive driveSystem;
    public Roller<DcMotorEx> launcher;
    public Roller intake, transfer, transport;
    public ServoEx stopper;

    public VisionPortalCamera camera;
    public AprilTagProcessor aTagProc;

    public IMU imu;

    //Storage/Files
    protected final String storagePath = Environment.getExternalStorageDirectory().getPath();
    protected final String logsPath = storagePath + "/Logs/";


    //If overriding init(), make to sure call super.init();
    @Override
    public void init() {
        driveSystem = new MecanumDrive(hardwareMap, "lf_drive", "lb_drive", "rf_drive", "rb_drive");
        imu = (IMU) hardwareMap.get("imu");
        driveSystem.imu = imu;

        launcher = new Roller(hardwareMap, "launcher");
        launcher.SWAP_DIRECTION = true;
        launcher.motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        transfer = new Roller(hardwareMap, "transfer");
        transport = new Roller(hardwareMap, "transport");

        stopper = new ServoEx(hardwareMap, "stopper");
        stopper.MIN_POSITION = 0.2;
        stopper.MAX_POSITION = 0.5;

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



    /*
    -------
    Voltage
    -------
     */
    public static final double NOMINAL_VOLTAGE = 14; //TODO: Check battery voltage
    public static final double TOP_SCALE_FACTOR = 1.2; //120%
    public static final double NO_VOLTAGE = -1;
    protected double scaleVoltPF() {
        double currentVoltage = getVoltage();
        if (currentVoltage == NO_VOLTAGE) return 1.; //There's gotta be a better way to do this
        return Math.min((NOMINAL_VOLTAGE / currentVoltage), TOP_SCALE_FACTOR);
    }

    // temporary
    protected double scaleVoltPF(double currVoltage) {
        double currentVoltage = currVoltage;
        if (currentVoltage == NO_VOLTAGE) return 1.; //There's gotta be a better way to do this
//        return Math.min((NOMINAL_VOLTAGE / currentVoltage), TOP_SCALE_FACTOR);
        return NOMINAL_VOLTAGE/currentVoltage;
    }
    public VoltageSensor getVoltageSensor() {
        return hardwareMap.voltageSensor.get("Control Hub");
    }
    public double getVoltage() {
        return Optional.ofNullable(getVoltageSensor()).map(VoltageSensor::getVoltage).orElse(NO_VOLTAGE);
    }

}
