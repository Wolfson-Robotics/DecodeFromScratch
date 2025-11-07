package org.firstinspires.ftc.teamcode.debug;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class ReplayRobotBase extends TranslationRobotBase {

    protected List<HardwareSnapshot> mmSnapshots, omSnapshots;

    protected List<HardwareSnapshot> parseLog(String type) {

        String instructName = type + "_instruct.txt";
        telemetry.addLine("Which " + type + "file would you like to load?");
        telemetry.addLine("Press gamepad1 a for " + instructName);
        telemetry.addLine("Press gamepad1 b for latest " + type + " log");
        telemetry.update();

        String logPath = "";
        if (gamepad1.a) {
            telemetry.addLine("Loading " + instructName + "...");
            telemetry.update();
            logPath = storagePath + "/autonomous/" + instructName;
        }
        else if (gamepad1.b) {
            File[] logFiles = new File(logsPath).listFiles((dir, name) -> name.startsWith("debug_" + type + "_"));
            if (logFiles == null || logFiles.length == 0) {
                telemetry.addLine("No logs found");
                telemetry.update();
                sleep(10000);
                this.terminateOpModeNow();
                return null;
            }

            telemetry.addLine("Loading latest " + type + " log...");
            telemetry.update();
            Arrays.sort(logFiles, (f1, f2) -> f2.getName().compareTo(f1.getName()));
            logPath = logsPath + "/" + logFiles[0].getName();
        } else {
            return null;
        }


        FileReader fileReader;
        try {
            fileReader = new FileReader(logPath);
        } catch (FileNotFoundException e) {
            telemetry.addLine("Log path " + logPath + " not found");
            telemetry.update();
            sleep(10000);
            this.terminateOpModeNow();
            return null;
        }

        List<HardwareSnapshot> snapshots = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(fileReader)) {
            String line;
            while ((line = reader.readLine()) != null) {
                snapshots.add(HardwareSnapshot.deserialize(line));
            }
        } catch (IOException e) {
            telemetry.addLine("Failed to read log file " + logPath + ". Details:");
            e.printStackTrace();
            sleep(10000);
            this.terminateOpModeNow();
            return null;
        }
        telemetry.addLine("Read " + type + " log file");
        telemetry.update();

        return snapshots;
    }


    @Override
    public void init_loop() {
        super.init_loop();
        if (this.mmSnapshots == null) {
            this.mmSnapshots = this.parseLog("mm");
            return;
        }
        if (this.omSnapshots == null) {
            this.omSnapshots = this.parseLog("om");
            return;
        }
        telemetry.addLine("Loaded " + this.mmSnapshots.size() + " MM snapshots");
        telemetry.addLine("Loaded " + this.omSnapshots.size() + " OM snapshots");
        telemetry.update();
    }


}