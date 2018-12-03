package Controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

public class BoardController implements Initializable {

	@FXML
	private Label lblScore1, lblScore2, lblPlayer1, lblPlayer2;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		lblPlayer1.setText("SCHRUKTURK");
		lblPlayer2.setText("BOEDER");
		lblScore1.setText("1");
		lblScore2.setText("9");
	}
}
