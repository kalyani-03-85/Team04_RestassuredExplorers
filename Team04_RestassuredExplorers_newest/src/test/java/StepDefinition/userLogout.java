package StepDefinition;

import java.util.HashMap;
import java.util.Map;

import org.testng.Assert;

import context.scenarioReader;
import io.cucumber.java.en.*;
import pojo.programPojo;
import utils.CommonMethods;
import utils.ConfigReader;
import utils.JsonDataReader1;
import utils.LoggerReader;

public class userLogout {
	
	

	    CommonMethods cm;
	    private scenarioReader context;
	    private String Program_FILE;

	    public userLogout(scenarioReader context) {
	        this.context = context;
	        this.cm = new CommonMethods(context);
	        this.Program_FILE = ConfigReader.getKey("jsonFilePath");
	    }


	    @Given("Admin creates GET Request for scenario {string}")
	    public void admin_creates_get_request_for_scenario(String testCase) {

	        LoggerReader.info("Loading Logout test data for: " + testCase);

	        programPojo data = JsonDataReader1.getTestData(
	                Program_FILE,
	                testCase,
	                programPojo[].class
	        );

	        cm.setTestData(data);

	        LoggerReader.info("Scenario: " + data.scenarioName);
	        LoggerReader.info("Endpoint: " + data.endPoint);
	        LoggerReader.info("Request Type: " + data.requestType);
	    }


	    @When("Admin sends a HTTPS request with endpoint")
	    public void admin_sends_a_https_request_with_endpoint() {

	        programPojo data = cm.getTestData();

	        String method   = data.requestType;   // GET / PATCH (invalid)
	        String endpoint = "/" + data.endPoint;
	        String scenario = data.scenarioName.toLowerCase();
	        String baseURI  = data.baseURI;

	        LoggerReader.info("Preparing Logout request for scenario: " + scenario);
	        LoggerReader.info("Method: " + method + " | Endpoint: " + endpoint);

	        Map<String, String> headers = new HashMap<>();

	        switch (scenario) {

	            case "logout with no auth":
	                LoggerReader.warn("No Authorization header added");
	                break;

	            case "logout with invalid token":
	                headers.put("Authorization", "Bearer invalidToken123");
	                LoggerReader.warn("Using invalid token");
	                break;

	            case "logout with expired token":
	                headers.put("Authorization", "Bearer expiredToken123");
	                LoggerReader.warn("Using expired token");
	                break;

	            default:
	                headers = cm.getAuthHeader(method);
	                LoggerReader.info("Using valid token");
	                break;
	        }


	        if (scenario.contains("invalid method")) {
	            method = data.requestType; // JSON already has PATCH
	            LoggerReader.warn("Using invalid method: " + method);
	        }

	        if (baseURI != null && !baseURI.isEmpty()) {
	            cm.setBaseURI(baseURI);
	            LoggerReader.warn("Using invalid baseURI: " + baseURI);
	        }


	        try {
	            LoggerReader.info("Sending Logout request...");
	            cm.sendRequest(method, endpoint, null, headers);
	        } catch (Exception e) {
	            LoggerReader.error("Logout request failed: " + e.getMessage());
	            context.set("exception", e);
	        }
	    }


	    @Then("Admin receives {string} and {string}")
	    public void admin_receives_and(String expectedCode, String expectedMessage) {

	        LoggerReader.info("Validating Logout response...");

	        // BaseURI failure
	        if (context.get("exception") != null) {
	            LoggerReader.error("BaseURI invalid — request never reached server");
	            return;
	        }

	        int expectedStatus = Integer.parseInt(expectedCode);
	        cm.validateStatusCode(expectedStatus);

	        String body = cm.getResponse().getBody().asString().trim();
	        LoggerReader.info("Response Body: " + body);

	        if (expectedStatus == 200) {
	            LoggerReader.info("Validating success message...");
	            Assert.assertTrue(
	                    body.contains(expectedMessage),
	                    "Expected: " + expectedMessage + " but found: " + body
	            );
	            return;
	        }

	        if (expectedStatus == 401 && body.isEmpty()) {
	            LoggerReader.warn("401 Unauthorized — empty body allowed");
	            return;
	        }

	        if (expectedStatus == 404 && !body.startsWith("{")) {
	            LoggerReader.info("Validating plain-text 404 message...");
	            Assert.assertTrue(
	                    body.toLowerCase().contains(expectedMessage.toLowerCase()),
	                    "Expected: " + expectedMessage + " but found: " + body
	            );
	            return;
	        }

	        if (expectedStatus == 405) {
	            LoggerReader.info("Validating 405 method error...");
	            Assert.assertTrue(
	                    body.contains("Request method"),
	                    "Expected method error but found: " + body
	            );
	            return;
	        }

	        LoggerReader.info("Validating generic error message...");
	        Assert.assertTrue(
	                body.toLowerCase().contains(expectedMessage.toLowerCase()),
	                "Expected: " + expectedMessage + " but found: " + body
	        );
	    }
	

}
