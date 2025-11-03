package org.firstinspires.ftc.teamcode.debug.toauto;

import static org.firstinspires.ftc.teamcode.debug.util.Async.sleep;
import static org.firstinspires.ftc.teamcode.debug.util.GeneralUtils.boolSign;

import org.firstinspires.ftc.teamcode.debug.ReplayBase;
import org.firstinspires.ftc.teamcode.debug.HardwareSnapshot;
import org.firstinspires.ftc.teamcode.debug.handlers.DcMotorExHandler;
import org.firstinspires.ftc.teamcode.debug.handlers.HardwareComponentHandler;
import org.firstinspires.ftc.teamcode.debug.CustomTelemetryLogger;
import org.firstinspires.ftc.teamcode.debug.util.math.Vector2D;
import org.firstinspires.ftc.teamcode.debug.util.robot.RobotVector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

public class ReplayToInstructions extends ReplayBase {

    private static final double POWER_THRESHOLD = 0.1, VEC_SIMILARITY = 0.995;
    private static final long DISTINCT_DURATION = (long) (0.1*10E9);
    private static final Vector2D I_HAT = new Vector2D(1, 0), J_HAT = new Vector2D(0, 1);

    // TODO: Implement other movement instructions
    @Override
    public void start() {

        StringJoiner finalInstructions = new StringJoiner("\n");

        // TODO: Implement position correcting with addition of vectors as instructions
        List<RobotVector> similarVecs = new ArrayList<>();
        long similarDuration = 0L;
        double startLFPos = 0, startRFPos = 0;

        RobotVector prevVec;
        List<RobotVector> vecsToInterpolate = new ArrayList<>();
        for (HardwareSnapshot mmSnapshot : mmSnapshots) {
            Map<DcMotorExHandler, Double> motorPowers = mmSnapshot.motorPowers();
            Map<HardwareComponentHandler<?>, Double> motorPoses = mmSnapshot.motorPositions();

            double lfDir = boolSign(lf_drive.forward(motorPowers.get(lf_drive))),
                    rfDir = boolSign(rf_drive.forward(motorPowers.get(rf_drive))),
                    lbDir = boolSign(lb_drive.forward(motorPowers.get(lb_drive))),
                    rbDir = boolSign(rb_drive.forward(motorPowers.get(rb_drive))),
                    lfPos = motorPoses.get(lf_drive),
                    rfPos = motorPoses.get(rf_drive);

            // Friction force vectors
            Vector2D lfVec = new Vector2D(lfDir, lfDir),
                    rfVec = new Vector2D(-rfDir, rfDir),
                    lbVec = new Vector2D(-lbDir, lbDir),
                    rbVec = new Vector2D(rbDir, rbDir);
            RobotVector robotVec = new RobotVector(lfVec, lbVec, rfVec, rbVec);

            if (similarVecs.isEmpty()) {
                similarVecs.add(robotVec);
                similarDuration += mmSnapshot.duration();
                startLFPos = lfPos;
                startRFPos = rfPos;
                continue;
            }

            if (similarVecs.get(0).cosineSimilarity(robotVec) < VEC_SIMILARITY) {

                if (similarVecs.size() == 1) {
                    vecsToInterpolate.add(robotVec);
                    // TODO: Add cubic spline interpolation here
                } else {
//                    RobotVector totalMotionVec = similarVecs.stream().reduce(RobotVector.empty(), RobotVector::add);
                    RobotVector totalMotionVec = null;
                    if ((similarDuration += mmSnapshot.duration()) >= DISTINCT_DURATION) {

                        double vertIN = ticsToInches(rfPos - startLFPos),
                                horizIN = ticsToInches(lfPos - startRFPos);
                        if (Math.abs(totalMotionVec.cosineSimilarity(I_HAT)) >= VEC_SIMILARITY) {
                            finalInstructions.add("new CardinalMovement(CardinalDirection." + (totalMotionVec.y < 0 ? "S" : "N") + ", " + vertIN + "),");
                        } else if (Math.abs(totalMotionVec.cosineSimilarity(J_HAT)) >= VEC_SIMILARITY) {
                            finalInstructions.add("new CardinalMovement(CardinalDirection." + (totalMotionVec.x < 0 ? "W" : "E") + ", " + horizIN + "),");
                        } else {
                            finalInstructions.add("new OrdinalMovement(CardinalDirection." + totalMotionVec.compassDirection().name() + ", " + horizIN + ", " + vertIN + "),");
                        }

                        finalInstructions.add(totalMotionVec.toString());
                        similarVecs.clear();
                        similarDuration = 0L;
                    } else {
                        vecsToInterpolate.add(totalMotionVec);
                    }

                }
                continue;
            }

            similarDuration += mmSnapshot.duration();
            similarVecs.add(robotVec);
        }

        System.out.println(finalInstructions);
        try {
            CustomTelemetryLogger logger = new CustomTelemetryLogger("/sdcard/Logs/debug_auto.txt");
            logger.logData(finalInstructions.toString());
            logger.close();
        } catch (IOException e) {
            telemetry.addLine("\nFailed to write instructions to file");
            telemetry.update();
        }

        sleep(10000);
    }

    @Override
    public void loop() {}
}
