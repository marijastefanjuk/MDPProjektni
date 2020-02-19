package org.unibl.etf.mdp.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import org.json.JSONObject;
import org.unibl.etf.mdp.model.ExceptionHandler;
import org.unibl.etf.mdp.model.User;
import org.unibl.etf.mdp.model.UserChangePassword;
import org.unibl.etf.mdp.service.UserChat;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Alert.AlertType;

public class ChangePasswordController {

	@FXML
	private PasswordField oldPassword;

	@FXML
	private PasswordField newPassword;

	public static String BASE_URL = "";
	public static User user;
	private static final String PROPERTIES_FILE = "configuration"+File.separator+"user-configuration.properties";
	
	static {
		InputStream input;
		try {
			Properties prop = new Properties();
			input = new FileInputStream(PROPERTIES_FILE);
			prop.load(input);
			BASE_URL = prop.getProperty("REST_SERVICE_URL");
		} catch (FileNotFoundException e1) {
			ExceptionHandler.exceptionRecord(UserChat.class.getName(), e1.getMessage());
		} catch (IOException e) {
			ExceptionHandler.exceptionRecord(UserChat.class.getName(), e.getMessage());
		}
	}
	
	@FXML
	void changePassword(ActionEvent event) {
		try {
			URL url = new URL(BASE_URL + "update");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setRequestMethod("PUT");
			conn.setRequestProperty("Content-Type", "application/json");
			UserChangePassword userChange = new UserChangePassword(user.getUsername(), oldPassword.getText(),
					newPassword.getText());
			JSONObject json = new JSONObject(userChange);
			OutputStream os = conn.getOutputStream();
			os.write(json.toString().getBytes());
			os.flush();

			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				System.out.println("Failed : HTTP error code : " + conn.getResponseCode());
			} else {
				Alert alert = new Alert(AlertType.INFORMATION);
		        alert.setTitle("Promjena passworda");
		        alert.setHeaderText(null);
		        alert.setContentText("Password je promijenjen.");
		        alert.showAndWait();
			}

		} catch (MalformedURLException e) {
			ExceptionHandler.exceptionRecord(ChangePasswordController.class.getName(), e.getMessage());
		} catch (IOException e) {
			ExceptionHandler.exceptionRecord(ChangePasswordController.class.getName(), e.getMessage());
		}
	}
}
