package org.example;

import org.bytedeco.javacpp.indexer.UByteIndexer;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Size;

import static org.bytedeco.opencv.global.opencv_imgproc.resize;

/**
 * FilterOverlay class for the FaceFilter application.
 * This class overlays a filter image onto a detected face in the main image.
 *
 * @author Tenzin Konchok
 * @author Pranay Tiru
 */

public class FilterOverlay {
    public void overlayFilter(Mat image, Mat filter, int x, int y, int faceWidth, int faceHeight, FilterData filterData) {
        // calculates filter size based on the face size and the filter's scaling factors
        int filterWidth = (int) (faceWidth * filterData.getWidthScale());
        int filterHeight = (int) (faceHeight * filterData.getHeightScale());

        // resizes the filter image
        Mat resizedFilter = new Mat();
        resize(filter, resizedFilter, new Size(filterWidth, filterHeight));

        UByteIndexer filterIndexer = resizedFilter.createIndexer();
        UByteIndexer imageIndexer = image.createIndexer();

        // Adjusting filter position with offsets
        int filterX = x + filterData.getXOffset() - filterWidth / 2;
        int filterY = y + filterData.getYOffset() - filterHeight / 2;

        // overlaying the resized filter onto the image
        for (int i = 0; i < resizedFilter.rows(); i++) {
            for (int j = 0; j < resizedFilter.cols(); j++) {
                double[] filterPixel = new double[4];
                for (int k = 0; k < 4; k++) {
                    filterPixel[k] = filterIndexer.get(i, j, k);
                }
                if (filterPixel[3] > 0) { // checks the alpha channel for transparency
                    int imgY = filterY + i;
                    int imgX = filterX + j;
                    if (imgY >= 0 && imgY < image.rows() && imgX >= 0 && imgX < image.cols()) {
                        imageIndexer.put(imgY, imgX, 0, (byte) filterPixel[0]);
                        imageIndexer.put(imgY, imgX, 1, (byte) filterPixel[1]);
                        imageIndexer.put(imgY, imgX, 2, (byte) filterPixel[2]);
                    }
                }
            }
        }
    }
}
