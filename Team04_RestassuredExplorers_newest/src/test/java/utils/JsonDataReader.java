package utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import pojo.batchPojo;
import pojo.loginPojo;
import java.io.File;
import java.util.Arrays;
import java.util.List;


public class JsonDataReader {

	private static List<loginPojo> loginDataList;
    private static List<batchPojo> batchDataList;

    static {
        try {
            ObjectMapper mapper = new ObjectMapper();
            File file = new File("src/test/resources/TestData/test_data.json");

            loginDataList = Arrays.asList(
                    mapper.readValue(file, loginPojo[].class)
            );

            batchDataList = Arrays.asList(
                    mapper.readValue(file, batchPojo[].class)
            );

        } catch (Exception e) {
            throw new RuntimeException("Failed to load JSON file", e);
        }
    }
    
    public static loginPojo getLoginData(String scenarioName) {

        return loginDataList.stream()
                .filter(d -> d.scenarioName != null &&
                             d.scenarioName.equalsIgnoreCase(scenarioName.trim()))
                .findFirst()
                .orElseThrow(() ->
                        new RuntimeException("Data not found for scenario: " + scenarioName)
                );
    }
    
    public static batchPojo getBatchData(String scenarioName) {
        return batchDataList.stream()
                .filter(d -> d.scenarioName != null &&
                        d.scenarioName.equalsIgnoreCase(scenarioName.trim()))
                .findFirst()
                .orElseThrow(() ->
                        new RuntimeException("Batch data not found for scenario: " + scenarioName)
                );
    }
    
}
    
