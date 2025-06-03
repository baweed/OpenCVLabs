package org.example;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class ImageFilters {

    // Усредняющий фильтр
    public static Mat applyBlur(Mat src, int kernelSize) {
        Mat dst = new Mat();
        Imgproc.blur(src, dst, new Size(kernelSize, kernelSize));
        return dst;
    }

    // Гауссовский фильтр
    public static Mat applyGaussianBlur(Mat src, int kernelSize) {
        Mat dst = new Mat();
        Imgproc.GaussianBlur(src, dst, new Size(kernelSize, kernelSize), 0);
        return dst;
    }

    // Медианный фильтр
    public static Mat applyMedianBlur(Mat src, int kernelSize) {
        Mat dst = new Mat();
        Imgproc.medianBlur(src, dst, kernelSize);
        return dst;
    }

    // Двусторонний фильтр
    public static Mat applyBilateralFilter(Mat src, int diameter, double sigmaColor, double sigmaSpace) {
        Mat dst = new Mat();
        Imgproc.bilateralFilter(src, dst, diameter, sigmaColor, sigmaSpace);
        return dst;
    }
}