package runner;
import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

public class Runner {

	
	@CucumberOptions(
	    features = "src/test/resources/features", // Path to feature files
	    glue = {"stepdef", "hooks"},              // Package with step definitions
	    tags = "@login or @post or @getoperations or @putoperations or @Logout",plugin = {"pretty", "html:target/cucumber-reports.html"}, 
	    monochrome = true
	)
	public class TestRunner extends AbstractTestNGCucumberTests{
	}
}
