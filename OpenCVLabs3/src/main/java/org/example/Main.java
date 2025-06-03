package org.example;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import java.io.File;

public class Main {
    static {
        String libPath = new File("libopencv_java4120.so").getAbsolutePath();
        System.load(libPath);
    }

    public static void main(String[] args) {
        new File(Constants.OUTPUT_DIR).mkdirs();

        String inputImage = Main.class.getResource("/test_image.jpg").getFile();
        Mat srcImage = Imgcodecs.imread(inputImage);
        
        if (srcImage.empty()) {
            System.err.println("Не удалось загрузить изображение: " + inputImage);
            return;
        }

        System.out.println("Демонстрация трансформаций изображений");
        System.out.println("Исходное изображение: " + inputImage);

        // Операторы Собеля
        System.out.println("\n1. Применение оператора Собеля:");
        Mat sobelX = ImageTransforms.applySobelX(srcImage);
        Mat sobelY = ImageTransforms.applySobelY(srcImage);
        saveAndPrint("sobel_x.jpg", sobelX);
        saveAndPrint("sobel_y.jpg", sobelY);

        // Преобразование Лапласа
        System.out.println("\n2. Применение преобразования Лапласа:");
        Mat laplace = ImageTransforms.applyLaplace(srcImage);
        saveAndPrint("laplace.jpg", laplace);

        // Зеркальное отражение
        System.out.println("\n3. Зеркальное отражение:");
        Mat flipV = ImageTransforms.flipImage(srcImage, 0);
        Mat flipH = ImageTransforms.flipImage(srcImage, 1);
        saveAndPrint("flip_vertical.jpg", flipV);
        saveAndPrint("flip_horizontal.jpg", flipH);

        // Изменение размера
        System.out.println("\n4. Изменение размера (100x100):");
        Mat resized = ImageTransforms.resizeImage(srcImage, 100, 100);
        saveAndPrint("resized.jpg", resized);

        // Вращение изображения
        System.out.println("\n5. Вращение изображения:");
        Mat rotated45 = ImageTransforms.rotateImage(srcImage, 45, false);
        Mat rotated90 = ImageTransforms.rotateImage(srcImage, 90, true);
        saveAndPrint("rotated_45.jpg", rotated45);
        saveAndPrint("rotated_90_crop.jpg", rotated90);

        // Сдвиг изображения
        System.out.println("\n6. Сдвиг изображения (50px по X, 30px по Y):");
        Mat shifted = ImageTransforms.shiftImage(srcImage, 50, 30);
        saveAndPrint("shifted.jpg", shifted);

        // Трансформация перспективы
        System.out.println("\n7. Трансформация перспективы:");
        Mat perspective = applyDemoPerspectiveTransform(srcImage);
        saveAndPrint("perspective.jpg", perspective);

        System.out.println("\nВсе операции успешно выполнены!");
        System.out.println("Результаты сохранены в: " + Constants.OUTPUT_DIR);
    }

    private static Mat applyDemoPerspectiveTransform(Mat src) {
        int w = src.width();
        int h = src.height();
        
        Point[] srcPoints = {
            new Point(0, 0),
            new Point(w, 0),
            new Point(w, h),
            new Point(0, h)
        };
        
        Point[] dstPoints = {
            new Point(w*0.1, h*0.1),
            new Point(w*0.9, h*0.2),
            new Point(w*0.8, h*0.9),
            new Point(w*0.2, h*0.8)
        };
        
        return ImageTransforms.perspectiveTransform(src, srcPoints, dstPoints);
    }

    private static void saveAndPrint(String filename, Mat image) {
        String outputPath = Constants.OUTPUT_DIR + filename;
        Imgcodecs.imwrite(outputPath, image);
        System.out.println("Сохранено: " + filename + 
                         " (размер: " + image.width() + "x" + image.height() + ")");
    }
}