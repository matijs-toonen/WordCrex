package Controller;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

import Model.Account;
import Model.ChatLine;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class ChatController implements Initializable {
	//dummy values
	private DatabaseController<ChatLine> _db;
	private ArrayList<ChatLine> _chatLines;
	
	@FXML
	private VBox textScreen;
	
	@FXML
	private TextField chatInput;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		_db = new DatabaseController<ChatLine>();
		
		startChatListener();
	}
	
	public void submitChatInput(ActionEvent e) {
		sendMessage();
	}

	private void sendMessage() {
		
		String message = chatInput.getText();
		
		try {
			String insertStatement = String.format("INSERT INTO chatline (username, game_id, moment, message) VALUES ('%s', '500', NOW(), '%s');", "test-player", message);
			System.out.println(insertStatement);
			_db.Insert(insertStatement);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		chatInput.setText("");
		//updateChat();
	}
	
	private void startChatListener() {
		
		Thread chatThread = new Thread(){
		    public void run(){
		    	
		    	while(true) {
	    			updateChat();
	    			
	    			try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
	    		}
		    }
		};
		
		chatThread.setDaemon(true);
		chatThread.start();
		
	}
	
	private void updateChat() {
		try {
			_chatLines = (ArrayList<ChatLine>) _db.SelectAll("SELECT * FROM chatline", ChatLine.class);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				showMessages();
			}
	    });
	}
	
	private void showMessages() {
		textScreen.getChildren().clear();
		
		_chatLines.forEach(chatLine -> {
			Label label = new Label();
			label.setText(chatLine.getMessage());
			
			textScreen.getChildren().add(label);
		});
	}

}
