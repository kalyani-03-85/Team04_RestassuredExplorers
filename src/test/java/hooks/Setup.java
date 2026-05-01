package hooks;
import java.util.Properties;
import io.cucumber.java.Before;
import io.restassured.RestAssured;
import utils.ConfigReader;
public class Setup {
	ConfigReader config=new ConfigReader();
	Properties properties;
		@Before
		public void baseurlsetup() {
			RestAssured.baseURI=config.getKey("baseURL");
			//String username = config.getKey("username");
			//String password = config.getKey("password");
		}
}
