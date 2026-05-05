package utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.util.Arrays;

public class JsonDataReader {

    /**
     * Generic method to read any JSON array file and return matching scenario object
     */
    public static <T> T getTestData(String filePath,
                                    String scenarioName,
                                    Class<T[]> clazz) {

        LoggerReader.info("Reading JSON Test Data File: " + filePath);
        LoggerReader.info("Looking for scenario: " + scenarioName);

        try {
            ObjectMapper mapper = new ObjectMapper();

            // Read JSON array into POJO array
            T[] dataArray = mapper.readValue(new File(filePath), clazz);

            LoggerReader.info("Total scenarios loaded: " + dataArray.length);

            return Arrays.stream(dataArray)
                    .filter(obj -> getScenarioName(obj)
                            .equalsIgnoreCase(scenarioName.trim()))
                    .findFirst()
                    .orElseThrow(() -> {
                        LoggerReader.error("Scenario NOT FOUND in JSON: " + scenarioName);
                        return new RuntimeException("No test data found for scenario: " + scenarioName);
                    });

        } catch (Exception e) {
            LoggerReader.error("Failed to read JSON file: " + filePath);
            LoggerReader.error("Error: " + e.getMessage());
            throw new RuntimeException("Failed to read JSON file: " + filePath, e);
        }
    }

    /**
     * Helper method to extract scenarioName from any POJO
     */
    private static <T> String getScenarioName(T obj) {
        try {
            return (String) obj.getClass()
                    .getDeclaredField("scenarioName")
                    .get(obj);

        } catch (Exception e) {
            LoggerReader.error("POJO missing 'scenarioName' field: " + obj.getClass().getSimpleName());
            throw new RuntimeException(
                    "POJO must contain 'scenarioName' field: "
                            + obj.getClass().getSimpleName()
            );
        }
    }
}
