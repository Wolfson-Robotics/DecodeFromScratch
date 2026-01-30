package org.firstinspires.ftc.teamcode.debug;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.teamcode.RobotBase;
import org.firstinspires.ftc.teamcode.components.MecanumDrive;
import org.firstinspires.ftc.teamcode.util.PersistentTelemetry;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@TeleOp(name = "DebugLogger", group = "Debug")
public class DebugLogger extends RobotBase {

    private PersistentTelemetry pTelem;
    private List<String> logEntries = new ArrayList<>();
    
    private boolean recordingDrive = false;
    private int[] startDrivePos = new int[4]; // lf, lb, rf, rb
    
    private boolean recordingIntake = false;
    private double intakeStartTime = 0;

    private enum DriveMode {
        FREE, TURN, HORIZ_STRAIGHT_ONLY, STRAIGHT_ONLY, HORIZ_ONLY
    }
    private DriveMode currentDriveMode = DriveMode.FREE;
    private double diagonalThreshold = 3.0; // In inches

    @Override
    public void init() {
        super.init();
        pTelem = new PersistentTelemetry(telemetry);
        
        pTelem.addLine("--- AUTO LOGGER ---");
        pTelem.setData("Drive Recording", "OFF");
        pTelem.setData("Intake Recording", "OFF");
        pTelem.setData("Mode", () -> currentDriveMode.toString());
        pTelem.setData("Diag Threshold", () -> String.format("%.1f", diagonalThreshold));
        pTelem.addLine("Controls: A (Drive), X (Intake), B (Mode), Dpad Up/Down (Thresh), Y (Stopper)");
        pTelem.addLine("-------------------");
        pTelem.addLine("RECENT LOGS:");
        pTelem.addLine(() -> {
            StringBuilder sb = new StringBuilder();
            int start = Math.max(0, logEntries.size() - 3);
            for (int i = start; i < logEntries.size(); i++) {
                sb.append("> ").append(logEntries.get(i).replace("\n", " ")).append("\n");
            }
            return sb.toString();
        });
    }

    @Override
    public void loop() {
        // --- Mode Toggles ---
        if (gamepad1.bWasPressed()) {
            DriveMode[] modes = DriveMode.values();
            currentDriveMode = modes[(currentDriveMode.ordinal() + 1) % modes.length];
        }
        
        if (gamepad1.dpadUpWasPressed()) diagonalThreshold += 0.5;
        if (gamepad1.dpadDownWasPressed()) diagonalThreshold = Math.max(0.5, diagonalThreshold - 0.5);

        // --- Driver Controls ---
        float y = -gamepad1.left_stick_y;
        float x = gamepad1.left_stick_x;
        float rot = gamepad1.right_stick_x;

        // Apply Mode Restrictions
        switch (currentDriveMode) {
            case TURN:
                y = 0;
                x = 0;
                break;
            case HORIZ_STRAIGHT_ONLY:
                rot = 0;
                if (Math.abs(x) > Math.abs(y)) y = 0; else x = 0;
                break;
            case STRAIGHT_ONLY:
                rot = 0;
                x = 0;
                break;
            case HORIZ_ONLY:
                rot = 0;
                y = 0;
                break;
        }

        driveSystem.drive(y, x, rot);
        
        // Mechanisms
        boolean activeIntake = gamepad2.right_trigger > 0.1;
        intake.togglePower(activeIntake, intake.MAX_POWER);
        transfer.togglePower(activeIntake, transfer.MAX_POWER);
        lTransport.togglePower(activeIntake, lTransport.MAX_POWER);
        rTransport.togglePower(activeIntake, rTransport.MAX_POWER);
        stopper.togglePosition(!gamepad1.y);

        // --- Logging Logic ---

        // Drive Logging (Button A)
        if (gamepad1.aWasPressed()) {
            if (!recordingDrive) {
                startDrivePos[0] = driveSystem.lf.getCurrentPosition();
                startDrivePos[1] = driveSystem.lb.getCurrentPosition();
                startDrivePos[2] = driveSystem.rf.getCurrentPosition();
                startDrivePos[3] = driveSystem.rb.getCurrentPosition();
                recordingDrive = true;
                pTelem.setData("Drive Recording", "RECORDING...");
            } else {
                int[] endDrivePos = {
                    driveSystem.lf.getCurrentPosition(), driveSystem.lb.getCurrentPosition(),
                    driveSystem.rf.getCurrentPosition(), driveSystem.rb.getCurrentPosition()
                };
                processDriveLog(startDrivePos, endDrivePos);
                recordingDrive = false;
                pTelem.setData("Drive Recording", "OFF");
            }
        }

        // Intake Logging (Button X)
        if (gamepad2.xWasPressed()) {
            if (!recordingIntake) {
                intakeStartTime = getRuntime();
                recordingIntake = true;
                pTelem.setData("Intake Recording", "RECORDING...");
            } else {
                double duration = getRuntime() - intakeStartTime;
                logEntries.add(String.format("runFeed();\nAsync.sleep(%d);\nstopFeed();", (int)(duration * 1000)));
                recordingIntake = false;
                pTelem.setData("Intake Recording", "OFF");
            }
        }
        
        pTelem.update();
    }

