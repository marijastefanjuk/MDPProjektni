package org.unibl.etf.mdp.model;

public class UserWrapper {

	private User user;
	private String dateLogin;
	private String dateLogout;
	
	public UserWrapper() {}
	
	public UserWrapper(User user, String dateLogin, String dateLogout) {
		super();
		this.user = user;
		this.dateLogin = dateLogin;
		this.dateLogout = dateLogout;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public String getDateLogin() {
		return dateLogin;
	}
	public void setDateLogin(String dateLogin) {
		this.dateLogin = dateLogin;
	}
	public String getDateLogout() {
		return dateLogout;
	}
	public void setDateLogout(String dateLogout) {
		this.dateLogout = dateLogout;
	}
	
	
}
