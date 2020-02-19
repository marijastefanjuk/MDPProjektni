package org.unibl.etf.mdp.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.json.JSONException;
import org.json.JSONObject;
import org.unibl.etf.mdp.model.ExceptionHandler;
import org.unibl.etf.mdp.model.User;
import org.unibl.etf.mdp.model.UserWrapper;
import org.unibl.etf.mdp.service.UserChat;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;

public class Main extends Application {
	private static boolean goodLogin = false;
	public static String BASE_URL = "";
	public UserWrapper currentUser;
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

	/*private static String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}*/

	/*public static JSONObject readOne(String username) throws IOException, JSONException {
		InputStream is = new URL(BASE_URL + username).openStream();
		try {
			BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
			String jsonText = readAll(rd);
			JSONObject json = new JSONObject(jsonText);
			return json;
		} finally {
			is.close();
		}
	}*/

	public boolean checkUserLogIn(User user) {
		try {
			URL url = new URL(BASE_URL + "login");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setRequestMethod("PUT");
			conn.setRequestProperty("Content-Type", "application/json");

			JSONObject json = new JSONObject(user);
			OutputStream os = conn.getOutputStream();
			os.write(json.toString().getBytes());
			os.flush();

			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				System.out.println("Failed : HTTP error code : " + conn.getResponseCode());
				return false;
			}
			
			currentUser = new UserWrapper();
			currentUser.setUser(user);
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = new Date();
			String datef = dateFormat.format(date);
			datef = datef.replace(" ", "T");
			currentUser.setDateLogin(datef);
			UserStartController.user = currentUser;
			conn.disconnect();

		} catch (MalformedURLException e) {
			ExceptionHandler.exceptionRecord(Main.class.getName(), e.getMessage());
			//System.out.println("url: "+BASE_URL);
			//e.printStackTrace();
			return false;
		} catch (IOException e) {
			ExceptionHandler.exceptionRecord(Main.class.getName(), e.getMessage());
			//e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public void start(Stage primaryStage) {
		try {
			primaryStage.setTitle("LogIn");
			Group root = new Group();
			Scene scene = new Scene(root, 300, 150, Color.WHITE);
			primaryStage.setScene(scene);

			GridPane gridpane = new GridPane();
			gridpane.setPadding(new Insets(5));
			gridpane.setHgap(5);
			gridpane.setVgap(5);

			Label userNameLbl = new Label("User Name: ");
			gridpane.add(userNameLbl, 0, 1);

			Label passwordLbl = new Label("Password: ");
			gridpane.add(passwordLbl, 0, 2);
			final TextField userNameFld = new TextField("marija");
			gridpane.add(userNameFld, 1, 1);

			final PasswordField passwordFld = new PasswordField();
			passwordFld.setText("marija");
			gridpane.add(passwordFld, 1, 2);

			Button login = new Button("LogIn");
			login.setOnAction(new EventHandler<ActionEvent>() {

				public void handle(ActionEvent event) {
					// primaryStage.close();
					String username = userNameFld.getText();
					String password = passwordFld.getText();
					if ("".equals(username) || "".equals(password)) {
						AlertHelper.showAlert(AlertType.ERROR, "Greska", "Nisu uneseni svi podaci");
						goodLogin = false;
					} else {
						// getProizvodi();
						User user = new User(username, password);
						if (checkUserLogIn(user)) {
							
							goodLogin = true;
						}
						else
							goodLogin = false;
					}
					if (goodLogin) {
						Parent korisnikView;
						try {
							FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/unibl/etf/mdp/gui/UserStart.fxml"));
							//korisnikView = FXMLLoader.load(getClass().getResource("/org/unibl/etf/mdp/gui/UserStart.fxml"));
							
							Scene scene2 = new Scene(loader.load());
							UserStartController controler = loader.getController();
							//Scene scene2 = new Scene(korisnikView);
							primaryStage.setScene(scene2);
							primaryStage.centerOnScreen();
							primaryStage.setResizable(false);
							primaryStage.show();
							
							primaryStage.setOnCloseRequest((new EventHandler<WindowEvent>() {

						         @Override
						         public void handle(WindowEvent event) {
						             Platform.runLater(new Runnable() {
						                 @Override
						                 public void run() {
						                     controler.signOut();
						                 }
						             });
						         }
							}));
						} catch (IOException e) {
							ExceptionHandler.exceptionRecord(Main.class.getName(), e.getMessage());
						}
					} else {
						//AlertHelper.showAlert(AlertType.ERROR, "Greska", "Nisu ispravni podaci");
						System.out.println();
					}
				}
			});
			gridpane.add(login, 1, 3);
			GridPane.setHalignment(login, HPos.RIGHT);
			root.getChildren().add(gridpane);
			primaryStage.setResizable(false);
			primaryStage.show();
		} catch (Exception e) {
			ExceptionHandler.exceptionRecord(Main.class.getName(), e.getMessage());
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
