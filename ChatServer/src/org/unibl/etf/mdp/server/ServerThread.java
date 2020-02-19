package org.unibl.etf.mdp.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Properties;

import org.unibl.etf.mdp.model.ExceptionHandler;
import org.unibl.etf.mdp.model.User;

public class ServerThread extends Thread {

	private Socket socket;
	private BufferedReader in;
	private PrintWriter out;
	static ArrayList<User> onlineUsers = new ArrayList<>();
	static String MESSAGE_FOLDER = "";
	private String messageSender;
	public static Object sync = new Object();
	private static PrintWriter adminOut;
	private static final String PROPERTIES_FILE = "configuration" + File.separator + "chatServer.properties";
	
	static {
		InputStream input;
		try {
			Properties prop = new Properties();
			input = new FileInputStream(PROPERTIES_FILE);
			prop.load(input);
			MESSAGE_FOLDER = prop.getProperty("MESSAGE_FOLDER");
		} catch (FileNotFoundException e1) {
			ExceptionHandler.exceptionRecord(ServerThread.class.getName(), e1.getMessage());
		} catch (IOException e) {
			ExceptionHandler.exceptionRecord(ServerThread.class.getName(), e.getMessage());
		}
	}

	public ServerThread(Socket ss) throws IOException {
		this.socket = ss;
		this.in = new BufferedReader(new InputStreamReader(ss.getInputStream()));
		this.out = new PrintWriter(new OutputStreamWriter(ss.getOutputStream()), true);
		start();
	}

