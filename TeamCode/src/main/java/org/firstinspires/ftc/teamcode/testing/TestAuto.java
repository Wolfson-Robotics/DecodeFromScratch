package org.firstinspires.ftc.teamcode.testing;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.AutoDrive;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;

@Autonomous(name = "TestAuto")
public class TestAuto extends AutoDrive {


    @Override
    public void init() {
        USE_CAMERA = true;
        super.init();
    }


    /*
    Ways to "measure" or keep track of autonomous:
        - April Tags (Distance, Angle, Marker)
        - Odometry Wheels (Distance Travelled)
        - IMU (Rotation, Angular Velocity)

    Tips from online:
        - Minimize Strafing: encoder counts can end up not matching distance travelled
        - Correct position using April Tags


     */
    @Override
    public void loop() {

        if (USE_CAMERA) {
            for (AprilTagDetection tag : aTagProc.getDetections()) {
                if (tag.id == BLUE_TAG) {
                    telemetry.addLine(String.format("XYZ %6.1f %6.1f %6.1f  (inch)",
                        tag.robotPose.getPosition().x,
                        tag.robotPose.getPosition().y,
                        tag.robotPose.getPosition().z));
                    telemetry.addLine(String.format("PRY %6.1f %6.1f %6.1f  (deg)",
                        tag.robotPose.getOrientation().getPitch(AngleUnit.DEGREES),
                        tag.robotPose.getOrientation().getRoll(AngleUnit.DEGREES),
                        tag.robotPose.getOrientation().getYaw(AngleUnit.DEGREES)));
                }
            }
        }
    }

}
