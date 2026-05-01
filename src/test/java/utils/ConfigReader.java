package utils;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
public class ConfigReader {
	
		
		static Properties properties;
		static String propertyFilePath = "src/test/resources/config/config.properties";

		// To get the path of config properties file
		public static void loadConfig() throws IOException {
		
				BufferedReader reader = new BufferedReader(new FileReader(propertyFilePath));
				
						properties = new Properties();
						properties.load(reader);
				
	}
		public static String getKey(String value) {
			return value;
		}
	}