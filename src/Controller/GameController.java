package Controller;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import Model.Game;
import javafx.fxml.Initializable;

public class GameController implements Initializable{

	private DatabaseController db;
	
	private void getGames() {
		db = new DatabaseController<Game>(null, null, null, null);
		String gameCommand = "SELECT * FROM HemelObject";
		try {
			var games = db.SelectAll(gameCommand, Game.class);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		getGames();
	}

}
