package StepDefinition;

import io.cucumber.java.en.*;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import pojo.loginPojo;
import utils.JsonDataReader;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import java.util.HashMap;
import java.util.Map;

import context.commonVariables;

import context.scenarioReader;

public class userLogin {

	String baseURL = commonVariables.baseURL;
	private scenarioReader context;
	Response response;

	public userLogin(scenarioReader context) {
		this.context = context;
	}

	public void postMethod() {
		String scenarioName = context.get("scenarioName");

		// fetch data from JSON and match it to POJO object
		loginPojo data = JsonDataReader.getLoginData(scenarioName);
		context.set("testData", data);
		System.out.println("Scenario Name from JSON: " + data.scenarioName);
	}

	public void sendRequest() {

		loginPojo data = context.get("testData");

		Map<String, Object> body = new HashMap<>();
		body.put("userLoginEmailId", data.userLoginEmailId);
		if (!data.endPoint.toLowerCase().contains("forgotpassword")) 	
			{
			body.put("password", data.password);
			}

		response = given().log().all().baseUri(baseURL).contentType(ContentType.JSON).body(body).when()
				.request(data.requestType, "/" + data.endPoint);

		context.set("response", response);

	}
	
	public void validateStatusCodeResponse() {
		loginPojo data = context.get("testData");
		response = context.get("response");
		response.then().log().ifValidationFails() // logs only if test fails
				.statusCode(data.expectedStatusCode);
		System.out.println("Status code passed");
	}

	public void validateStatusLineResponse() {
		loginPojo data = context.get("testData");
		response = context.get("response");
		response.then().log().ifValidationFails() // logs only if test fails
				.statusLine(containsStringIgnoringCase(data.expectedstatusline));
		System.out.println("Status Line passed");
	}

	public void validateContentTypeResponse() {
		// loginPojo data = context.get("testData");
		response = context.get("response");
		response.then().log().ifValidationFails() // logs only if test fails
				.header("Content-Type", containsString("application/json"));
		System.out.println("Content Type is application/json");
	}

	@Given("Admin creates POST request with valid credentials")
	public void admin_creates_post_request_with_valid_credentials() {

		postMethod();
	}

	@When("Admin sends a HTTPS request to the valid endpoint")
	public void admin_sends_a_https_request_to_the_valid_endpoint() {

		sendRequest();
	}

	@Then("Admin receives status code with auto generated token")
	public void admin_receives_status_code_with_auto_generated_token() {

		validateStatusCodeResponse();
		validateStatusLineResponse();
		validateContentTypeResponse();

		response = context.get("response");
		response.then().body("token", notNullValue()).log().all(true);
		//set token for the flow
		commonVariables.token = response.jsonPath().getString("token");
		System.out.println(commonVariables.token);

	}

	// commented to run batch easily

	@Given("Admin creates GET request with valid credentials")
	public void admin_creates_get_request_with_valid_credentials() {

		postMethod();
	}

	@Then("Admin receives status code")
	public void admin_receives_status_code() {
		loginPojo data = context.get("testData");
		response = context.get("response");

		validateStatusCodeResponse();
		validateStatusLineResponse();

		// int statusCode = response.getStatusCode();

		if ((response.getStatusCode() == 200 && data.expectedStatusCode == 200)
				|| (response.getStatusCode() == 201 && data.expectedStatusCode == 201)) {
			validateContentTypeResponse();
		}
	}

	// 2

	@Given("Admin creates POST request with invalid base URL")
	public void admin_creates_post_request_with_invalid_base_url() {
		postMethod();

	}

	@When("Admin sends a HTTPS request to the valid endpoint with invalid url")
	public void admin_sends_a_https_request_to_the_valid_endpoint_with_invalid_url() {

		loginPojo data = context.get("testData");

		Map<String, Object> body = new HashMap<>();
		body.put("userLoginEmailId", data.userLoginEmailId);
		body.put("password", data.password);

		response = given().log().all()
				.baseUri("https://lms-hackathon-api-april-2026-8b55b53c8fab.herokuapp.com/")
				.contentType(ContentType.JSON).body(body).when().request(data.requestType, "/" + data.endPoint);

		context.set("response", response);

	}

	// 3

	@Given("Admin creates POST request with invalid content type")
	public void admin_creates_post_request_with_invalid_content_type() {
		postMethod();
	}

