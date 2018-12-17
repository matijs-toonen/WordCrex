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
import View.Items.GameItemObserver;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class GameObserverController implements Initializable{
	
	private DatabaseController<Game> _db;
	private ArrayList<Game> _activeGames;
	private ArrayList<Game> _finishedGames;
	private AnchorPane _rootPane;
	
	@FXML
	private VBox vboxGames, vboxFinishedGames;
	
	@FXML 
	private TextField searchBox;
	
	public GameObserverController(AnchorPane rootPane) {
		_rootPane = rootPane;
	}
	
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
		
		String gameCommandFinished = Game.getWinnerQueryObserver(); 
		String gameCommandActive = Game.getActiveQueryObserver();

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
		
		Game.hasGameWithUsername(_finishedGames, searchBox.getText()).forEach(game -> {
			var GameItemObserver = new GameItemObserver(game);
			vboxFinishedGames.getChildren().add(GameItemObserver);
			GameItemObserver.setUserOnClickEvent(onLabelClick());
		});
		
		Game.hasGameWithUsername(_activeGames, searchBox.getText()).forEach(game -> {
			var GameItemObserver = new GameItemObserver(game);
			vboxGames.getChildren().add(GameItemObserver);
			GameItemObserver.setUserOnClickEvent(onLabelClick());
		});
	}
	
	private void loadBoard(Game game, Turn turn) {
		ScrollPane pane = null;
		try {
			var con = new ObserverBoardController(game, turn, _rootPane);
			var panes = new FXMLLoader(getClass().getResource("/View/Observe.fxml"));
			panes.setController(con);
			pane = panes.load();
		}
		catch(Exception ex) {
			Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
		}
		//borderPane.setCenter(root);
		_rootPane.getChildren().setAll(pane);
	}

	
	private int getTurn(String query) {
		try {
			return _db.SelectCount(query);
		} catch (SQLException e) {
			
		}
		return 1;
	}
	
	//Custom function for handeling the onmouseclickEvent
	public Consumer<MouseEvent> onLabelClick() {
		return (event -> {
	    	var userLabel = (Label) event.getSource();
	    	var parent = (GameItemObserver) userLabel.getParent();
	    	var game = parent.getGame();
	    	var turnQuery = Game.getTurnFromActiveGame(game.getGameId());
	    	var turn = new Turn(getTurn(turnQuery));
	    	loadBoard(game, turn);
		});
    }
}