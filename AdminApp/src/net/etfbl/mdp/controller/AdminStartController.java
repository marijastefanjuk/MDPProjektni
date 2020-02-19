package net.etfbl.mdp.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Timer;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import net.etfbl.mdp.model.ExceptionHandler;
import net.etfbl.mdp.service.AdminSocketServerCommunication;
import net.etfbl.mdp.service.RequestImageThread;
import net.etfbl.mdp.service.UserListRequestThread;

public class AdminStartController implements Initializable {

	@FXML
	private TextField obavjestenje;

	@FXML
	private ListView<String> activeUsersList;

	private static int MULTICAST_PORT = 0;
	private static String MULTICAST_HOST = "";

	public static String image = "";
	public static ShowImageController controler;
	private static boolean monitorStarted = false;

	private static final String PROPERTIES_FILE = "configuration" + File.separator + "adminApp.properties";

	static {
		InputStream input;
		try {
			Properties prop = new Properties();
			input = new FileInputStream(PROPERTIES_FILE);
			prop.load(input);
			MULTICAST_PORT = Integer.parseInt(prop.getProperty("MULTICAST_PORT"));
			MULTICAST_HOST = prop.getProperty("MULTICAST_HOST");
		} catch (FileNotFoundException e1) {
			ExceptionHandler.exceptionRecord(AdminStartController.class.getName(), e1.getMessage());
		} catch (IOException e) {
			ExceptionHandler.exceptionRecord(AdminStartController.class.getName(), e.getMessage());
		}
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		try {
			AdminSocketServerCommunication users = new AdminSocketServerCommunication(/* user, */ activeUsersList);
			users.startCommunication();
		} catch (UnknownHostException e) {
			ExceptionHandler.exceptionRecord(AdminStartController.class.getName(), e.getMessage());
		} catch (IOException e) {
			ExceptionHandler.exceptionRecord(AdminStartController.class.getName(), e.getMessage());
		}
		Timer timer = new Timer(true);
		timer.schedule(new UserListRequestThread(), 0, 10000);
	}

	@FXML
	void blokirajKorisnika(ActionEvent event) throws IOException {
		Stage window = new Stage();
		Scene korisnikScena = new Scene(
				FXMLLoader.load(getClass().getResource("/net/etfbl/mdp/gui/BlokirajNalog.fxml")));
		window.resizableProperty().setValue(Boolean.FALSE);
		window.setScene(korisnikScena);
		window.centerOnScreen();
		window.initModality(Modality.APPLICATION_MODAL);
		window.setResizable(false);
		window.showAndWait();
	}

	@FXML
	void dodajNalog(ActionEvent event) throws IOException {
		Stage window = new Stage();
		Scene korisnikScena = new Scene(
				FXMLLoader.load(getClass().getResource("/net/etfbl/mdp/gui/DodajZaposlenog.fxml")));
		window.resizableProperty().setValue(Boolean.FALSE);
		window.setScene(korisnikScena);
		window.centerOnScreen();
		window.initModality(Modality.APPLICATION_MODAL);
		window.setResizable(false);
		window.showAndWait();
	}

	@FXML
	void pokreniMonitor(ActionEvent event) throws IOException {
		String username = activeUsersList.getSelectionModel().getSelectedItem();
		if (username != null) {
			if (!monitorStarted) {
				monitorStarted = true;
				Timer timer = new Timer(true);
				timer.schedule(new RequestImageThread(username), 0, 100);
				Stage window = new Stage();
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/net/etfbl/mdp/gui/ShowImage.fxml"));
				Scene korisnikScena = new Scene(loader.load());
				controler = loader.getController();
				window.resizableProperty().setValue(Boolean.FALSE);
				window.setScene(korisnikScena);
				window.centerOnScreen();
				window.initModality(Modality.NONE);
				window.show();
				window.setOnCloseRequest(new EventHandler<WindowEvent>() {
					public void handle(WindowEvent we) {
						monitorStarted = false;
						timer.cancel();
					}
				});
			} else {
				AlertHelper.showAlert(AlertType.ERROR, "Error", "Vec posmatrate jednog korisnika!");
			}
		} else {
			AlertHelper.showAlert(AlertType.ERROR, "Error", "Niste odabrali korisnika za posmatranje!");
		}
	}

	/*
	 * public static BufferedImage slika() { //if(imageList.size()>0) {
	 * if(!"".equals(image)) {
	 * //System.out.println("imagelist size "+imageList.size()); String
	 * imgg=image;//imageList.remove();
	 * //System.out.println("image u slika "+image); byte[] img =
	 * Base64.getDecoder().decode(imgg); ByteArrayInputStream bis = new
	 * ByteArrayInputStream(img); System.out.println("admin poslao sliku "); try {
	 * BufferedImage bImage2 = ImageIO.read(bis); return bImage2; } catch
	 * (IOException e) { e.printStackTrace(); } } return null; }
	 */
	@FXML
	void posaljiObavjestenje(ActionEvent event) {
		MulticastSocket socket = null;
		byte[] buf = new byte[6];
		try {
			socket = new MulticastSocket();
			InetAddress address = InetAddress.getByName(MULTICAST_HOST);
			socket.joinGroup(address);
			String msg = obavjestenje.getText();
			if (!"".equals(msg)) {
				// System.out.println("obavjestenje: " +msg);
				buf = msg.getBytes();
				DatagramPacket packet = new DatagramPacket(buf, buf.length, address, MULTICAST_PORT);
				socket.send(packet);
				obavjestenje.setText("");
				try {
					Thread.sleep(1000);
				} catch (InterruptedException ex) {
					ExceptionHandler.exceptionRecord(AdminStartController.class.getName(), ex.getMessage());
				}
			}else {
				AlertHelper.showAlert(AlertType.INFORMATION, "Poruka", "Nije popunjeno polje za slanje obavjestenja!");
			}
		} catch (IOException ioe) {
			ExceptionHandler.exceptionRecord(AdminStartController.class.getName(), ioe.getMessage());
		}
	}

	@FXML
	void pregledIstorijeKorisnika(ActionEvent event) throws IOException {
		String selectedUser = activeUsersList.getSelectionModel().getSelectedItem();
		if (selectedUser!=null) {
			ViewActivityController.user = selectedUser;
			Stage window = new Stage();
			Scene korisnikScena = new Scene(
					FXMLLoader.load(getClass().getResource("/net/etfbl/mdp/gui/ViewActivity.fxml")));
			window.resizableProperty().setValue(Boolean.FALSE);
			window.setScene(korisnikScena);
			window.centerOnScreen();
			window.initModality(Modality.APPLICATION_MODAL);
			window.setResizable(false);
			window.showAndWait();
		} else {
			AlertHelper.showAlert(AlertType.ERROR, "Greska", "Niste odabrali korisnika");
		}
	}
}
