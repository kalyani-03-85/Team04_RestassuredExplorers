package context;
import java.util.HashMap;
import java.util.Map;

public class scenarioReader {


	    private Map<String, Object> data = new HashMap<>();

	    public  void set(String key, Object value) {
	        data.put(key, value);
	    }

	    public  <T> T get(String key) {
	        return (T) data.get(key);
	    }
	}

