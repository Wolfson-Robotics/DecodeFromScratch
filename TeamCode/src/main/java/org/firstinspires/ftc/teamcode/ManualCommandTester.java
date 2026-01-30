package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.util.ControllerNumberInput;
import org.firstinspires.ftc.teamcode.util.PersistentTelemetry;

@TeleOp(name = "ManualCommandTester")
public class ManualCommandTester extends AutoBase {

    private PersistentTelemetry pTelem;
    private ControllerNumberInput input;

    private double currentPower = 0.5;
    private boolean dpadUpEdge = false;
    private boolean dpadDownEdge = false;

    private enum CommandType {
        MOVE_VERTICAL("Move Vertical (Forward)"),
        MOVE_HORIZONTAL("Move Horizontal (Right)"),
        TURN_CCW("Turn Bot (Counter-Clockwise)"),
        TURN_CW("Turn Bot (Clockwise)");

        private final String label;
        CommandType(String label) { this.label = label; }
        @Override
        public String toString() { return label; }
    }

    private CommandType currentType = CommandType.MOVE_VERTICAL;
    private boolean lbPressed = false;

    @Override
    public void init() {
        super.init();
        pTelem = new PersistentTelemetry(telemetry);
        input = new ControllerNumberInput(gamepad1, pTelem);

        pTelem.addLine("--- MANUAL COMMAND TESTER ---");
        pTelem.setData("Current Command Type", () -> currentType.toString());
        pTelem.setData("Current Power", () -> String.format("%.1f", currentPower));
        pTelem.addLine("Press LEFT BUMPER to cycle command type");
        pTelem.addLine("Dpad Up/Down: Adjust Power (-1.0 to 1.0)");
        pTelem.addLine("Enter value (inches/degrees) and press A to run");
        pTelem.update();
    }

    @Override
    public void loop() {
        // Cycle command type with Left Bumper
        if (gamepad2.left_bumper && !lbPressed) {
            lbPressed = true;
            CommandType[] values = CommandType.values();
            currentType = values[(currentType.ordinal() + 1) % values.length];
        } else if (!gamepad2.left_bumper) {
            lbPressed = false;
        }

        // Adjust power with Dpad Up/Down (increments of 0.1)
        if (gamepad2.dpad_up && !dpadUpEdge) {
            currentPower = Math.min(1.0, currentPower + 0.1);
            dpadUpEdge = true;
        } else if (!gamepad2.dpad_up) {
            dpadUpEdge = false;
        }

        if (gamepad2.dpad_down && !dpadDownEdge) {
            currentPower = Math.max(-1.0, currentPower - 0.1);
            dpadDownEdge = true;
        } else if (!gamepad2.dpad_down) {
            dpadDownEdge = false;
        }

        // Get value from controller
        double val = input.getInput();
        if (!Double.isNaN(val)) {
            executeCommand(val);
        }

        pTelem.update();
    }

    private void executeCommand(double value) {
        pTelem.addLine("Executing: " + currentType + " with value " + value + " at power " + currentPower);
        pTelem.update();

        // Apply currentPower to driveSystem for moveBot calls
        driveSystem.powerFactor = currentPower;

        switch (currentType) {
            case MOVE_VERTICAL:
                // distIN = value, vertical = 1, pivot = 0, horizontal = 0
                moveBot(value, 1, 0, 0);
                break;
            case MOVE_HORIZONTAL:
                // distIN = value, vertical = 0, pivot = 0, horizontal = 1
                moveBot(value, 0, 0, 1);
                break;
            case TURN_CCW:
                // power = currentPower, degrees = value
                turnBot(currentPower, value);
                break;
            case TURN_CW:
                // power = currentPower, degrees = -value
                turnBot(currentPower, -value);
                break;
        }
    }
}
