package Controller;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import Model.Account;
import Model.Game;
import Model.Letter;
import Model.LetterSet;
import Model.Symbol;
import View.Items.ChallengeItem;
import View.Items.ChallengePlayerItem;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class ChallengeController implements Initializable  {

	private ArrayList<Game> _gameChallenges;
	private ArrayList<Account> _uninvitedPlayers;
	private DatabaseController _db;
	private DatabaseController<Game> _dbGame = new DatabaseController<Game>();
	private DatabaseController<Account> _dbAccount = new DatabaseController<Account>();
	private AnchorPane _rootPane;
	
	@FXML
	private VBox vboxChallenges, vboxChallengePlayers;
	
	@FXML 
	private TextField searchBox;
	
	
	public ChallengeController(AnchorPane rootPane) {
		_rootPane = rootPane;
	}
	
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
	    		insertLetters(gameId);
//	    		var statement = "UPDATE game SET answer_player2 = 'accepted' WHERE game_id = " + gameId; 
//	    		try {
//					_db.Update(statement);
//				} catch (SQLException e) {
//					System.out.println(e.getMessage());
//				}
    		try {
	    		loadBoard(gameId);
				_dbGame.Update(statement);
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}
    		
    		setChallenges();
    		showChallenges();
		});
    }
	
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
	
	private void insertLetters(int gameId) {
		if(hasAllLettersWithGameId(gameId))
			return;
		
		if(!deleteExistingLettersWithGameId(gameId))
			return;
		
		_db = new DatabaseController<Symbol>();
		
		try {
			String[] queries = new String[102];
			
			var symbols = (ArrayList<Symbol>)_db.SelectAll("SELECT * FROM symbol", Symbol.class);
			int letterId = 0;
			for(var symbol : symbols) {
				for(int i = 1; i <= symbol.getAmount(); i++) {
					letterId++;
					var letterSet = new LetterSet("NL");
					queries[letterId - 1] = "INSERT INTO letter VALUES (" + letterId + ", " + gameId + ", '" + letterSet.getLetterCode() + "', '" + symbol.getChar() + "'); ";
				}
			}
			
			_db.InsertBatch(queries);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private boolean hasAllLettersWithGameId(int gameId) {
		_db = new DatabaseController<Letter>();
		try {
			return _db.SelectCount("SELECT COUNT(*) FROM letter where game_id = " + gameId) == 102;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	private boolean deleteExistingLettersWithGameId(int gameId) {
		_db = new DatabaseController<Letter>();
		try {
			_db.Delete("DELETE FROM letter where game_id = " + gameId);
			return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	private void loadBoard(int gameId) {
		AnchorPane pane = null;
		try {
			var con = new BoardController(new Game(gameId));
			var panes = new FXMLLoader(getClass().getResource("/View/Board.fxml"));
			panes.setController(con);
			pane = panes.load();
		}
		catch(Exception ex) {
			Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
		}
		//borderPane.setCenter(root);
		_rootPane.getChildren().setAll(pane);
	}
}
