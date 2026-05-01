package StepDefinition;

import io.cucumber.java.en.*;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import pojo.loginPojo;
import utils.JsonDataReader;
import static io.restassured.RestAssured.given;
//import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.Map;

import context.scenarioReader;

public class userLogin {

	String baseURL = "https://lms-hackathon-api-april-2026-8b55b53c8fab.herokuapp.com/lms";
	private scenarioReader context;
	Response response;

	public userLogin(scenarioReader context) {
		this.context = context;
	}
	
	
	public void postMethod()
	{
		 String scenarioName = context.get("scenarioName");
	        
		    //fetch data from JSON and match it to POJO object
		    loginPojo data = JsonDataReader.getData(scenarioName);

		    context.set("testData", data);
			 System.out.println("Scenario Name from JSON: " + data.scenarioName);
	}

	public void sendRequest() {

		loginPojo data = context.get("testData");

		Response response = given().log().all().baseUri(baseURL).contentType(ContentType.JSON)
				.body(Map.of("userLoginEmailId", data.userLoginEmailId, "password", data.password)).when()
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

	public void validateStatusLineResponse() 
	{
		loginPojo data = context.get("testData");
		response = context.get("response");

		response.then().log().ifValidationFails() // logs only if test fails
				.statusLine(containsStringIgnoringCase(data.expectedstatusline));
		System.out.println("Status Line passed");
	}

	public void validateContentTypeResponse() 
	{
		loginPojo data = context.get("testData");
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
		// throw new io.cucumber.java.PendingException();
	}

	@Then("Admin receives status code with auto generated token")
	public void admin_receives_status_code_with_auto_generated_token() {

		validateStatusCodeResponse();
		validateStatusLineResponse();
		validateContentTypeResponse();

		response = context.get("response");

		response.then().body("token", notNullValue()).log().all(true);

	}


@Given("Admin creates GET request with valid credentials")
public void admin_creates_get_request_with_valid_credentials() {

   postMethod();
}

@Then("Admin receives status code")
public void admin_receives_status_code() {
	validateStatusCodeResponse();
	validateStatusLineResponse();
	validateContentTypeResponse();
}

//2

@Given("Admin creates POST request with invalid base URL")
public void admin_creates_post_request_with_invalid_base_url() {
postMethod();

}

@When("Admin sends a HTTPS request to the valid endpoint with invalid url")
public void admin_sends_a_https_request_to_the_valid_endpoint_with_invalid_url() {

	loginPojo data = context.get("testData");

	Response response = given().log().all().baseUri("https://lms-hackathon-api.com/lms").contentType(ContentType.JSON)
			.body(Map.of("userLoginEmailId", data.userLoginEmailId, "password", data.password)).when()
			.request(data.requestType, "/" + data.endPoint);

	context.set("response", response);
}

//3

@Given("Admin creates POST request with invalid content type")
public void admin_creates_post_request_with_invalid_content_type() {
	postMethod();
}

@When("Admin sends a HTTPS request to the valid endpoint with invalid content type")
public void admin_sends_a_https_request_to_the_valid_endpoint_with_invalid_content_type() {
	loginPojo data = context.get("testData");

	Response response = given().log().all().baseUri(baseURL).contentType(ContentType.HTML)
			.body(Map.of("userLoginEmailId", data.userLoginEmailId, "password", data.password)).when()
			.request(data.requestType, "/" + data.endPoint);

	context.set("response", response);

}

//4



}
