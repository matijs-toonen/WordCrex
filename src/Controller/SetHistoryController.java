package Controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class SetHistoryController implements Initializable {
	
	@FXML
	private VBox itemWrapper;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		
		for(int i = 0; i < 10; i++) {
			Label label = new Label();
			label.setText("Label Num: " + i);
			itemWrapper.getChildren().add(label);
		}
		
	}

}
