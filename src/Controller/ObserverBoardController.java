package Controller;

import java.awt.Point;
import java.io.File;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import Model.Game;
import Model.Letter;
import Model.Score;
import Model.Symbol;
import Model.Tile;
import Model.Turn;
import Model.TurnBoardLetter;
import Model.Board.Board;
import Model.Board.PositionStatus;
import Tests.BoardTileTest;
import View.BoardPane.BoardTile;
import View.BoardPane.BoardTilePane;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class ObserverBoardController implements Initializable {
	
	private DatabaseController _db;
	private ArrayList<Letter> _letters = new ArrayList<Letter>();
	private Turn _currentTurn;
	private Game _currentGame;
	private HashMap<Point, BoardTilePane> _boardTiles;
	private HashMap<Point, BoardTileTest> _boardTilesTest;
	private Board _board;
    private ArrayList<BoardTile> _currentHand;
    private HashMap<Point, BoardTile> _fieldHand;
    private AnchorPane _rootPane;
    private Score _currentScore;
	
	@FXML
	private Label lblScore1, lblScore2, lblPlayer1, lblPlayer2, errorPaneLabel;
	
	@FXML
	private Button btnTest, btnShuffle;
	
	@FXML
	private Pane panePlayField, paneHand, boardPane, panePlayField2;
	
	@FXML
	private AnchorPane rightBarAnchor;
	
	@FXML
	private ImageView shuffle, accept;
	
	@FXML
	private AnchorPane screenPane;	
	
	public ObserverBoardController(Game game, Turn turn, AnchorPane rootPane) {
		_currentGame = game;
		_currentTurn = turn;
		_rootPane = rootPane;
		_board = new Board();
		_boardTiles = new HashMap<Point, BoardTilePane>();
        _currentHand = new ArrayList<BoardTile>();
        _fieldHand = new HashMap<Point, BoardTile>();
	}

	public ObserverBoardController(Game game, AnchorPane rootPane) {
		this(game, new Turn(1), rootPane);
	}
	
	private void showGameScreen() {
		AnchorPane pane = null;
		try 
		{
			GameController con = new GameController(_rootPane);			
			var panes = new FXMLLoader(getClass().getResource("/View/Games.fxml"));
			
			panes.setController(con);
			pane = panes.load();
		}
		catch(Exception ex) {
			Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
		}
		_rootPane.getChildren().setAll(pane);
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
		lblScore1.setText("1");
		lblScore1.setStyle("-fx-font-size: 20; -fx-background-color: #F4E4D3; -fx-background-radius: 25 0 0 25");
		lblScore2.setText("9");
		lblScore2.setStyle("-fx-font-size: 20; -fx-background-color: #F4E4D3; -fx-background-radius: 0 25 25 0");
		
		scoreRefreshThread();

		createField(false);
	}
	
	private void scoreRefreshThread() {
		
		Thread chatThread = new Thread(){
		    public void run(){
		    	
		    	while(MainController.getUser() != null) {
		    		refreshScore();
	    			
	    			try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
	    		}
		    }
		};
		
		chatThread.setDaemon(true);
		chatThread.start();
	}
	
	private void refreshScore() {
		var _dbScore = new DatabaseController<Score>();
		String scoreQuery = Score.getScoreFromGameQuery(_currentGame.getGameId());
		
		try {
			_currentScore = (Score) _db.SelectFirst(scoreQuery, Score.class);
		} catch (SQLException e) {
		}
		
		Platform.runLater(() -> {
			lblScore1.setText(Integer.toString(_currentScore.getOwnScore()));
			lblScore2.setText(Integer.toString(_currentScore.getOpponentScore()));
	    });
		
	}
		
	private void createField(boolean test) 
	{
		var existingTurns = getTurns();
		
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
						

					if(!test)
					{
						BoardTile boardTile = null;
						var cords = new Point(i, j);
						if(existingTurns.containsKey(cords)) {
							var turn = existingTurns.get(cords);
							boardTile = createBoardTile(cords, turn);
						}
						var tilePane = new BoardTilePane(tile);
						tilePane.setLayoutX(x);
						tilePane.setLayoutY(y);
						tilePane.setMinWidth(39);
						tilePane.setMinHeight(39);
						
						tilePane.setBoardTile(boardTile);
						switch(String.valueOf(tile.getType().getValue()) + String.valueOf(tile.getType().getLetter()).trim()) {
						case "6L":
							tilePane.getStyleClass().add("tile6L");
							break;
						case "4L":
							tilePane.getStyleClass().add("tile4L");
							break;
						case "4W":
							tilePane.getStyleClass().add("tile4W");
							break;
						case "3W":
							tilePane.getStyleClass().add("tile3W");
							break;
						case "2L":
							tilePane.getStyleClass().add("tile2L");
							break;
						case "0*":
							tilePane.getStyleClass().add("tileCenter");
							break;
						case "0":
							tilePane.getStyleClass().add("tile0");
							break;
						}
						
						_boardTiles.put(new Point(i, j), tilePane);
						panePlayField.getChildren().add(tilePane);						
					}
					else
					{
						var boardTileTest = new BoardTileTest(tile);
						_boardTilesTest.put(new Point(i, j), boardTileTest);
					}
					
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
	
	private BoardTile createBoardTile(Point cords, TurnBoardLetter turn) {
		var boardTile = new BoardTile(turn.getSymbol(), turn.getLetterId());
		boardTile.setMinWidth(39);
		boardTile.setMinHeight(39);
		boardTile.setStyle("-fx-background-color: #43425D; -fx-background-radius: 6");
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
	
	private Tile getTileFromCollection(ArrayList<Tile> collection, int x, int y)
	{
		var allTiles = collection.stream().filter(t -> t.isAtPoint(new Point(x,y))).collect(Collectors.toList());
		return  allTiles.size() == 1 ? allTiles.get(0) : null;
	}
	
	private void resetFieldHand() {
		_fieldHand.entrySet().forEach(handLetter -> {
			var cords = handLetter.getKey();
			
			var tilePane = _boardTiles.get(cords);
			tilePane.removeBoardTile();
			_board.updateStatus(cords, PositionStatus.Open);
		});	

		_fieldHand.clear();
	}
	
	private void playTileSound() {
		String bip = "src/Resources/tileMove.mp3";
		Media hit = new Media(new File(bip).toURI().toString());
		MediaPlayer mediaPlayer = new MediaPlayer(hit);
		mediaPlayer.play();
	}
}
