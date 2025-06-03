package org.example;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import java.io.File;

public class ImageTransforms {
    static {
        String libPath = new File("libopencv_java4120.so").getAbsolutePath();
        System.load(libPath);
    }

    public static Mat applySobelX(Mat src) {
        Mat gray = new Mat();
        Mat dst = new Mat();
        Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY);
        Imgproc.Sobel(gray, dst, CvType.CV_32F, 1, 0, Constants.SOBEL_KERNEL_SIZE, 
                     Constants.SOBEL_SCALE, Constants.SOBEL_DELTA);
        Core.convertScaleAbs(dst, dst);
        return dst;
    }

    public static Mat applySobelY(Mat src) {
        Mat gray = new Mat();
        Mat dst = new Mat();
        Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY);
        Imgproc.Sobel(gray, dst, CvType.CV_32F, 0, 1, Constants.SOBEL_KERNEL_SIZE, 
                     Constants.SOBEL_SCALE, Constants.SOBEL_DELTA);
        Core.convertScaleAbs(dst, dst);
        return dst;
    }

    public static Mat applyLaplace(Mat src) {
        Mat gray = new Mat();
        Mat dst = new Mat();
        Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY);
        Imgproc.Laplacian(gray, dst, CvType.CV_32F, Constants.LAPLACE_KERNEL_SIZE);
        Core.convertScaleAbs(dst, dst);
        return dst;
    }

    public static Mat flipImage(Mat src, int flipCode) {
        Mat dst = new Mat();
        Core.flip(src, dst, flipCode);
        return dst;
    }

    public static Mat repeatImage(Mat src, int ny, int nx) {
        Mat dst = new Mat();
        Core.repeat(src, ny, nx, dst);
        return dst;
    }

    public static Mat resizeImage(Mat src, int width, int height) {
        Mat dst = new Mat();
        Imgproc.resize(src, dst, new Size(width, height));
        return dst;
    }

    public static Mat rotateImage(Mat src, double angle, boolean crop) {
        Point center = new Point(src.width()/2.0, src.height()/2.0);
        Mat rotMat = Imgproc.getRotationMatrix2D(center, angle, 1.0);
        
        if (!crop) {
            double radians = Math.toRadians(angle);
            double sin = Math.abs(Math.sin(radians));
            double cos = Math.abs(Math.cos(radians));
            int newWidth = (int) (src.width() * cos + src.height() * sin);
            int newHeight = (int) (src.width() * sin + src.height() * cos);
            
            rotMat.put(0, 2, rotMat.get(0, 2)[0] + (newWidth/2 - src.width()/2));
            rotMat.put(1, 2, rotMat.get(1, 2)[0] + (newHeight/2 - src.height()/2));
            
            Mat dst = new Mat();
            Imgproc.warpAffine(src, dst, rotMat, new Size(newWidth, newHeight));
            return dst;
        } else {
            Mat dst = new Mat();
            Imgproc.warpAffine(src, dst, rotMat, new Size(src.width(), src.height()));
            return dst;
        }
    }

    public static Mat shiftImage(Mat src, int dx, int dy) {
        Mat shiftMat = new Mat(2, 3, CvType.CV_64F);
        shiftMat.put(0, 0, 1, 0, dx, 0, 1, dy);
        
        Mat dst = new Mat();
        Imgproc.warpAffine(src, dst, shiftMat, new Size(src.width() + dx, src.height() + dy));
        return dst;
    }

    public static Mat perspectiveTransform(Mat src, Point[] srcPoints, Point[] dstPoints) {
        Mat perspectiveMat = Imgproc.getPerspectiveTransform(
            new MatOfPoint2f(srcPoints), 
            new MatOfPoint2f(dstPoints)
        );
        
        Mat dst = new Mat();
        Imgproc.warpPerspective(src, dst, perspectiveMat, src.size());
        return dst;
    }
}