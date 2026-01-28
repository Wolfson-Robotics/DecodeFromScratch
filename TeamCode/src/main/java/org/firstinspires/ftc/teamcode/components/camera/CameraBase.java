package org.firstinspires.ftc.teamcode.components.camera;

import org.firstinspires.ftc.robotcore.external.stream.CameraStreamSource;
import org.opencv.core.Mat;

public abstract class CameraBase<T extends CameraStreamSource> {

    public final T device;

    public CameraBase(T device) {
        this.device = device;
    }

    public abstract void closeCamera();

    protected Mat current_frame;
    public abstract Mat getCurrentFrame();

}
