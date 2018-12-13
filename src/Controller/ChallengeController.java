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
	
	/*
	 * PROPS
	 */
	private ArrayList<Game> _gameChallenges;
	private ArrayList<Account> _uninvitedPlayers;
	private DatabaseController<Game> _dbGame = new DatabaseController<Game>();
	private DatabaseController<Account> _dbAccount = new DatabaseController<Account>();
	
	
	/*
	 * FIELDS
	 */
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
	
	
	/**
	 * Load all challenges where user is involved
	 */
	private void setChallenges() {
		String challengeQuery = Game.getChallengeQuery(MainController.getUser().getUsername());
		
		try {
			_gameChallenges = (ArrayList<Game>) _dbGame.SelectAll(challengeQuery, Game.class);
			showChallenges();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
	
	
	/**
	 * Load all players that have no open invitation
	 */
	private void setPlayersToChallenge() {
		
		String playerQuery = Game.getUninvitedUsersQuery(MainController.getUser().getUsername());
		try {
			_uninvitedPlayers = (ArrayList<Account>) _dbAccount.SelectAll(playerQuery, Account.class);
			showPlayersToChallenge();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
	
	
	/**
	 * Show challenges in the view
	 */
	private void showChallenges() {
		vboxChallenges.getChildren().clear();
		
		Game.hasGameWithUsername(_gameChallenges, searchBox.getText()).forEach(gameChallenge -> {
			System.out.println(gameChallenge.getGameId());
			var challengeItem = new ChallengeItem(gameChallenge);
			vboxChallenges.getChildren().add(challengeItem);
			challengeItem.setOnClickEvent(onHandleChallengeClick());
		});
	}
	
	
	/**
	 * Show players in the view
	 */
	private void showPlayersToChallenge() {
		vboxChallengePlayers.getChildren().clear();
		
		Account.getAllAccountsByUsername(_uninvitedPlayers, searchBox.getText()).forEach(player -> {
			var challengeItem = new ChallengePlayerItem(player);
			vboxChallengePlayers.getChildren().add(challengeItem);
			challengeItem.setOnClickEvent(onSentChallenge());	
		});
	}
	
	
	/**
	 * Click handler that handels accept and reject
	 * in the challengeItem
	 * 
	 * @return
	 */
	private Consumer<ActionEvent> onHandleChallengeClick() {
		
		return (event -> {
			
			// get item values
	    	var btnReaction = (Button) event.getSource();
	    	var challengeItem = (ChallengeItem) btnReaction.getParent();
	    	var gameId = challengeItem.getGame().getGameId();
	    	var type = btnReaction.getText();
	    	
	    	// get awnser value
	    	String awnser = "";
	    	if(type.equals(ChallengeItem.acceptText)) {
	    		awnser = "accepted";
	    	}else if(type.equals(ChallengeItem.rejectText)) {
	    		awnser = "rejected";
	    	}
	    	
	    	// update in database
	    	var statement = Game.getChallengeAwnserQuery(gameId, awnser);
    		try {
				_dbGame.Update(statement);
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}
    		
    		setChallenges();
    		showChallenges();
		});
    }
	
	
	/**
	 * Click when user send inventation to other player
	 * @return
	 */
	private Consumer<ActionEvent> onSentChallenge() {
		
		return (event -> {
	    	
			var btnReaction = (Button) event.getSource();
	    	var challengePlayer = (ChallengePlayerItem) btnReaction.getParent();
	    	var opponent = challengePlayer.getOpponent().getUsername();
	    	
	    	var statement = Game.getRequestGameQuery(MainController.getUser().getUsername(), opponent);
    		try {
				_dbGame.Update(statement);
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}
    		
    		setChallenges();
    		setPlayersToChallenge();
    		showChallenges();
    		showPlayersToChallenge();
		});
    }
	
	
	
}
