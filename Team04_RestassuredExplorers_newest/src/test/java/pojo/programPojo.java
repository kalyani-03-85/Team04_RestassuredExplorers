package pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class programPojo {
	public String scenarioName;
    public String endPoint;
    public String requestType;
    public int expectedStatusCode;
    public String authType;
    public String contentType;
    public String expectedStatusLine;
    public String expectedMessage;
    public String programDescription;
    public String programName;
    public String programStatus;
    public String baseURI;
	public int programId;
	
	 private String email;
	    private String password;

	    public String getEmail() {
	        return email;
	    }

	    public void setEmail(String email) {
	        this.email = email;
	    }

	    public String getPassword() {
	        return password;
	    }

	    public void setPassword(String password) {
	        this.password = password;
	    }

   
}