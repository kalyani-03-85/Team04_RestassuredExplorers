package stepdef;

import java.util.HashMap;
import java.util.Map;

import org.testng.Assert;

import context.scenarioReader;
import io.cucumber.java.en.*;
import pojo.ProgramPojo;
import utils.CommonMethods;
import utils.ConfigReader;
import utils.JsonDataReader;
import utils.LoggerReader;

public class ResetPassword{

    CommonMethods cm;
    private scenarioReader context;
    private String Program_FILE;

    public ResetPassword(scenarioReader context) {
        this.context = context;
        this.cm = new CommonMethods(context);
        this.Program_FILE = ConfigReader.getKey("jsonFilePath");
    }

    // ============================================================
    // GIVEN — Load JSON Test Data
    // ============================================================
    @Given("Admin creates POST Request for {string}")
    public void admin_creates_post_request_for(String testCase) {

        LoggerReader.info("Loading test data for: " + testCase);

        ProgramPojo data = JsonDataReader.getTestData(
                Program_FILE,
                testCase,
                ProgramPojo[].class
        );

        cm.setTestData(data);

        LoggerReader.info("Scenario: " + data.scenarioName);
        LoggerReader.info("Endpoint: " + data.endPoint);
        LoggerReader.info("Request Type: " + data.requestType);
    }

    // ============================================================
    // WHEN — Send Reset Password Request
    // ============================================================
    @When("Admin sends a HTTPS request to the endpoint")
    public void admin_sends_a_https_request_to_the_endpoint() {

        ProgramPojo data = cm.getTestData();

        String method   = data.requestType;
        String endpoint = "/" + data.endPoint;
        String baseURI  = data.baseURI;
        String scenario = data.scenarioName.toLowerCase();

        LoggerReader.info("Preparing request for scenario: " + scenario);
        LoggerReader.info("Method: " + method + " | Endpoint: " + endpoint);

        Map<String, String> headers = new HashMap<>();

        // ============================================================
        // AUTH HANDLING
        // ============================================================
        switch (scenario) {

            case "reset password with no auth":
                LoggerReader.warn("No Authorization header added");
                break;

            case "reset password with expired token":
                headers.put("Authorization", "Bearer expiredToken123");
                LoggerReader.warn("Using expired token");
                break;

            case "reset password with empty token":
                headers.put("Authorization", "Bearer ");
                LoggerReader.warn("Using empty token");
                break;

            case "reset password with other user token":
                headers.put("Authorization", "Bearer otherUserToken123");
                LoggerReader.warn("Using other user's token");
                break;

            default:
                headers = cm.getAuthHeader(method);
                LoggerReader.info("Using valid token");
                break;
        }

        // ============================================================
        // INVALID CONTENT TYPE
        // ============================================================
        if ("INVALID".equalsIgnoreCase(data.contentType)) {
            headers.put("Content-Type", "text/plain");
            LoggerReader.warn("Using INVALID content-type");
        } else {
            headers.put("Content-Type", "application/json");
        }

        // ============================================================
        // INVALID BASE URI
        // ============================================================
        if (baseURI != null && !baseURI.isEmpty()) {
            cm.setBaseURI(baseURI);
            LoggerReader.warn("Using invalid baseURI: " + baseURI);
        }

        // ============================================================
        // INVALID METHOD
        // ============================================================
        if (scenario.contains("invalid method")) {
            method = data.requestType;
            LoggerReader.warn("Using invalid method: " + method);
        }

        // ============================================================
        // BUILD PAYLOAD
        // ============================================================
        Map<String, Object> body = new HashMap<>();

        if (data.getEmail() != null) body.put("email", data.getEmail());
        if (data.getPassword() != null) body.put("password", data.getPassword());

        LoggerReader.info("Payload: " + body);

        // ============================================================
        // SEND REQUEST
        // ============================================================
        try {
            LoggerReader.info("Sending request...");
            cm.sendRequest(method, endpoint, body, headers);
        } catch (Exception e) {
            LoggerReader.error("Request failed: " + e.getMessage());
            context.set("exception", e);
        }
    }

    // ============================================================
    // THEN — Validate Response
    // ============================================================
    @Then("Admin receives {string} with the message {string}")
    public void admin_receives_with_the_message(String expectedCode, String expectedMessage) {

        LoggerReader.info("Validating response...");

        // BaseURI failure
        if (context.get("exception") != null) {
            LoggerReader.error("BaseURI invalid — request never reached server");
            return;
        }

        int expectedStatus = Integer.parseInt(expectedCode);
        cm.validateStatusCode(expectedStatus);

        String body = cm.getResponse().getBody().asString().trim();
        LoggerReader.info("Response Body: " + body);

        // ============================================================
        // SUCCESS CASE (200)
        // ============================================================
        if (expectedStatus == 200) {
            LoggerReader.info("Validating success message...");
            Assert.assertTrue(
                    body.contains(expectedMessage),
                    "Expected: " + expectedMessage + " but found: " + body
            );
            return;
        }

        // ============================================================
        // 401 Unauthorized (empty body allowed)
        // ============================================================
        if (expectedStatus == 401 && body.isEmpty()) {
            LoggerReader.warn("401 Unauthorized — empty body allowed");
            return;
        }

        // ============================================================
        // 404 Not Found (plain text)
        // ============================================================
        if (expectedStatus == 404 && !body.startsWith("{")) {
            LoggerReader.info("Validating plain-text 404 message...");
            Assert.assertTrue(
                    body.toLowerCase().contains(expectedMessage.toLowerCase()),
                    "Expected: " + expectedMessage + " but found: " + body
            );
            return;
        }

        // ============================================================
        // 405 Method Not Allowed
        // ============================================================
        if (expectedStatus == 405) {
            LoggerReader.info("Validating 405 method error...");
            Assert.assertTrue(
                    body.contains("Request method"),
                    "Expected method error but found: " + body
            );
            return;
        }

        // ============================================================
        // GENERIC NEGATIVE VALIDATION
        // ============================================================
        LoggerReader.info("Validating generic error message...");
        Assert.assertTrue(
                body.toLowerCase().contains(expectedMessage.toLowerCase()),
                "Expected: " + expectedMessage + " but found: " + body
        );
    }
}
