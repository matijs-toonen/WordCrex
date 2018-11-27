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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

public class GameController implements Initializable{
	
	private DatabaseController<Game> db;

	@FXML
	private VBox vboxGames;
	
	@Override
	public void initialize(URL url, ResourceBundle resources) {
		// TODO Auto-generated method stub
		getGames();
	}
	
	private void getGames() {
		db = new DatabaseController<Game>();
		String gameCommand = "SELECT * FROM game";
		try {
			ArrayList<Game> games = (ArrayList<Game>) db.SelectAll(gameCommand, Game.class);
			games.forEach(game -> {
				var item = new GameItem(game); 
				vboxGames.getChildren().add(item);
				item.setThing(onLabelClick());
			});	
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		}
	}
	

	//Custom function for handeling the onmouseclickEvent
	public Consumer<MouseEvent> onLabelClick() {
		return (event -> {
	    	var userLabel = (Label) event.getSource();
	        System.out.println("mouse click detected! " + userLabel.getText());
		});
    }
}