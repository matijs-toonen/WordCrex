package controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

public class mainController implements Initializable {

	@FXML
	private Label lblText;
	
	public void showMessage(ActionEvent actionEvent) {
		lblText.setText("Nieuwe test value");
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		
	}
}
