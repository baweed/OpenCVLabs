package org.example;

import org.opencv.core.Core;
import java.io.File;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.Constants.OSType;

public class ImageAPI {
    private static final Logger log = LogManager.getLogger(ImageAPI.class);

    public ImageAPI() throws Exception {
        log.info("Checking OS...");
        String libPath;
        switch (getOperatingSystemType()) {
            case LINUX:
                libPath = Config.getProp(Constants.PATH_TO_NATIVE_LIB_LINUX);
                break;
            case WINDOWS:
                libPath = Config.getProp(Constants.PATH_TO_NATIVE_LIB_WIN);
                break;
            case MACOS:
                throw new Exception("MacOS not supported!");
            default:
                throw new Exception("Unsupported OS!");
        }

        File libFile = new File(libPath);
        if (!libFile.exists()) {
            throw new Exception("OpenCV native library not found at: " + libFile.getAbsolutePath());
        }

        System.load(libFile.getAbsolutePath());
        log.info("OpenCV loaded. Version: {}", Core.getVersionString());
    }

    public OSType getOperatingSystemType() {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("linux"))
            return OSType.LINUX;
        if (osName.contains("win"))
            return OSType.WINDOWS;
        if (osName.contains("mac"))
            return OSType.MACOS;
        return OSType.OTHER;
    }

}