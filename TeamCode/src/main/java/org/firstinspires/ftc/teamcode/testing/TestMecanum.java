package org.firstinspires.ftc.teamcode.testing;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.RobotBase;

@TeleOp(name = "TestMecanum")
public class TestMecanum extends RobotBase {

    //TEMP TEST CONSISTENT DRIVE
    static final double HD_COUNTS_PER_REV = 537.7;
    static final double DRIVE_GEAR_REDUCTION = 19.2; //Or maybe just 1.0
    static final double WHEEL_DIAMETER_MM =  104;
    static final double WHEEL_CIRCUMFERENCE_MM = WHEEL_DIAMETER_MM * Math.PI;
    static final double DRIVE_COUNTS_PER_MM = (HD_COUNTS_PER_REV * DRIVE_GEAR_REDUCTION) / WHEEL_CIRCUMFERENCE_MM;
    static final double DRIVE_COUNTS_PER_IN = DRIVE_COUNTS_PER_MM * 25.4;

    @Override
    public void init() {
        super.init();
        // driveSystem.getAllMotors().forEach(n -> n.setMode(DcMotor.RunMode.RUN_USING_ENCODER));
    }

    boolean yToggle = false;
    @Override
    public void loop() {

        if (gamepad1.a) {
            driveSystem.getAllMotors().forEach(
                    n -> n.setVelocity(30., AngleUnit.DEGREES)
            );
        } else if (gamepad1.b) {
            //Need to provide IMU for this to work: (also test it)
            driveSystem.driveFieldCentric(-gamepad1.left_stick_y, gamepad1.left_stick_x, gamepad1.right_stick_x);
        } else if (gamepad1.x) {
            driveSystem.driveForSeconds(1, 0, 3); //Should pause and drive for 3 seconds
        } else if (gamepad1.y && !yToggle) {
            yToggle = true;
            driveDistance(3);
        }

    }

    public void driveDistance(float yIn) {
        int targetPosition = (int)(yIn * DRIVE_COUNTS_PER_IN) + driveSystem.lb.getCurrentPosition();

        driveSystem.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        driveSystem.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        //TO TEST: change the power value from different ones to see if it goes the same distance
        driveSystem.getAllMotors().forEach(
                n -> {
                    n.setTargetPosition(targetPosition);
                    n.setPower(0.4);
                }
        );
    }
}
