package pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)

public class batchPojo {
	    public String scenarioName;
	    public String endPoint;
	    public String requestType;
	    public int expectedStatusCode;
	    public String expectedstatusline;	    

	    public String batchName;
	    public String batchDescription;                                                        
	    public String batchStatus;
	    public int batchNoOfClasses;
	    //public String programId;
	    public String programName;
	}