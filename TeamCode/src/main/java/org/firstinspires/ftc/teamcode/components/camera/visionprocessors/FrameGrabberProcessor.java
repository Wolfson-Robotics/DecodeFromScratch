package org.firstinspires.ftc.teamcode.components.camera.visionprocessors;

import android.graphics.Canvas;

import org.firstinspires.ftc.robotcore.internal.camera.calibration.CameraCalibration;
import org.firstinspires.ftc.vision.VisionProcessor;
import org.opencv.core.Mat;

public class FrameGrabberProcessor implements VisionProcessor {

    private volatile Mat curFrame = new Mat();

    @Override
    public void init(int width, int height, CameraCalibration calibration) {}

    @Override
    public Object processFrame(Mat frame, long captureTimeNanos) {
        curFrame = frame.clone();
        return null;
    }

    @Override
    public void onDrawFrame(Canvas canvas, int onscreenWidth, int onscreenHeight, float scaleBmpPxToCanvasPx, float scaleCanvasDensity, Object userContext) {}

    public Mat getLatestFrame() {
        if (curFrame != null) {
            return curFrame.clone(); // Return a copy to prevent external modification
        }
        return null;
    }
}
