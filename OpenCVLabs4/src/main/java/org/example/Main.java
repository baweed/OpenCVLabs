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

        // Загрузка тестового изображения
        Mat testImage = loadImage(TEST_IMAGE);
        if (testImage.empty()) {
            System.err.println("Не удалось загрузить тестовое изображение");
            return;
        }

        createOutputDirectory();
        applyFilters(testImage);
        applyMorphologyOperations(testImage);

        System.out.println("Обработка завершена. Результаты сохранены в директории: " + new File(OUTPUT_DIR).getAbsolutePath());
    }

    private static void loadOpenCVLibrary() {
        try {
            // Поиск библиотеки в нескольких возможных местах
            String[] possiblePaths = {
                "libopencv_java4120.so",
                "lib/libopencv_java4120.so",
                "../libopencv_java4120.so"
            };

            for (String path : possiblePaths) {
                File libFile = new File(path);
                if (libFile.exists()) {
                    System.load(libFile.getAbsolutePath());
                    System.out.println("Библиотека OpenCV загружена из: " + libFile.getAbsolutePath());
                    return;
                }
            }
            throw new UnsatisfiedLinkError("Библиотека OpenCV не найдена");
        } catch (UnsatisfiedLinkError e) {
            System.err.println("Ошибка загрузки библиотеки OpenCV: " + e.getMessage());
            System.exit(1);
        }
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
                System.out.println("Создана директория для результатов: " + path.toAbsolutePath());
            }
        } catch (IOException e) {
            System.err.println("Ошибка создания директории для результатов: " + e.getMessage());
        }
    }

    private static void applyFilters(Mat image) {
        // Усредняющий фильтр
        Mat blurred = ImageFilters.applyBlur(image, 5);
        saveImage(blurred, "blur_5.jpg");

        // Гауссовский фильтр
        Mat gaussianBlurred = ImageFilters.applyGaussianBlur(image, 5);
        saveImage(gaussianBlurred, "gaussian_blur_5.jpg");

        // Медианный фильтр
        Mat medianBlurred = ImageFilters.applyMedianBlur(image, 5);
        saveImage(medianBlurred, "median_blur_5.jpg");

        // Двусторонний фильтр
        Mat bilateralFiltered = ImageFilters.applyBilateralFilter(image, 9, 75, 75);
        saveImage(bilateralFiltered, "bilateral_filter.jpg");
    }

    private static void applyMorphologyOperations(Mat image) {
        // Эрозия
        Mat eroded = MorphologyOperations.applyErosion(image, 5, Imgproc.MORPH_RECT);
        saveImage(eroded, "erosion_5.jpg");

        // Расширение
        Mat dilated = MorphologyOperations.applyDilation(image, 5, Imgproc.MORPH_RECT);
        saveImage(dilated, "dilation_5.jpg");
    }

    private static void saveImage(Mat image, String fileName) {
        String outputPath = OUTPUT_DIR + fileName;
        System.out.println("Сохранение изображения в: " + outputPath);
        boolean saved = Imgcodecs.imwrite(outputPath, image);
        if (!saved) {
            System.err.println("Ошибка сохранения изображения: " + fileName);
        }
    }
}