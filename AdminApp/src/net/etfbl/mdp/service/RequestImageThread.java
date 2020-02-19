package net.etfbl.mdp.service;
import java.util.TimerTask;

public class RequestImageThread extends TimerTask {

	private String username;
	
	public RequestImageThread(String username) {
		this.username=username;
	}
	
	public void run() {
		AdminSocketServerCommunication.requestImage(username);
	}
}
