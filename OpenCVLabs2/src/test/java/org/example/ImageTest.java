package org.example;

import org.junit.jupiter.api.*;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class ImageTest {
    private static final String TEST_IMAGE = "src/test/resources/test_image.jpg";
    private static final String OUTPUT_DIR = "target/test-output/";

    @BeforeAll
    static void init() {
        // Полный абсолютный путь к библиотеке
        String libPath = Paths.get("").toAbsolutePath()
                          .resolve("libopencv_java4120.so")
                          .toString();
        
        try {
            System.load(libPath);
        } catch (UnsatisfiedLinkError e) {
            fail("Не удалось загрузить OpenCV библиотеку по пути: " + libPath + 
                "\nОшибка: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Тест обработки изображения с обнулением каналов")
    void testImageProcessing() throws Exception {
        // Загрузка изображения
        Mat originalImage = loadImage(TEST_IMAGE);
        assertNotNull(originalImage, "Изображение не загружено");
        assertFalse(originalImage.empty(), "Загружено пустое изображение");

        // Отображение оригинала
        showImage(originalImage, "Original Image");
        Thread.sleep(2000);

        // Обработка и отображение для каждого канала
        String[] channelNames = {"Blue (B) channel zeroed", 
                               "Green (G) channel zeroed", 
                               "Red (R) channel zeroed"};

        for (int channel = 0; channel < 3; channel++) {
            Mat modified = originalImage.clone();
            zeroChannel(modified, channel);
            
            // Отображение результата
            showImage(modified, channelNames[channel]);
            Thread.sleep(2000);
            
            // Сохранение результата
            saveImage(modified, OUTPUT_DIR + "zeroed_channel_" + channel + ".jpg");
        }
    }

    private Mat loadImage(String path) {
        Mat image = Imgcodecs.imread(path, Imgcodecs.IMREAD_COLOR);
        if (image.empty()) {
            throw new IllegalArgumentException("Не удалось загрузить изображение: " + path);
        }
        return image;
    }

    private void zeroChannel(Mat image, int channelIndex) {
        byte[] buffer = new byte[(int)(image.total() * image.elemSize())];
        image.get(0, 0, buffer);
        
        for (int i = channelIndex; i < buffer.length; i += 3) {
            buffer[i] = 0; // Обнуляю указанный канал
        }
        
        image.put(0, 0, buffer);
    }

    private void showImage(Mat mat, String title) {
        BufferedImage image = new BufferedImage(mat.cols(), mat.rows(), 
            BufferedImage.TYPE_3BYTE_BGR);
        
        byte[] data = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        mat.get(0, 0, data);
        
        JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.add(new JLabel(new ImageIcon(image)));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void saveImage(Mat image, String path) {
        Imgcodecs.imwrite(path, image);
    }

    @AfterAll
    static void cleanup() {
        // Закрывание всех окок после тестов
        for (Window window : Window.getWindows()) {
            if (window instanceof JFrame) {
                window.dispose();
            }
        }
    }
}