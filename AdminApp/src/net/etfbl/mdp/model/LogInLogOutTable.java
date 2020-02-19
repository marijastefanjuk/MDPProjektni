package net.etfbl.mdp.model;

public class LogInLogOutTable {

	private String login;
	private String logout;
	private String timeSpent;
	public LogInLogOutTable() {}
	
	public LogInLogOutTable(String login, String logout, String timeSpent) {
		super();
		this.login = login;
		this.logout = logout;
		this.timeSpent = timeSpent;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getLogout() {
		return logout;
	}

	public void setLogout(String logout) {
		this.logout = logout;
	}

	public String getTimeSpent() {
		return timeSpent;
	}

	public void setTimeSpent(String timeSpent) {
		this.timeSpent = timeSpent;
	}
	
	
}
