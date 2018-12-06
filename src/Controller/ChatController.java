package Controller;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

import Model.ChatLine;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

public class ChatController implements Initializable {
	
	private DatabaseController<ChatLine> _db;
	private ArrayList<ChatLine> _chatLines;
	
	// TODO: remove dummy data
	final String username = "test-player";
	final int game_id = 500;
	
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
		
		if (message == null || message.isEmpty()) {
			return;
		}
		
		try {
			String insertStatement = String.format("INSERT INTO chatline (username, game_id, moment, message) VALUES ('%s', '%d', NOW(), '%s');", username, game_id, message);
			_db.Insert(insertStatement);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		chatInput.setText("");
		updateChat();
	}
	
	private void startChatListener() {
		
		Thread chatThread = new Thread(){
		    public void run(){
		    	
		    	while(true) {
	    			updateChat();
	    			
	    			try {
						Thread.sleep(1000);
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
			String selectStatement = String.format("SELECT * FROM chatline WHERE game_id = '%d' ORDER BY moment", game_id);
			_chatLines = (ArrayList<ChatLine>) _db.SelectAll(selectStatement, ChatLine.class);
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
			
			Label messageLabel = new Label();
			messageLabel.setText(chatLine.getMessage());
			messageLabel.setWrapText(true);
			messageLabel.setTextAlignment(TextAlignment.LEFT);

			HBox vBox = new HBox();
			vBox.setStyle("-fx-background-color: #FFFFFF");
		    vBox.getChildren().add(messageLabel);
			
			if (chatLine.getUsername().equals(username)) {
				messageLabel.setStyle("-fx-background-radius: 20 20 0 20; -fx-padding: 5px 10px; -fx-background-color: #3B86FF; -fx-text-fill: white;");
				vBox.setPadding(new Insets(0,5,10,30));
				vBox.setAlignment(Pos.BASELINE_RIGHT);
			}else {
				messageLabel.setStyle("-fx-background-radius: 20 20 20 0; -fx-padding: 5px 10px; -fx-background-color: #EDF0F5; -fx-text-fill: black;");
				vBox.setPadding(new Insets(0,30,10,5));
				vBox.setAlignment(Pos.BASELINE_LEFT);
			}
			
			textScreen.getChildren().add(vBox);
		});
	}

}
