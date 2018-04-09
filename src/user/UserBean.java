package user;

public class UserBean {
	String userName;
	String userPWD;
	boolean logined = false;	
	
	public UserBean() {}
	
	public String getUserName() {
		return userName;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public String getUserPWD() {
		return userPWD;
	}
	
	public void setUserPWD(String userPWD) {
		this.userPWD = userPWD;
	}
	
	public boolean isLogined() {
		return logined;
	}
	
	public void setLogined(boolean logined) {
		this.logined =logined;
	}
}
