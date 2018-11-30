package Controller;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import Model.Game;
import Pane.GameItem;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

public class GameController implements Initializable{
	
	private DatabaseController<Game> db;
	private ArrayList<Game> games;
	
	@FXML
	private VBox vboxGames;
	
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
		db = new DatabaseController<Game>();
		String gameCommand = "SELECT * FROM game";
		try {
			this.games = (ArrayList<Game>) db.SelectAll(gameCommand, Game.class);
			renderGames();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		}
	}
	
	private void renderGames() {
		
		vboxGames.getChildren().clear();
		
		this.games.forEach(game -> {
			
			boolean addItem = false;
			
			boolean searchEmpty = searchBox.getText() == null || searchBox.getText().isEmpty();
			boolean containsUser1 = game.usernamePlayer1.toLowerCase().contains(searchBox.getText());
			boolean containsUser2 = game.usernamePlayer2.toLowerCase().contains(searchBox.getText());
			
			if (searchEmpty) {
				addItem = true;
			}
			
			if (containsUser1 || containsUser2) {
				addItem = true;
			}
			
			if (addItem) {
				var item = new GameItem(game); 
				vboxGames.getChildren().add(item);
				item.setThing(onLabelClick());
			}
			
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