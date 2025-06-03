package org.example;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class MorphologyOperations {

        // Эрозия
    public static Mat applyErosion(Mat src, int kernelSize, int shape) {
        Mat dst = new Mat();
        Mat kernel = Imgproc.getStructuringElement(shape, new Size(kernelSize, kernelSize));
        Imgproc.erode(src, dst, kernel);
        return dst;
    }

    // Расширение
    public static Mat applyDilation(Mat src, int kernelSize, int shape) {
        Mat dst = new Mat();
        Mat kernel = Imgproc.getStructuringElement(shape, new Size(kernelSize, kernelSize));
        Imgproc.dilate(src, dst, kernel);
        return dst;
    }
}