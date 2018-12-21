package Controller;

import java.awt.Point;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import Model.BoardPlayer;
import Model.Game;
import Model.HandLetter;
import Model.Letter;
import Model.Score;
import Model.Tile;
import Model.Turn;
import Model.TurnBoardLetter;
import Model.Board.Board;
import Model.Board.PositionStatus;
import View.BoardPane.BoardTile;
import View.BoardPane.BoardTilePane;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.TextAlignment;

public class ObserverBoardController implements Initializable {
	
	private DatabaseController _db;
	private Turn _currentTurn;
	private Game _currentGame;
	private HashMap<Point, BoardTilePane> _boardTiles;
	private Board _board;
    private ArrayList<BoardTile> _currentHand;
    private int _maxTurnId;
	
	@FXML
	private Label lblScore1, lblScore2, lblPlayer1, lblPlayer2, lblTurn, errorPaneLabel;
	
	@FXML
	private Button btnNextTurn, btnPrevTurn;
	
	@FXML
	private Pane panePlayField, paneHand, boardPane, panePlayField2;
	
	@FXML
	private AnchorPane screenPane;	
	
	public ObserverBoardController(Game game, Turn turn) {
		_currentGame = game;
		_currentTurn = turn;
		_board = new Board();
		_boardTiles = new HashMap<Point, BoardTilePane>();
        _currentHand = new ArrayList<BoardTile>();
	}

	public ObserverBoardController(Game game) {
		this(game, new Turn(1));
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		setLabels();
	}
	
	private void setLabels() {
		lblPlayer1.setText(_currentGame.getUser1());
		lblPlayer1.setStyle("-fx-font-size: 28");
        lblPlayer2.setText(_currentGame.getUser2());
		lblPlayer2.setStyle("-fx-font-size: 28");
		lblScore1.setStyle("-fx-padding: 0 10; -fx-font-size: 20; -fx-background-color: #F4E4D3; -fx-background-radius: 25");
		lblScore1.setTextAlignment(TextAlignment.CENTER);
		lblScore2.setStyle("-fx-padding: 0 10; -fx-font-size: 20; -fx-background-color: #F4E4D3; -fx-background-radius: 25");
		btnPrevTurn.setDisable(_currentTurn.getTurnId() <= 1);
		
		checkMaxTurn();
		
		drawField();
		updateScore();
		drawHand();
	}
	
