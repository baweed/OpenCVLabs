package org.example;

import org.junit.jupiter.api.*;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class ImageSegmentationTest {
    private static final String TEST_IMAGE = "test_image.jpg";
    private static final String OUTPUT_DIR = "test-output/";
    
    private Mat testImage;

    @BeforeAll
    static void loadLibrary() {
        try {
            String libPath = findLibrary("libopencv_java4120.so");
            System.load(libPath);
            System.out.println("Библиотека загружена: " + libPath);
        } catch (UnsatisfiedLinkError e) {
            fail("Ошибка загрузки библиотеки: " + e.getMessage());
        }

        createOutputDirectory();
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
        throw new UnsatisfiedLinkError("Библиотека не найдена: " + libName);
    }

    private static void createOutputDirectory() {
        try {
            Path path = Paths.get(OUTPUT_DIR);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
        } catch (IOException e) {
            fail("Ошибка создания директории: " + e.getMessage());
        }
    }

    @BeforeEach
    void setUp() {
        String imagePath = getClass().getClassLoader().getResource(TEST_IMAGE).getFile();
        testImage = Imgcodecs.imread(imagePath);
        if (testImage.empty()) {
            fail("Не удалось загрузить тестовое изображение");
        }
    }

    @Test
    @DisplayName("Тест операций сегментации")
    void testSegmentationOperations() {
        // Тест заливки
        Point seedPoint = new Point(10, 10);
        Mat flooded = ImageSegmentation.applyRandomFloodFill(testImage, seedPoint);
        assertFalse(flooded.empty());
        saveImage(flooded, "flood_fill_test.jpg");

        // Тест пирамид
        Mat pyrDown = ImageSegmentation.applyPyrDown(testImage, 2);
        assertFalse(pyrDown.empty());
        saveImage(pyrDown, "pyr_down_test.jpg");

        Mat pyrUp = ImageSegmentation.applyPyrUp(pyrDown, 2);
        assertFalse(pyrUp.empty());
        saveImage(pyrUp, "pyr_up_test.jpg");

        // Тест фрагмента
        Mat fragment = ImageSegmentation.getFragmentAfterPyramid(testImage, 2);
        assertFalse(fragment.empty());
        saveImage(fragment, "fragment_test.jpg");

        // Тест прямоугольников
        int count = ImageSegmentation.identifyRectangles(testImage, 100, 50);
        assertTrue(count >= 0);
    }

    private void saveImage(Mat image, String filename) {
        String outputPath = OUTPUT_DIR + filename;
        assertTrue(Imgcodecs.imwrite(outputPath, image));
    }
}