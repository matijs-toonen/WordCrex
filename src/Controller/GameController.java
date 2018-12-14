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
	
	private DatabaseController<Game> _db;
	private ArrayList<Game> _activeGames;
	private AnchorPane _rootPane;
	private ArrayList<Game> _finishedGames;
	
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
	
	private void getGames() {
		_db = new DatabaseController<Game>();
		
		var user = MainController.getUser();
		String username = user.getUsername();
		
		String gameCommandFinished = Game.getWinnerQuery(username); 
		String gameCommandActive = Game.getAcitveQuery(username);

		try {
			_activeGames = (ArrayList<Game>) _db.SelectAll(gameCommandActive, Game.class);
			_finishedGames = (ArrayList<Game>) _db.SelectAll(gameCommandFinished, Game.class);
			
			renderGames();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		}
	}
	
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

	//Custom function for handeling the onmouseclickEvent
	public Consumer<MouseEvent> onLabelClick() {
		return (event -> {
	    	var userLabel = (Label) event.getSource();
	    	var parent = (GameItem) userLabel.getParent();
	    	loadBoard(parent.getGame().getGameId());
	        System.out.println("mouse click detected! " + userLabel.getText());
		});
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