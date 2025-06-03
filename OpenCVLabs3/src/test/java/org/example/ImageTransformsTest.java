package org.example;

import org.junit.jupiter.api.*;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class ImageTransformsTest {
    private static final String TEST_IMAGE = "test_image.jpg";
    private static final String OUTPUT_DIR = "output_test/";

    private Mat testImage;

    @BeforeAll
    static void loadLibrary() {
        String libPath = new File("libopencv_java4120.so").getAbsolutePath();
        System.load(libPath);
        new File(OUTPUT_DIR).mkdirs();
    }

    @BeforeEach
    void setUp() {
        String imagePath = ImageTransformsTest.class.getResource("/" + TEST_IMAGE).getFile();
        testImage = Imgcodecs.imread(imagePath);
        if (testImage.empty()) {
            fail("Не удалось загрузить тестовое изображение");
        }
    }

    @Test
    @DisplayName("Тест оператора Собеля")
    void testSobelOperators() {
        Mat sobelX = ImageTransforms.applySobelX(testImage);
        Mat sobelY = ImageTransforms.applySobelY(testImage);
        
        assertFalse(sobelX.empty());
        assertFalse(sobelY.empty());
        
        String outputPathX = OUTPUT_DIR + "sobel_x.jpg";
        String outputPathY = OUTPUT_DIR + "sobel_y.jpg";
        
        assertTrue(Imgcodecs.imwrite(outputPathX, sobelX));
        assertTrue(Imgcodecs.imwrite(outputPathY, sobelY));
    }

    @Test
    @DisplayName("Тест преобразования Лапласа")
    void testLaplace() {
        Mat laplace = ImageTransforms.applyLaplace(testImage);
        assertFalse(laplace.empty());
        
        String outputPath = OUTPUT_DIR + "laplace.jpg";
        assertTrue(Imgcodecs.imwrite(outputPath, laplace));
    }

    @Test
    @DisplayName("Тест зеркального отражения")
    void testFlip() {
        Mat flipV = ImageTransforms.flipImage(testImage, 0);
        Mat flipH = ImageTransforms.flipImage(testImage, 1);
        
        assertFalse(flipV.empty());
        assertFalse(flipH.empty());
        
        String outputPathV = OUTPUT_DIR + "flip_vertical.jpg";
        String outputPathH = OUTPUT_DIR + "flip_horizontal.jpg";
        
        assertTrue(Imgcodecs.imwrite(outputPathV, flipV));
        assertTrue(Imgcodecs.imwrite(outputPathH, flipH));
    }

    @Test
    @DisplayName("Тест вращения изображения")
    void testRotation() {
        Mat rotatedWithCrop = ImageTransforms.rotateImage(testImage, 45, true);
        Mat rotatedWithoutCrop = ImageTransforms.rotateImage(testImage, 45, false);
        
        assertFalse(rotatedWithCrop.empty());
        assertFalse(rotatedWithoutCrop.empty());
        
        String outputPathCrop = OUTPUT_DIR + "rotated_crop.jpg";
        String outputPathFull = OUTPUT_DIR + "rotated_full.jpg";
        
        assertTrue(Imgcodecs.imwrite(outputPathCrop, rotatedWithCrop));
        assertTrue(Imgcodecs.imwrite(outputPathFull, rotatedWithoutCrop));
    }

    @Test
    @DisplayName("Тест трансформации перспективы")
    void testPerspectiveTransform() {
        int w = testImage.width();
        int h = testImage.height();
        
        Point[] srcPoints = {
            new Point(0, 0),
            new Point(w, 0),
            new Point(w, h),
            new Point(0, h)
        };
        
        Point[] dstPoints = {
            new Point(0 + 50, 0 + 50),
            new Point(w - 50, 0 + 100),
            new Point(w - 100, h - 50),
            new Point(0 + 100, h - 100)
        };
        
        Mat transformed = ImageTransforms.perspectiveTransform(testImage, srcPoints, dstPoints);
        assertFalse(transformed.empty());
        
        String outputPath = OUTPUT_DIR + "perspective.jpg";
        assertTrue(Imgcodecs.imwrite(outputPath, transformed));
    }
}