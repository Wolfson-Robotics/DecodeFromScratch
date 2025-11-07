package org.firstinspires.ftc.teamcode;

import android.os.Environment;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.VoltageSensor;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.components.Roller;
import org.firstinspires.ftc.teamcode.components.MecanumDrive;
import org.firstinspires.ftc.teamcode.components.camera.VisionPortalCamera;
import org.firstinspires.ftc.teamcode.debug.util.Async;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.Optional;

public abstract class RobotBase extends OpMode {

    //Components
    public MecanumDrive driveSystem;
    public Roller<DcMotorEx> launcher;
    public Roller intake, leftSpinner, centerSpinner;

    public Servo tongue;

    public VisionPortalCamera camera;
    public AprilTagProcessor aTagProc;

    public IMU imu;

    //Storage/Files
    protected final String storagePath = Environment.getExternalStorageDirectory().getPath();
    protected final String logsPath = storagePath + "/Logs/";

    public DcMotorEx lf, lb, rf, rb;

    //If overriding init(), make to sure call super.init();
    @Override
    public void init() {
        imu = (IMU) hardwareMap.get("imu");

        lf = hardwareMap.get(DcMotorEx.class, "lf_drive");
        lb = hardwareMap.get(DcMotorEx.class, "lb_drive");
        rf = hardwareMap.get(DcMotorEx.class, "rf_drive");
        rb = hardwareMap.get(DcMotorEx.class, "rb_drive");

        driveSystem = new MecanumDrive(lf, lb, rf, rb);
        tongue = (Servo) hardwareMap.get("tongue");
        driveSystem.imu = imu;
        launcher = new Roller<>(hardwareMap, "launcher");
        intake = new Roller<>(hardwareMap, "intake");
        intake.SWAP_DIRECTION = true;
        leftSpinner = new Roller<>(hardwareMap, "left_spin");
        leftSpinner.SWAP_DIRECTION = true;
        centerSpinner = new Roller<>(hardwareMap, "center_spin");
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









    double ticsPerInch = 1;
    double intCon = 1;
    double powerFactor = 1;
    // moveBotOld (uses tics-based motion)
    protected void moveBotOld(double distIN, double vertical, double pivot, double horizontal) {

        // 23 motor tics = 1 IN
        int motorTics;
        int posNeg = (vertical >= 0) ? 1 : -1;

        rf.setPower(powerFactor * (-pivot + (vertical - horizontal)));
        rb.setPower(powerFactor * (-pivot + vertical + horizontal));
        lf.setPower(powerFactor * (pivot + vertical + horizontal));
        lb.setPower(powerFactor * (pivot + (vertical - horizontal)));

        if (horizontal != 0) {
            posNeg = (horizontal > 0) ? 1 : -1;
            motorTics = lf.getCurrentPosition() + (int) ((distIN * intCon) * (posNeg));
            if (posNeg == 1) {
                // right goes negative
                while ((lf.getCurrentPosition() < motorTics)) {
                    Thread.yield();
                }
            } else {
                // left goes positive
                while ((lf.getCurrentPosition() > motorTics)) {
                    Thread.yield();
                }
            }
        } else {
            posNeg = vertical >= 0 ? -1 : 1;
            motorTics = rf.getCurrentPosition() + (int) ((distIN * intCon) * posNeg);
            if (posNeg == -1) {
                while (rf.getCurrentPosition() > motorTics) {
                    Thread.yield();
                }
            } else {
                while ((rf.getCurrentPosition() < motorTics)) {
                    Thread.yield();
                }
            }

        }
//        removePower();
        lf.setPower(0);
        lb.setPower(0);
        rf.setPower(0);
        rb.setPower(0);

    }

    // wrapper
    protected void moveBot(double in, double vertical, double pivot, double horizontal) {
        //moveBotOld((in/12d) * 73.6770894730908, vertical, pivot, horizontal);
        moveBotOld(in*ticsPerInch, vertical, pivot, horizontal);
    }

}
