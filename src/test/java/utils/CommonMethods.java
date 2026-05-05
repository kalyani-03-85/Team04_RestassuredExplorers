package utils;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import pojo.ProgramPojo;
import utils.LoggerReader;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import context.scenarioReader;

public class CommonMethods {

    private scenarioReader context;
    public static String baseURL = ConfigReader.getKey("baseURL");
    public static String overrideBaseURL = null;

    public CommonMethods(scenarioReader context) {
        this.context = context;
    }

    // ============================================================
    // ⭐ Store & Retrieve Test Data
    // ============================================================
    public <T> void setTestData(T data) {
        LoggerReader.info("Storing test data: " + data);
        context.set("testData", data);
    }

    public <T> T getTestData() {
        return context.get("testData");
    }

    // ============================================================
    // ⭐ UNIVERSAL SEND REQUEST METHOD
    // ============================================================
    public void sendRequest(String method, String endpoint, Object body, Map<String, String> headers) {

        String finalBaseURI = (overrideBaseURL != null) ? overrideBaseURL : baseURL;

        LoggerReader.info("Preparing request:");
        LoggerReader.info("BaseURI: " + finalBaseURI);
        LoggerReader.info("Method: " + method);
        LoggerReader.info("Endpoint: " + endpoint);
        LoggerReader.info("Headers: " + headers);
        LoggerReader.info("Body: " + body);

        RequestSpecification request = RestAssured.given()
                .log().all()
                .baseUri(finalBaseURI);

        // Apply headers
        if (headers != null) {
            request.headers(headers);

            if (!headers.containsKey("Content-Type") && !method.equalsIgnoreCase("GET")) {
                request.contentType(ContentType.JSON);
            }
        } else {
            if (!method.equalsIgnoreCase("GET")) {
                request.contentType(ContentType.JSON);
            }
        }

        // Add body for non‑GET
        if (!method.equalsIgnoreCase("GET") && body != null) {
            request.body(body);
        }

        // Send request
        Response response = request.when().request(method.toUpperCase(), endpoint);

        LoggerReader.info("Response Status: " + response.getStatusCode());
        LoggerReader.info("Response Body: " + response.getBody().asString());

        context.set("response", response);

        // Store programId for POST success
        if (method.equalsIgnoreCase("POST") && response.getStatusCode() == 201) {
            try {
                Integer programId = response.jsonPath().getInt("programId");
                if (programId != null) {
                    globalvariables.programId = programId;
                    LoggerReader.info("Stored Program ID: " + programId);
                }
            } catch (Exception e) {
                LoggerReader.error("Failed to extract programId: " + e.getMessage());
            }
        }

        overrideBaseURL = null;
    }

    // ============================================================
    // ⭐ BaseURI Override
    // ============================================================
    public void setBaseURI(String newBaseURI) {
        overrideBaseURL = newBaseURI;
        LoggerReader.warn("Override BaseURI SET: " + newBaseURI);
    }

    // ============================================================
    // ⭐ Get Response
    // ============================================================
    public Response getResponse() {
        return context.get("response");
    }

    // ============================================================
    // ⭐ VALIDATIONS
    // ============================================================
    public void validateStatusCode(int expectedCode) {
        LoggerReader.info("Validating Status Code: " + expectedCode);
        getResponse().then().log().ifValidationFails().statusCode(expectedCode);
    }

 // ============================================================
 // ⭐ VALIDATE STATUS LINE
 // ============================================================
 public void validateStatusLine(String expectedLine) {

     if (expectedLine == null || expectedLine.trim().isEmpty()) {
         LoggerReader.warn("Skipping status line validation (empty expected value)");
         return;
     }

     LoggerReader.info("Validating Status Line contains: " + expectedLine);

     getResponse()
             .then()
             .log().ifValidationFails()
             .statusLine(org.hamcrest.Matchers.containsStringIgnoringCase(expectedLine));
 }

 // ============================================================
 // ⭐ VALIDATE CONTENT-TYPE
 // ============================================================
 public void validateContentType() {

     LoggerReader.info("Validating Content-Type is application/json");

     getResponse()
             .then()
             .log().ifValidationFails()
             .header("Content-Type", org.hamcrest.Matchers.containsString("application/json"));
 }

    public void validateErrorMessage(String expectedMessage) {

        String body = getResponse().getBody().asString().trim();
        LoggerReader.info("Validating error message. Expected: " + expectedMessage);
        LoggerReader.info("Actual Body: " + body);

        if (body.isEmpty()) {
            throw new AssertionError("Error response body is empty — cannot validate error message");
        }

        String actualMessage = null;

        try {
            actualMessage = getResponse().jsonPath().getString("message");
        } catch (Exception ignored) {}

        if (actualMessage != null && !actualMessage.isEmpty()) {
            if (!actualMessage.toLowerCase().contains(expectedMessage.toLowerCase())) {
                throw new AssertionError("Expected: " + expectedMessage + " but found: " + actualMessage);
            }
            return;
        }

        if (!body.toLowerCase().contains(expectedMessage.toLowerCase())) {
            throw new AssertionError("Expected: " + expectedMessage + " but body was: " + body);
        }
    }

    // ============================================================
    // ⭐ TOKEN HANDLING
    // ============================================================
    public void storeToken(String jsonPath) {

        String token = getResponse().jsonPath().getString(jsonPath);

        if (token == null || token.isEmpty()) {
            LoggerReader.error("Token not found in login response");
            throw new RuntimeException("Token not found in login response");
        }

        globalvariables.token = token;
        LoggerReader.info("Token stored globally: " + token);
    }

    // ============================================================
    // ⭐ Program ID & Name Storage
    // ============================================================
    public void storeProgramId() {
        try {
            Integer programId = getResponse().jsonPath().getInt("programId");
            if (programId != null && programId != 0) {
                globalvariables.programId = programId;
                LoggerReader.info("Stored Program ID: " + programId);
            }
        } catch (Exception e) {
            LoggerReader.error("programId not found in response");
        }
    }

    public void storeProgramName(String programName) {
        if (programName == null || programName.isEmpty()) {
            throw new RuntimeException("Program name is null or empty — cannot store");
        }
        globalvariables.programName = programName;
        LoggerReader.info("Stored Program Name: " + programName);
    }

    // ============================================================
    // ⭐ AUTH HEADER
    // ============================================================
    public Map<String, String> getAuthHeader(String method) {

        if (globalvariables.token == null) {
            LoggerReader.error("Token missing. Login not executed.");
            throw new RuntimeException("Token missing. Login not executed.");
        }

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + globalvariables.token);
        headers.put("Content-Type", "application/json");

        LoggerReader.info("Generated Auth Header");
        return headers;
    }

    // ============================================================
    // ⭐ RANDOM NAME GENERATOR
    // ============================================================
    public static class NameGenerator {

        public static String generateProgramName() {
            String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
            Random random = new Random();
            StringBuilder name = new StringBuilder();

            for (int i = 0; i < 8; i++) {
                name.append(chars.charAt(random.nextInt(chars.length())));
            }

            return name.toString();
        }
    }
}
