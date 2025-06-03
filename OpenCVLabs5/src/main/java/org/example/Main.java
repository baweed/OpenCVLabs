package org.example;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    private static final String TEST_IMAGE = "test_image.jpg";
    private static final String OUTPUT_DIR = "output/";

    public static void main(String[] args) {
        loadOpenCVLibrary();

        Mat testImage = loadImage(TEST_IMAGE);
        if (testImage.empty()) {
            System.err.println("Не удалось загрузить тестовое изображение");
            return;
        }

        createOutputDirectory();
        applySegmentationOperations(testImage);

        System.out.println("Обработка завершена. Результаты сохранены в: " + 
                         new File(OUTPUT_DIR).getAbsolutePath());
    }

    private static void loadOpenCVLibrary() {
        try {
            String libPath = findLibrary("libopencv_java4120.so");
            System.load(libPath);
            System.out.println("Библиотека OpenCV загружена из: " + libPath);
        } catch (UnsatisfiedLinkError e) {
            System.err.println("Ошибка загрузки библиотеки OpenCV: " + e.getMessage());
            System.exit(1);
        }
    }

    private static String findLibrary(String libName) {
        String[] possiblePaths = {
            libName,
            "lib/" + libName,
            System.getProperty("user.dir") + "/" + libName
        };

        for (String path : possiblePaths) {
            File libFile = new File(path);
            if (libFile.exists()) {
                return libFile.getAbsolutePath();
            }
        }
        throw new UnsatisfiedLinkError("Библиотека " + libName + " не найдена");
    }

    private static Mat loadImage(String imageName) {
        try {
            String imagePath = Main.class.getClassLoader().getResource(imageName).getFile();
            System.out.println("Загрузка изображения из: " + imagePath);
            return Imgcodecs.imread(imagePath);
        } catch (NullPointerException e) {
            System.err.println("Изображение не найдено в ресурсах: " + imageName);
            return new Mat();
        }
    }

    private static void createOutputDirectory() {
        try {
            Path path = Paths.get(OUTPUT_DIR);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
        } catch (IOException e) {
            System.err.println("Ошибка создания директории: " + e.getMessage());
        }
    }

    private static void applySegmentationOperations(Mat image) {
        // Применение заливки
        Point seedPoint = new Point(image.width()/2, image.height()/2);
        Mat flooded = ImageSegmentation.applyRandomFloodFill(image, seedPoint);
        saveImage(flooded, "flood_fill.jpg");

        // Применение пирамид
        Mat pyrDown = ImageSegmentation.applyPyrDown(image, 2);
        saveImage(pyrDown, "pyr_down.jpg");

        Mat pyrUp = ImageSegmentation.applyPyrUp(pyrDown, 2);
        saveImage(pyrUp, "pyr_up.jpg");

        // Получение фрагмента
        Mat fragment = ImageSegmentation.getFragmentAfterPyramid(image, 2);
        saveImage(fragment, "pyramid_fragment.jpg");

        // Идентификация прямоугольников
        int count = ImageSegmentation.identifyRectangles(image, 100, 50);
        System.out.println("Найдено прямоугольников: " + count);
    }

    private static void saveImage(Mat image, String filename) {
        String outputPath = OUTPUT_DIR + filename;
        System.out.println("Сохранение: " + outputPath);
        Imgcodecs.imwrite(outputPath, image);
    }
}