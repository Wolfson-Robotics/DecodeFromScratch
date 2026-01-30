package org.firstinspires.ftc.teamcode;

import android.os.Environment;

import com.arcrobotics.ftclib.controller.PIDController;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.ImuOrientationOnRobot;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.VoltageSensor;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.components.Turret;
import org.firstinspires.ftc.teamcode.components.Roller;
import org.firstinspires.ftc.teamcode.components.MecanumDrive;
import org.firstinspires.ftc.teamcode.components.RollerEx;
import org.firstinspires.ftc.teamcode.components.ServoEx;
import org.firstinspires.ftc.teamcode.components.camera.VisionPortalCamera;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.Optional;

public abstract class RobotBase extends OpMode {

    //Components
    public MecanumDrive driveSystem;
    public RollerEx launcher;
    public Turret turret;
    public Roller<DcMotor> intake, transfer, lTransport, rTransport;
    public ServoEx<Servo> stopper;

    public VisionPortalCamera camera;
    public AprilTagProcessor aTagProc;

    public IMU imu;
    public GoBildaPinpointDriver pinpoint;

    //Storage/Files
    protected final String storagePath = Environment.getExternalStorageDirectory().getPath();
    protected final String logsPath = storagePath + "/Logs/";

    PIDFCoefficients launcherPIDF = new PIDFCoefficients(167, 0, 0, 13.3);

    public double FAR_VELOCITY = 1625;
    public double CLOSE_VELOCITY = 1400;
    public final int BLUE_TAG = 20, RED_TAG = 24, GPP_TAG = 22, PGP_TAG = 22, PPG_TAG = 23;

    //If overriding init(), make to sure call super.init();
    @Override
    public void init() {
        driveSystem = new MecanumDrive(hardwareMap, "lf_drive", "lb_drive", "rf_drive", "rb_drive");

        launcher = new RollerEx(hardwareMap, "launcher");
        launcher.swapDirection();
        launcher.motor.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, launcherPIDF);
        launcher.MAX_POWER = 1;

        imu = (IMU) hardwareMap.get("imu");
        ImuOrientationOnRobot o = new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.BACKWARD,
                RevHubOrientationOnRobot.UsbFacingDirection.UP
        );
        imu.initialize(new IMU.Parameters(o));
        imu.resetYaw();
        driveSystem.imu = imu;

        pinpoint = (GoBildaPinpointDriver) hardwareMap.get("pinpoint");
        pinpoint.setPosY(0, DistanceUnit.INCH);
        pinpoint.setPosX(-1.75, DistanceUnit.INCH);
        pinpoint.setEncoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_SWINGARM_POD);
        pinpoint.setEncoderDirections(
                GoBildaPinpointDriver.EncoderDirection.REVERSED,
                GoBildaPinpointDriver.EncoderDirection.REVERSED
        );
        pinpoint.resetPosAndIMU();

        turret = new Turret((DcMotorEx) hardwareMap.get("aimer"));
        turret.imu = imu;
        turret.TICKS_PER_REV = 537.7;
        turret.GEAR_RATIO = 92.0 / 200.0;
        turret.MAX_POWER = 0.5;
        turret.curTurretState = Turret.TurretState.GO_TO_ZERO;

        transfer = new Roller<>(hardwareMap, "transfer");
        transfer.MAX_POWER = 0.8;
        lTransport = new Roller<>(hardwareMap, "left transport");
        lTransport.swapDirection();
        lTransport.MAX_POWER = 0.8;
        rTransport = new Roller<>(hardwareMap, "right transport");
        rTransport.MAX_POWER = 0.8;

        stopper = new ServoEx<>(hardwareMap, "stopper");
        stopper.swapDirection();
        stopper.MIN_POSITION = 0.27; //Closed
        stopper.MAX_POSITION = 0.4; //Open

        intake = new Roller<>(hardwareMap, "intake");
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
