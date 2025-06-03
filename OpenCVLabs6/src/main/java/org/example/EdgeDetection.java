package org.example;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class EdgeDetection {

    // Поиск границ с использованием алгоритма Canny
    public static Mat detectEdges(Mat src) {
        Mat grayImage = new Mat();
        Imgproc.cvtColor(src, grayImage, Imgproc.COLOR_BGR2GRAY);

        // Удаление шума с помощью размытия
        Mat blurredImage = new Mat();
        Imgproc.blur(grayImage, blurredImage, new Size(3, 3));

        // Пороговая обработка
        Mat thresholdImage = new Mat();
        double threshold = Imgproc.threshold(blurredImage, thresholdImage, 0, 255, Imgproc.THRESH_OTSU);

        // Поиск границ с помощью алгоритма Canny
        Mat edges = new Mat();
        Imgproc.Canny(blurredImage, edges, threshold, threshold * 3);

        return edges;
    }
}