package Controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class ChatController 
{
	//dummy values
	private String player = "Davod";
	private String time = "5-12-2018 16:46";
	
	
	@FXML
	private TextArea textScreen;
	
	@FXML
	private TextArea chatInput;
	
	public void sendMessage(KeyEvent e)
	{
		if(e.getCode() == KeyCode.ENTER)
		{	
			String inputMessage = chatInput.getText();
			String finalMessage =  "(" + time + ") " + "<" + player + ">: " + inputMessage;
			
			textScreen.setText(textScreen.getText() + finalMessage + "\n");
			textScreen.appendText("");
			chatInput.setText("");
		}
	}
}
