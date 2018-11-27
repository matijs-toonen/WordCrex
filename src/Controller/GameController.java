package Controller;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

import Model.Game;
import Pane.GameItem;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class GameController implements Initializable{
	
	private DatabaseController db;
	
	@FXML
	private Pane paneGames;
	
	@FXML
	private VBox vboxGames;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		getGames();
	}
	
	private void getGames() {
		db = new DatabaseController<Game>(null, null, null, null);
		String gameCommand = "SELECT objectnaam, afstand FROM hemelobject";
		try {
			ArrayList<Game> games = (ArrayList<Game>) db.SelectAll(gameCommand, Game.class);
			games.forEach(game -> {
				var item = new GameItem(game); 
				vboxGames.getChildren().add(item);
			});
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}