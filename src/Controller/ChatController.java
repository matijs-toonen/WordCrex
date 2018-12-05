package Controller;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

import Model.ChatLine;
import Model.Game;
import Model.GameStatus;
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
		
		
		
		chatInput.setText("");
	}
	
	private void startChatListener() {
		
		Thread chatThread = new Thread(){
		    public void run(){
		    	
		    	try {
		    		
		    		while(true) {
		    			
		    			_chatLines = (ArrayList<ChatLine>) _db.SelectAll("SELECT * FROM chatline", ChatLine.class);
						
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								showMessages();
							}
					    });
						
						System.out.println("Chat Updated.");
		    			
		    			Thread.sleep(2000);
		    		}
					
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					System.out.println(e.getMessage());
				}catch (InterruptedException e1) {
					e1.printStackTrace();
				}
		    }
		};
		
		chatThread.setDaemon(true);
		chatThread.start();
		
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
