package Controller;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import Model.Game;
import Model.GameStatus;
import Model.Turn;
import View.Items.GameItem;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class GameController implements Initializable{
	
	/*
	 * PROPS
	 */
	private DatabaseController<Game> _db = new DatabaseController<Game>();;
	private ArrayList<Game> _activeGames;
	private AnchorPane _rootPane;
	private ArrayList<Game> _finishedGames;
	
	/*
	 * FIELDS
	 */
	@FXML
	private VBox vboxGames, vboxFinishedGames;
	
	@FXML 
	private TextField searchBox;
	
	public GameController(AnchorPane rootPane) {
		_rootPane = rootPane;
	}
	
	@Override
	public void initialize(URL url, ResourceBundle resources) {
		getGames();
		
		searchBox.textProperty().addListener((observable) -> {
			renderGames();
		});
	}
	
	
	/**
	 * Get all games from database
	 */
	private void getGames() {
		String username = MainController.getUser().getUsername();
		
		String gameCommandFinished = Game.getWinnerQuery(username); 
		String gameCommandActive = Game.getActiveQuery(username);

		try {
			_activeGames = (ArrayList<Game>) _db.SelectAll(gameCommandActive, Game.class);
			_finishedGames = (ArrayList<Game>) _db.SelectAll(gameCommandFinished, Game.class);
			
			renderGames();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
	
	
	/**
	 * Render games in view
	 */
	private void renderGames() {
		vboxGames.getChildren().clear();
		vboxFinishedGames.getChildren().clear();
		
		Game.hasWinnerWithUsername(_finishedGames, searchBox.getText()).forEach(game -> {
			var gameItem = new GameItem(game);
			vboxFinishedGames.getChildren().add(gameItem);
			gameItem.setUserOnClickEvent(onLabelClick());
		});
		
		Game.hasGameWithUsername(_activeGames, searchBox.getText()).forEach(game -> {
			var gameItem = new GameItem(game);
			vboxGames.getChildren().add(gameItem);
			gameItem.setUserOnClickEvent(onLabelClick());
		});
	}

	/**
	 * Handles user click
	 * @return
	 */
	private Consumer<MouseEvent> onLabelClick() {
		return (event -> {
	    	var userLabel = (Label) event.getSource();
	    	var parent = (GameItem) userLabel.getParent();
	    	var currentGame= parent.getGame();
	    	var turnQuery = Game.getTurnFromActiveGame(currentGame.getGameId());
	    	var turn = new Turn(getTurn(turnQuery));
	    	loadBoard(currentGame, turn);
	        System.out.println("mouse click detected! " + userLabel.getText());
		});
    }
	
	private int getTurn(String query) {
		try {
			return _db.SelectCount(query);
		} catch (SQLException e) {
			
		}
		return 1;
	}
	
	
	private void loadBoard(Game game, Turn turn) {
		AnchorPane pane = null;
		try {
			var con = new BoardController(game, turn);
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