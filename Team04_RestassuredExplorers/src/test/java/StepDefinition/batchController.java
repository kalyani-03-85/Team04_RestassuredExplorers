package StepDefinition;

import io.cucumber.java.en.*;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import pojo.batchPojo;
import utils.JsonDataReader;
import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import context.commonVariables;
import context.scenarioReader;

public class batchController {

	private scenarioReader context;
	String baseURL = commonVariables.baseURL;
	Response response;
	String token;
	int curr_program_id, curr_batch_id;
	// String batch_name;
	private batchPojo data;
	String curr_batch_name, curr_program_name;

	public batchController(scenarioReader context) {
		this.context = context;
		// this.response = context.get("response");
		// this.data = context.get("testData");
	}

	public void batchCreateRequest() {
		String scenarioName = context.get("scenarioName");
		batchPojo data = JsonDataReader.getBatchData(scenarioName);
		context.set("testData", data);
		System.out.println("Scenario Name from JSON: " + data.scenarioName);
	}

	public void batchPOSTRequest() {
		batchPojo data = context.get("testData");

		Map<String, Object> body = new HashMap<>();

		body.put("batchName",
				(data.batchName == null || data.batchName.trim().isEmpty()) ? curr_batch_name : data.batchName);
		// body.put("batchName", curr_batch_name);
		body.put("batchDescription", data.batchDescription);
		body.put("batchStatus", data.batchStatus);
		body.put("batchNoOfClasses", data.batchNoOfClasses);
		body.put("programId", curr_program_id);
		body.put("programName", (data.programName == null || data.programName.trim().isEmpty()
				|| data.programName.contains("{program_name}")) ? curr_program_name : data.programName);

		response = given().log().all().baseUri(baseURL).header("Authorization", "Bearer " + token)
				.contentType(ContentType.JSON).when().body(body).request(data.requestType, "/" + data.endPoint);

		context.set("response", response);

	}

	public void validatePostBatchSchema() {

		response.then().assertThat().body(matchesJsonSchemaInClasspath("TestData/batchPostSchema.json"));
		System.out.println("Schema Validation Passed");

	}

	public void validatePostBatchResponseData() {

		String batchName = response.jsonPath().getString("batchName");
		String programName = response.jsonPath().getString("programName");
		// check mandatory data
		response.then().body("batchName", notNullValue()).body("batchStatus", notNullValue())
				.body("batchNoOfClasses", notNullValue()).body("programId", notNullValue());

		response.then().body("batchId", greaterThan(0));

		response.then().body("batchStatus", anyOf(equalToIgnoringCase("Active"), equalToIgnoringCase("Inactive")));

		response.then().body("batchName", matchesPattern(programName + "_\\d+"));

		response.then().body("batchName.length()", greaterThanOrEqualTo(6)).body("batchName.length()",
				lessThanOrEqualTo(28));

		String desc = response.jsonPath().getString("batchDescription");

		if (desc != null && !desc.trim().isEmpty()) {

			response.then().body("batchDescription", matchesPattern("^[A-Za-z][A-Za-z0-9,\\-:._ ]*$"));
		}
		System.out.println("Data Validations Passed");
	}

	// batch - Send GET request
	public void sendGetRequest() {
		response = context.get("response");
		batchPojo data = context.get("testData");
		String endpoint = data.endPoint;

		if (endpoint.contains("{batch_id}")) {
			endpoint = endpoint.replace("{batch_id}", String.valueOf(curr_batch_id));
		}

		if (endpoint.contains("{batch_name}")) {
			endpoint = endpoint.replace("{batch_name}", curr_batch_name);
		}

		if (endpoint.contains("{program_id}")) {
			endpoint = endpoint.replace("{program_id}", String.valueOf(curr_program_id));
		}

		response = given().log().all().baseUri(baseURL).header("Authorization", "Bearer " + token)
				.contentType(ContentType.JSON).when()
				// .request(data.requestType, "/" + data.endPoint);
				.request(data.requestType, "/" + endpoint);
		context.set("response", response);

	}

	public void validateStatusCodeResponse() {
		batchPojo data = context.get("testData");
		// Response response = context.get("response");
		response = context.get("response");
		response.then().log().all() // logs only if test fails
				.statusCode(data.expectedStatusCode);
		System.out.println("Status code passed");
	}

