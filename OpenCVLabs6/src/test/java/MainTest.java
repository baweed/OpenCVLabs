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

public class MainTest {
    private static final String TEST_IMAGE_1 = "schris1.jpg";
    private static final String TEST_IMAGE_2 = "test_image.jpg";
    private static final String OUTPUT_DIR = "test-output/";

    @BeforeAll
    static void setup() {
        loadLibrary();
        createOutputDirectory();
    }

    private static void loadLibrary() {
        try {
            String libPath = findLibraryPath("libopencv_java4120.so");
            System.load(libPath);
            System.out.println("Библиотека загружена: " + libPath);
        } catch (Exception e) {
            fail("Ошибка загрузки библиотеки: " + e.getMessage());
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
            fail("Ошибка создания директории: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Тест обработки schris1.jpg")
    void testProcessImage1() {
        testImageProcessing(TEST_IMAGE_1, "schris1_");
    }

    @Test
    @DisplayName("Тест обработки test_image.jpg")
    void testProcessImage2() {
        testImageProcessing(TEST_IMAGE_2, "test_image_");
    }

    private void testImageProcessing(String imageName, String prefix) {
        Mat image = loadImage(imageName);
        assertFalse(image.empty(), "Пустое изображение: " + imageName);

        processImage(image, prefix);

        assertFileExists(prefix + "edges.jpg");
        assertFileExists(prefix + "pyr_down.jpg");
        assertFileExists(prefix + "pyr_up.jpg");
        assertFileExists(prefix + "fragment_after_pyramid.jpg");
        assertFileExists(prefix + "rectangles.jpg");
    }

    private Mat loadImage(String imageName) {
        try {
            String imagePath = getClass().getClassLoader().getResource(imageName).getFile();
            System.out.println("Загрузка тестового изображения: " + imagePath);
            Mat image = Imgcodecs.imread(imagePath);
            assertNotNull(image, "Изображение не загружено: " + imageName);
            return image;
        } catch (Exception e) {
            fail("Ошибка загрузки изображения: " + e.getMessage());
            return new Mat();
        }
    }

    private void processImage(Mat image, String prefix) {
        Mat edges = EdgeDetection.detectEdges(image);
        saveImage(edges, prefix + "edges.jpg");

        Mat pyrDown = ImagePyramids.applyPyrDown(image, 2);
        saveImage(pyrDown, prefix + "pyr_down.jpg");

        Mat pyrUp = ImagePyramids.applyPyrUp(pyrDown, 2);
        saveImage(pyrUp, prefix + "pyr_up.jpg");

        Mat fragment = ImagePyramids.getFragmentAfterPyramid(image, 2);
        saveImage(fragment, prefix + "fragment_after_pyramid.jpg");

        Mat rectangles = RectangleDetection.identifyRectangles(image, 20, 20);
        saveImage(rectangles, prefix + "rectangles.jpg");
    }

    private void saveImage(Mat image, String fileName) {
        String outputPath = OUTPUT_DIR + fileName;
        System.out.println("Сохранение тестового результата: " + outputPath);
        assertTrue(Imgcodecs.imwrite(outputPath, image), "Ошибка сохранения: " + fileName);
    }

    private void assertFileExists(String fileName) {
        File file = new File(OUTPUT_DIR + fileName);
        assertTrue(file.exists(), "Файл не найден: " + fileName);
    }
}