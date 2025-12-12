package org.firstinspires.ftc.teamcode.testing;

import com.bylazar.telemetry.JoinedTelemetry;
import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name = "PanelsTest")
public class PanelsTest extends OpMode {

    JoinedTelemetry pt = new JoinedTelemetry(PanelsTelemetry.INSTANCE.getFtcTelemetry(), telemetry);

    @Override
    public void init() {}


    @Override
    public void loop() {
        pt.addLine("What the...");
        pt.update();
    }

}