	public void validateStatusLineResponse() {
		batchPojo data = context.get("testData");
		// Response response = context.get("response");
		response = context.get("response");
		response.then().log().ifValidationFails() // logs only if test fails
				.statusLine(containsStringIgnoringCase(data.expectedstatusline));
		System.out.println("Status Line passed");
	}

	public void validateResponseTime() {

		response = context.get("response");
		response.then().log().ifValidationFails().time(lessThan(2000L), TimeUnit.MILLISECONDS);
	}

	public void validateContentTypeResponse() {
		// batchPojo data = context.get("testData");
		response = context.get("response");
		response.then().log().ifValidationFails() // logs only if test fails
				.header("Content-Type", containsString("application/json"));
		System.out.println("Content Type is application/json");
	}

	@Given("Admin sets valid authorization")
	public void admin_sets_valid_authorization() {
		token = commonVariables.token;
	}

	@Given("Admin creates POST request with mandatory and optional fields")
	public void admin_creates_post_request_with_mandatory_and_optional_fields() {

		batchCreateRequest();
	}

	@When("Admin sends HTTPS request to the endpoint for POST in Batch controller")
	public void admin_sends_https_request_to_the_endpoint_for_post_in_batch_controller() {
		curr_program_id = commonVariables.valid_programid;
		curr_program_name = commonVariables.valid_programname;
		int randomNum = new java.util.Random().nextInt(1000); // 0–999

		curr_batch_name = curr_program_name + "_" + randomNum;
		System.out.println(curr_batch_name);
		batchPOSTRequest();
	}

	@Then("Admin receives status code with response body")
	public void admin_receives_status_code_with_response_body() {
		batchPojo data = context.get("testData");

		response = context.get("response");

		validateStatusCodeResponse();
		validateStatusLineResponse();
		validateResponseTime();

		if ((response.getStatusCode() == 200 && data.expectedStatusCode == 200)
				|| (response.getStatusCode() == 201 && data.expectedStatusCode == 201)) {

			validateContentTypeResponse();

			if ((data.requestType.equalsIgnoreCase("POST")) || (data.requestType.equalsIgnoreCase("PUT"))) {

				curr_batch_id = response.jsonPath().getInt("batchId");// d
				commonVariables.valid_batchid = curr_batch_id;
				System.out.println(commonVariables.valid_batchid);

				curr_batch_name = response.jsonPath().getString("batchName");
				commonVariables.valid_batchname = curr_batch_name;
				System.out.println(commonVariables.valid_batchname);

				validatePostBatchSchema();
				validatePostBatchResponseData();
			}

		}
	}

	@Given("Admin creates a POST request with program name that does not match the associated program id")
	public void admin_creates_a_post_request_with_program_name_that_does_not_match_the_associated_program_id() {
		batchCreateRequest();
	}

	@Then("Admin receives status code and response body contains the program details corresponding to the provided program id")
	public void admin_receives_status_code_and_response_body_contains_the_program_details_corresponding_to_the_provided_program_id() {
		batchPojo data = context.get("testData");
		response = context.get("response");

		validateStatusCodeResponse();
		validateStatusLineResponse();
		validateResponseTime();

		if ((response.getStatusCode() == 200 && data.expectedStatusCode == 200)
				|| (response.getStatusCode() == 201 && data.expectedStatusCode == 201)) {

			validateContentTypeResponse();
			validatePostBatchSchema();
			validatePostBatchResponseData();

			response.then().body("programId", equalTo(commonVariables.valid_programid)).body("programName",
					equalTo(commonVariables.valid_programname));
			System.out.println("Response body contains the program details corresponding to the provided program id");
		}

	}

	@Given("Admin creates POST request with only optional fields in request body")
	public void admin_creates_post_request_with_only_optional_fields_in_request_body() {
		batchCreateRequest();
	}

	@Given("Admin creates POST request with only mandatory fields in request body")
	public void admin_creates_post_request_with_only_mandatory_fields_in_request_body() {
		batchCreateRequest();

	}

	@Given("Admin creates POST request without underscore in batch name")
	public void admin_creates_post_request_without_underscore_in_batch_name() {
		batchCreateRequest();

	}

