package stepdef;

import io.cucumber.java.en.*;

public class Logout {
	@Given("Admin sets authorization to {string}")
	public void admin_sets_authorization_to(String string) {
	    System.out.println("test");
	}

	@When("Admin sends {string} request to {string}")
	public void admin_sends_request_to(String string, String string2) {
		System.out.println("test1");
	}

	@Then("Admin validates status code {int} with message {string}")
	public void admin_validates_status_code_with_message(Integer int1, String string) {
		System.out.println("test2");
	}
}
