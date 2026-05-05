package context;
import java.util.HashMap;
import java.util.Map;

public class scenarioReader {


	    private  Map<String, Object> data = new HashMap<>();

	    public  void set(String key, Object value) {
	        data.put(key, value);
	    }

	    public  <T> T get(String key) {
	    	
	        return (T) data.get(key);
	    }

	    public void setToken(String token) {
	        set("token", token);
	    }

	    public String getToken() {
	        return get("token");
	    }
	    public Integer getProgramId() {
	        return get("programId");
	    }

	    public void setProgramId(Integer id) {
	        set("programId", id);
	    } 
	}
