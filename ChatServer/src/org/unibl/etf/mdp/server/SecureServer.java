package org.unibl.etf.mdp.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.util.Properties;

import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

import org.unibl.etf.mdp.model.ExceptionHandler;

public class SecureServer {

	private static int PORT = 0;
	private static String KEY_STORE_PATH = "";
	private static String KEY_STORE_PASSWORD = "";
	private static final String PROPERTIES_FILE = "configuration" + File.separator + "chatServer.properties";

	public static void main(String[] args) throws IOException {
		InputStream input;
		try {
			Properties prop = new Properties();
			input = new FileInputStream(PROPERTIES_FILE);
			prop.load(input);
			PORT = Integer.parseInt(prop.getProperty("PORT"));
			KEY_STORE_PATH = prop.getProperty("KEY_STORE_PATH");
			KEY_STORE_PASSWORD = prop.getProperty("KEY_STORE_PASSWORD");
		} catch (FileNotFoundException e1) {
			ExceptionHandler.exceptionRecord(SecureServer.class.getName(), e1.getMessage());
		} catch (IOException e) {
			ExceptionHandler.exceptionRecord(SecureServer.class.getName(), e.getMessage());
		}

		System.setProperty("javax.net.ssl.keyStore", KEY_STORE_PATH);
		System.setProperty("javax.net.ssl.keyStorePassword", KEY_STORE_PASSWORD);

		SSLServerSocketFactory ssf = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
		ServerSocket ss = ssf.createServerSocket(PORT);
		System.out.println("Server je pokrenut");
		while (true) {
			SSLSocket s = (SSLSocket) ss.accept();
			new ServerThread(s);
		}
	}
}
