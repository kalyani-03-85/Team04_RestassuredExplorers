package pojo;

import java.util.List;

public class LMSPojo {

	    public String userFirstName;
	    public String userMiddleName;
	    public String userLastName;
	    public String userVisaStatus;
	    public String userPhoneNumber;
	    public String userLocation;
	    public String userTimeZone;  
	    public UserLogin userLogin;
	    public List<UserRoleMap> userRoleMaps;
	    
	 // Getter & Setter for userFirstName
	    public String getUserFirstName() {
	        return userFirstName;
	    }

	    public void setUserFirstName(String userFirstName) {
	        this.userFirstName = userFirstName;
	    }

	    // Getter & Setter for userLastName
	    public String getUserLastName() {
	        return userLastName;
	    }

	    public void setUserLastName(String userLastName) {
	        this.userLastName = userLastName;
	    }

	    // Getter & Setter for userMiddleName
	    public String getUserMiddleName() {
	        return userMiddleName;
	    }

	    public void setUserMiddleName(String userMiddleName) {
	        this.userMiddleName = userMiddleName;
	    }

	    // Getter & Setter for userPhoneNumber
	    public String getUserPhoneNumber() {
	        return userPhoneNumber;
	    }

	    public void setUserPhoneNumber(String userPhoneNumber) {
	        this.userPhoneNumber = userPhoneNumber;
	    }

	    // Getter & Setter for userLocation
	    public String getUserLocation() {
	        return userLocation;
	    }

	    public void setUserLocation(String userLocation) {
	        this.userLocation = userLocation;
	    }

	    // Getter & Setter for userTimeZone
	    public String getUserTimeZone() {
	        return userTimeZone;
	    }

	    public void setUserTimeZone(String userTimeZone) {
	        this.userTimeZone = userTimeZone;
	    }

	    // Getter & Setter for userVisaStatus
	    public String getUserVisaStatus() {
	        return userVisaStatus;
	    }

	    public void setUserVisaStatus(String userVisaStatus) {
	        this.userVisaStatus = userVisaStatus;
	    }

	    // Getter & Setter for userLogin
	    public UserLogin getUserLogin() {
	        return userLogin;
	    }

	    public void setUserLogin(UserLogin userLogin) {
	        this.userLogin = userLogin;
	    }

	    // Getter & Setter for userRoleMaps
	    public List<UserRoleMap> getUserRoleMaps() {
	        return userRoleMaps;
	    }

	    public void setUserRoleMaps(List<UserRoleMap> userRoleMaps) {
	        this.userRoleMaps = userRoleMaps;
	    }

	
}
