package Controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class ChatController implements Initializable {
	//dummy values
	private String player = "Davod";
	private String time = "5-12-2018 16:46";
	
	@FXML
	private VBox textScreen;
	
	@FXML
	private TextField chatInput;
	
	public void submitChatInput(ActionEvent e) {
		sendMessage();
	}

	private void sendMessage() {
		
		Label label = new Label();
		label.setText("Bader");
		
		textScreen.getChildren().add(label);
		
		System.out.println("Sending Message");
		
		chatInput.setText("");
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		
	}
}