	@Given("Admin creates POST request with hyphen in batch name")
	public void admin_creates_post_request_with_hyphen_in_batch_name() {
		batchCreateRequest();
	}

	@Given("Admin creates POST request with characters in the suffix of batch name")
	public void admin_creates_post_request_with_characters_in_the_suffix_of_batch_name() {
		batchCreateRequest();

	}

	@Given("Admin creates POST request with special characters in the suffix of batch name")
	public void admin_creates_post_request_with_special_characters_in_the_suffix_of_batch_name() {
		batchCreateRequest();
	}

	@Given("Admin creates POST request with batch name length more than max characters including prefixed program name")
	public void admin_creates_post_request_with_batch_name_length_more_than_max_characters_including_prefixed_program_name() {
		batchCreateRequest();

	}

	@Given("Admin creates POST request with batch name length less than six characters including prefixed program name")
	public void admin_creates_post_request_with_batch_name_length_less_than_six_characters_including_prefixed_program_name() {
		batchCreateRequest();

	}

	@Given("Admin creates POST request with batch name that is already existing in the system")
	public void admin_creates_post_request_with_batch_name_that_is_already_existing_in_the_system() {
		batchCreateRequest();

	}

	@Given("Admin creates POST request with batch description less than four characters")
	public void admin_creates_post_request_with_batch_description_less_than_four_characters() {
		batchCreateRequest();

	}

	@Given("Admin creates POST request with batch description more than twenty five characters")
	public void admin_creates_post_request_with_batch_description_more_than_twenty_five_characters() {
		batchCreateRequest();
	}

	@Given("Admin creates POST request with random characters in status field")
	public void admin_creates_post_request_with_random_characters_in_status_field() {
		batchCreateRequest();

	}

	@Given("Admin creates POST request with random numbers in status field")
	public void admin_creates_post_request_with_random_numbers_in_status_field() {
		batchCreateRequest();
	}

	@Given("Admin creates POST request with special characters in status field")
	public void admin_creates_post_request_with_special_characters_in_status_field() {
		batchCreateRequest();
	}

	@Given("Admin creates POST request with number of classes length less than one")
	public void admin_creates_post_request_with_number_of_classes_length_less_than_one() {
		batchCreateRequest();
	}

	@Given("Admin creates POST request with with number of classes length more than allowed")
	public void admin_creates_post_request_with_with_number_of_classes_length_more_than_allowed() {
		batchCreateRequest();
	}

	@Given("Admin creates POST request with inactive program id")
	public void admin_creates_post_request_with_inactive_program_id() {
		batchCreateRequest();
	}

	@When("Admin sends HTTPS request to the endpoint with inactive program id for POST in Batch controller")
	public void admin_sends_https_request_to_the_endpoint_with_inactive_program_id_for_post_in_batch_controller() {
		curr_program_id = commonVariables.inactive_programid;
		curr_program_name = commonVariables.inactive_programname;
		int randomNum = new java.util.Random().nextInt(1000); // 0–999

		curr_batch_name = curr_program_name + "_" + randomNum;
		System.out.println(curr_batch_name);
		batchPOSTRequest();

	}

	@Given("Admin creates POST request with program id that is not exist in the system")
	public void admin_creates_post_request_with_program_id_that_is_not_exist_in_the_system() {
		batchCreateRequest();
	}

	@When("Admin sends HTTPS request to the endpoint with invalid program id for POST in Batch controller")
	public void admin_sends_https_request_to_the_endpoint_with_invalid_program_id_for_post_in_batch_controller() {

		curr_program_id = commonVariables.invalid_programid;
		curr_program_name = commonVariables.invalid_programname;
		int randomNum = new java.util.Random().nextInt(1000); // 0–999

		curr_batch_name = curr_program_name + "_" + randomNum;
		System.out.println(curr_batch_name);
		batchPOSTRequest();
	}

	@Given("Admin creates POST request with valid request body and invalid end point")
	public void admin_creates_post_request_with_valid_request_body_and_invalid_end_point() {

		batchCreateRequest();

	}

	@Given("Admin creates POST request with invalid content type in Batch Controller")
	public void admin_creates_post_request_with_invalid_content_type_in_batch_controller() {
		batchCreateRequest();
	}

