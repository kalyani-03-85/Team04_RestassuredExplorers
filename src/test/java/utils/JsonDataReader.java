package utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.List;
import java.util.Map;

public class JsonDataReader {

    private static List<Map<String, Object>> testData;

    public static List<Map<String, Object>> readJson(String filePath) {
        if (testData == null) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                testData = mapper.readValue(
                        new File(filePath),
                        new TypeReference<List<Map<String, Object>>>() {}
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return testData;
    }

    public static Map<String, Object> getTestCaseById(String filePath, String testCaseId) {
        List<Map<String, Object>> allData = readJson(filePath);
        if (allData == null) {
            throw new RuntimeException("Test data list is null. File may not be loaded correctly.");
        }
        for (Map<String, Object> data : allData) {
            Object id = data.get("testCaseId");
            if (id != null && testCaseId.equals(id.toString())) {
                return data;
            }
        }
        System.err.println("Available test case IDs in file:");
        for (Map<String, Object> d : allData) {
            System.err.println(d.get("testCaseId"));
        }
        throw new RuntimeException("Test case with ID " + testCaseId + " not found in file " + filePath);
    }
        /*for (Map<String, Object> data : allData) {
            if (testCaseId.equals(data.get("testCaseId"))) {
                return data;
            }
        }
        throw new RuntimeException("Test case with ID " + testCaseId + " not found.");
    }*/
}