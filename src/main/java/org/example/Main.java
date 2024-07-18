package org.example;

import javax.swing.*;

/**
 * Main class for the FaceFilter application.
 * This class initializes and starts the GUI.
 *
 * @author Akshaj Srirambhatla
 * @author Celine
 * @author Pranay
 * @author Tenzin
 */

public class Main {
    private static final String PROTO_FILE = "deploy.prototxt";
    private static final String CAFFE_MODEL_FILE = "res10_300x300_ssd_iter_140000.caffemodel";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new GUI();
        });
    }

    public static String getProtoFile() {
        return PROTO_FILE;
    }

    public static String getCaffeModelFile() {
        return CAFFE_MODEL_FILE;
    }
}
