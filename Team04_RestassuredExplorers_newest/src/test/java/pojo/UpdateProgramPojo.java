package pojo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateProgramPojo {
	public int programId;
	public String programName;
	public String programDescription;
	public String programStatus;
	public String creationTime;
	public String lastModTime;
	public String endPoint;
    public String requestType;
    public String scenarioName;
    public String expectedStatusLine;
    public String expectedMessage;
    public int expectedStatusCode;
    public String baseURI;
    public boolean isCustomBaseURI;

}
