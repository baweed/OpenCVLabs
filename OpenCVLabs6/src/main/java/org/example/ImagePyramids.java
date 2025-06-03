package org.example;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

public class ImagePyramids {

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
}