	public void run() {
		while (true) {
			String request = "";
			try {
				request = in.readLine();
			} catch (IOException e2) {
				ExceptionHandler.exceptionRecord(ServerThread.class.getName(), e2.getMessage());
			}
			if (request.startsWith("sender##")) { // korisnik se prijavljuje
				String sender = request.split("##")[1];// sender##messageSender
				messageSender = sender;
				User newUser = new User(sender, socket, in, out);
				onlineUsers.add(newUser);
			} else if (request.startsWith("logout##")) {
				String username = request.split("##")[1];
				int index = -1;
				for (int i = 0; i < onlineUsers.size(); i++) {
					if (onlineUsers.get(i).getUsername().equals(username))
						index = i;
				}
				if (index >= 0)
					onlineUsers.remove(index);
			} else if (request.startsWith("restoreChat")) { // restoreChat##messageSender##messageReceiver

				String messageSender = request.split("##")[1];
				String messageReceiver = request.split("##")[2];
				if (new File(MESSAGE_FOLDER + File.separator + messageSender + "_" + messageReceiver + ".txt")
						.exists()) {
					synchronized (sync) {
						try {
							BufferedReader input = new BufferedReader(new FileReader(new File(
									MESSAGE_FOLDER + File.separator + messageSender + "_" + messageReceiver + ".txt")));
							String s;
							String pomoc = "restoreChat##";
							while ((s = input.readLine()) != null) {
								pomoc += (s + "<nl>");
							}
							input.close();
							out.println(pomoc);
							out.flush();
						} catch (FileNotFoundException e) {
							ExceptionHandler.exceptionRecord(ServerThread.class.getName(), e.getMessage());
						} catch (IOException e) {
							ExceptionHandler.exceptionRecord(ServerThread.class.getName(), e.getMessage());
						}
					}
				} else if (new File(MESSAGE_FOLDER + File.separator + messageReceiver + "_" + messageSender + ".txt")
						.exists()) {
					synchronized (sync) {
						BufferedReader input;
						try {
							input = new BufferedReader(new FileReader(new File(
									MESSAGE_FOLDER + File.separator + messageReceiver + "_" + messageSender + ".txt")));
							String s;
							String pomoc = "restoreChat##";
							while ((s = input.readLine()) != null) {
								pomoc += (s + "<nl>");
							}
							input.close();
							out.println(pomoc);
							out.flush();
						} catch (FileNotFoundException e) {
							ExceptionHandler.exceptionRecord(ServerThread.class.getName(), e.getMessage());
						} catch (IOException e) {
							ExceptionHandler.exceptionRecord(ServerThread.class.getName(), e.getMessage());
						}
					}
				} else {
					String pomoc = "restoreChat##FAJLNEPOSTOJI";
					out.println(pomoc);
					out.flush();
				}
			} else if (request.startsWith("LIST")) {
				String returnMessage = "LIST##";
				for (User us : onlineUsers) {
					returnMessage += us.getUsername() + "&&";
				}
				out.println(returnMessage);
				out.flush();
			} else if (request.startsWith("chat")) {// u slucaju da prijavljeni korisnik prima poruku i da je online
				File userMessages;
				String receiver = request.split("##")[2];
				String message = request.split("##")[3];

				User messageReceiver = null;
				for (User u : onlineUsers) {
					if (u.getUsername().equals(receiver)) {
						messageReceiver = u;
					}
				}
				if (messageReceiver != null) {
					messageReceiver.getOut().println("chat" + "##" + messageSender + ":" + message);
					messageReceiver.getOut().flush();
				}
				if (!(new File(MESSAGE_FOLDER + File.separator + messageSender + "_" + receiver + ".txt").exists())
						&& !(new File(MESSAGE_FOLDER + File.separator + receiver + "_" + messageSender + ".txt")
								.exists())) {
					// ako ne postoji ni jedan oblik fajlova u kojem su sacuvane poruke
					userMessages = new File(MESSAGE_FOLDER + File.separator + messageSender + "_" + receiver + ".txt");
					try {
						userMessages.createNewFile();
					} catch (IOException e) {
						ExceptionHandler.exceptionRecord(ServerThread.class.getName(), e.getMessage());
					}
					synchronized (sync) {
						PrintWriter write;
						try {
							write = new PrintWriter(new FileWriter(userMessages, true));
							write.append(messageSender + " : " + message + System.getProperty("line.separator"));
							write.flush();
							write.close();
						} catch (IOException e) {
							ExceptionHandler.exceptionRecord(ServerThread.class.getName(), e.getMessage());
						}
					}
				} else if (new File(MESSAGE_FOLDER + File.separator + messageSender + "_" + receiver + ".txt")
						.exists()) {
					synchronized (sync) {
						PrintWriter write;
						try {
							write = new PrintWriter(new FileWriter(
									new File(MESSAGE_FOLDER + File.separator + messageSender + "_" + receiver + ".txt"),
									true));
							write.append(messageSender + " : " + message + System.getProperty("line.separator"));
							write.flush();
							write.close();
						} catch (IOException e) {
							ExceptionHandler.exceptionRecord(ServerThread.class.getName(), e.getMessage());
						}
					}
				} else if (new File(MESSAGE_FOLDER + File.separator + receiver + "_" + messageSender + ".txt")
						.exists()) {
					synchronized (sync) {
						PrintWriter write;
						try {
							write = new PrintWriter(new FileWriter(
									new File(MESSAGE_FOLDER + File.separator + receiver + "_" + messageSender + ".txt"),
									true));
							write.append(messageSender + " : " + message + System.getProperty("line.separator"));
							write.flush();
							write.close();
						} catch (IOException e) {
							ExceptionHandler.exceptionRecord(ServerThread.class.getName(), e.getMessage());
						}
					}
				}
			} else if (request.startsWith("groupChat")) {
				String message = request.split("##")[2];
				for (User u : onlineUsers) {
					if (!u.getUsername().equals(messageSender)) {
						u.getOut().println("groupChat##" + messageSender + ":" + message);
						u.getOut().flush();
					}
				}
				if (!(new File(MESSAGE_FOLDER + File.separator + "groupChat.txt").exists())) {
					File groupMessages = new File(MESSAGE_FOLDER + File.separator + "groupChat.txt");
					try {
						groupMessages.createNewFile();
					} catch (IOException e) {
						ExceptionHandler.exceptionRecord(ServerThread.class.getName(), e.getMessage());
					}
					synchronized (sync) {
						PrintWriter write;
						try {
							write = new PrintWriter(new FileWriter(groupMessages, true));
							write.append(messageSender + " : " + message + System.getProperty("line.separator"));
							write.flush();
							write.close();
						} catch (IOException e) {
							ExceptionHandler.exceptionRecord(ServerThread.class.getName(), e.getMessage());
						}
					}
				} else {
					synchronized (sync) {
						PrintWriter write;
						try {
							write = new PrintWriter(
									new FileWriter(new File(MESSAGE_FOLDER + File.separator + "groupChat.txt"), true));
							write.append(messageSender + " : " + message + System.getProperty("line.separator"));
							write.flush();
							write.close();
						} catch (IOException e) {
							ExceptionHandler.exceptionRecord(ServerThread.class.getName(), e.getMessage());
						}
					}
				}
			} else if (request.startsWith("restoreGroupChat")) {

				if (new File(MESSAGE_FOLDER + File.separator + "groupChat.txt").exists()) {
					synchronized (sync) {
						try {
							BufferedReader input = new BufferedReader(
									new FileReader(new File(MESSAGE_FOLDER + File.separator + "groupChat.txt")));
							String s;
							String pomoc = "restoreGroupChat##";
							while ((s = input.readLine()) != null) {
								pomoc += (s + "<nl>");
							}
							input.close();
							out.println(pomoc);
							out.flush();
						} catch (FileNotFoundException e) {
							ExceptionHandler.exceptionRecord(ServerThread.class.getName(), e.getMessage());
						} catch (IOException e) {
							ExceptionHandler.exceptionRecord(ServerThread.class.getName(), e.getMessage());
						}
					}
				} else {
					String pomoc = "restoreGroupChat##FAJLNEPOSTOJI";
					out.println(pomoc);
					out.flush();
				}
			} else if (request.startsWith("Image")) {
				adminOut = out;
				String receiver = request.split("##")[1];
				User messageReceiver = null;
				for (User u : onlineUsers) {
					if (u.getUsername().equals(receiver)) {
						messageReceiver = u;
					}
				}
				if (messageReceiver != null) {
					messageReceiver.getOut().println(request);
					messageReceiver.getOut().flush();
				}
			} else if (request.startsWith("IMAGE##")) {
				adminOut.println(request);
				adminOut.flush();
			}
		}
	}
}
