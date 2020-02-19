package net.etfbl.mdp.controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ShowImageController implements Initializable{

	@FXML
    private ImageView showImage;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
	}
	
	public void displayImage(Image imageTOShow) {
		if(imageTOShow!=null) {
			showImage.setImage(imageTOShow);
		}
	}
}
