package stepdef;

import io.cucumber.java.en.*;
import pojo.ProgramPojo;
import pojo.loginPojo;
import utils.CommonMethods;
import utils.ConfigReader;
import utils.JsonDataReader;

import java.util.Map;

import context.scenarioReader;

public class userLogin {

    private scenarioReader context;
    private CommonMethods cm;

    private static final String LOGIN_FILE =
            "src/test/resources/TestData/testdata.json";
    //private String Program_FILE;

    public userLogin(scenarioReader context) {
        this.context = context;
        this.cm = new CommonMethods(context);
        //this.Program_FILE = ConfigReader.getKey("jsonFilePath");
    }

    @Given("Admin creates POST request with valid credentials")
    public void admin_creates_post_request_with_valid_credentials() {

        context.set("scenarioName",
                "Admin generates token with valid credentials");

        loginPojo data = JsonDataReader.getTestData(
                LOGIN_FILE,
                context.get("scenarioName"),
                loginPojo[].class
        );

        cm.setTestData(data);
    }

   
    @When("Admin sends a HTTPS request to the valid endpoint")
    public void admin_sends_a_https_request_to_the_valid_endpoint() {

        loginPojo data = cm.getTestData();

        cm.sendRequest(
                data.requestType,
                "/" + data.endPoint,
                Map.of(
                        "userLoginEmailId", data.userLoginEmailId,
                        "password", data.password
                ),
                null
        );
//        String token = response.jsonPath().getString("token");
//
//        // ✅ STORE TOKEN GLOBALLY
//        Token.setToken(token);

    }

    @Then("Admin receives status code with auto generated token")
    public void admin_receives_status_code_with_auto_generated_token() {

        loginPojo data = cm.getTestData();

        
        // VALIDATIONS
       
        cm.validateStatusCode(data.expectedStatusCode);
        cm.validateStatusLine(data.expectedstatusline);
        cm.validateContentType();

        
        // TOKEN EXTRACTION
        
        cm.storeToken("token");
        
    }
    
   /* @Given("Admin creates POST Request with {string}")
	public void admin_creates_post_request_with(String testCase) {
		ProgramPojo data = JsonDataReader.getTestData(
				Program_FILE,
				testCase,
	            ProgramPojo[].class
	    );

	    cm.setTestData(data); 
	}

	@When("Admin sends a HTTPS request to the endpoint")
	public void admin_sends_a_https_request_to_the_endpoint() {
	
		 ProgramPojo data = cm.getTestData();

		 cm.sendRequest(
			        data.requestType,
			        "/" + data.endPoint,
			        Map.of(
			                "programName", data.programName,
			                "programDescription", data.programDescription,
			                "programStatus", data.programStatus
			        ),
			        Map.of(
			                "Authorization", "Bearer " + context.getToken(),
			                "Content-Type", "application/json"
			        )
			);
	    }
	

	@Then("Admin receives {string} with {string}")
	public void admin_receives_with(String string, String string2) {
	   
		ProgramPojo data = cm.getTestData();
		 cm.validateStatusCode(data.expectedStatusCode);
	        cm.validateStatusLine(data.expectedstatusline);
	        cm.validateContentType();
}*/
}