package org.firstinspires.ftc.teamcode.testing;

/*
automatically figure out the velocity needed to fire from any distance or angle
 */

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.teamcode.AutoBase;
import org.firstinspires.ftc.teamcode.debug.util.Async;
import org.firstinspires.ftc.teamcode.util.LinearRegression;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;

public class AutoAimShoot extends AutoBase {

    //TODO: UPDATE VALUES WITH LauncherVelocityInput
    double[] distance = {2.0, 2.0, 2.0, 3.0, 3.0, 3.0};
    double[] angle = {0.0, 30.0, -30.0, 0.0, 45.0, -45.0};
    double[] velocity = {1500, 1520, 1510, 1650, 1700, 1690};

    double a1 = 1.0, a2 = 1.0, a3 = 0.01;
    double[] features = new double[distance.length];

    @Override
    public void init() {
        initCamera();
    }


    @Override
    public void start() {

        //Get blue april tag positional data
        Pose3D posData = null;
        while (posData == null) {
            for (AprilTagDetection tag : aTagProc.getDetections()) {
                if (tag.id == BLUE_TAG) {
                    posData = tag.robotPose;
                    telemetry.addLine(String.format("XYZ %6.1f %6.1f %6.1f  (inch)",
                            posData.getPosition().x,
                            posData.getPosition().y,
                            posData.getPosition().z));
                    telemetry.addLine(String.format("PRY %6.1f %6.1f %6.1f  (deg)",
                            posData.getOrientation().getPitch(AngleUnit.DEGREES),
                            posData.getOrientation().getRoll(AngleUnit.DEGREES),
                            posData.getOrientation().getYaw(AngleUnit.DEGREES)));
                }
            }
        }


        for (int i = 0; i < distance.length; i++) {
            features[i] = a1 * distance[i] + a2 * distance[i] * distance[i] + a3 * angle[i] * angle[i];
        }

        LinearRegression model = new LinearRegression(features, velocity);
        double dNew = 2.5 ;
        double thetaNew = 20;
        double featureNew = a1 * dNew + a2 * dNew + a3 * thetaNew * thetaNew;
        double predictedVelocity = model.predict(featureNew);

        telemetry.addLine(String.format("Estimated Speed: (d=%.2f, 0=%.2f degrees): %.2f deg/s%n",
                dNew, thetaNew, predictedVelocity));
        telemetry.update();

        Async.sleep(5000);
        setLauncher(predictedVelocity);
        prepareShoot();
        Async.sleep(3000);
        stopShoot();
    }

    @Override
    public void loop() {}


}