	private void checkMaxTurn() {
		var thread = new Thread(() -> {
			while(true) {
				try {
					var maxTurnQuery = "SELECT MAX(turn_id) from turn where game_id = " + _currentGame.getGameId();
					_maxTurnId = _db.SelectCount(maxTurnQuery);
					Platform.runLater(() -> {
						btnNextTurn.setDisable(_currentTurn.getTurnId() >= _maxTurnId);
					});
					Thread.sleep(1000);
				}
				 catch (SQLException e) {
					e.printStackTrace();
				}	
				catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
			
		});
		thread.setDaemon(true);
		thread.start();
	}
	
	public void nextTurn(ActionEvent event) {
		_currentTurn.incrementId();
		btnPrevTurn.setDisable(false);
		btnNextTurn.setDisable(_currentTurn.getTurnId() >= _maxTurnId);
		drawField();
		updateScore();
		drawHand();
	}
	
	public void previousTurn(ActionEvent event) {
		_currentTurn.decrementId();
		
		btnPrevTurn.setDisable(_currentTurn.getTurnId() <= 1);
		btnNextTurn.setDisable(_currentTurn.getTurnId() >= _maxTurnId);
		drawField();
		updateScore();
		drawHand();
	}
	
	private void updateScore() {
		var scorePlayer1 = "SELECT SUM(score) FROM turnplayer1 WHERE turn_id <= " + _currentTurn.getTurnId() + " AND game_id = " + _currentGame.getGameId();
		var scorePlayer2 = "SELECT SUM(score) FROM turnplayer2 WHERE turn_id <= " + _currentTurn.getTurnId() + " AND game_id = " + _currentGame.getGameId();
		
		try {
			var score1 = String.valueOf(_db.SelectCount(scorePlayer1));
			var score2 = String.valueOf(_db.SelectCount(scorePlayer2));

			lblScore1.setText(score1);
			lblScore2.setText(score2);	
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void drawHand() {
		var handLetters = getExistingHandLetters();
		visualizeHand(handLetters);
	}
	
	private void visualizeHand(ArrayList<HandLetter> handLetters) {
		paneHand.getChildren().clear();
		_currentHand.clear();
		int x = 10;
		int y = 13;

		for(var handLetter : handLetters) {
			for(var letter : handLetter.getLetters()) {
				var boardTile = new BoardTile(letter.getSymbol(), letter.getLetterId());
				boardTile.setLayoutX(x);
				boardTile.setLayoutY(y);
				boardTile.setStyle("-fx-background-color: #3B86FF; -fx-background-radius: 6");
				y += 44.5;
				boardTile.setMinWidth(39);
				boardTile.setMinHeight(39);
				
				_currentHand.add(boardTile);
				paneHand.getChildren().add(boardTile);
			}
		};
	}
	
	private void drawField() {
		createField(panePlayField, true);
		createField(panePlayField2, false);
	}
	
	private void createField(Pane board, boolean isPlayer1) 
	{
		var existingTurns = getTurns();
		var playedTurn = getPlayedTurn(isPlayer1);
		
		_db = new DatabaseController<Tile>();
		try 
		{
			var allTiles = (ArrayList<Tile>) _db.SelectAll("SELECT * FROM tile", Tile.class);
			int x = 13;
			for(int i = 0; i < 15; i++) {
				int y = 13;
				for(int j = 0; j < 15; j++) {
					Tile tile = null;
					
                    try
                    {
                        tile = getTileFromCollection(allTiles, i+1, j+1);
                    }
                    catch (Exception e)
                    {
                        System.err.println("Tile(s) does/do not not exist");
                    }
                    
					if(tile == null)
						throw new Exception("Tile == null");
						
					BoardTile boardTile = null;
					var cords = new Point(i, j);
					if(existingTurns.containsKey(cords)) {
						var turn = existingTurns.get(cords);
						boardTile = createBoardTile(cords, turn);
					}
					else if(playedTurn.containsKey(cords)) {
						var turn = playedTurn.get(cords);
						boardTile = createBoardTile(cords, turn);
					}
					var tilePane = new BoardTilePane(tile);
					tilePane.setLayoutX(x);
					tilePane.setLayoutY(y);
					tilePane.setMinWidth(39);
					tilePane.setMinHeight(39);
					
					tilePane.setBoardTile(boardTile);
					var value = String.valueOf(tile.getType().getValue()) + String.valueOf(tile.getType().getLetter()).trim(); 
					tilePane.getStyleClass().add(getStyle(value));
					
					_boardTiles.put(new Point(i, j), tilePane);
					board.getChildren().add(tilePane);
					
					y += 44.5;
				}
				x += 44.5;
			}
		} 
		catch (SQLException e) 
		{
			System.err.println("SQL Error");
			e.printStackTrace();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private String getStyle(String value) {
		switch(value) {
		case "6L":
			return "tile6L";
		case "4L":
			return "tile4L";
		case "4W":
			return "tile4W";
		case "3W":
			return "tile3W";
		case "2L":
			return "tile2L";
		case "0*":
			return "tileCenter";
		case "0":
			return "tile0";
		default:
			return "";
		}
	}
	
	private BoardTile createBoardTile(Point cords, TurnBoardLetter turn) {
		var boardTile = new BoardTile(turn.getSymbol(), turn.getLetterId());
		boardTile.setMinWidth(39);
		boardTile.setMinHeight(39);
		boardTile.setStyle("-fx-background-color: #43425D; -fx-background-radius: 6");
		_board.updateStatus(cords, PositionStatus.Taken);
		return boardTile;
	}
	
	private BoardTile createBoardTile(Point cords, BoardPlayer turn) {
		var boardTile = new BoardTile(turn.getSymbol(), turn.getLetterId());
		boardTile.setMinWidth(39);
		boardTile.setMinHeight(39);
		boardTile.setStyle("-fx-background-color: #3B86FF; -fx-background-radius: 6");
		_board.updateStatus(cords, PositionStatus.Taken);
		return boardTile;
	}

	private HashMap<Point, TurnBoardLetter> getTurns() {
		_db = new DatabaseController<TurnBoardLetter>();
		var turns = new HashMap<Point, TurnBoardLetter>();
		
		String tileQuery = Game.getExistingTiles(_currentGame.getGameId(), _currentTurn.getTurnId());
		try {
			((ArrayList<TurnBoardLetter>)_db.SelectAll(tileQuery, TurnBoardLetter.class)).forEach(turn -> {
				var cords = turn.getTileCords();
				var point = new Point((int) cords.getX() - 1, (int) cords.getY() - 1);
				turns.put(point, turn);	
			});
		}
		catch(SQLException e) {
			
		}
		return turns;
	}
	
	private HashMap<Point, BoardPlayer> getPlayedTurn(boolean isPlayer1){
		_db = new DatabaseController<BoardPlayer>();
		var turns = new HashMap<Point, BoardPlayer>();
		var table = isPlayer1 ? "boardplayer1" : "boardplayer2";
		
		var playedTilesQuery = Game.getPlayedTiles(_currentGame.getGameId(), _currentTurn.getTurnId(), table);
		try {
			((ArrayList<BoardPlayer>)_db.SelectAll(playedTilesQuery, BoardPlayer.class)).forEach(turn -> {
				var cords = turn.getCords();
				var point = new Point((int) cords.getX() - 1, (int) cords.getY() - 1);
				turns.put(point, turn);	
			});
		}
		catch(SQLException e) {
			
		}
		return turns;
	}
	
	private ArrayList<HandLetter> getExistingHandLetters() {
		_db = new DatabaseController<HandLetter>();
		
		var statement = Game.getExisitingHandLetters(_currentGame.getGameId(), _currentTurn.getTurnId());
		ArrayList<HandLetter> handLetters = new ArrayList<HandLetter>();
		
		try {
			handLetters = (ArrayList<HandLetter>) _db.SelectWithCustomLogic(getHandLetter(), statement);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return handLetters;
	}
	
	private Function<ResultSet, ArrayList<HandLetter>> getHandLetter(){
		return (resultSet -> {
			var handLetters = new ArrayList<HandLetter>();
			try {
				while(resultSet.next()) {	
					var columns = DatabaseController.getColumns(resultSet.getMetaData());
					
					var turn = new Turn(resultSet.getInt("turn_id"));
					var letter = new Letter(resultSet, columns);
					
					var existingHandLetter = HandLetter.getHandByGameAndTurn(handLetters, letter.getGame().getGameId(), turn.getTurnId());
					
					if(existingHandLetter.isPresent()) 
						existingHandLetter.get().addLetters(letter);
					else {
						var handLetter = new HandLetter(resultSet, columns);
						handLetters.add(handLetter);
					}
				}	
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return handLetters;
		});
	}
	
	private Tile getTileFromCollection(ArrayList<Tile> collection, int x, int y)
	{
		var allTiles = collection.stream().filter(t -> t.isAtPoint(new Point(x,y))).collect(Collectors.toList());
		return  allTiles.size() == 1 ? allTiles.get(0) : null;
	}
}
