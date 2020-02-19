package org.unibl.etf.mdp.model;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

public class User {

	private String username;
	private Socket socket;
	private BufferedReader in;
	private PrintWriter out;

	public User(String username, Socket socket, BufferedReader in, PrintWriter out) {
		super();
		this.username = username;
		this.socket = socket;
		this.in = in;
		this.out = out;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public BufferedReader getIn() {
		return in;
	}

	public void setIn(BufferedReader in) {
		this.in = in;
	}

	public PrintWriter getOut() {
		return out;
	}

	public void setOut(PrintWriter out) {
		this.out = out;
	}

}
