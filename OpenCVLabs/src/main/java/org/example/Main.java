package org.example;

public class Main {
    public static void main(String[] args) {
        try {
            ImageAPI api = new ImageAPI();
            System.out.println("OpenCV loaded successfully!");
            System.out.println("OpenCV version: " + org.opencv.core.Core.VERSION);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}