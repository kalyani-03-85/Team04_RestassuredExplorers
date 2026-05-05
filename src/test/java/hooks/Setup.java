package hooks;

import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.restassured.RestAssured;
import pojo.loginPojo;
import utils.ConfigReader;
import utils.JsonDataReader;
import utils.LoggerReader;
import utils.globalvariables;
import utils.CommonMethods;

import java.util.Map;

import context.scenarioReader;

public class Setup {

    public static scenarioReader context;
    private CommonMethods cm;

    private static final String LOGIN_FILE =
            "src/test/resources/TestData/testdata.json";

    public Setup(scenarioReader context) {
        Setup.context = context;
        this.cm = new CommonMethods(context);
    }

    // ============================================================
    // ⭐ Before Hook — Initialize BaseURI + Scenario Name
    // ============================================================
    @Before(order = 0)
    public void init(Scenario scenario) {

        RestAssured.baseURI = ConfigReader.getKey("baseURL");
        LoggerReader.info("Loaded BaseURI: " + RestAssured.baseURI);

        context.set("scenarioName", scenario.getName());
        LoggerReader.info("Running Scenario: " + scenario.getName());
    }

    // ============================================================
    // ⭐ Before Hook — Global Login (Runs Once Per Test Run)
    // ============================================================
    @Before(order = 1)
    public void globalLogin() {

        CommonMethods.overrideBaseURL = null;

        if (globalvariables.token != null) {
            LoggerReader.info("Token already exists. Skipping global login.");
            return;
        }

        LoggerReader.info("Executing global login...");

        loginPojo data = JsonDataReader.getTestData(
                LOGIN_FILE,
                "Admin generates token with valid credentials",
                loginPojo[].class
        );

        cm.sendRequest(
                data.requestType,
                "/" + data.endPoint,
                Map.of(
                        "userLoginEmailId", data.userLoginEmailId,
                        "password", data.password
                ),
                null
        );

        cm.storeToken("token");

        LoggerReader.info("Global login successful. Token stored.");
    }

    // ============================================================
    // ⭐ Before Hook — Ensure Token Exists for @program Scenarios
    // ============================================================
    @Before("@program")
    public void ensureTokenForProgram() {

        if (globalvariables.token == null) {
            LoggerReader.error("Token missing before @program scenario.");
            throw new RuntimeException("Token missing before @program scenario.");
        }

        LoggerReader.info("Token available for @program scenario.");
    }
}
