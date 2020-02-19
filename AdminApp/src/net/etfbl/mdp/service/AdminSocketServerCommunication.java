package net.etfbl.mdp.service;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.UnknownHostException;
import java.util.Base64;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.image.Image;
import net.etfbl.mdp.controller.AdminStartController;
import net.etfbl.mdp.model.ExceptionHandler;

public class AdminSocketServerCommunication extends Thread {

	private static String KEY_STORE_PATH = "";
	private static String KEY_STORE_PASSWORD = "";
	public static int TCP_PORT = 0;
	public static String IP_ADDRESS="";
	//private User user;
	private SSLSocket socket;
	private BufferedReader in;
	public static PrintWriter out;
	private ListView<String> activeUsersList;
	
	private static final String PROPERTIES_FILE = "configuration" + File.separator + "adminApp.properties";
	
	static {
		InputStream input;
		try {
			Properties prop = new Properties();
			input = new FileInputStream(PROPERTIES_FILE);
			prop.load(input);
			KEY_STORE_PATH = prop.getProperty("KEY_STORE_PATH");
			KEY_STORE_PASSWORD = prop.getProperty("KEY_STORE_PASSWORD");
			TCP_PORT = Integer.parseInt(prop.getProperty("TCP_PORT"));
			IP_ADDRESS=prop.getProperty("IP_ADDRESS");
		} catch (FileNotFoundException e1) {
			ExceptionHandler.exceptionRecord(AdminSocketServerCommunication.class.getName(), e1.getMessage());
		} catch (IOException e) {
			ExceptionHandler.exceptionRecord(AdminSocketServerCommunication.class.getName(), e.getMessage());
		}
	}

	public AdminSocketServerCommunication(ListView<String> activeUsersList) throws UnknownHostException, IOException {
		System.setProperty("javax.net.ssl.trustStore", KEY_STORE_PATH);
		System.setProperty("javax.net.ssl.trustStorePassword", KEY_STORE_PASSWORD);
		SSLSocketFactory sf = (SSLSocketFactory) SSLSocketFactory.getDefault();
		socket = (SSLSocket) sf.createSocket(IP_ADDRESS, TCP_PORT);
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
		this.activeUsersList = activeUsersList;
		start();
	}
	
	public AdminSocketServerCommunication() throws UnknownHostException, IOException {
		System.setProperty("javax.net.ssl.trustStore", KEY_STORE_PATH);
		System.setProperty("javax.net.ssl.trustStorePassword", KEY_STORE_PASSWORD);
		SSLSocketFactory sf = (SSLSocketFactory) SSLSocketFactory.getDefault();
		socket = (SSLSocket) sf.createSocket(IP_ADDRESS, TCP_PORT);
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
		start();
	}

	public static void startCommunication() {
		out.println("LIST##");
		out.flush();
	}
	
	public static void requestImage(String username) {
		out.println("Image##"+username);
		out.flush();
	}
	
	public void updateUserList(String response) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				String[] params = response.split("\\&\\&");
				activeUsersList.getItems().clear();
				activeUsersList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
				activeUsersList.getItems().addAll(params);
			}
		});
	}

	public void run() {
		try {
			while (true) {
				String receivedMessage = in.readLine();
				if (receivedMessage == null || receivedMessage.equals("")) {
                    continue;
				}else if(receivedMessage.startsWith("LIST")) {
					String[] listOfUsers=receivedMessage.split("##");
					if(listOfUsers.length>1) {
						String response=listOfUsers[1];
						updateUserList(response);
					}else {
						Platform.runLater(() -> {
							activeUsersList.getItems().clear();
		    	        });
					}
				}else if(receivedMessage.startsWith("IMAGE")) {
					String base64Image = receivedMessage.split("##")[1];
					AdminStartController.image=base64Image;
					
					byte[] img = Base64.getDecoder().decode(base64Image);
					ByteArrayInputStream bis = new ByteArrayInputStream(img);
					Image i=SwingFXUtils.toFXImage(ImageIO.read(bis),null);
					AdminStartController.controler.displayImage(i);
				}
			}
		} catch (IOException e1) {
			ExceptionHandler.exceptionRecord(AdminSocketServerCommunication.class.getName(), e1.getMessage());
		}
	}
}
