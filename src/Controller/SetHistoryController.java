package Controller;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

import Model.SetHistory;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

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
		
		GridPane playerBox1 = playerBox(setHistory.getPlayer1(), setHistory.getWoord1(), setHistory.getScore1());
		GridPane playerBox2 = playerBox(setHistory.getPlayer2(), setHistory.getWoord2(), setHistory.getScore2());
		
		if (setHistory.getScore1() > setHistory.getScore2()) {
			playerBox1.setStyle("-fx-background-color: #DAF7E8;");
			playerBox2.setStyle("-fx-background-color: #FFE2E6;");
		}
		
		if (setHistory.getScore2() > setHistory.getScore1()) {
			playerBox1.setStyle("-fx-background-color: #FFE2E6;");
			playerBox2.setStyle("-fx-background-color: #DAF7E8;");
		}
		
		playersSetBox.getChildren().add(playerBox1);
		playersSetBox.getChildren().add(playerBox2);
		
		return playersSetBox;
	}
	
	private GridPane playerBox(String player, String word, int score) {
		// Player 1 Box
		GridPane playerSetBox = new GridPane();
		
		// Player 1 Name
		HBox playerNameBox = new HBox();
		
		// Player 1 Score
		HBox playerScoreBox = new HBox();
		
		// Player 1 Word
		HBox playerWordBox = new HBox();
		
		
		Label playerLabel = new Label();
		playerLabel.setText(player);
		playerLabel.setStyle("-fx-padding: 15, 0, 0, 0 ; -fx-text-fill: #4D4F5C; -fx-font-family: 'Source Sans Pro'; -fx-font-weight: bold; -fx-font-size: 14px;");
		
		playerSetBox.getChildren().add(playerNameBox);
		playerSetBox.add(playerLabel, 0, 0);
		
		Label scoreLabel = new Label();
		scoreLabel.setText(Integer.toString(score));
		scoreLabel.setStyle("-fx-text-fill: #4D4F5C; -fx-font-family: 'Source Sans Pro'; -fx-font-size: 14px; -fx-background-color: rgba(255,255,255,1); -fx-padding: 5 10; -fx-border-width: 5px ; -fx-background-radius: 25");
		
		playerScoreBox.getChildren().add(scoreLabel);
		playerSetBox.add(scoreLabel, 1, 0);
		
		playerSetBox.setHalignment(scoreLabel, HPos.CENTER);
		
		Label wordLabel = new Label();
		wordLabel.setText(word);
		wordLabel.setStyle("-fx-padding: 0 15 0 0; -fx-text-fill: #4D4F5C; -fx-font-family: 'Source Sans Pro'; -fx-font-size: 14px;");
		
		playerWordBox.getChildren().add(wordLabel);
		playerSetBox.add(wordLabel, 2, 0);
		
		playerSetBox.setHalignment(wordLabel, HPos.RIGHT);
		
		ColumnConstraints cc1 = new ColumnConstraints();
		ColumnConstraints cc2 = new ColumnConstraints();
		 
		cc1.setPercentWidth(40);
		cc1.setHgrow(Priority.ALWAYS);
		cc2.setPercentWidth(20);
		cc2.setHgrow(Priority.ALWAYS);
		 
		playerSetBox.getColumnConstraints().add(cc1);
		playerSetBox.getColumnConstraints().add(cc2);
		playerSetBox.getColumnConstraints().add(cc1);
		
		return playerSetBox;
	}

}
