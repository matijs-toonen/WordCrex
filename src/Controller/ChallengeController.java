package Controller;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import Model.Account;
import Model.Game;
import View.Items.ChallengeItem;
import View.Items.ChallengePlayerItem;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class ChallengeController implements Initializable  {
	
	private DatabaseController _db;
	private ArrayList<Game> _challenges;
	private ArrayList<Account> _possibleChallenges;
	
	@FXML
	private VBox vboxChallenges, vboxChallengePlayers;
	
	@FXML 
	private TextField searchBox;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		setChallenges();
		setPlayersToChallenge();
		
		searchBox.textProperty().addListener((observable) -> {
			showPlayersToChallenge();
			showChallenges();
		});
	}
	
	private void setChallenges() {
		_db = new DatabaseController<Game>();
		String gameCommand = "SELECT * FROM game";
		try {
			_challenges = (ArrayList<Game>) _db.SelectAll(gameCommand, Game.class);
			showChallenges();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		}
	}
	
	private void setPlayersToChallenge() {
		_db = new DatabaseController<Account>();
		String gameCommand = "SELECT * FROM account";
		try {
			_possibleChallenges = (ArrayList<Account>) _db.SelectAll(gameCommand, Account.class);
			showPlayersToChallenge();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		}
	}
	
	private void showPlayersToChallenge() {
		vboxChallengePlayers.getChildren().clear();
		
		Account.getAllAccountsByUsername(_possibleChallenges, searchBox.getText()).forEach(player -> {
			var challengeItem = new ChallengePlayerItem(player);
			vboxChallengePlayers.getChildren().add(challengeItem);
			challengeItem.setOnClickEvent(onSentChallenge());	
		});
	}
	
	//Custom function for handeling the onmouseclickEvent
	private Consumer<ActionEvent> onSentChallenge() {
		return (event -> {
	    	var btnReaction = (Button) event.getSource();
	    	var challengePlayer = (ChallengePlayerItem) btnReaction.getParent();
	    	var opponent = challengePlayer.getOpponent().getUsername();
	    	if(btnReaction.getText().equals(ChallengePlayerItem.challengeText)) {
	    		var statement = "INSERT INTO game (game_state, letterset_code, username_player1, username_player2, answer_player2) VALUES('request', 'NL', 'ger', '" + opponent + "', 'unknown')"; 
	    		try {
	    			//TODO: change to insert when insert is added
					_db.Update(statement);
				} catch (SQLException e) {
					System.out.println(e.getMessage());
				}
	    	}
	        System.out.println("mouse click detected! " + btnReaction.getText());
		});
    }

	
	private void showChallenges() {
		vboxChallenges.getChildren().clear();
		
		Game.hasGameWithUsername(_challenges, searchBox.getText()).forEach(challenge -> {
			var challengeItem = new ChallengeItem(challenge);
			vboxChallenges.getChildren().add(challengeItem);
			challengeItem.setOnClickEvent(onHandleChallengeClick());
		});
	}
	
	//Custom function for handeling the onmouseclickEvent
	private Consumer<ActionEvent> onHandleChallengeClick() {
		return (event -> {
	    	var btnReaction = (Button) event.getSource();
	    	var challengeItem = (ChallengeItem) btnReaction.getParent();
	    	var gameId = challengeItem.getGame().getGameId();
	    	var type = btnReaction.getText();
	    	if(type.equals(ChallengeItem.acceptText)) {
	    		var statement = "UPDATE game SET answer_player2 = 'accepted' WHERE game_id = " + gameId; 
	    		try {
					_db.Update(statement);
				} catch (SQLException e) {
					System.out.println(e.getMessage());
				}
	    	}else if(type.equals(ChallengeItem.rejectText)) {
	    		var statement = "UPDATE game SET answer_player2 = 'rejected' WHERE game_id = " + gameId; 
	    		try {
					_db.Update(statement);
				} catch (SQLException e) {
					System.out.println(e.getMessage());
				}
	    	}
	        System.out.println("mouse click detected! " + btnReaction.getText());
		});
    }
}
