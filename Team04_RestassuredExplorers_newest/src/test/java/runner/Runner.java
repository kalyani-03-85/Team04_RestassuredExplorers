package runner;
import org.testng.annotations.DataProvider;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;


@CucumberOptions(

		plugin = { "pretty", "html:target/reports.html" }, // reporting purpose
		//dryRun = false, monochrome = true, // console output color
		 // tags from feature file
		/*features = { "C:\\Users\\gayat\\git\\Team04_RestassuredExplorers\\src\\test\\resources\\Features\\01Login.feature",
		             "C:\\Users\\gayat\\git\\Team04_RestassuredExplorers\\src\\test\\resources\\Features\\02BatchPost.feature", // location of feature files
			         //"C:\\Users\\gayat\\git\\Team04_RestassuredExplorers\\src\\test\\resources\\Features\\03BatchGET.feature",
			         "C:\\Users\\gayat\\git\\Team04_RestassuredExplorers\\src\\test\\resources\\Features\\04BatchUpdate.feature",
			         "C:\\Users\\gayat\\git\\Team04_RestassuredExplorers\\src\\test\\resources\\Features\\05BatchDelete.feature"

		}, // location of feature files*/
		
				features = "src/test/resources/features",

			//tags = "@runnow",
		glue = { "StepDefinition", "hooks", "context", "utils" }) // location of step definition files

public class Runner extends AbstractTestNGCucumberTests {
	
/*	run parallel code
 * @Override
	@DataProvider(parallel = false)
	public Object[][] scenarios() 
	{
		return super.scenarios();
	} */

}