	@When("Admin sends a HTTPS request to the valid endpoint with invalid content type for POST in Batch controller")
	public void admin_sends_a_https_request_to_the_valid_endpoint_with_invalid_content_type_for_post_in_batch_controller() {
		curr_program_id = commonVariables.valid_programid;
		curr_program_name = commonVariables.valid_programname;
		int randomNum = new java.util.Random().nextInt(1000); // 0–999

		curr_batch_name = curr_program_name + "_" + randomNum;
		System.out.println(curr_batch_name);

		batchPojo data = context.get("testData");

		Map<String, Object> body = new HashMap<>();

		body.put("batchName",
				(data.batchName == null || data.batchName.trim().isEmpty()) ? curr_batch_name : data.batchName);
		// body.put("batchName", curr_batch_name);
		body.put("batchDescription", data.batchDescription);
		body.put("batchStatus", data.batchStatus);
		body.put("batchNoOfClasses", data.batchNoOfClasses);
		body.put("programId", curr_program_id);
		body.put("programName", (data.programName == null || data.programName.trim().isEmpty()
				|| data.programName.contains("{program_name}")) ? curr_program_name : data.programName);

		response = given().log().all().baseUri(baseURL).header("Authorization", "Bearer " + token)
				.contentType(ContentType.TEXT).when().body(body).request(data.requestType, "/" + data.endPoint);

		context.set("response", response);

	}

	@Given("Admin creates GET request with valid request body")
	public void admin_creates_get_request_with_valid_request_body() {
		batchCreateRequest();
	}

	@When("Admin sends GET HTTPS request to the valid endpoint for POST in Batch controller")
	public void admin_sends_get_https_request_to_the_valid_endpoint_for_post_in_batch_controller() {
		curr_program_id = commonVariables.valid_programid;
		curr_program_name = commonVariables.valid_programname;
		int randomNum = new java.util.Random().nextInt(1000); // 0–999

		curr_batch_name = curr_program_name + "_" + randomNum;
		System.out.println(curr_batch_name);
		batchPOSTRequest();
	}

	@Then("Admin receives Status Code with message {string}")
	public void admin_receives_status_code_with_message(String msg) {
		batchPojo data = context.get("testData");

		response = context.get("response");

		validateStatusCodeResponse();
		validateStatusLineResponse();
		validateResponseTime();

		response.then().body(containsStringIgnoringCase(msg));

		System.out.println("Expected Message " + msg + " is displayed");

	}

//////////////////////////////////////////////BATCH GET REQUEST/////////////////////////////////////////////////////

	@Given("Admin creates GET request")
	public void admin_creates_get_request() {

		curr_batch_id = commonVariables.valid_batchid;
		curr_batch_name = commonVariables.valid_batchname;
		curr_program_id = commonVariables.valid_programid;
		batchCreateRequest();
	}

	@When("Admin sends HTTPS request to the invalid endpoint")
	public void admin_sends_https_request_to_the_invalid_endpoint() {
		curr_batch_id = commonVariables.valid_batchid;
		curr_batch_name = commonVariables.valid_batchname;
		curr_program_id = commonVariables.valid_programid;
		sendGetRequest();
	}

	@Then("Admin receives status code with error message")
	public void admin_receives_status_code_with_error_message() {
		validateStatusCodeResponse();
		validateStatusLineResponse();
		validateResponseTime();

		response = context.get("response");
		String contentType = response.getContentType();

		if (contentType.contains("application/json")) {
			response.then().body("message", notNullValue());
			System.out.println("Error message JSON displayed");
		} else {
			response.then().body(containsString("Invalid"));
			System.out.println("Text message displayed");
		}

	}

	@Given("Admin creates POST request")
	public void admin_creates_post_request() {
		batchCreateRequest();

	}

	@Given("Admin creates GET request with invalid content type")
	public void admin_creates_get_request_with_invalid_content_type() {
		batchCreateRequest();
	}

