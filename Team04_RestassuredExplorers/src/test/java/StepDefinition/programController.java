package StepDefinition;

import java.util.HashMap;
import java.util.Map;
import org.testng.Assert;
import context.scenarioReader;
import io.cucumber.java.en.*;
import pojo.programPojo;
import pojo.UpdateProgramPojo;
import utils.CommonMethods;
import utils.ConfigReader;
import utils.JsonDataReader1;
import utils.LoggerReader;
import context.commonVariables;


public class programController {
	String token = commonVariables.token;

    CommonMethods cm;
    private scenarioReader context;
    private String Program_FILE;

    public programController(scenarioReader context) {
        this.context = context;
        this.cm = new CommonMethods(context);
        this.Program_FILE = ConfigReader.getKey("jsonFilePath");
    }

 
    @Given("Admin creates POST Request with valid requestbody {string}")
    public void admin_creates_post_request_with_valid_requestbody(String testCase) {

        LoggerReader.info("Loading POST test data for: " + testCase);

        programPojo data = JsonDataReader1.getTestData(
                Program_FILE,
                testCase,
                programPojo[].class
        );

        // Generate unique programName ONLY for positive scenarios
        if (data.expectedStatusCode < 400 && data.programName != null) {
            String unique = data.programName + "-" + CommonMethods.NameGenerator.generateProgramName();
            data.programName = unique;
            commonVariables.programName = unique;

            LoggerReader.info("Generated unique programName: " + unique);
        }

        cm.setTestData(data);

        LoggerReader.info("Scenario: " + data.scenarioName);
        LoggerReader.info("Endpoint: " + data.endPoint);
        LoggerReader.info("Request Type: " + data.requestType);
    }

       
    @When("Admin sends a HTTPS POST request to the endpoint")
    public void admin_sends_a_https_post_request_to_the_endpoint() {

        programPojo data = cm.getTestData();

        String method = data.requestType;
        String endpoint = "/" + data.endPoint;

        LoggerReader.info("Preparing POST request...");
        LoggerReader.info("Method: " + method + " | Endpoint: " + endpoint);

        Map<String, String> headers = cm.getAuthHeader(method);

        // Invalid token
        if ("invalid token".equalsIgnoreCase(data.scenarioName)) {
            headers.put("Authorization", "Bearer invalidToken123");
            LoggerReader.warn("Using invalid token");
        }

        // Invalid content-type
        if ("INVALID".equalsIgnoreCase(data.contentType)) {
            headers.put("Content-Type", "text/plain");
            LoggerReader.warn("Using INVALID content-type");
        }

        // Invalid method
        if (data.requestType.equalsIgnoreCase("GET")) {
            method = "GET";
            LoggerReader.warn("Using invalid method: GET");
        }

        // Empty payload
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

       
        if (expectedStatus == 201) {
            LoggerReader.info("201 Created — skipping message validation");

            cm.storeProgramId();
            String actualProgramName = cm.getResponse().jsonPath().getString("programName");
            cm.storeProgramName(actualProgramName);
            
            //For data chain
            

            return;
        }

       
        if (expectedStatus == 401 && body.isEmpty()) {
            LoggerReader.warn("401 Unauthorized with empty body — skipping message validation");
            return;
        }

        
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

  //GET
    @Given("Admin creates GET Request for {string}")
    public void admin_creates_get_request_for(String testCase) {

        LoggerReader.info("Loading GET test data for: " + testCase);

        programPojo data = JsonDataReader1.getTestData(
                Program_FILE,
                testCase,
                programPojo[].class
        );

        cm.setTestData(data);
    }

 
    @When("Admin sends a GET request to the endpoint")
    public void admin_sends_a_get_request_to_the_endpoint() {

        programPojo data = cm.getTestData();

        String method   = data.requestType;
        String endpoint = "/" + data.endPoint;
        String baseURI  = data.baseURI;
        int id          = data.programId;
        String scenario = data.scenarioName.toLowerCase();

        LoggerReader.info("Preparing GET request for scenario: " + scenario);

        Map<String, String> headers = cm.getAuthHeader(method);

        switch (scenario) {

            case "get program with valid id":
                if (commonVariables.programId != null) {
                    endpoint = "/" + data.endPoint + "/" + commonVariables.programId;
                    LoggerReader.info("GET by valid ID: " + commonVariables.programId);
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

   
    // PUT
   
    @Given("Admin updates PUT Request for {string}")
    public void admin_updates_put_request_for(String testCase) {

        LoggerReader.info("Loading PUT test data for: " + testCase);

        UpdateProgramPojo data = JsonDataReader1.getTestData(
                Program_FILE,
                testCase,
                UpdateProgramPojo[].class
        );

        cm.setTestData(data);
    }

    @When("Admin sends a PUT request to the endpoint")
    public void admin_sends_a_put_request_to_the_endpoint() {

        UpdateProgramPojo data = cm.getTestData();

        String method   = data.requestType;
        String endpoint = "/" + data.endPoint;
        String scenario = data.scenarioName.toLowerCase();
        String baseURI  = data.baseURI;

        Integer programId   =  commonVariables.programId;
        String programName  = commonVariables.programName;

        LoggerReader.info("Preparing PUT request for scenario: " + scenario);

        Map<String, String> headers = cm.getAuthHeader(method);

        Map<String, Object> body = new HashMap<>();
        body.put("programName", data.programName);
        body.put("programDescription", data.programDescription);
        body.put("programStatus", data.programStatus);

        switch (scenario) {

         
            // PUT BY PROGRAM ID
           
            case "valid program id":
                endpoint = "/" + data.endPoint + "/" + programId;

                String updatedName = programName + "-" + CommonMethods.NameGenerator.generateProgramName();
                body.put("programName", updatedName);
                body.put("programDescription", "Updated Desc");
                body.put("programStatus", "Active");

                commonVariables.programName = updatedName;
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

           
            // PUT BY PROGRAM NAME
           
            case "valid program name":
                endpoint = "/" + data.endPoint + "/" + programName;

                String newName = "Updated-" + CommonMethods.NameGenerator.generateProgramName();
                body.put("programName", newName);
                body.put("programDescription", "Updated Desc");
                body.put("programStatus", "Active");

                commonVariables.programName = newName;
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

        try {
            LoggerReader.info("Sending PUT request...");
            cm.sendRequest(method, endpoint, body, headers);
        } catch (Exception e) {
            LoggerReader.error("PUT request failed: " + e.getMessage());
            context.set("exception", e);
        }
    }

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

   
        if (expectedStatus == 200) {
            LoggerReader.info("PUT Success — skipping message validation");
            return;
        }

       
        if (expectedStatus == 404 && !body.startsWith("{")) {
            org.testng.Assert.assertTrue(
                    body.toLowerCase().contains(expectedMessage.toLowerCase()),
                    "Expected: " + expectedMessage + " but found: " + body
            );
            return;
        }

       
        if (expectedStatus == 405) {
            org.testng.Assert.assertTrue(
                    body.contains("Request method"),
                    "Expected method error but found: " + body
            );
            return;
        }

       
        org.testng.Assert.assertTrue(
                body.toLowerCase().contains(expectedMessage.toLowerCase()),
                "Expected: " + expectedMessage + " but found: " + body
        );
    }
    
    @Given("Admin creates DELETE Request for {string}")
    public void admin_creates_delete_request_for(String testCase) {

        LoggerReader.info("Loading DELETE test data for: " + testCase);

        programPojo data = JsonDataReader1.getTestData(
                Program_FILE,
                testCase,
                programPojo[].class
        );

        cm.setTestData(data);
    }

    // WHEN — Send DELETE Request
    @When("Admin sends a DELETE request to the endpoint")
    public void admin_sends_a_delete_request_to_the_endpoint() {

        programPojo data = cm.getTestData();

        String method   = "DELETE";
        String endpoint = "/" + data.endPoint;
        String scenario = data.scenarioName.toLowerCase();
        String baseURI  = data.baseURI;

        LoggerReader.info("Preparing DELETE request for scenario: " + scenario);

        Map<String, String> headers = new HashMap<>();

        // AUTH HANDLING
        if (scenario.contains("without authorization")) {
            LoggerReader.warn("Sending DELETE request WITHOUT Authorization");
        } else {
            headers = cm.getAuthHeader(method);
        }

        // INVALID METHOD
        if (scenario.contains("invalid method")) {
            method = "POST";
            LoggerReader.warn("Using invalid method: POST");
        }

        // INVALID ENDPOINT
        if (scenario.contains("invalid endpoint")) {
            endpoint = "/" + data.endPoint; // JSON already wrong
            LoggerReader.warn("Using invalid endpoint: " + endpoint);
        }

        // DELETE BY PROGRAM NAME
        if (scenario.contains("programname")) {

            String programName = commonVariables.programName;

            if (scenario.contains("non existing")) {
                programName = "DoesNotExistXYZ";
            }

            endpoint = "/" + data.endPoint + "/" + programName;

            LoggerReader.info("DELETE by programName: " + programName);
        }

        // DELETE BY PROGRAM ID
        if (scenario.contains("program id")) {

            String id = String.valueOf(commonVariables.programId);

            if (scenario.contains("alphabets")) {
                id = "ABC123";
            }

            endpoint = "/" + data.endPoint + "/" + id;

            LoggerReader.info("DELETE by programId: " + id);
        }

        // INVALID BASE URI
        if (baseURI != null && !baseURI.isEmpty()) {
            cm.setBaseURI(baseURI);
            LoggerReader.warn("Using invalid baseURI: " + baseURI);
        }

        // SEND REQUEST
        try {
            LoggerReader.info("Sending DELETE request...");
            cm.sendRequest(method, endpoint, null, headers);
        } catch (Exception e) {
            LoggerReader.error("DELETE request failed: " + e.getMessage());
            context.set("exception", e);
        }
    }

    
    // Validate DELETE Response
   
    @Then("Admin receives {string} with message {string}")
    public void admin_receives_with_message(String expectedCode, String expectedMessage) {

        LoggerReader.info("Validating DELETE response...");

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
            LoggerReader.info("200 OK — Program deleted successfully");
            return;
        }

        // 401 Unauthorized
        if (expectedStatus == 401 && body.isEmpty()) {
            LoggerReader.warn("401 Unauthorized — empty body allowed");
            return;
        }

        // 404 Not Found (plain text)
        if (expectedStatus == 404 && !body.startsWith("{")) {
            Assert.assertTrue(
                    body.toLowerCase().contains(expectedMessage.toLowerCase()),
                    "Expected: " + expectedMessage + " but found: " + body
            );
            return;
        }

        // 405 Method Not Allowed
        if (expectedStatus == 405) {
            Assert.assertTrue(
                    body.contains("Request method"),
                    "Expected method error but found: " + body
            );
            return;
        }

        // Generic negative validation
        Assert.assertTrue(
                body.toLowerCase().contains(expectedMessage.toLowerCase()),
                "Expected: " + expectedMessage + " but found: " + body
        );
    }

}
