package Controller;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

import Model.Game;
import Pane.GameItem;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
		db = new DatabaseController<Game>("jdbc:mysql://digitalbyali.nl/", "u17091p12601_wordcrex", "u17091p12601_groepd", "P@s5w0rd!");
		String gameCommand = "SELECT * FROM game";
		try {
			ArrayList<Game> games = (ArrayList<Game>) db.SelectAll(gameCommand, Game.class);
			games.forEach(game -> {
				var item = new GameItem(game); 
				vboxGames.getChildren().add(item);
				item.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
				    @Override
				    public void handle(MouseEvent mouseEvent) {
				    	var game = (GameItem) mouseEvent.getSource();
				    	game.callEvent(mouseEvent);
				        System.out.println("mouse click detected! " + mouseEvent.getSource());
				    }
				});
			});
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
			//e.printStackTrace();
		}
	}
}