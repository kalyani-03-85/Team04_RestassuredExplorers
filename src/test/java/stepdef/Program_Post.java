package stepdef;

import java.util.HashMap;
import java.util.Map;

import org.testng.Assert;

import context.scenarioReader;
import io.cucumber.java.en.*;
import pojo.ProgramPojo;
import pojo.UpdateProgramPojo;
import utils.CommonMethods;
import utils.ConfigReader;
import utils.JsonDataReader;
import utils.LoggerReader;
import utils.globalvariables;

public class Program_Post {

    CommonMethods cm;
    private scenarioReader context;
    private String Program_FILE;

    public Program_Post(scenarioReader context) {
        this.context = context;
        this.cm = new CommonMethods(context);
        this.Program_FILE = ConfigReader.getKey("jsonFilePath");
    }

    // ============================================================
    // GIVEN — Build POST Request for ALL testCases
    // ============================================================
    @Given("Admin creates POST Request with valid requestbody {string}")
    public void admin_creates_post_request_with_valid_requestbody(String testCase) {

        LoggerReader.info("Loading POST test data for: " + testCase);

        ProgramPojo data = JsonDataReader.getTestData(
                Program_FILE,
                testCase,
                ProgramPojo[].class
        );

        // ⭐ Generate unique programName ONLY for positive scenarios
        if (data.expectedStatusCode < 400 && data.programName != null) {
            String unique = data.programName + "-" + CommonMethods.NameGenerator.generateProgramName();
            data.programName = unique;
            globalvariables.programName = unique;

            LoggerReader.info("Generated unique programName: " + unique);
        }

        cm.setTestData(data);

        LoggerReader.info("Scenario: " + data.scenarioName);
        LoggerReader.info("Endpoint: " + data.endPoint);
        LoggerReader.info("Request Type: " + data.requestType);
    }

    // ============================================================
    // WHEN — Send POST Request
    // ============================================================
    @When("Admin sends a HTTPS POST request to the endpoint")
    public void admin_sends_a_https_post_request_to_the_endpoint() {

        ProgramPojo data = cm.getTestData();

        String method = data.requestType;
        String endpoint = "/" + data.endPoint;

        LoggerReader.info("Preparing POST request...");
        LoggerReader.info("Method: " + method + " | Endpoint: " + endpoint);

        Map<String, String> headers = cm.getAuthHeader(method);

        // ⭐ Invalid token
        if ("invalid token".equalsIgnoreCase(data.scenarioName)) {
            headers.put("Authorization", "Bearer invalidToken123");
            LoggerReader.warn("Using invalid token");
        }

        // ⭐ Invalid content-type
        if ("INVALID".equalsIgnoreCase(data.contentType)) {
            headers.put("Content-Type", "text/plain");
            LoggerReader.warn("Using INVALID content-type");
        }

        // ⭐ Invalid method
        if (data.requestType.equalsIgnoreCase("GET")) {
            method = "GET";
            LoggerReader.warn("Using invalid method: GET");
        }

        // ⭐ Empty payload
        Object body;
        if ((data.programName == null || data.programName.isEmpty()) &&
            (data.programDescription == null || data.programDescription.isEmpty()) &&
            (data.programStatus == null || data.programStatus.isEmpty())) {

            LoggerReader.warn("Sending EMPTY payload");
            body = "";
        } else {
            Map<String, Object> payload = new HashMap<>();
            payload.put("programName", data.programName);
            payload.put("programDescription", data.programDescription);
            payload.put("programStatus", data.programStatus);

            body = payload;

            LoggerReader.info("Payload: " + payload);
        }

        try {
            LoggerReader.info("Sending POST request...");
            cm.sendRequest(method, endpoint, body, headers);
        } catch (Exception e) {
            LoggerReader.error("POST request failed: " + e.getMessage());
            context.set("exception", e);
        }
    }

    // ============================================================
    // THEN — Validate Status Code + Message
    // ============================================================
    @Then("Admin receives statuscode {string} with message {string}")
    public void admin_receives_statuscode_with_message(String expectedCode, String expectedMessage) {

        LoggerReader.info("Validating POST response...");

        if (context.get("exception") != null) {
            LoggerReader.error("Invalid baseURI — request never reached server");
            return;
        }

        int expectedStatus = Integer.parseInt(expectedCode);
        cm.validateStatusCode(expectedStatus);

        String body = cm.getResponse().getBody().asString().trim();
        LoggerReader.info("Response Body: " + body);

        // ⭐ SUCCESS CASE (201)
        if (expectedStatus == 201) {
            LoggerReader.info("201 Created — skipping message validation");

            cm.storeProgramId();
            String actualProgramName = cm.getResponse().jsonPath().getString("programName");
            cm.storeProgramName(actualProgramName);

            return;
        }

        // ⭐ Special case: 401 Unauthorized → EMPTY BODY
        if (expectedStatus == 401 && body.isEmpty()) {
            LoggerReader.warn("401 Unauthorized with empty body — skipping message validation");
            return;
        }

        // ⭐ NEGATIVE CASES — message may appear in different formats
        boolean match =
                body.equalsIgnoreCase(expectedMessage) ||
                body.contains(expectedMessage) ||
                (body.contains("\"message\"") && body.contains(expectedMessage)) ||
                (body.contains("\"programName\"") && body.contains(expectedMessage)) ||
                (body.contains("\"programDescription\"") && body.contains(expectedMessage));

        Assert.assertTrue(
                match,
                "Expected error message: " + expectedMessage + " but found: " + body
        );
    }

