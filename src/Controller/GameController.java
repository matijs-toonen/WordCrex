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
	
	private DatabaseController<Game> _db;
	private ArrayList<Game> _games;
	
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
	
	private void getGames() {
		_db = new DatabaseController<Game>();
		var user = MainController.getUser();
		System.out.println(user.getUsername());
		String gameCommandActive = "SELECT * FROM game WHERE game_state != 'request'";
		try {
			_games = (ArrayList<Game>) _db.SelectAll(gameCommandActive, Game.class);
			renderGames();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		}
	}
	
	private void renderGames() {
		vboxGames.getChildren().clear();
		vboxFinishedGames.getChildren().clear();
		
		Game.hasGameWithUsername(_games, searchBox.getText()).forEach(game -> {
			var gameItem = new GameItem(game);
			if(game.getSatus() == GameStatus.Playing)
				vboxGames.getChildren().add(gameItem);
			else if(game.getSatus() != GameStatus.Playing)
				vboxFinishedGames.getChildren().add(gameItem);
			
			gameItem.setUserOnClickEvent(onLabelClick());
		});
	}
	

	//Custom function for handeling the onmouseclickEvent
	public Consumer<MouseEvent> onLabelClick() {
		return (event -> {
	    	var userLabel = (Label) event.getSource();
	        System.out.println("mouse click detected! " + userLabel.getText());
		});
    }
}