	@When("Admin sends HTTPS request to the endpoint with invalid content type")
	public void admin_sends_https_request_to_the_endpoint_with_invalid_content_type() {
		// d
		curr_batch_id = commonVariables.valid_batchid;
		curr_batch_name = commonVariables.valid_batchname;
		curr_program_id = commonVariables.valid_programid;
		batchPojo data = context.get("testData");

		String endpoint = data.endPoint;

		if (endpoint.contains("{batch_id}")) {
			endpoint = endpoint.replace("{batch_id}", String.valueOf(curr_batch_id));
		}

		if (endpoint.contains("{batch_name}")) {
			endpoint = endpoint.replace("{batch_name}", curr_batch_name);
		}

		if (endpoint.contains("{program_id}")) {
			endpoint = endpoint.replace("{program_id}", String.valueOf(curr_program_id));
		}

		// Map<String, Object> body = new HashMap<>();

		response = given().log().all().baseUri(baseURL).header("Authorization", "Bearer " + token)
				.contentType(ContentType.TEXT)// .when().request(data.requestType, "/" + data.endPoint);
				.request(data.requestType, "/" + endpoint);

		context.set("response", response);
	}

	@When("Admin sends HTTPS request to the endpoint")
	public void admin_sends_https_request_to_the_endpoint() {

		sendGetRequest();
	}

	@Given("Admin creates GET request with valid Batch ID")
	public void admin_creates_get_request_with_valid_batch_id() {
		curr_batch_id = commonVariables.valid_batchid;
		curr_batch_name = commonVariables.valid_batchname;
		curr_program_id = commonVariables.valid_programid;
		batchCreateRequest();

	}

	@Given("Admin creates GET request with inactive Batch ID")
	public void admin_creates_get_request_with_inactive_batch_id() {

		curr_batch_id = commonVariables.inactive_batchid;
		batchCreateRequest();
	}

	@Given("Admin creates GET request with invalid Batch ID")
	public void admin_creates_get_request_with_invalid_batch_id() {

		curr_batch_id = commonVariables.invalid_batchid;
		batchCreateRequest();
	}

	@Given("Admin creates POST request with valid endpoint")
	public void admin_creates_post_request_with_valid_endpoint() {

		curr_batch_id = commonVariables.valid_batchid;
		batchCreateRequest();
	}

	@When("Admin sends POST HTTPS request to the endpoint")
	public void admin_sends_post_https_request_to_the_endpoint() {
		curr_batch_id = commonVariables.valid_batchid;
		curr_batch_name = commonVariables.valid_batchname;
		curr_program_id = commonVariables.valid_programid;
		sendGetRequest();
	}

	@Given("Admin creates GET request with invalid Batch Name")
	public void admin_creates_get_request_with_invalid_batch_name() {
		curr_batch_name = commonVariables.invalid_batchname;
		batchCreateRequest();
	}

	@Given("Admin creates GET request with valid Batch Name")
	public void admin_creates_get_request_with_valid_batch_name() {

		curr_batch_name = commonVariables.valid_batchname;
		batchCreateRequest();

	}

	@Given("Admin creates GET request with invalid Program Id")
	public void admin_creates_get_request_with_invalid_program_id() {

		curr_program_id = commonVariables.invalid_programid;
		batchCreateRequest();

	}

	@Given("Admin creates GET request with valid Program Id")
	public void admin_creates_get_request_with_valid_program_id() {
		curr_program_id = commonVariables.valid_programid;
		batchCreateRequest();
	}

///////////////////////////////////////////// BATCH UPDATE REQUESTS ////////////////////////////////////////////////

	public void updateSendRequest() {
		batchPojo data = context.get("testData");
		Map<String, Object> body = new HashMap<>();

		body.put("batchName",
				(data.batchName == null || data.batchName.trim().isEmpty()) ? curr_batch_name : data.batchName);
		// body.put("batchName", curr_batch_name);
		body.put("batchDescription", data.batchDescription);
		body.put("batchStatus", data.batchStatus);
		body.put("batchNoOfClasses", data.batchNoOfClasses);
		body.put("programId", curr_program_id);
		body.put("programName", (data.programName == null || data.programName.trim().isEmpty()
				|| data.programName.contains("{program_name}")) ? curr_program_name : data.programName);

		response = given().log().all().baseUri(baseURL).header("Authorization", "Bearer " + token)
				.contentType(ContentType.JSON).when().body(body)
				.request(data.requestType, "/" + data.endPoint + "/" + curr_batch_id);

		context.set("response", response);

	}

