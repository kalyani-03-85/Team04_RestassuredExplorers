package hooks;

import context.commonVariables;
import context.scenarioReader;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import utils.CommonMethods;
import utils.LoggerReader;
import utils.CommonMethods;

public class Hooks {

    private scenarioReader context;
    private CommonMethods cm;


    public Hooks(scenarioReader context) {
        this.context = context;
        //Setup.context = context;
        this.cm = new CommonMethods(context); //code added after merge
    }

    @Before("@usersignin or @batchPost or @batchGET or @batchUpdate or @batchDelete")
    public void beforeScenario(Scenario scenario) {

       String scenarioName = scenario.getName();

        context.set("scenarioName", scenario.getName());

        System.out.println("Running Scenario: " + scenarioName);
    }
    
    //data added for merge
    
    @Before("@programPost or @programDelete or @programPUT or @programGET")
    public void ensureTokenForProgram() {

        if (commonVariables.token == null) {
            LoggerReader.error("Token missing before @program scenario.");
            throw new RuntimeException("Token missing before @program scenario.");
        }

        LoggerReader.info("Token available for @program scenario.");
    }
}