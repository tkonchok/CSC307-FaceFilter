package org.example;

/**
 * FilterData class for the FaceFilter application.
 * This class stores information about a filter, including its file path, offsets, and scaling factors.
 * @author: Akshaj Srirambhatla
 */

public class FilterData {
    private final String filePath;
    private final int xOffset;
    private final int yOffset;
    private final double widthScale;
    private final double heightScale;

    public FilterData(String filePath, int xOffset, int yOffset, double widthScale, double heightScale) {
        this.filePath = filePath;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.widthScale = widthScale;
        this.heightScale = heightScale;
    }

    public String getFilePath() {
        return filePath;
    }

    public int getXOffset() {
        return xOffset;
    }

    public int getYOffset() {
        return yOffset;
    }

    public double getWidthScale() {
        return widthScale;
    }

    public double getHeightScale() {
        return heightScale;
    }
}