	@Given("Admin creates PUT request with valid BatchId")
	public void admin_creates_put_request_with_valid_batch_id() {
		curr_program_id = commonVariables.valid_programid;
		curr_program_name = commonVariables.valid_programname;
		curr_batch_name = commonVariables.valid_batchname;
		curr_batch_id = commonVariables.valid_batchid;
		batchCreateRequest();

	}

	@When("Admin sends PUT HTTPS request to the endpoint")
	public void admin_sends_put_https_request_to_the_endpoint() {
		curr_program_id = commonVariables.valid_programid;
		curr_program_name = commonVariables.valid_programname;
		int randomNum = new java.util.Random().nextInt(1000); // 0–999

		curr_batch_name = curr_program_name + "_" + randomNum;
		System.out.println(curr_batch_name);

		System.out.println(curr_batch_name);
		updateSendRequest();

	}
/////////////////////////////////////////////////////////////

	@Given("Admin creates PUT request with Invalid batch id")
	public void admin_creates_put_request_with_invalid_batch_id() {
		curr_program_id = commonVariables.valid_programid;
		curr_program_name = commonVariables.valid_programname;
		curr_batch_name = commonVariables.valid_batchname;
		curr_batch_id = commonVariables.invalid_batchid;
		batchCreateRequest();
	}

	///////////////

	@Given("Admin creates PUT request with out mandatory fileds")
	public void admin_creates_put_request_with_out_mandatory_fileds() {
		curr_program_id = commonVariables.valid_programid;
		curr_program_name = commonVariables.valid_programname;
		curr_batch_name = commonVariables.valid_batchname;
		curr_batch_id = commonVariables.valid_batchid;
		batchCreateRequest();
	}

	@Given("Admin creates PUT request with duplicate batchname")
	public void admin_creates_put_request_with_duplicate_batchname() {
		curr_program_id = commonVariables.valid_programid;
		curr_program_name = commonVariables.valid_programname;
		curr_batch_name = commonVariables.valid_batchname;
		curr_batch_id = commonVariables.valid_batchid;
		batchCreateRequest();
	}

	@Given("Admin creates a PUT request with invalid program id")
	public void admin_creates_a_put_request_with_invalid_program_id() {
		curr_program_id = commonVariables.invalid_programid;
		curr_program_name = commonVariables.valid_programname;
		curr_batch_name = commonVariables.valid_batchname;
		curr_batch_id = commonVariables.valid_batchid;
		batchCreateRequest();
	}

	@Given("Admin creates PUT request with inactive program")
	public void admin_creates_put_request_with_inactive_program() {
		curr_program_id = commonVariables.inactive_programid;
		curr_program_name = commonVariables.inactive_programname;
		curr_batch_name = commonVariables.valid_batchname;
		curr_batch_id = commonVariables.valid_batchid;
		batchCreateRequest();
	}

	@When("Admin sends HTTPS PUT request to the invalid endpoint")
	public void admin_sends_https_put_request_to_the_invalid_endpoint() {
		curr_program_id = commonVariables.valid_programid;
		curr_program_name = commonVariables.valid_programname;
		int randomNum = new java.util.Random().nextInt(1000); // 0–999

		curr_batch_name = curr_program_name + "_" + randomNum;
		System.out.println(curr_batch_name);
		
		System.out.println(curr_batch_name);
		updateSendRequest();
	}

	@Given("Admin creates POST request with valid request body")
	public void admin_creates_post_request_with_valid_request_body() {
		batchCreateRequest();

	}

	@Given("Admin creates PUT request with invalid content type")
	public void admin_creates_put_request_with_invalid_content_type() {
		curr_program_id = commonVariables.inactive_programid;
		curr_program_name = commonVariables.inactive_programname;
		curr_batch_name = commonVariables.valid_batchname;
		curr_batch_id = commonVariables.valid_batchid;
		batchCreateRequest();

	}

	@Given("Admin creates a PUT request with program name that does not match the associated program id")
	public void admin_creates_a_put_request_with_program_name_that_does_not_match_the_associated_program_id() {
		curr_program_id = commonVariables.inactive_programid;
		// curr_program_name = commonVariables.inactive_programname;
		curr_batch_name = commonVariables.valid_batchname;
		curr_batch_id = commonVariables.valid_batchid;
		batchCreateRequest();

	}

