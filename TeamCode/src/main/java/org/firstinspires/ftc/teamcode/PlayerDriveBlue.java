package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name = "PlayerDriveBlue", group = "Drive")
public class PlayerDriveBlue extends PlayerDrive {
    @Override
    public void init() {
        super.init();
        targetTag = BLUE_TAG;
    }
}
