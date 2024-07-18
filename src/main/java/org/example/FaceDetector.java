package org.example;

import org.bytedeco.javacpp.indexer.FloatIndexer;
import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_dnn.Net;

import static org.bytedeco.opencv.global.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_dnn.*;

/**
 * FaceDetector class for the FaceFilter application.
 * This class uses OpenCV DNN module to detect faces in an image.
 *
 * @author Celine Ha
 */

public class FaceDetector {
    private final Net net;

    public FaceDetector(String protoFile, String modelFile) {
        net = readNetFromCaffe(protoFile, modelFile);
    }

    public Mat detectFaces(Mat image) {
        Mat blob = blobFromImage(image, 1.0, new Size(300, 300), new Scalar(104.0, 177.0, 123.0, 0), false, false, CV_32F);
        net.setInput(blob);
        return net.forward();
    }

    public FloatIndexer getOutputIndexer(Mat output) {
        Mat ne = new Mat(new Size(output.size(3), output.size(2)), CV_32F, output.ptr(0, 0));
        return ne.createIndexer();
    }
}