	@When("Admin sends POST HTTPS request to the endpoint with invalid method")
	public void admin_sends_post_https_request_to_the_endpoint_with_invalid_method() {
		curr_program_id = commonVariables.valid_programid;
		curr_program_name = commonVariables.valid_programname;
		int randomNum = new java.util.Random().nextInt(1000); // 0–999

		curr_batch_name = curr_program_name + "_" + randomNum;
		System.out.println(curr_batch_name);
		

		System.out.println(curr_batch_name);
		updateSendRequest();
	}

	@When("Admin sends PUT HTTPS request to the endpoint with invalid content type")
	public void admin_sends_put_https_request_to_the_endpoint_with_invalid_content_type() {
		batchPojo data = context.get("testData");
		Map<String, Object> body = new HashMap<>();

		body.put("batchName",
				(data.batchName == null || data.batchName.trim().isEmpty()) ? curr_batch_name : data.batchName);
		// body.put("batchName", curr_batch_name);
		body.put("batchDescription", data.batchDescription);
		body.put("batchStatus", data.batchStatus);
		body.put("batchNoOfClasses", data.batchNoOfClasses);
		body.put("programId", curr_program_id);
		body.put("programName", (data.programName == null || data.programName.trim().isEmpty()
				|| data.programName.contains("{program_name}")) ? curr_program_name : data.programName);

		response = given().log().all().baseUri(baseURL).header("Authorization", "Bearer " + token)
				.contentType(ContentType.TEXT).when().body(body)
				.request(data.requestType, "/" + data.endPoint + "/" + curr_batch_id);

		context.set("response", response);

	}


	
///////////////////////////////////////////DELETE REQUEST///////////////////////////////////
	
	public void deleteSendRequest()
	{
	response = context.get("response");
	batchPojo data = context.get("testData");
	//String endpoint = data.endPoint;

	response = given().log().all().baseUri(baseURL).header("Authorization", "Bearer " + token)
			.contentType(ContentType.JSON).when()
			// .request(data.requestType, "/" + data.endPoint);
			.request(data.requestType, "/" + data.endPoint + "/" + curr_batch_id);

	context.set("response", response);

  }


@Given("Admin creates DELETE request with valid BatchId")
public void admin_creates_delete_request_with_valid_batch_id() {
    curr_batch_id = commonVariables.valid_batchid;
	batchCreateRequest();
}

@When("Admin sends Delete HTTPS request to the endpoint")
public void admin_sends_delete_https_request_to_the_endpoint() {
    
	deleteSendRequest();
}

@Then("Admin receives Ok status with message")
public void admin_receives_ok_status_with_message() {
	validateStatusCodeResponse();
	validateStatusLineResponse();
	validateResponseTime();
}

@Given("Admin creates DELETE request with invalid BatchId")
public void admin_creates_delete_request_with_invalid_batch_id() {
    curr_batch_id = commonVariables.invalid_batchid;
	batchCreateRequest();
}

@When("Admin sends Delete HTTPS request to the invalid endpoint")
public void admin_sends_delete_https_request_to_the_invalid_endpoint() {

	deleteSendRequest();

	
}

@Given("Admin creates POST request with valid BatchId")
public void admin_creates_post_request_with_valid_batch_id() {

	curr_batch_id = commonVariables.valid_batchid;
	batchCreateRequest();
}    


@When("Admin sends a POST HTTPS request to the valid endpoint")
public void admin_sends_a_post_https_request_to_the_valid_endpoint() {
     deleteSendRequest();
}

@Given("Admin creates DELETE request with invalid content type")
public void admin_creates_delete_request_with_invalid_content_type() {
	curr_batch_id = commonVariables.valid_batchid;
	batchCreateRequest();
    }


@When("Admin sends Delete HTTPS request to the valid endpoint with invalid content type")
public void admin_sends_delete_https_request_to_the_valid_endpoint_with_invalid_content_type() {
	response = context.get("response");
	batchPojo data = context.get("testData");
	//String endpoint = data.endPoint;

	response = given().log().all().baseUri(baseURL).header("Authorization", "Bearer " + token)
			.contentType(ContentType.TEXT).when()
			// .request(data.requestType, "/" + data.endPoint);
			.request(data.requestType, "/" + data.endPoint + "/" + curr_batch_id);

	context.set("response", response);

  }


}



