package Controller;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import Model.Game;
import Model.Turn;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;

public class BoardObserverController implements Initializable {
	private AnchorPane _rootPane;
	private Game _currentGame;
	private Turn _currentTurn;
	
	@FXML
	private AnchorPane _boardPlayer1, _boardPlayer2;
	
	public BoardObserverController(Game game, Turn turn, AnchorPane rootPane) {
		_rootPane = rootPane;
		_currentTurn = turn;
		_currentGame = game;
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		showBoards();
	}
	
	private void showBoards() {
		AnchorPane pane = null;
		try 
		{
			var con = new BoardController(_currentGame, _currentTurn, true, _rootPane);			
			var panes = new FXMLLoader(getClass().getResource("/View/Games.fxml"));
			
			panes.setController(con);
			pane = panes.load();
		}
		catch(Exception ex) {
			Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
		}
		_rootPane.getChildren().setAll(pane);
	}
}
