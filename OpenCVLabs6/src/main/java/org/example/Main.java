package org.example;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    private static final String TEST_IMAGE_1 = "schris1.jpg";
    private static final String TEST_IMAGE_2 = "test_image.jpg";
    private static final String OUTPUT_DIR = "output/";

    public static void main(String[] args) {
        loadOpenCVLibrary();
        createOutputDirectory();

        try {
            processImage(TEST_IMAGE_1, "schris1_");
            processImage(TEST_IMAGE_2, "test_image_");
            System.out.println("Обработка завершена. Результаты в: " + 
                            new File(OUTPUT_DIR).getAbsolutePath());
        } catch (Exception e) {
            System.err.println("Ошибка обработки: " + e.getMessage());
        }
    }

    private static void loadOpenCVLibrary() {
        try {
            String libPath = findLibraryPath("libopencv_java4120.so");
            System.load(libPath);
            System.out.println("Библиотека загружена: " + libPath);
        } catch (Exception e) {
            System.err.println("Ошибка загрузки библиотеки: " + e.getMessage());
            System.exit(1);
        }
    }

    private static String findLibraryPath(String libName) {
        String[] searchPaths = {
            libName,
            "lib/" + libName,
            System.getProperty("user.dir") + "/" + libName
        };

        for (String path : searchPaths) {
            File libFile = new File(path);
            if (libFile.exists()) {
                return libFile.getAbsolutePath();
            }
        }
        throw new RuntimeException("Библиотека " + libName + " не найдена");
    }

    private static void createOutputDirectory() {
        try {
            Path path = Paths.get(OUTPUT_DIR);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
        } catch (IOException e) {
            System.err.println("Ошибка создания директории: " + e.getMessage());
            System.exit(1);
        }
    }

    private static void processImage(String imageName, String prefix) {
        try {
            Mat image = loadImage(imageName);
            if (image.empty()) {
                throw new RuntimeException("Не удалось загрузить изображение: " + imageName);
            }

            // Обработка изображения
            applyEdgeDetection(image, prefix + "edges.jpg");
            applyPyramidOperations(image, prefix);
            identifyRectangles(image, prefix + "rectangles.jpg");
        } catch (Exception e) {
            System.err.println("Ошибка обработки изображения " + imageName + ": " + e.getMessage());
        }
    }

    private static Mat loadImage(String imageName) throws Exception {
        String imagePath = Main.class.getClassLoader().getResource(imageName).getFile();
        System.out.println("Загрузка изображения: " + imagePath);
        Mat image = Imgcodecs.imread(imagePath);
        if (image.empty()) {
            throw new Exception("Пустое изображение: " + imageName);
        }
        return image;
    }

    private static void applyEdgeDetection(Mat image, String outputName) {
        Mat edges = EdgeDetection.detectEdges(image);
        saveImage(edges, outputName);
    }

    private static void applyPyramidOperations(Mat image, String prefix) {
        Mat pyrDown = ImagePyramids.applyPyrDown(image, 2);
        saveImage(pyrDown, prefix + "pyr_down.jpg");

        Mat pyrUp = ImagePyramids.applyPyrUp(pyrDown, 2);
        saveImage(pyrUp, prefix + "pyr_up.jpg");

        Mat fragment = ImagePyramids.getFragmentAfterPyramid(image, 2);
        saveImage(fragment, prefix + "fragment_after_pyramid.jpg");
    }

    private static void identifyRectangles(Mat image, String outputName) {
        Mat result = RectangleDetection.identifyRectangles(image, 1000, 1000);
        saveImage(result, outputName);
    }

    private static void saveImage(Mat image, String fileName) {
        String outputPath = OUTPUT_DIR + fileName;
        System.out.println("Сохранение: " + outputPath);
        if (!Imgcodecs.imwrite(outputPath, image)) {
            System.err.println("Ошибка сохранения: " + fileName);
        }
    }
}