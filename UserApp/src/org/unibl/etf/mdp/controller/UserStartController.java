package org.unibl.etf.mdp.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.MulticastSocket;
import java.net.URL;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import org.unibl.etf.mdp.model.ExceptionHandler;
import org.unibl.etf.mdp.model.UserWrapper;
import org.unibl.etf.mdp.serialization.SerializationService;
import org.unibl.etf.mdp.server.FileTransferInterface;
import org.unibl.etf.mdp.service.UserChat;

import com.healthmarketscience.rmiio.RemoteInputStreamClient;
import com.healthmarketscience.rmiio.SimpleRemoteInputStream;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.CheckBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class UserStartController implements Initializable {

	@FXML
	private MenuBar menu;

	@FXML
	private ListView<String> userList;

	@FXML
	private TextArea chat;

	@FXML
	private TextField lblMessage;

	@FXML
	private ComboBox<String> cbUsers;

	@FXML
	private TextField lblFile;

	@FXML
	private Label obavjestenje;

	@FXML
	private ListView<String> receivedFiles;
	
	@FXML
    private CheckBox groupChat;

	public static String PATH_DOWNLOAD = "";
	public static UserWrapper user;
	public static String BASE_URL = "";
	public static String CLIENT_POLICY_FILE="";
	private UserChat userChat;
	private static int MULTICAST_PORT = 0;
	private static String MULTICAST_HOST = "";
	private static int counterSerialization=0;
	private static final String PROPERTIES_FILE = "configuration"+File.separator+"user-configuration.properties";
	
	static {
		InputStream input;
		try {
			Properties prop = new Properties();
			input = new FileInputStream(PROPERTIES_FILE);
			prop.load(input);
			MULTICAST_PORT = Integer.parseInt(prop.getProperty("MULTICAST_PORT"));
			MULTICAST_HOST = prop.getProperty("MULTICAST_HOST");
			CLIENT_POLICY_FILE = prop.getProperty("CLIENT_POLICY_FILE");
			BASE_URL=prop.getProperty("REST_SERVICE_URL");
			PATH_DOWNLOAD=prop.getProperty("PATH_DOWNLOAD");
		} catch (FileNotFoundException e1) {
			ExceptionHandler.exceptionRecord(UserChat.class.getName(), e1.getMessage());
		} catch (IOException e) {
			ExceptionHandler.exceptionRecord(UserChat.class.getName(), e.getMessage());
		}
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		fillUsersList();
		fillUsersComboBox();
		fillFilesList();
		try {
			userChat = new UserChat(user.getUser(), chat, userList, groupChat);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Timer timer = new Timer(true); // set it as a deamon
		timer.schedule(new CheckMessagesFromAdmin(), 0, 1000);
		
	}

	@FXML
	void chooseFile(ActionEvent event) {
		FileChooser fileChooser = new FileChooser();
		File selectedFile = fileChooser.showOpenDialog(null);

		if (selectedFile != null) {
			lblFile.setText(selectedFile.getAbsolutePath());
		} else {
			lblFile.setText("");
		}
	}

	@FXML
	void downloadFile(ActionEvent event) {
		System.setProperty("java.security.policy", CLIENT_POLICY_FILE);
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}
		try {
			String name = "FileTransfer";
			Registry registry = LocateRegistry.getRegistry(1099);
			FileTransferInterface fileTransfer = (FileTransferInterface) registry.lookup(name);
			String serverpath = PATH_DOWNLOAD + File.separator + user.getUser().getUsername() + File.separator;
			File file = new File(serverpath);
			if (!file.exists()) {
				file.mkdir();
			}
			String fileName = receivedFiles.getSelectionModel().getSelectedItem();
			InputStream istream = RemoteInputStreamClient
					.wrap(fileTransfer.downloadFileFromServer(user.getUser().getUsername(), fileName));
			File serverpathfile = new File(serverpath + fileName);
			serverpathfile.createNewFile();
			FileOutputStream out = new FileOutputStream(serverpathfile);

			byte[] buf = new byte[1024];

			int bytesRead = 0;
			while ((bytesRead = istream.read(buf)) >= 0) {
				out.write(buf, 0, bytesRead);
			}
			out.flush();
			out.close();
			istream.close();
		} catch (IOException e) {
			//ExceptionHandler.exceptionRecord(UserStartController.class.getName(), e.getMessage());
			e.printStackTrace();
		} catch (NotBoundException e) {
			//ExceptionHandler.exceptionRecord(UserStartController.class.getName(), e.getMessage());
			e.printStackTrace();
		}
	}

	@FXML
	void sendFile(ActionEvent event) {
		String selectedUser = cbUsers.getSelectionModel().getSelectedItem();
		String path = lblFile.getText();
		if ("".equals(selectedUser) || "".equals(path)) {
			AlertHelper.showAlert(AlertType.ERROR, "Greska",
					"Nije odabran korisnik kojem se salje fajl ili nije odabran fajl za slanje");
		} else {
			System.setProperty("java.security.policy", CLIENT_POLICY_FILE);
			if (System.getSecurityManager() == null) {
				System.setSecurityManager(new SecurityManager());
			}
			try {
				String name = "FileTransfer";
				Registry registry = LocateRegistry.getRegistry(1099);
				FileTransferInterface fileTransfer = (FileTransferInterface) registry.lookup(name);
				File clientpathfile = new File(path);
				System.out.println("uploading to server...");
				SimpleRemoteInputStream istream = new SimpleRemoteInputStream(new FileInputStream(clientpathfile));
				String nazivFajla = clientpathfile.getName();
				fileTransfer.uploadFileToServerRMIIO(istream.export(), selectedUser, nazivFajla);
				istream.close();
			} catch (Exception ex) {
				ExceptionHandler.exceptionRecord(UserStartController.class.getName(), ex.getMessage());
			}
		}
		lblFile.setText("");
	}

	@FXML
	void sendMessage(ActionEvent event) {
		if(!groupChat.isSelected()) {
			String message = lblMessage.getText();
			userChat.sendMessage(message, userList.getSelectionModel().getSelectedItem());
			chat.appendText(user.getUser().getUsername() + ":" + message + System.getProperty("line.separator"));
			lblMessage.setText("");
		}else {
			String message = lblMessage.getText();
			userChat.sendGroupMessage(message);
			chat.appendText(user.getUser().getUsername() + ":" + message + System.getProperty("line.separator"));
			lblMessage.setText("");
		}
	}

	public void fillUsersList() {
		userList.getItems().clear();
		JedisPool pool = new JedisPool("localhost");
		String instanceName = "users";
		ObservableList<String> names = FXCollections.observableArrayList();
		try (Jedis jedis = pool.getResource()) {
			Map<String, String> fields = jedis.hgetAll(instanceName);
			for (String key : fields.keySet()) {
				String[] help = key.split("##");
				if ("1".equals(help[1]) && (!help[0].equals(user.getUser().getUsername()))) {
					names.add(help[0]);
				}
			}
		}
		userList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		userList.getItems().addAll(names);
		pool.close();
	}

	public void fillUsersComboBox() {
		JedisPool pool = new JedisPool("localhost");
		String instanceName = "users";
		try (Jedis jedis = pool.getResource()) {
			Map<String, String> fields = jedis.hgetAll(instanceName);
			for (String key : fields.keySet()) {
				String[] help = key.split("##");
				if ("1".equals(help[1]) && !user.getUser().getUsername().equals(help[0])) {
					cbUsers.getItems().add(help[0]);
				}
			}
		}
		pool.close();
	}

	public void fillFilesList() {
		System.setProperty("java.security.policy", CLIENT_POLICY_FILE);
		ObservableList<String> names = FXCollections.observableArrayList();
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}
		try {
			String name = "FileTransfer";
			Registry registry = LocateRegistry.getRegistry(1099);
			FileTransferInterface fileTransfer = (FileTransferInterface) registry.lookup(name);
			ArrayList<String> files = new ArrayList<>();
			files.addAll(fileTransfer.getFilesForUser(user.getUser().getUsername()));
			if (files.size() > 0) {
				for (String fileName : files) {
					names.add(fileName);
				}
			}
			receivedFiles.getItems().addAll(names);
		} catch (Exception ex) {
			ExceptionHandler.exceptionRecord(UserStartController.class.getName(), ex.getMessage());
		}
	}

	@FXML
	void logOut(ActionEvent event) {
		signOut();
	}
	
	public void signOut() {
		try {
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = new Date();
			String datef = dateFormat.format(date);
			datef = datef.replace(" ", "T");
			user.setDateLogout(datef);
			URL url = new URL(BASE_URL + "logout/" + user.getUser().getUsername() + "/timelogin/" + user.getDateLogin()
					+ "/timelogout/" + user.getDateLogout());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");

			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				System.out.println("Failed : HTTP error code : " + conn.getResponseCode());
			} else {
				userChat.logout();
				Platform.exit();
			}
			conn.disconnect();
		} catch (MalformedURLException e) {
			ExceptionHandler.exceptionRecord(UserStartController.class.getName(), e.getMessage());
		} catch (IOException e) {
			ExceptionHandler.exceptionRecord(UserStartController.class.getName(), e.getMessage());
		}
	}

	@FXML
	void changePassword(ActionEvent event) throws IOException {
		ChangePasswordController.user = user.getUser();
		Stage window = new Stage();
		Scene korisnikScena = new Scene(
				FXMLLoader.load(getClass().getResource("/org/unibl/etf/mdp/gui/ChangePassword.fxml")));
		window.resizableProperty().setValue(Boolean.FALSE);
		window.setScene(korisnikScena);
		window.centerOnScreen();
		window.initModality(Modality.APPLICATION_MODAL);
		window.setResizable(false);
		window.showAndWait();
	}

	@FXML
	void checkLogs(ActionEvent event) throws IOException {
		ViewActivityController.user = user.getUser();
		Stage window = new Stage();
		Scene korisnikScena = new Scene(
				FXMLLoader.load(getClass().getResource("/org/unibl/etf/mdp/gui/ViewActivity.fxml")));
		window.resizableProperty().setValue(Boolean.FALSE);
		window.setScene(korisnikScena);
		window.centerOnScreen();
		window.initModality(Modality.APPLICATION_MODAL);
		window.setResizable(false);
		window.showAndWait();
	}
	
	class CheckMessagesFromAdmin extends TimerTask {
		@Override
		public void run() {
			MulticastSocket socket = null;
			byte[] buffer = new byte[256];
			try {
				socket = new MulticastSocket(MULTICAST_PORT);
				InetAddress address = InetAddress.getByName(MULTICAST_HOST);
				socket.joinGroup(address);
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
				socket.receive(packet);
				String received = new String(packet.getData(), 0, packet.getLength());
				Platform.runLater(() -> {
					if (!"".equals(received)) {
						obavjestenje.setText(received);
						if(counterSerialization%4==0) {
							SerializationService.serializeWithJava(received,user.getUser().getUsername());
							counterSerialization++;
						}else if(counterSerialization%4==1) {
							SerializationService.serializeWithGson(received,user.getUser().getUsername());
							counterSerialization++;
						}else if(counterSerialization%4==2) {
							SerializationService.serializeWithKryo(received,user.getUser().getUsername());
							counterSerialization++;
						}else {
							SerializationService.serializeWithXML(received,user.getUser().getUsername());
							counterSerialization++;
						}
					}
				});
			} catch (IOException ioe) {
				ExceptionHandler.exceptionRecord(UserStartController.class.getName(), ioe.getMessage());
			}
		}
	}

}
