package org.firstinspires.ftc.teamcode.testing;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.teamcode.AutoBase;
import org.firstinspires.ftc.teamcode.util.ControllerNumberInput;
import org.firstinspires.ftc.teamcode.util.PersistentTelemetry;

@TeleOp(name = "TestShooter", group = "Testing")
public class TestShooter extends AutoBase {

    private PersistentTelemetry pTelem;
    private ControllerNumberInput input;
    private double targetVelocity = 1500;

    @Override
    public void init() {
        super.init();
        pTelem = new PersistentTelemetry(telemetry);
        input = new ControllerNumberInput(gamepad1, pTelem);
        
        pTelem.addLine("--- TEST SHOOTER ---");
        pTelem.setData("Target Velocity", () -> String.format("%.0f", targetVelocity));
        pTelem.addLine("Enter velocity with D-pad, press A to Shoot");
        pTelem.update();
    }

    @Override
    public void loop() {
        // Use ControllerNumberInput to update targetVelocity
        double val = input.getInput();
        if (!Double.isNaN(val)) {
            targetVelocity = val;
            // Execute the shoot sequence immediately upon commission
            shootBetter(targetVelocity);
            // Ensure flywheel velocity goes back to 0/at rest after shooting
            launcher.applyVelocity(0);
        }

        pTelem.update();
    }
}
