package org.example;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Config {
    private static final Logger log = LogManager.getLogger(Config.class);
    private static final Properties props = new Properties();

    static {
    try (InputStream in = Config.class.getClassLoader().getResourceAsStream("config.properties")) {
        if (in == null) {
            throw new RuntimeException("config.properties not found in classpath");
        }
        props.load(in);
        log.debug("Properties loaded successfully");
    } catch (IOException e) {
        throw new RuntimeException("Error loading config: " + e.getMessage(), e);
    }
}

    public static String getProp(String key) {
        return props.getProperty(key);
    }
}