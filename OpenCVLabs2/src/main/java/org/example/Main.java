package org.example;

import java.nio.file.Paths;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

public class Main {
    public static void main(String[] args) {
        // Инициализация OpenCV
        ImageProcessor.initOpenCV();
        
        System.out.println("OpenCV loaded successfully");
        System.out.println("OS: " + ImageProcessor.getOperatingSystemType());
        System.out.println("OpenCV version: " + Core.getVersionString());
        
        // Путь к тестовому изображению
        String imagePath = Paths.get(Constants.SOURCE_IMAGES_DIR, "test_image.jpg").toString();
        
        // Демонстрация работы
        ImageProcessor.demonstrateChannelEffects(imagePath);
        
        // Тестовая матрица
        Mat testMatrix = Mat.eye(3, 3, CvType.CV_8UC1);
        System.out.println("Test matrix:\n" + testMatrix.dump());
    }
}