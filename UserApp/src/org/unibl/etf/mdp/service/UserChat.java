package org.unibl.etf.mdp.service;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.UnknownHostException;

import javax.imageio.ImageIO;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import org.unibl.etf.mdp.model.ExceptionHandler;
import org.unibl.etf.mdp.model.User;
import java.util.Base64;
import java.util.Properties;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;

public class UserChat extends Thread {
	private static String KEY_STORE_PATH = "";
	private static String KEY_STORE_PASSWORD = "";
	public static int TCP_PORT = 0;
	public static String IP_ADDRESS=""; //localhost
	private User user;
	private static SSLSocket socket;
	private BufferedReader in;
	private PrintWriter out;
	private TextArea chat;
	private ListView<String> usersList;
	private CheckBox groupChat;

	private static final String PROPERTIES_FILE = "configuration"+File.separator+"user-configuration.properties";

	static {
		InputStream input;
		try {
			Properties prop = new Properties();
			input = new FileInputStream(PROPERTIES_FILE);
			prop.load(input);
			TCP_PORT = Integer.parseInt(prop.getProperty("TCP_PORT"));
			KEY_STORE_PATH = prop.getProperty("KEY_STORE_PATH");
			KEY_STORE_PASSWORD = prop.getProperty("KEY_STORE_PASSWORD");
			IP_ADDRESS=prop.getProperty("IP_ADDRESS");
		} catch (FileNotFoundException e1) {
			ExceptionHandler.exceptionRecord(UserChat.class.getName(), e1.getMessage());
		} catch (IOException e) {
			ExceptionHandler.exceptionRecord(UserChat.class.getName(), e.getMessage());
		}
	}

	public UserChat(User user, TextArea chat, ListView<String> list, CheckBox group) throws IOException {
		System.setProperty("javax.net.ssl.trustStore", KEY_STORE_PATH);
		System.setProperty("javax.net.ssl.trustStorePassword", KEY_STORE_PASSWORD);
		this.user = user;
		this.usersList = list;
		this.groupChat = group;
		SSLSocketFactory sf = (SSLSocketFactory) SSLSocketFactory.getDefault();
		socket = (SSLSocket) sf.createSocket(IP_ADDRESS, TCP_PORT);
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
		this.chat = chat;
		usersList.getSelectionModel().selectedItemProperty().addListener( // v-observable list
				(v, oldValue, newValue) -> {
					out.println("restoreChat##" + user.getUsername() + "##" + newValue);
					out.flush();
				});
		groupChat.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if(newValue) {
					out.println("restoreGroupChat");
					out.flush();
				}
			}
		});
		online();
		start();
	}

	public void online() {
		out.println("sender##" + user.getUsername());
		out.flush();
	}

	public void sendMessage(String message, String receiver) {
		out.println("chat" + "##" + user.getUsername() + "##" + receiver + "##" + message);
		out.flush();
	}

	public void logout() {
		out.println("logout##" + user.getUsername());
		out.flush();
	}

	public void sendGroupMessage(String message) {
		out.println("groupChat" + "##" + user.getUsername() + "##" + message);
		out.flush();
	}

	public void screenCapture() {
		Rectangle rectangle = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
		try {
			BufferedImage screen = new Robot().createScreenCapture(rectangle);
			ImageIO.write(screen, "jpg", new File("screenshot.jpg"));
			File transferFile = new File("screenshot.jpg");
			int varijaba = (int) transferFile.length();
			byte[] bytearray = new byte[varijaba];

			FileInputStream fin = new FileInputStream(transferFile);
			BufferedInputStream bin = new BufferedInputStream(fin);
			bin.read(bytearray, 0, bytearray.length);
			String outString = Base64.getEncoder().encodeToString(bytearray);
			out.println("IMAGE##" + outString);
			out.flush();
			bin.close();
			fin.close();
		} catch (IOException e) {
			ExceptionHandler.exceptionRecord(UserChat.class.getName(), e.getMessage());
		} catch (AWTException e) {
			ExceptionHandler.exceptionRecord(UserChat.class.getName(), e.getMessage());
		}
	}

	public void run() {
		try {
			while (true) {
				String receivedMessage = in.readLine();
				if (receivedMessage == null || receivedMessage.equals("")) {
					continue;
				} else if (receivedMessage.startsWith("chat")) {
					String message = receivedMessage.split("##")[1];
					String receiver = message.split(":")[0];
					String help = (String) usersList.getSelectionModel().getSelectedItem();
					if (help.equals(receiver)) {
						chat.appendText((message + System.getProperty("line.separator")));
					}
				} else if (receivedMessage.startsWith("groupChat")) {
					String message = receivedMessage.split("##")[1];
					if (groupChat.isSelected()) {
						chat.appendText((message + System.getProperty("line.separator")));
					}
				} else if (receivedMessage.startsWith("restoreGroupChat")) {
					String porukica = receivedMessage.split("##")[1];
					if (porukica.equals("FAJLNEPOSTOJI")) {
						chat.setText("");
					} else {
						StringBuilder chat1 = new StringBuilder("");
						String[] niz = porukica.split("<nl>");
						for (int i = 0; i < niz.length; i++) {
							chat1.append(niz[i] + System.getProperty("line.separator"));
						}
						chat.setText("");
						chat.appendText(chat1.toString());
					}
				} else if (receivedMessage.startsWith("restoreChat")) {
					String porukica = receivedMessage.split("##")[1];
					if (porukica.equals("FAJLNEPOSTOJI")) {
						chat.setText("");
					} else {
						StringBuilder chat1 = new StringBuilder("");
						String[] niz = porukica.split("<nl>");
						for (int i = 0; i < niz.length; i++) {
							chat1.append(niz[i] + System.getProperty("line.separator"));
						}
						chat.setText("");
						chat.appendText(chat1.toString());
					}
				} else if (receivedMessage.startsWith("Image")) {
					String[] help = receivedMessage.split("##");
					if (help[1].equals(user.getUsername()))
						screenCapture();
				}
			}
		} catch (IOException e) {
			ExceptionHandler.exceptionRecord(UserChat.class.getName(), e.getMessage());
		}
	}
}
