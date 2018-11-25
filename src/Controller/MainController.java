package Controller;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

import Model.Game;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.Window;

public class MainController implements Initializable {

	@FXML
	private Label lblText;
	
	@FXML
	private Pane paneChild;
	

	private DatabaseController db;
	
	private void getGames() {
		db = new DatabaseController<Game>(null, null, null, null);
		String gameCommand = "SELECT objectnaam FROM hemelobject";
		try {
			ArrayList<Game> games = (ArrayList<Game>) db.SelectAll(gameCommand, Game.class);
			games.forEach(game -> System.out.println(game.objectNaam));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public void showChild(ActionEvent actionEvent) {
		//Type van de frame die je wilt inladen (graag alles Pane maken)
		Pane endFrame = null;
		try {
			endFrame = (Pane) FXMLLoader.load(getClass().getResource("/View/TestFrame.fxml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(endFrame != null) {
			paneChild.getChildren().setAll(endFrame);
		}
	}

	
	public void showMessage(ActionEvent actionEvent) {
		lblText.setText("Nieuwe test value");
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		getGames();
		
	}
}
