package org.example;

public class Constants {
    public static final String PATH_TO_NATIVE_LIB_LINUX = "libopencv_java4120.so";
    public static final String PATH_TO_NATIVE_LIB_WIN = "libopencv_java4120.dll";
    public static final String SOURCE_IMAGES_DIR = "src/main/resources";
    public static final String OUTPUT_IMAGES_DIR = "src/main/resources";
    
    public enum OSType {
        LINUX, WINDOWS, MACOS, OTHER
    }
}