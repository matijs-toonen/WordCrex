package Controller;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

import Model.ChatLine;
import Model.Game;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

public class ChatController implements Initializable {
	
	/*
	 * PROPS
	 */
	private DatabaseController<ChatLine> _db;
	private ArrayList<ChatLine> _chatLines;
	private boolean updatedOnce = false;
	private boolean jumpToBottom = true;
	private Game _currentGame;
	
	
	/*
	 * FIELDS
	 */
	@FXML
	private VBox textScreen;
	
	@FXML
	private ScrollPane chatScroll;
	
	@FXML
	private TextField chatInput;
	
	
	
	public ChatController(Game currentGame) {
		_currentGame = currentGame;
	}

	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		_db = new DatabaseController<ChatLine>();
		
		startChatListener();
	}
		
	/**
	 * Onaction from view
	 * @param e
	 */
	public void submitChatInput(ActionEvent e) {
		sendMessage();
	}
	
	
	/**
	 * Send message
	 */
	private void sendMessage() {
		
		String message = chatInput.getText();
		
		if (message == null || message.isEmpty()) {
			return;
		}
		
		try {
			String insertStatement = ChatLine.insertQuery(MainController.getUser().getUsername(), _currentGame.getGameId(), message);
			_db.Insert(insertStatement);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		chatInput.setText("");
		updateChat();
	}
	
	
	/**
	 * Thread that refreshes the chat
	 */
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
	
	
	/**
	 * Update the chat array from the database
	 */
	private void updateChat() {
		try {
			String selectStatement = ChatLine.getQuery(_currentGame.getGameId());
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
		
		if(updatedOnce) {
			if(jumpToBottom) {
				chatScroll.setVvalue(1.0);
				jumpToBottom = false;
			}
		}
		
		updatedOnce = true;
	}
	
	
	/**
	 * Render message item
	 */
	private void showMessages() {
		textScreen.getChildren().clear();
		textScreen.setStyle("-fx-padding: 15 5 0 5");
		_chatLines.forEach(chatLine -> {
			
			Label messageLabel = new Label();
			messageLabel.setText(chatLine.getMessage());
			messageLabel.setWrapText(true);
			messageLabel.setTextAlignment(TextAlignment.LEFT);

			HBox vBox = new HBox();
			vBox.setStyle("-fx-background-color: #FFFFFF");
		    vBox.getChildren().add(messageLabel);
			
			if (chatLine.getUsername().equals(MainController.getUser().getUsername())) {
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
