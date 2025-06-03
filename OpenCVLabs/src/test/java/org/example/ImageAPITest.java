package org.example;

import org.junit.jupiter.api.Test;
import org.opencv.core.Core;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import static org.junit.jupiter.api.Assertions.*;

public class ImageAPITest {
    private static final Logger log = LogManager.getLogger(ImageAPITest.class);

    @Test
    public void testOpenCVInitialization() {
        try {
            log.info("Checking OS.....");
            
            // Создаю экземпляр API
            ImageAPI api = new ImageAPI();
            
            // Версия OpenCV
            String version = Core.getVersionString();
            
            // Вывожу информацию согласно Лабораторной
            System.out.println("OS version - " + api.getOperatingSystemType());
            System.out.println("Open CV version - " + version);
            
            // Проверяка на версию
            assertNotNull(version);
            assertFalse(version.isEmpty());
            
        } catch (Exception e) {
            fail("OpenCV initialization failed: " + e.getMessage());
        }
    }
}