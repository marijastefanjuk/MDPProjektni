package net.etfbl.mdp.controller;

import java.net.URL;
import java.rmi.RemoteException;
import java.util.Map;
import java.util.ResourceBundle;

import javax.xml.rpc.ServiceException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import net.etfbl.mdp.model.ExceptionHandler;
import net.etfbl.mdp.soap.AddRemoveUser;
import net.etfbl.mdp.soap.AddRemoveUserServiceLocator;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class BlockUserController implements Initializable{

	@FXML
    private ListView<String> naloziList;

    @FXML
    void blokirajNalog(ActionEvent event) throws RemoteException {
    	String username=naloziList.getSelectionModel().getSelectedItem();
    	AddRemoveUserServiceLocator loc=new AddRemoveUserServiceLocator();
		try {
			AddRemoveUser servis=loc.getAddRemoveUser();
			boolean result=servis.blockUser(username);
			if(!result) {
				AlertHelper.showAlert(Alert.AlertType.ERROR, "Greska", "Nije moguce blokirati korisnika!");
			}else {
				popuniListu();
			}
		} catch (ServiceException e) {
			ExceptionHandler.exceptionRecord(BlockUserController.class.getName(), e.getMessage());
		}
    }

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		popuniListu();
	}
	
	public void popuniListu() {
		naloziList.getItems().clear();
		JedisPool pool=new JedisPool("localhost");
		String instanceName="users";
		ObservableList<String> names = FXCollections.observableArrayList();
		try(Jedis jedis=pool.getResource()){
			Map<String,String> fields=jedis.hgetAll(instanceName);
			for(String key:fields.keySet()) {
				String[] help=key.split("##");
				if("1".equals(help[1])) {
					names.add(help[0]);
				}
			}
		}
		naloziList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		naloziList.getItems().addAll(names);
		pool.close();
	}
}
