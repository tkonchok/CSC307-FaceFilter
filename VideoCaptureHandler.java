package org.example;

import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_videoio.VideoCapture;

import static org.bytedeco.opencv.global.opencv_videoio.CAP_PROP_FRAME_HEIGHT;
import static org.bytedeco.opencv.global.opencv_videoio.CAP_PROP_FRAME_WIDTH;

/**
 * VideoCaptureHandler class for handling video capture from the webcam.
 * This class initializes the video capture, sets the frame dimensions, and provides methods to read and release frames.
 *
 * @author Celine Ha
 */

public class VideoCaptureHandler {
    private final VideoCapture capture;

    public VideoCaptureHandler() {
        capture = new VideoCapture();
        capture.set(CAP_PROP_FRAME_WIDTH, 800);
        capture.set(CAP_PROP_FRAME_HEIGHT, 800);
    }

    public boolean isOpen() {
        return capture.open(0);
    }

    public boolean readFrame(Mat frame) {
        return capture.read(frame);
    }

    public void release() {
        capture.release();
    }
}
