package org.example;

import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.javacv.Frame;
import org.bytedeco.opencv.opencv_core.Mat;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imwrite;
import java.text.SimpleDateFormat;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imread;
import static org.bytedeco.opencv.global.opencv_imgcodecs.IMREAD_UNCHANGED;
import static org.bytedeco.opencv.global.opencv_core.flip;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * GUI class for the FaceFilter application.
 * This class handles the user interface, capturing video frames, detecting faces, and overlaying filters.
 *
 * @author Akshaj Srirambhatla
 * @author Celine Ha
 * @author Tenzin Konchok
 * @author Pranay Tiru
 */

public class GUI extends JFrame {
    private final VideoCaptureHandler captureHandler;
    private final FaceDetector faceDetector;
    private final FilterOverlay filterOverlay;
    private Mat filterImage;
    private final OpenCVFrameConverter.ToIplImage converter;

    private JButton captureButton;
    private JComboBox<String> filterComboBox;
    private JLabel imageLabel;

    private final Map<String, FilterData> filters;
    private FilterData selectedFilter;

    public GUI() {
        setTitle("Face Filter App");
        setSize(900, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
        setVisible(true);

        captureHandler = new VideoCaptureHandler();
        faceDetector = new FaceDetector(Main.getProtoFile(), Main.getCaffeModelFile());
        filterOverlay = new FilterOverlay();
        converter = new OpenCVFrameConverter.ToIplImage();

        filters = new HashMap<>();
        loadFilters();
        selectedFilter = filters.get("Hearts"); // Default filter

        captureButton = new JButton("Capture");
        filterComboBox = new JComboBox<>(filters.keySet().toArray(new String[0]));

        captureButton.addActionListener(new CaptureAction());
        filterComboBox.addActionListener(new FilterSelectionAction());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(filterComboBox);
        buttonPanel.add(captureButton);

        add(buttonPanel, BorderLayout.SOUTH);

        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        imageLabel.setVerticalAlignment(JLabel.CENTER);
        add(imageLabel, BorderLayout.CENTER);

        setLocationRelativeTo(null);
        setVisible(true);

        new StartCameraAction().actionPerformed(null);
    }

    private void loadFilters() {
        filters.put("Glasses", new FilterData("dope.png", 0, -30, 1.5, 0.8)); // Example scaling factors
        filters.put("Hearts", new FilterData("hearts.png", 0, -250, 1.2, 1.0));
        filters.put("Hacker Mask", new FilterData("hackermask.png", 0, 20, 1.5, 1));
        filters.put("Moustache", new FilterData("moustache.png", 0, 70, 0.8, 0.2));
    }

    private void detectAndOverlayFilters(Mat colorImg) {
        Mat output = faceDetector.detectFaces(colorImg);
        var srcIndexer = faceDetector.getOutputIndexer(output);

        for (int i = 0; i < output.size(3); i++) {
            float confidence = srcIndexer.get(i, 2);
            if (confidence > .6) {
                int tx = Math.round(srcIndexer.get(i, 3) * colorImg.cols());
                int ty = Math.round(srcIndexer.get(i, 4) * colorImg.rows());
                int bx = Math.round(srcIndexer.get(i, 5) * colorImg.cols());
                int by = Math.round(srcIndexer.get(i, 6) * colorImg.rows());

                int faceWidth = bx - tx;
                int faceHeight = by - ty;
                int faceCenterX = tx + faceWidth / 2;
                int faceCenterY = ty + faceHeight / 2;

                filterImage = loadFilter(selectedFilter.getFilePath());
                filterOverlay.overlayFilter(colorImg, filterImage, faceCenterX, faceCenterY, faceWidth, faceHeight, selectedFilter);
            }
        }

        flip(colorImg, colorImg, 1);
    }

    private class StartCameraAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!captureHandler.isOpen()) {
                System.out.println("Cannot open the camera!");
                return;
            }

            Mat colorImg = new Mat();

            new Thread(() -> {
                try {
                    while (captureHandler.readFrame(colorImg) && isVisible()) {
                        detectAndOverlayFilters(colorImg);

                        Frame frame = converter.convert(colorImg);
                        BufferedImage image = convertToBufferedImage(frame);
                        SwingUtilities.invokeLater(() -> imageLabel.setIcon(new ImageIcon(image)));
                        Thread.sleep(50);
                    }
                } catch (InterruptedException ex) {
                    System.out.println(ex.getMessage());
                } finally {
                    captureHandler.release();
                }
            }).start();
        }
    }

    private class CaptureAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            Mat colorImg = new Mat();
            if (captureHandler.readFrame(colorImg)) {
                detectAndOverlayFilters(colorImg);

                flip(colorImg, colorImg, 1);

                Frame frame = converter.convert(colorImg);
                BufferedImage image = convertToBufferedImage(frame);


                imageLabel.setIcon(new ImageIcon(image));


                String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String fileName = "captured_" + timestamp + ".png";
                imwrite(fileName, colorImg);
                JOptionPane.showMessageDialog(GUI.this, "Image Captured and Saved as " + fileName);
            }
        }
    }

    private class FilterSelectionAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String selectedOption = (String) filterComboBox.getSelectedItem();
            if (selectedOption != null && filters.containsKey(selectedOption)) {
                selectedFilter = filters.get(selectedOption);
            }
        }
    }

    private BufferedImage convertToBufferedImage(Frame frame) {
        Java2DFrameConverter java2DFrameConverter = new Java2DFrameConverter();
        return java2DFrameConverter.convert(frame);
    }

    private Mat loadFilter(String filePath) {
        Mat filterImage = imread(filePath, IMREAD_UNCHANGED);
        if (filterImage.empty()) {
            throw new RuntimeException("Error loading the filter image!");
        }
        return filterImage;
    }
}