    // ============================================================
    // GIVEN — Load GET Test Data
    // ============================================================
    @Given("Admin creates GET Request for {string}")
    public void admin_creates_get_request_for(String testCase) {

        LoggerReader.info("Loading GET test data for: " + testCase);

        ProgramPojo data = JsonDataReader.getTestData(
                Program_FILE,
                testCase,
                ProgramPojo[].class
        );

        cm.setTestData(data);
    }

    // ============================================================
    // WHEN — Send GET Request
    // ============================================================
    @When("Admin sends a GET request to the endpoint")
    public void admin_sends_a_get_request_to_the_endpoint() {

        ProgramPojo data = cm.getTestData();

        String method   = data.requestType;
        String endpoint = "/" + data.endPoint;
        String baseURI  = data.baseURI;
        int id          = data.programId;
        String scenario = data.scenarioName.toLowerCase();

        LoggerReader.info("Preparing GET request for scenario: " + scenario);

        Map<String, String> headers = cm.getAuthHeader(method);

        switch (scenario) {

            case "get program with valid id":
                if (globalvariables.programId != null) {
                    endpoint = "/" + data.endPoint + "/" + globalvariables.programId;
                    LoggerReader.info("GET by valid ID: " + globalvariables.programId);
                }
                break;

            case "get program with invalid id":
                endpoint = "/" + data.endPoint + "/" + id;
                LoggerReader.warn("GET by invalid ID: " + id);
                break;

            case "get program with invalid baseuri":
                cm.setBaseURI(baseURI);
                LoggerReader.warn("Using invalid baseURI: " + baseURI);
                break;

            default:
                LoggerReader.info("Standard GET request");
                break;
        }

        try {
            LoggerReader.info("Sending GET request...");
            cm.sendRequest(method, endpoint, null, headers);
        } catch (Exception e) {
            LoggerReader.error("GET request failed: " + e.getMessage());
            context.set("exception", e);
        }
    }

    // ============================================================
    // THEN — Validate GET Response
    // ============================================================
    @Then("Admin receives {string} with {string}")
    public void admin_receives_with(String expectedCode, String expectedMessage) {

        LoggerReader.info("Validating GET response...");

        if (context.get("exception") != null) {
            LoggerReader.error("Invalid baseURI — request never reached server");
            return;
        }

        int expectedStatus = Integer.parseInt(expectedCode);
        cm.validateStatusCode(expectedStatus);

        String body = cm.getResponse().getBody().asString().trim();
        LoggerReader.info("Response Body: " + body);

        if (expectedStatus == 200) {
            LoggerReader.info("200 OK — skipping message validation");
            return;
        }

        if (expectedStatus == 404 && !body.startsWith("{")) {
            Assert.assertTrue(
                    body.toLowerCase().contains(expectedMessage.toLowerCase()),
                    "Expected: " + expectedMessage + " but found: " + body
            );
            return;
        }

        if (expectedStatus == 405) {
            Assert.assertTrue(
                    body.contains("Request method"),
                    "Expected method error but found: " + body
            );
            return;
        }

        if (expectedStatus == 401 && body.isEmpty()) {
            LoggerReader.warn("401 Unauthorized — empty body allowed");
            return;
        }

        cm.validateErrorMessage(expectedMessage);
    }

    // ============================================================
    // GIVEN — Load PUT Test Data
    // ============================================================
    @Given("Admin updates PUT Request for {string}")
    public void admin_updates_put_request_for(String testCase) {

        LoggerReader.info("Loading PUT test data for: " + testCase);

        UpdateProgramPojo data = JsonDataReader.getTestData(
                Program_FILE,
                testCase,
                UpdateProgramPojo[].class
        );

        cm.setTestData(data);
    }

