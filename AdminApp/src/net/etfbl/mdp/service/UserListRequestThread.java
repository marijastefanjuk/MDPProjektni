package net.etfbl.mdp.service;

import java.util.TimerTask;

public class UserListRequestThread extends TimerTask{
	public void run() {
		AdminSocketServerCommunication.startCommunication();
	}
}
