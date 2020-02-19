package net.etfbl.mdp.controller;

import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import net.etfbl.mdp.model.ExceptionHandler;
import net.etfbl.mdp.soap.AddRemoveUser;
import net.etfbl.mdp.soap.AddRemoveUserServiceLocator;

public class AddEmployeeController {

	@FXML
	private TextField username;

	@FXML
	private PasswordField password;

	public AddEmployeeController() {
	}

	@FXML
	void napraviNalog(ActionEvent event) throws RemoteException {
		AddRemoveUserServiceLocator loc = new AddRemoveUserServiceLocator();
		try {
			AddRemoveUser servis = loc.getAddRemoveUser();
			boolean result = servis.addUser(username.getText(), password.getText());
			if (!result) {
				AlertHelper.showAlert(Alert.AlertType.ERROR, "Greska",
						"Postoji vec korisnik sa trazenim korisnickim imenom");
			}else {
				Alert alert = new Alert(AlertType.INFORMATION);
		        alert.setTitle("Dodavanje korisnika");
		        alert.setHeaderText(null);
		        alert.setContentText("Korisnik je dodan");
		        alert.showAndWait();
			}
		} catch (ServiceException e) {
			ExceptionHandler.exceptionRecord(AddEmployeeController.class.getName(), e.getMessage());
		}
	}
}