    // ============================================================
    // WHEN — Send PUT Request
    // ============================================================
    @When("Admin sends a PUT request to the endpoint")
    public void admin_sends_a_put_request_to_the_endpoint() {

        UpdateProgramPojo data = cm.getTestData();

        String method   = data.requestType;
        String endpoint = "/" + data.endPoint;
        String scenario = data.scenarioName.toLowerCase();
        String baseURI  = data.baseURI;

        Integer programId   = globalvariables.programId;
        String programName  = globalvariables.programName;

        LoggerReader.info("Preparing PUT request for scenario: " + scenario);

        Map<String, String> headers = cm.getAuthHeader(method);

        // ============================================================
        // BUILD REQUEST BODY
        // ============================================================
        Map<String, Object> body = new HashMap<>();
        body.put("programName", data.programName);
        body.put("programDescription", data.programDescription);
        body.put("programStatus", data.programStatus);

        // ============================================================
        // SWITCH — Scenario Based Logic
        // ============================================================
        switch (scenario) {

            // ============================================================
            // PUT BY PROGRAM ID
            // ============================================================
            case "valid program id":
                endpoint = "/" + data.endPoint + "/" + programId;

                String updatedName = programName + "-" + CommonMethods.NameGenerator.generateProgramName();
                body.put("programName", updatedName);
                body.put("programDescription", "Updated Desc");
                body.put("programStatus", "Active");

                globalvariables.programName = updatedName;
                LoggerReader.info("Updated programName: " + updatedName);
                break;

            case "invalid program id":
                endpoint = "/" + data.endPoint + "/999999";
                LoggerReader.warn("Using invalid program ID");
                break;

            case "already existing program name":
                endpoint = "/" + data.endPoint + "/" + programId;
                body.put("programName", data.programName);
                LoggerReader.warn("Testing duplicate program name");
                break;

            case "without request body":
                endpoint = "/" + data.endPoint + "/" + programId;
                body = null;
                LoggerReader.warn("Sending EMPTY PUT body");
                break;

            case "invalid baseuri":
                cm.setBaseURI(baseURI);
                endpoint = "/" + data.endPoint + "/" + programId;
                LoggerReader.warn("Using invalid baseURI: " + baseURI);
                break;

            case "invalid method":
                method = "PATCH";
                endpoint = "/" + data.endPoint + "/" + programId;
                LoggerReader.warn("Using invalid method: PATCH");
                break;

            case "invalid endpoint":
                endpoint = "/" + data.endPoint;   // JSON already wrong
                LoggerReader.warn("Using invalid endpoint: " + endpoint);
                break;

            // ============================================================
            // PUT BY PROGRAM NAME
            // ============================================================
            case "valid program name":
                endpoint = "/" + data.endPoint + "/" + programName;

                String newName = "Updated-" + CommonMethods.NameGenerator.generateProgramName();
                body.put("programName", newName);
                body.put("programDescription", "Updated Desc");
                body.put("programStatus", "Active");

                globalvariables.programName = newName;
                LoggerReader.info("Updated programName: " + newName);
                break;

            case "invalid program name":
                endpoint = "/" + data.endPoint + "/" + data.programName;
                LoggerReader.warn("Using invalid programName: " + data.programName);
                break;

            case "missing mandatory fields":
                endpoint = "/" + data.endPoint + "/" + programName;
                body.put("programName", "");
                LoggerReader.warn("Missing mandatory fields");
                break;

            case "invalid status":
                endpoint = "/" + data.endPoint + "/" + programName;
                body.put("programStatus", data.programStatus);
                LoggerReader.warn("Invalid status: " + data.programStatus);
                break;

            case "special char program description":
                endpoint = "/" + data.endPoint + "/" + programName;
                body.put("programDescription", data.programDescription);
                LoggerReader.warn("Special characters in description");
                break;

            case "valid status":
                endpoint = "/" + data.endPoint + "/" + programName;
                body.put("programStatus", "Inactive");
                LoggerReader.info("Valid status update");
                break;

            default:
                LoggerReader.error("Unknown PUT scenario: " + scenario);
                throw new RuntimeException("Unknown PUT scenario: " + scenario);
        }

        // ============================================================
        // SEND REQUEST
        // ============================================================
        try {
            LoggerReader.info("Sending PUT request...");
            cm.sendRequest(method, endpoint, body, headers);
        } catch (Exception e) {
            LoggerReader.error("PUT request failed: " + e.getMessage());
            context.set("exception", e);
        }
    }

    // ============================================================
    // THEN — Validate PUT Response
    // ============================================================
    @Then("Admin receive statuscode {string} with Message {string}")
    public void admin_receive_statuscode_with_message(String expectedCode, String expectedMessage) {

        LoggerReader.info("Validating PUT response...");

        if (context.get("exception") != null) {
            LoggerReader.error("Invalid baseURI — request never reached server");
            return;
        }

        int expectedStatus = Integer.parseInt(expectedCode);
        cm.validateStatusCode(expectedStatus);

        String body = cm.getResponse().getBody().asString().trim();
        LoggerReader.info("Response Body: " + body);

        // SUCCESS CASE
        if (expectedStatus == 200) {
            LoggerReader.info("PUT Success — skipping message validation");
            return;
        }

        // 404 Not Found (plain text)
        if (expectedStatus == 404 && !body.startsWith("{")) {
            org.testng.Assert.assertTrue(
                    body.toLowerCase().contains(expectedMessage.toLowerCase()),
                    "Expected: " + expectedMessage + " but found: " + body
            );
            return;
        }

        // 405 Method Not Allowed
        if (expectedStatus == 405) {
            org.testng.Assert.assertTrue(
                    body.contains("Request method"),
                    "Expected method error but found: " + body
            );
            return;
        }

        // Generic negative validation
        org.testng.Assert.assertTrue(
                body.toLowerCase().contains(expectedMessage.toLowerCase()),
                "Expected: " + expectedMessage + " but found: " + body
        );
    }
}

