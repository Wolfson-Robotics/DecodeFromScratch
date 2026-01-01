package org.firstinspires.ftc.teamcode.components.camera;

import android.util.Size;

import org.firstinspires.ftc.robotcore.external.hardware.camera.CameraName;
import org.firstinspires.ftc.teamcode.components.camera.visionprocessors.FrameGrabberProcessor;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.VisionProcessor;
import org.opencv.core.Mat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class VisionPortalCamera extends CameraBase<VisionPortal> {

    private FrameGrabberProcessor frameGrabber;

    public VisionPortalCamera(VisionPortal device) {
        super(device);
    }

    @Override
    public void closeCamera() {
        device.close();
    }

    @Override
    public Mat getCurrentFrame() {
        if (this.frameGrabber == null) { return null; }
        return this.frameGrabber.getLatestFrame();
    }

    //https://ftc-docs.firstinspires.org/en/latest/apriltag/vision_portal/visionportal_init/visionportal-init.html
    public static VisionPortal createCustomVisionPortal(CameraName camera, VisionProcessor... processor) {
        VisionPortal.Builder builder = new VisionPortal.Builder();

        //Add VisionProcessors
        Arrays.stream(processor).forEach(builder::addProcessor);

        return builder
                .setCamera(camera)
                .setCameraResolution(new Size(432, 240))
                .setStreamFormat(VisionPortal.StreamFormat.MJPEG) // MJPEG offers more frames, YUY2 offers better quality
                .enableLiveView(true)
                .setAutoStopLiveView(true)
                .setShowStatsOverlay(true) //Seems interesting TODO: remove or not
                .build();
    }

    public static VisionPortalCamera createVisionPortalCamera(CameraName camera, VisionProcessor... processor) {
        FrameGrabberProcessor frameGrabber = new FrameGrabberProcessor();
        ArrayList<VisionProcessor> processors =
                Arrays.stream(processor).collect(Collectors.toCollection(ArrayList::new));
        processors.add(frameGrabber);
        VisionPortal vp = createCustomVisionPortal(
                camera,
                processors.toArray(new VisionProcessor[0])
        );
        VisionPortalCamera cam = new VisionPortalCamera(vp);
        cam.frameGrabber = frameGrabber;
        return cam;
    }

}
