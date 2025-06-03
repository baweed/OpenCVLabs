package org.example;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.photo.Photo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

public class ImageSegmentation {

    // Заливка
    public static Mat applyFloodFill(Mat src, Point seedPoint, Scalar newVal, Scalar loDiff, Scalar upDiff) {
        Mat dst = src.clone();
        Mat mask = new Mat();
        Imgproc.floodFill(dst, mask, seedPoint, newVal, new Rect(), loDiff, upDiff, Imgproc.FLOODFILL_FIXED_RANGE);
        return dst;
    }

    // Понижающая пирамида
    public static Mat applyPyrDown(Mat src, int levels) {
        Mat dst = src.clone();
        for (int i = 0; i < levels; i++) {
            Imgproc.pyrDown(dst, dst);
        }
        return dst;
    }

    // Повышающая пирамида
    public static Mat applyPyrUp(Mat src, int levels) {
        Mat dst = src.clone();
        for (int i = 0; i < levels; i++) {
            Imgproc.pyrUp(dst, dst);
        }
        return dst;
    }

    // Получение фрагмента изображения после понижения и повышения
public static Mat getFragmentAfterPyramid(Mat src, int levels) {
    Mat down = applyPyrDown(src, levels);
    Mat up = applyPyrUp(down, levels);

    // Приведение размера up к размеру src
    Mat resizedUp = new Mat();
    Imgproc.resize(up, resizedUp, src.size());

    Mat fragment = new Mat();
    Core.subtract(src, resizedUp, fragment);
    return fragment;
}
    public static Mat applyRandomFloodFill(Mat src, Point seedPoint) {
        Random random = new Random();
        Scalar newVal = new Scalar(random.nextInt(256), random.nextInt(256), random.nextInt(256)); // Случайный цвет
        Scalar loDiff = new Scalar(50, 50, 50); // Нижний порог цвета
        Scalar upDiff = new Scalar(50, 50, 50); // Верхний порог цвета
    return applyFloodFill(src, seedPoint, newVal, loDiff, upDiff);
}
    // Идентификация прямоугольников
    public static int identifyRectangles(Mat src, int width, int height) {
        Mat grayImage = new Mat();
        Imgproc.cvtColor(src, grayImage, Imgproc.COLOR_BGR2GRAY);

        // Удаление шума
        Mat denoisingImage = new Mat();
        Photo.fastNlMeansDenoising(grayImage, denoisingImage);

        // Выравнивание гистограммы
        Mat histogramEqualizationImage = new Mat();
        Imgproc.equalizeHist(denoisingImage, histogramEqualizationImage);

        // Морфологическое открытие
        Mat morphologicalOpeningImage = new Mat();
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5, 5));
        Imgproc.morphologyEx(histogramEqualizationImage, morphologicalOpeningImage, Imgproc.MORPH_OPEN, kernel);

        // Пороговая обработка
        Mat thresholdImage = new Mat();
        Imgproc.threshold(morphologicalOpeningImage, thresholdImage, 50, 255, Imgproc.THRESH_OTSU);

        // Поиск контуров
        ArrayList<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(thresholdImage, contours, new Mat(), Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

        // Сортировка контуров по площади
        contours.sort(Collections.reverseOrder(Comparator.comparing(Imgproc::contourArea)));

        // Подсчет прямоугольников заданного размера
        int count = 0;
        for (MatOfPoint contour : contours) {
            MatOfPoint2f point2f = new MatOfPoint2f();
            contour.convertTo(point2f, CvType.CV_32FC2);
            double arcLength = Imgproc.arcLength(point2f, true);
            MatOfPoint2f approxContour2f = new MatOfPoint2f();
            Imgproc.approxPolyDP(point2f, approxContour2f, 0.03 * arcLength, true);
            MatOfPoint approxContour = new MatOfPoint();
            approxContour2f.convertTo(approxContour, CvType.CV_32S);

            Rect rect = Imgproc.boundingRect(approxContour);
            double ratio = (double) rect.height / rect.width;

            if (Math.abs((double) height / width - ratio) <= 0.15) {
                count++;
            }
        }

        return count;
    }
}