	@When("Admin sends a HTTPS request to the valid endpoint with invalid content type")
	public void admin_sends_a_https_request_to_the_valid_endpoint_with_invalid_content_type() {

		loginPojo data = context.get("testData");
		String body = "{ \"userLoginEmailId\": \"" + data.userLoginEmailId + "\", \"password\": \"" + data.password
				+ "\" }";

		response = given().log().all().baseUri(baseURL).contentType(ContentType.TEXT).body(body).when()
				.request(data.requestType, "/" + data.endPoint);

		context.set("response", response);

	}

	// 4

	@When("Admin sends a HTTPS request to the invalid endpoint")
	public void admin_sends_a_https_request_to_the_invalid_endpoint() {

		sendRequest();
	}

	@Given("Admin creates POST request with empty email")
	public void admin_creates_post_request_with_empty_email() {
		postMethod();
	}

	@Then("Admin receives status code with message {string}")
	public void admin_receives_status_code_with_message(String msg) {

		validateStatusCodeResponse();
		validateStatusLineResponse();
		validateContentTypeResponse();

		response = context.get("response");

		response.then().body(containsStringIgnoringCase(msg));

		System.out.println("Expected Message " + msg + " is displayed");
	}

	@Given("Admin creates POST request with special characters in email")
	public void admin_creates_post_request_with_special_characters_in_email() {
		postMethod();
	}

	@Given("Admin creates POST request with email containing spaces")
	public void admin_creates_post_request_with_email_containing_spaces() {
		postMethod();
	}

	@Given("Admin creates POST request with null in email")
	public void admin_creates_post_request_with_null_in_email() {

		postMethod();
	}

	@Given("Admin creates POST request with unregistered email")
	public void admin_creates_post_request_with_unregistered_email() {

		postMethod();
	}

	@Given("Admin creates POST request with empty password")
	public void admin_creates_post_request_with_empty_password() {
		postMethod();
	}

	@Given("Admin creates POST request with special characters inserted in password")
	public void admin_creates_post_request_with_special_characters_inserted_in_password() {
		postMethod();
	}

	@Then("Admin receives status code with message {string} and false success")
	public void admin_receives_status_code_with_message_and_false_success(String msg) {

		validateStatusCodeResponse();
		validateStatusLineResponse();
		validateContentTypeResponse();

		response = context.get("response");

		response.then().log().ifValidationFails().body("message", equalToIgnoringCase(msg));

		System.out.println("Expected Message Displayed");

		response.then().log().ifValidationFails().body("success", equalTo(false));
		System.out.println("Message Success value is false");

	}

	@Given("Admin creates POST request with password containing spaces")
	public void admin_creates_post_request_with_password_containing_spaces() {

		postMethod();
	}

	@Given("Admin creates POST request with null in password")
	public void admin_creates_post_request_with_null_in_password() {
		postMethod();
	}

	@Given("Admin creates POST request with inactive user credentials")
	public void admin_creates_post_request_with_inactive_user_credentials() {
		postMethod();
	}

	@Given("Admin creates POST request without request body")
	public void admin_creates_post_request_without_request_body() {
		postMethod();
	}

	@When("Admin sends a HTTPS request to the valid endpoint without request body")
	public void admin_sends_a_https_request_to_the_valid_endpoint_without_request_body() {

		loginPojo data = context.get("testData");

		// Map<String, Object> body = new HashMap<>(); //body.put("userLoginEmailId",
		// data.userLoginEmailId); //body.put("password", data.password);

		response = given().log().all().baseUri(baseURL).contentType(ContentType.JSON) // .body(body) .when()
				.request(data.requestType, "/" + data.endPoint);

		context.set("response", response);
	}

	
///////////////////////////////////////////// FORGOT PASSWORD //////////////////////////////////////////////////


@Given("Admin creates POST request with valid credential")
public void admin_creates_post_request_with_valid_credential() {
    // Write code here that turns the phrase above into concrete actions
    throw new io.cucumber.java.PendingException();
}

@Then("Admin receives {int} Not found")
public void admin_receives_not_found(Integer int1) {
    // Write code here that turns the phrase above into concrete actions
    throw new io.cucumber.java.PendingException();
}

@Then("Admin receives {int} Bad request with valid error message")
public void admin_receives_bad_request_with_valid_error_message(Integer int1) {
    // Write code here that turns the phrase above into concrete actions
    throw new io.cucumber.java.PendingException();
}

@Given("Admin creates POST request with invalid email")
public void admin_creates_post_request_with_invalid_email() {
    // Write code here that turns the phrase above into concrete actions
    throw new io.cucumber.java.PendingException();
}

@Then("Admin receives {int} created with auto generated token")
public void admin_receives_created_with_auto_generated_token(Integer int1) {
    // Write code here that turns the phrase above into concrete actions
    throw new io.cucumber.java.PendingException();
}
	
}
