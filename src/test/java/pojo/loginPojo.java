package pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class loginPojo {
	public String scenarioName;
    public String endPoint;
    public String userLoginEmailId;
    public String password;
    public String requestType;
    public int expectedStatusCode;
    public String expectedstatusline;
    public String expectedMessage;
}
