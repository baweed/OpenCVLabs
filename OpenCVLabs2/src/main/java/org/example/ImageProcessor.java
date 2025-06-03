package org.example;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.nio.file.Paths;
import java.util.Locale;

public class ImageProcessor {
    private static final int BGR_CHANNELS = 3;

    // Метод для определения ОС
    public static Constants.OSType getOperatingSystemType() {
        final String osName = System.getProperty("os.name", "generic")
                .toLowerCase(Locale.ENGLISH);

        if (osName.contains("mac") || osName.contains("darwin")) {
            return Constants.OSType.MACOS;
        }
        if (osName.contains("win")) {
            return Constants.OSType.WINDOWS;
        }
        if (osName.contains("nux")) {
            return Constants.OSType.LINUX;
        }

        return Constants.OSType.OTHER;
    }

    public static void initOpenCV() {
    final Constants.OSType osType = getOperatingSystemType();
    final String libraryName;
    
    switch (osType) {
        case LINUX:
            libraryName = Constants.PATH_TO_NATIVE_LIB_LINUX;
            break;
        case WINDOWS:
            libraryName = Constants.PATH_TO_NATIVE_LIB_WIN;
            break;
        default:
            throw new UnsupportedOperationException(
                    String.format("OS %s is not supported", osType));
    }
    
    try {
        // Получаю абсолютный путь к файлу в директории проекта
        String absolutePath = Paths.get("").toAbsolutePath()
                                .resolve(libraryName)
                                .toString();
        
        System.load(absolutePath);
    } catch (UnsatisfiedLinkError e) {
        throw new RuntimeException(
                String.format("Failed to load OpenCV library '%s'. Error: %s",
                        libraryName, e.getMessage()),
                e);
    }
}

    // Загрузка изображения с проверкой
    public static Mat loadImage(String filePath) {
        Mat image = Imgcodecs.imread(filePath);
        if (image.empty()) {
            throw new IllegalArgumentException("Could not load image: " + filePath);
        }
        return image;
    }

    // Обнуления канала
    public static Mat zeroChannel(Mat image, int channelIndex) {
        if (image == null || image.empty()) {
            throw new IllegalArgumentException("Image is null or empty");
        }
        if (channelIndex < 0 || channelIndex >= image.channels()) {
            throw new IllegalArgumentException(
                    "Invalid channel index. Must be between 0 and " + (image.channels() - 1));
        }

        byte[] buffer = new byte[(int) (image.total() * image.elemSize())];
        image.get(0, 0, buffer);

        for (int i = channelIndex; i < buffer.length; i += image.channels()) {
            buffer[i] = 0;
        }

        image.put(0, 0, buffer);
        return image;
    }

    // Отображение изображения
    public static void showImage(Mat mat, String windowTitle) {
        if (mat == null || mat.empty()) {
            throw new IllegalArgumentException("Mat is null or empty");
        }

        int type = mat.channels() > 1 ? BufferedImage.TYPE_3BYTE_BGR : BufferedImage.TYPE_BYTE_GRAY;
        BufferedImage image = new BufferedImage(mat.cols(), mat.rows(), type);

        byte[] data = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        mat.get(0, 0, data);

        JFrame frame = new JFrame(windowTitle);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.add(new JLabel(new ImageIcon(image)));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // Демонстрация работы
    public static void demonstrateChannelEffects(String imagePath) {
        try {
            // Загрузка изображения
            Mat original = loadImage(imagePath);
            showImage(original, "Original Image");

            // Создаю копии для каждого канала
            String[] channelNames = { "Blue", "Green", "Red" };
            for (int i = 0; i < original.channels(); i++) {
                Mat modified = original.clone();
                zeroChannel(modified, i);
                showImage(modified, channelNames[i] + " Channel Zeroed");

                // Сохраняю результат
                String outputPath = Paths.get(Constants.OUTPUT_IMAGES_DIR,
                        "zeroed_" + channelNames[i].toLowerCase() + "_channel.jpg").toString();
                saveImage(modified, outputPath);
            }
        } catch (Exception e) {
            System.err.println("Error in demonstrateChannelEffects: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Сохранение изображения
    public static boolean saveImage(Mat image, String filePath) {
        return Imgcodecs.imwrite(filePath, image);
    }
}