package org.firstinspires.ftc.teamcode.util;

import com.qualcomm.robotcore.hardware.Gamepad;

public final class ControllerNumberInput {

    private static final char[] DIGIT_CYCLE = new char[] { '0','1','2','3','4','5','6','7','8','9','-','.' };

    private final Gamepad gamepad;
    private final PersistentTelemetry telemetry;

    private final StringBuilder buf = new StringBuilder();
    private int cycleIndex = 0;     // current candidate char
    private int cursor = -1;        // index of last edited digit; -1 means empty
    private boolean editing = false;

    private boolean dpadEdge = false;
    private boolean btnEdge = false;

    public ControllerNumberInput(Gamepad gamepad, PersistentTelemetry telemetry) {
        this.gamepad = gamepad;
        this.telemetry = telemetry;
        telemetry.addLine("Manual:");
        telemetry.addLine("D-pad Right: add digit");
        telemetry.addLine("D-pad Up/Down: cycle current digit");
        telemetry.addLine("A or D-pad Left: commit");
        telemetry.addLine("X: backspace");
        telemetry.addLine("Y: clear");
        telemetry.setLine("Current number", "Current number: ");
        telemetry.update();
    }

    public void reset() {
        buf.setLength(0);
        cycleIndex = 0;
        cursor = -1;
        editing = false;
        dpadEdge = false;
        btnEdge = false;
        telemetry.setLine("Current number", "Current number: ");
        telemetry.update();
    }

    /** Call once per loop. Returns committed number, or Double.NaN if no commit this loop. */
    public double getInput() {
        // Add digit
        if (gamepad.dpad_right && !dpadEdge) {
            dpadEdge = true;
            if (!editing) {
                editing = true;
                buf.setLength(0);
                cycleIndex = 0;
                buf.append(DIGIT_CYCLE[cycleIndex]); // '0'
                cursor = buf.length() - 1;
            } else {
                cycleIndex = 0;
                buf.append(DIGIT_CYCLE[cycleIndex]);
                cursor = buf.length() - 1;
            }
            render();
        }

        // Cycle up
        if (gamepad.dpad_up && !dpadEdge) {
            dpadEdge = true;
            if (editing && cursor >= 0) {
                cycleIndex = (cycleIndex + 1) % DIGIT_CYCLE.length;
                setAtCursor(DIGIT_CYCLE[cycleIndex]);
                render();
            }
        }

        // Cycle down
        if (gamepad.dpad_down && !dpadEdge) {
            dpadEdge = true;
            if (editing && cursor >= 0) {
                cycleIndex = (cycleIndex - 1 + DIGIT_CYCLE.length) % DIGIT_CYCLE.length;
                setAtCursor(DIGIT_CYCLE[cycleIndex]);
                render();
            }
        }

        // Backspace
        if (gamepad.x && !btnEdge) {
            btnEdge = true;
            if (editing && buf.length() > 0) {
                buf.deleteCharAt(buf.length() - 1);
                cursor = buf.length() - 1;
                if (cursor < 0) editing = false;
                render();
            }
        }

        // Clear
        if (gamepad.y && !btnEdge) {
            btnEdge = true;
            reset();
        }

        // Commit (A or D-pad Left)
        double committed = Double.NaN;
        boolean commitPressed = (gamepad.a && !btnEdge) || (gamepad.dpad_left && !dpadEdge);
        if (commitPressed) {
            if (gamepad.a && !btnEdge) btnEdge = true;
            if (gamepad.dpad_left && !dpadEdge) dpadEdge = true;
            if (editing && buf.length() > 0) {
                String raw = buf.toString();
                try {
                    committed = Double.parseDouble(raw);
                } catch (NumberFormatException e) {
                    committed = Double.NaN;
                }
                buf.setLength(0);
                cursor = -1;
                editing = false;
                cycleIndex = 0;
                render();
            }
        }

        // Edge release
        if (!gamepad.dpad_up && !gamepad.dpad_down && !gamepad.dpad_left && !gamepad.dpad_right && dpadEdge) {
            dpadEdge = false;
        }
        if (!gamepad.a && !gamepad.x && !gamepad.y && btnEdge) {
            btnEdge = false;
        }

        return committed;
    }

    private void render() {
        telemetry.setLine("Current number", "Current number: " + buf);
        telemetry.update();
    }

    private void setAtCursor(char c) {
        if (cursor < 0 || cursor >= buf.length()) return;
        buf.setCharAt(cursor, c);
        normalize();
    }

    // Minimal normalization: one dot max, single leading minus, minus at index 0.
    private void normalize() {
        int firstDot = -1;
        for (int i = 0; i < buf.length(); i++) {
            if (buf.charAt(i) == '.') {
                if (firstDot == -1) firstDot = i;
                else {
                    buf.deleteCharAt(i);
                    if (cursor >= i) cursor--;
                    i--;
                }
            }
        }
        int minusCount = 0;
        for (int i = 0; i < buf.length(); i++) if (buf.charAt(i) == '-') minusCount++;
        if (minusCount > 1) {
            boolean kept = false;
            for (int i = 0; i < buf.length(); i++) {
                if (buf.charAt(i) == '-') {
                    if (!kept) kept = true;
                    else {
                        buf.deleteCharAt(i);
                        if (cursor >= i) cursor--;
                        i--;
                    }
                }
            }
        }
        int minusIdx = -1;
        for (int i = 0; i < buf.length(); i++) if (buf.charAt(i) == '-') { minusIdx = i; break; }
        if (minusIdx > 0) {
            buf.deleteCharAt(minusIdx);
            buf.insert(0, '-');
            if (cursor >= 0) {
                if (cursor < minusIdx) { }
                else if (cursor == minusIdx) { cursor = 0; }
                else { cursor--; }
            }
        }
    }
}
