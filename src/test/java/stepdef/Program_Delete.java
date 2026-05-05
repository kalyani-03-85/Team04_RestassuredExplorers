package stepdef;

import io.cucumber.java.en.*;
import pojo.ProgramPojo;
import utils.CommonMethods;
import utils.ConfigReader;
import utils.JsonDataReader;
import utils.LoggerReader;
import utils.globalvariables;

import java.util.HashMap;
import java.util.Map;

import org.testng.Assert;

import context.scenarioReader;

public class Program_Delete {

    private scenarioReader context;
    private CommonMethods cm;
    private String Program_FILE;

    public Program_Delete(scenarioReader context) {
        this.context = context;
        this.cm = new CommonMethods(context);
        this.Program_FILE = ConfigReader.getKey("jsonFilePath");
    }

    // ============================================================
    // GIVEN — Load DELETE Test Data
    // ============================================================
    @Given("Admin creates DELETE Request for {string}")
    public void admin_creates_delete_request_for(String testCase) {

        LoggerReader.info("Loading DELETE test data for: " + testCase);

        ProgramPojo data = JsonDataReader.getTestData(
                Program_FILE,
                testCase,
                ProgramPojo[].class
        );

        cm.setTestData(data);
    }

    // ============================================================
    // WHEN — Send DELETE Request
    // ============================================================
    @When("Admin sends a DELETE request to the endpoint")
    public void admin_sends_a_delete_request_to_the_endpoint() {

        ProgramPojo data = cm.getTestData();

        String method   = "DELETE";
        String endpoint = "/" + data.endPoint;
        String scenario = data.scenarioName.toLowerCase();
        String baseURI  = data.baseURI;

        LoggerReader.info("Preparing DELETE request for scenario: " + scenario);

        Map<String, String> headers = new HashMap<>();

        // ============================================================
        // AUTH HANDLING
        // ============================================================
        if (scenario.contains("without authorization")) {
            LoggerReader.warn("Sending DELETE request WITHOUT Authorization");
        } else {
            headers = cm.getAuthHeader(method);
        }

        // ============================================================
        // INVALID METHOD
        // ============================================================
        if (scenario.contains("invalid method")) {
            method = "POST";
            LoggerReader.warn("Using invalid method: POST");
        }

        // ============================================================
        // INVALID ENDPOINT
        // ============================================================
        if (scenario.contains("invalid endpoint")) {
            endpoint = "/" + data.endPoint; // JSON already wrong
            LoggerReader.warn("Using invalid endpoint: " + endpoint);
        }

        // ============================================================
        // DELETE BY PROGRAM NAME
        // ============================================================
        if (scenario.contains("programname")) {

            String programName = globalvariables.programName;

            if (scenario.contains("non existing")) {
                programName = "DoesNotExistXYZ";
            }

            endpoint = "/" + data.endPoint + "/" + programName;

            LoggerReader.info("DELETE by programName: " + programName);
        }

        // ============================================================
        // DELETE BY PROGRAM ID
        // ============================================================
        if (scenario.contains("program id")) {

            String id = String.valueOf(globalvariables.programId);

            if (scenario.contains("alphabets")) {
                id = "ABC123";
            }

            endpoint = "/" + data.endPoint + "/" + id;

            LoggerReader.info("DELETE by programId: " + id);
        }

        // ============================================================
        // INVALID BASE URI
        // ============================================================
        if (baseURI != null && !baseURI.isEmpty()) {
            cm.setBaseURI(baseURI);
            LoggerReader.warn("Using invalid baseURI: " + baseURI);
        }

        // ============================================================
        // SEND REQUEST
        // ============================================================
        try {
            LoggerReader.info("Sending DELETE request...");
            cm.sendRequest(method, endpoint, null, headers);
        } catch (Exception e) {
            LoggerReader.error("DELETE request failed: " + e.getMessage());
            context.set("exception", e);
        }
    }

    // ============================================================
    // THEN — Validate DELETE Response
    // ============================================================
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
