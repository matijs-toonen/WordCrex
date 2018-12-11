package Controller;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

import Model.SetHistory;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class SetHistoryController implements Initializable {
	
	private DatabaseController<SetHistory> _db;
	private ArrayList<SetHistory> _setHistoryItems;
	private final int gameId = 502;
	
	@FXML
	private VBox itemWrapper;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		_db = new DatabaseController<SetHistory>();
		
		
		startHistoryListener();
	}
	
	private void startHistoryListener() {
		
		Thread chatThread = new Thread(){
		    public void run(){
		    	
		    	while(true) {
		    		updateHistory();
	    			
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
	
	private void updateHistory() {
		try {
			String selectStatement = SetHistory.getQuery(gameId);
			_setHistoryItems = (ArrayList<SetHistory>) _db.SelectAll(selectStatement, SetHistory.class);
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
		itemWrapper.getChildren().clear();
		for (SetHistory setHistory : _setHistoryItems) {
			VBox setHistoryItem = listItem(setHistory);
			itemWrapper.getChildren().add(setHistoryItem);
		}
	}
	
	private VBox listItem(SetHistory setHistory) {
		VBox playersSetBox = new VBox();
		
		HBox playerBox1 = playerBox(setHistory.getPlayer1(), setHistory.getWoord1(), setHistory.getScore1());
		HBox playerBox2 = playerBox(setHistory.getPlayer2(), setHistory.getWoord2(), setHistory.getScore2());
		
		if (setHistory.getScore1() > setHistory.getScore2()) {
			playerBox1.setStyle("-fx-background-color: green;");
			playerBox2.setStyle("-fx-background-color: red;");
		}
		
		if (setHistory.getScore2() > setHistory.getScore1()) {
			playerBox1.setStyle("-fx-background-color: red;");
			playerBox2.setStyle("-fx-background-color: green;");
		}
		
		playersSetBox.getChildren().add(playerBox1);
		playersSetBox.getChildren().add(playerBox2);
		
		return playersSetBox;
	}
	
	private HBox playerBox(String player, String word, int score) {
		// Player 1 Box
		HBox playerSetBox = new HBox();
		
		// Player 1 Name and Word
		VBox playerWordBox = new VBox();
		
		Label player1 = new Label();
		player1.setText(player);
		
		Label word1 = new Label();
		word1.setText(word);
		
		playerWordBox.getChildren().add(player1);
		playerWordBox.getChildren().add(word1);
		
		Label score1 = new Label();
		score1.setText(Integer.toString(score));
		
		playerSetBox.getChildren().add(playerWordBox);
		playerSetBox.getChildren().add(score1);
		
		return playerSetBox;
	}

}