    private void processDriveLog(int[] start, int[] end) {
        int dLF = end[0] - start[0];
        int dLB = end[1] - start[1];
        int dRF = end[2] - start[2];
        int dRB = end[3] - start[3];

        double avgY = (dLF + dLB + dRF + dRB) / 4.0;
        double avgX = ((dLF + dRB) - (dRF + dLB)) / 4.0;
        
        double distY = avgY / MecanumDrive.ticsPerInch; 
        double distX = avgX / MecanumDrive.ticsPerInch;

        double leftSide = (dLF + dLB) / 2.0;
        double rightSide = (dRF + dRB) / 2.0;

        // Mode-specific logging priority
        if (currentDriveMode == DriveMode.TURN) {
            logTurn(leftSide, rightSide);
            return;
        }

        // Check for diagonal movement crossing threshold first (only in FREE mode)
        if (currentDriveMode == DriveMode.FREE && Math.abs(distY) > diagonalThreshold && Math.abs(distX) > diagonalThreshold) {
            logEntries.add(String.format("moveBotDiagonal(%.2f, %.2f);", distX, distY));
            return;
        }

        // Handle specific modes or dominant axis
        if (currentDriveMode == DriveMode.FREE && Math.signum(leftSide) != Math.signum(rightSide) && Math.abs(leftSide - rightSide) > 100) {
            logTurn(leftSide, rightSide);
            return;
        }

        if (currentDriveMode == DriveMode.HORIZ_ONLY || (Math.abs(distX) > Math.abs(distY) && currentDriveMode != DriveMode.STRAIGHT_ONLY)) {
            logEntries.add(String.format("moveBot(%.2f, 0, 0, %.1f);", Math.abs(distX), Math.signum(distX)));
        } else {
            logEntries.add(String.format("moveBot(%.2f, %.1f, 0, 0);", Math.abs(distY), Math.signum(distY)));
        }
    }

    private void logTurn(double leftSide, double rightSide) {
        // Inverse of MecanumDrive.turnBot logic
        double rotTicks = (leftSide - rightSide) / 2.0;
        double distUnit = MecanumDrive.ROBOT_LENGTH_IN / Math.cos(Math.toRadians(45));
        double degrees = (rotTicks * 90.0) / (MecanumDrive.ticsPerInch * MecanumDrive.degConv * 1.75 * distUnit);
        logEntries.add(String.format("turnBot(0.5, %.2f);", degrees));
    }

    @Override
    public void stop() {
        super.stop();
        saveLogToFile();
    }

    private void saveLogToFile() {
        File dir = new File(logsPath);
        if (!dir.exists()) dir.mkdirs();
        
        File file = new File(dir, "auto_gen_" + System.currentTimeMillis() + ".txt");
        try (FileWriter writer = new FileWriter(file)) {
            for (String entry : logEntries) {
                writer.write(entry + "\nAsync.sleep(250);\n");
            }
        } catch (IOException ignored) {}
    }
}
