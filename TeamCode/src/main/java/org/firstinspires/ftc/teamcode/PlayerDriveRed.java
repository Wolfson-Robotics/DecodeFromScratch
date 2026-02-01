package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name = "PlayerDriveRed", group = "Drive")
public class PlayerDriveRed extends PlayerDrive {
    @Override
    public void init() {
        super.init();
        targetTag = RED_TAG;
    }
}
