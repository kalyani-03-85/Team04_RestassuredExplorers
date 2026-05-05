package utils;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {

    private static Properties properties;
    private static final String propertyFilePath = "src/test/resources/ConfigReader/config.properties";

    // Static block → loads config automatically when class is used
    static {
        try {
            properties = new Properties();
            FileInputStream fis = new FileInputStream(propertyFilePath);
            properties.load(fis);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config.properties file", e);
        }
    }

    // Get value by key
    public static String getKey(String key) {
        String value = properties.getProperty(key);

        if (value == null) {
            throw new RuntimeException("Key not found in config: " + key);
        }

        return value.trim();
    }
}