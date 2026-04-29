package runner;
import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;


@CucumberOptions(

		plugin = { "pretty", "html:target/reports.html" }, // reporting purpose
		//dryRun = false, monochrome = true, // console output color
		// tags = "@HomeScenario", // tags from feature file
		features = { "src/test/resources/features" }, // location of feature files
		glue = { "stepDef" }) // location of step definition files
public class Runner extends AbstractTestNGCucumberTests {

}
