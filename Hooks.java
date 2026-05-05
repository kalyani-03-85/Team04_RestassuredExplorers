package hooks;

import context.scenarioReader;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;

public class Hooks {

    private scenarioReader context;

    public Hooks(scenarioReader context) {
        this.context = context;
    }

    @Before
    public void beforeScenario(Scenario scenario) {

        String scenarioName = scenario.getName();

        System.out.println("Running Scenario: " + scenarioName);

        // Store in context (important for your JSON mapping)
        context.set("scenarioName", scenario.getName());
    }
}