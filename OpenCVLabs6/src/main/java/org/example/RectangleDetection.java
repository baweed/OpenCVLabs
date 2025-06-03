package org.example;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class RectangleDetection {

    public static Mat identifyRectangles(Mat src, int width, int height) {
        Mat grayImage = new Mat();
        Imgproc.cvtColor(src, grayImage, Imgproc.COLOR_BGR2GRAY);

        // Пороговая обработка
        Mat thresholdImage = new Mat();
        Imgproc.threshold(grayImage, thresholdImage, 50, 255, Imgproc.THRESH_OTSU);

        // Поиск контуров
        ArrayList<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(thresholdImage, contours, new Mat(), Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

        // Сортировка контуров по площади
        contours.sort(Collections.reverseOrder(Comparator.comparing(Imgproc::contourArea)));

        Mat resultImage = src.clone();

        // Подсчет прямоугольников заданного размера
        int count = 0;
        for (MatOfPoint contour : contours) {
            Rect rect = Imgproc.boundingRect(contour);
            double ratio = (double) rect.height / rect.width;

            // Проверка соотношения сторон
            if (Math.abs((double) height / width - ratio) <= 0.15) {
                // Отрисовка
                Imgproc.rectangle(resultImage, rect.tl(), rect.br(), new Scalar(0, 255, 0), 2);
                count++;
            }
        }

        System.out.println("Найдено прямоугольников: " + count);
        return resultImage;
    }
}