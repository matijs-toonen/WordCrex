package Controller;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import Model.Game;
import Model.GameStatus;
import View.Items.GameItem;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

public class GameController implements Initializable{
	
	/*
	 * PROPS
	 */
	private DatabaseController<Game> _db = new DatabaseController<Game>();;
	private ArrayList<Game> _activeGames;
	private ArrayList<Game> _finishedGames;
	
	/*
	 * FIELDS
	 */
	@FXML
	private VBox vboxGames, vboxFinishedGames;
	
	@FXML 
	private TextField searchBox;
	
	
	@Override
	public void initialize(URL url, ResourceBundle resources) {
		// TODO Auto-generated method stub
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
	        System.out.println("mouse click detected! " + userLabel.getText());
		});
    }
}