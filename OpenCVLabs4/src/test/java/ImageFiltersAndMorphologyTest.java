package org.example;

import org.junit.jupiter.api.*;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class ImageFiltersAndMorphologyTest {
    private static final String TEST_IMAGE = "test_image.jpg";
    private static final String TEST_IMAGE_2 = "294.jpg";
    private static final String OUTPUT_DIR = "test-output/";
    
    private Mat testImage;
    private Mat testImage2;

    @BeforeAll
    static void loadLibrary() {
        try {
            // Поиск библиотеки
            String[] possiblePaths = {
                "libopencv_java4120.so",
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
            throw e;
        }
    }

    @BeforeAll
    static void createOutputDirectory() {
        try {
            Path path = Paths.get(OUTPUT_DIR);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
                System.out.println("Создана директория для тестовых результатов: " + path.toAbsolutePath());
            }
        } catch (IOException e) {
            System.err.println("Ошибка создания директории для тестовых результатов: " + e.getMessage());
        }
    }

    @BeforeEach
    void setUp() {
        testImage = loadImage(TEST_IMAGE);
        testImage2 = loadImage(TEST_IMAGE_2);
        
        if (testImage.empty() || testImage2.empty()) {
            fail("Не удалось загрузить тестовые изображения");
        }
    }

    @Test
    @DisplayName("Тест фильтрации с разными размерами ядра")
    void testFiltersWithDifferentKernelSizes() {
        int[] kernelSizes = {3, 5, 7}; // Размеры ядра

        for (int size : kernelSizes) {
            Mat blurred = ImageFilters.applyBlur(testImage, size);
            saveImage(blurred, "blur_" + size + ".jpg");

            Mat gaussianBlurred = ImageFilters.applyGaussianBlur(testImage, size);
            saveImage(gaussianBlurred, "gaussian_blur_" + size + ".jpg");

            Mat medianBlurred = ImageFilters.applyMedianBlur(testImage, size);
            saveImage(medianBlurred, "median_blur_" + size + ".jpg");
        }

        Mat bilateralFiltered = ImageFilters.applyBilateralFilter(testImage, 9, 75, 75);
        saveImage(bilateralFiltered, "bilateral_filter.jpg");
    }

    @Test
    @DisplayName("Тест морфологических операций с разными формами и размерами ядра")
    void testMorphologyOperations() {
        int[] kernelSizes = {3, 5, 7, 9, 13, 15};
        int[] shapes = {
            Imgproc.MORPH_RECT,
            Imgproc.MORPH_ELLIPSE,

        };

        for (int shape : shapes) {
            for (int size : kernelSizes) {
                // Эрозия
                Mat eroded = MorphologyOperations.applyErosion(testImage2, size, shape);
                saveImage(eroded, getMorphologyFileName("erode", shape, size));

                // Расширение
                Mat dilated = MorphologyOperations.applyDilation(testImage2, size, shape);
                saveImage(dilated, getMorphologyFileName("dilate", shape, size));
            }
        }
    }

    // Загрузка изображения
    private Mat loadImage(String imageName) {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(imageName).getFile());
        String imagePath = file.getAbsolutePath();
        System.out.println("Loading image from: " + imagePath);
        return Imgcodecs.imread(imagePath);
    }

    // Сохранение изображения
    private void saveImage(Mat image, String fileName) {
        String outputPath = OUTPUT_DIR + fileName;
        System.out.println("Saving image to: " + outputPath);
        boolean saved = Imgcodecs.imwrite(outputPath, image);
        assertTrue(saved, "Failed to save image: " + fileName);
    }

    // Генерация имени файла для морфологических операций
    private String getMorphologyFileName(String operation, int shape, int size) {
        String shapeName;
        switch (shape) {
            case Imgproc.MORPH_RECT:
                shapeName = "rect";
                break;
            case Imgproc.MORPH_ELLIPSE:
                shapeName = "ellipse";
                break;
            default:
                shapeName = "unknown";
                break;
        }
        return operation + "_" + shapeName + "_" + size + ".jpg";
    }
}