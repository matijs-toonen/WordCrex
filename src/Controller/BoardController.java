package Controller;

import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import Model.BoardPlayer;
import Model.Game;
import Model.HandLetter;
import Model.Letter;
import Model.Score;
import Model.Symbol;
import Model.Tile;
import Model.Turn;
import Model.TurnBoardLetter;
import Model.TurnPlayer;
import Model.Word;
import Model.WordData;
import Model.Board.Board;
import Model.Board.PositionStatus;
import Tests.BoardTileTest;
import View.BoardPane.BoardTile;
import View.BoardPane.BoardTilePane;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.util.Pair;

public class BoardController implements Initializable {
	
	private DatabaseController _db;
	private Random _random = new Random();
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

	private boolean _chatVisible;
	private boolean _historyVisible;
	
	@FXML
	private Label lblScore1, lblScore2, lblPlayer1, lblPlayer2, lblTiles, errorPaneLabel;
	
	@FXML
	private Button btnTest, btnShuffle;
	
	@FXML
	private Pane panePlayField, paneHand, boardPane, waitingPane, errorMessagePane, blockedOverplay;
	
	@FXML
	private AnchorPane rightBarAnchor;
	
	@FXML
	private ImageView shuffle, accept;
	
	@FXML
	private AnchorPane screenPane;	
	
	public BoardController(Game game, Turn turn, AnchorPane rootPane) {
		_currentGame = game;
		_currentTurn = turn;
		_rootPane = rootPane;
		_board = new Board();
		_boardTiles = new HashMap<Point, BoardTilePane>();
        _currentHand = new ArrayList<BoardTile>();
        _fieldHand = new HashMap<Point, BoardTile>();
	}

	public BoardController(Game game, AnchorPane rootPane) {
		this(game, new Turn(1), rootPane);
	}

	private void getLettersAndValidate() {
		_db = new DatabaseController<Symbol>();
		var statement = Letter.getUnusedLetters(_currentGame.getGameId());
		
		try {
			_letters = (ArrayList<Letter>)_db.SelectAll(statement, Letter.class);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		checkValidPotSize();
	}
	
	private void checkValidPotSize()
	{
		if(_letters.size() == 0 && _currentHand.size() == 0)
		{
			try 
			{
				// This query sets the game to a finished state
				var score = (Score)_db.SelectFirst("SELECT * FROM score where game_id = " + _currentGame.getGameId(), Score.class);
				String winner = score.getOwnScore() > score.getOpponentScore() ? MainController.getUser().getUsername() : score.getOpponent();
				_db.Update("update game " + 
							"set game_state = 'finished', username_winner = '" + winner + "' " +   
							"where game_id = " + _currentGame.getGameId() + "");
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			Platform.runLater(() -> {
				showGameScreen();
			});
		}
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
		lblPlayer1.setText(MainController.getUser().getUsername());
		lblPlayer1.setStyle("-fx-font-size: 28");
        var opponent = getOpponent();
        lblPlayer2.setText(opponent);
		lblPlayer2.setStyle("-fx-font-size: 28");
		lblScore1.setText("1");
		lblScore1.setStyle("-fx-font-size: 20; -fx-background-color: #F4E4D3; -fx-background-radius: 25 0 0 25");
		lblScore2.setText("9");
		lblScore2.setStyle("-fx-font-size: 20; -fx-background-color: #F4E4D3; -fx-background-radius: 0 25 25 0");
		lblTiles.setStyle("-fx-font-size: 20;");
		
		scoreRefreshThread();

		createField(false);
		createHand();
		dragOnHand();
		
		
		// add tooltip to buttons
	}
	
    private String getOpponent() {
        if(_currentGame.getOpponent() == null)
            return checkPlayerIfPlayer1() ? _currentGame.getUser2() : _currentGame.getUser1();
        
        return _currentGame.getOpponent();
    }
	
	/**
	 * disable the board and show waiting animation
	 */
	private void disableBoard() {
		blockedOverplay.setVisible(true);
		waitingPane.setVisible(true);
		boardPane.setStyle("-fx-opacity: 0.3");
	}
	
	/**
	 * disable the board and show waiting animation
	 */
	private void enableBoard() {
		blockedOverplay.setVisible(false);
		waitingPane.setVisible(false);
		boardPane.setStyle("-fx-opacity: 1.0");
	}
	
	
	/**
	 * Hide error message
	 */
	public void hideErrorMessage() {
		blockedOverplay.setVisible(false);
		boardPane.setStyle("-fx-opacity: 1.0");
		
		errorMessagePane.setVisible(false);
	}
	
	/**
	 * Hide error message
	 */
	private void showErrorMessage(String text) {
		blockedOverplay.setVisible(true);
		boardPane.setStyle("-fx-opacity: 0.3");
		
		errorPaneLabel.setText(text);
		errorMessagePane.setVisible(true);
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
			
			try {
				lblTiles.setText(Integer.toString(_db.SelectCount("select COUNT(*) from pot where game_id = " + _currentGame.getGameId())));
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    });
		
	}

	public void initializeTest()
	{
		_boardTilesTest = new HashMap<Point, BoardTileTest>();
		createField(true);
	}
	
	public boolean placeTest(Point cord, Symbol symbol)
	{
		if(_boardTilesTest.containsKey(cord))
		{
			var tile = _boardTilesTest.get(cord);
			tile.setSymbol(symbol);
			
			return true;
		}
		
		return false;
	}

	public void shuffle(){
		placeHand(true);
	}
	
	public void playTurn()
	{
		if(!isMiddleOpen())
		{	
			if(!_board.allChainedToMiddle())
				showErrorMessage("Niet alle tiles zitten aan elkaar");
			else
			{
				var wordsData = getUniqueWordData();
				
				if(wordsData.size() > 1 && _currentTurn.getTurnId() == 1)
				{
					showErrorMessage("Je mag maar 1 woord leggen");
					return;
				}
				
				if(wordsData.size() == 0)
				{
					showErrorMessage("Je moet 1 woord leggen\nof je woord is geen valide woord");
					return;
				}
				
				var statementTurnPlayer = "";
				var statementBoardPlayer = new ArrayList<String>();
				var playerNum = checkPlayerIfPlayer1() ? 1 : 2;
				var score = 0;
				
				for(var wordData : wordsData)
				{
					score += wordData.getScore();
				}
				
				var gameId = _currentGame.getGameId();
				var turnId = _currentTurn.getTurnId();
				var username = MainController.getUser().getUsername();
								
				statementTurnPlayer = String.format("INSERT INTO turnplayer%1$s \n"
						+ "(game_id, turn_id, username_player%1$s, bonus, score, turnaction_type) \n"
						+ "VALUES(%2$s, %3$s, '%4$s', %5$s, %6$s, '%7$s')"
						,playerNum, gameId, turnId, username, 0, score, "play");
				
				var uniqueLettersData = new HashMap<Integer, Pair<Character, Point>>();
				
				for(var wordData : wordsData)
				{
					var lettersData = wordData.getLetters();
					
					lettersData.entrySet().forEach(letterData -> {
						
						var letterId = letterData.getKey();
						var letterCharCord = letterData.getValue();
						
						if(!uniqueLettersData.containsKey(letterId))
							uniqueLettersData.put(letterId, letterCharCord);
						
					});
				}
				
				uniqueLettersData.entrySet().forEach(letterData -> {
					
					var letterId = letterData.getKey();
					var tileX = (int) letterData.getValue().getValue().getX()+1;
					var tileY = (int) letterData.getValue().getValue().getY()+1;
					
					statementBoardPlayer.add(String.format("INSERT INTO boardplayer%1$s \n"
							+ "(game_id, username, turn_id, letter_id, tile_x, tile_y) \n"
							+ "VALUES (%2$s, '%3$s', %4$s, %5$s, %6$s, %7$s);"
							, playerNum,gameId, username, turnId, letterId, tileX, tileY));
				});
				

								
				String[] statementBoardPlayerArr = new String[statementBoardPlayer.size()];
				statementBoardPlayerArr = statementBoardPlayer.toArray(statementBoardPlayerArr);
				
				try 
				{
					if(_db.Insert(statementTurnPlayer)) 
					{
						if(_db.InsertBatch(statementBoardPlayerArr)) 
						{
							renewHand();
						}
					}
				}
				catch(SQLException e) {
					e.printStackTrace();
				}
			}
		}
		else
			showErrorMessage("Middelste tile moet gevuld zijn");
	}
	
	private LinkedList<WordData> getUniqueWordData()
	{
		var uniqueWordsData = new LinkedList<WordData>();
				
		for(Point point : _fieldHand.keySet())
		{
			var wordsData = getWordData(point);
			
			for(var wordData : wordsData)
			{
				uniqueWordsData.add(wordData);
			}
		}
				
		if(uniqueWordsData.size() == 1)
			return uniqueWordsData;
		
		for(int i = 0; i < uniqueWordsData.size(); i++)
		{
			for(int j = 0; j < uniqueWordsData.size(); j++)
			{
				if(uniqueWordsData.get(i).equals(uniqueWordsData.get(j)))
					continue;
				
				if(uniqueWordsData.get(i).hasSameLetterIds(uniqueWordsData.get(j).getLetterIds()))
					uniqueWordsData.remove(j);
			}
		}
		
		return uniqueWordsData;
	}
	
	private boolean isMiddleOpen()
	{		
		return _board.canPlace(_board.getMiddle());
	}
	
	public void clickSkipTurn() {
		System.out.println("currentturn = " + _currentTurn.getTurnId());
		String insertQuery = Game.getPassQuery(_currentGame.getGameId(), _currentTurn.getTurnId(), MainController.getUser().getUsername(), checkPlayerIfPlayer1());
		
		var _db = new DatabaseController<Game>();

		try {
			_db.Insert(insertQuery);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		renewHand();
	}
	
	public void resignGame()
	{		
		try 
		{
			var resignTurnStatement = TurnPlayer.getResignQuery(checkPlayerIfPlayer1(), "resign", _currentGame.getGameId(), _currentTurn.getTurnId(), MainController.getUser().getUsername());
			var resignGameStatement = Game.getResignQuery(_currentGame.getGameId(), getOpponent());
			
			if(_db.Insert(resignTurnStatement))
				_db.Update(resignGameStatement);
			
		} catch (SQLException e) {
		}
		
		showGameScreen();
	}
	
	public void reset() {
		resetFieldHand();
		placeHand(false);
		shuffle.setVisible(true);
	}
	
	public void renewHand() {
		createHand();
		shuffle.setVisible(true);
	}
	
	private boolean needsToWaitForHandLetters(String table) {
		
		var query = TurnPlayer.hasPlacedTurn(table, _currentTurn.getTurnId(), _currentGame.getGameId());
		try {
			return _db.SelectCount(query) == 0;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	private boolean checkPlayerIfPlayer1() {
		return MainController.getUser().getUsername().equals(_currentGame.getUser1());
	}
	
	public void openHistory() throws IOException{
		if(!_historyVisible) {
			
			closeCommunicationFrame();
			//Parent historyFrame = FXMLLoader.load(getClass().getResource("/View/SetHistory.fxml"));
			
			SetHistoryController setHistoryController = new SetHistoryController(_currentGame);        
    		FXMLLoader historyFrame = new FXMLLoader(getClass().getResource("/View/SetHistory.fxml"));
    		historyFrame.setController(setHistoryController);
    		AnchorPane pane = historyFrame.load();
    		
			rightBarAnchor.getChildren().setAll(pane);
			_historyVisible = true;
			
		}
		else {
			rightBarAnchor.getChildren().clear();
			_historyVisible = false;
		}
	}
	
	public void openChat() throws IOException {
		if(!_chatVisible) {
			closeCommunicationFrame();
			
			ChatController chatController = new ChatController(_currentGame);        
    		FXMLLoader chatFrame = new FXMLLoader(getClass().getResource("/View/Chat.fxml"));
    		chatFrame.setController(chatController);
    		HBox pane = chatFrame.load();
			
			rightBarAnchor.getChildren().setAll(pane);
			_chatVisible = true;
		}
		else {
			rightBarAnchor.getChildren().clear();
			_chatVisible = false;
		}
	}

	private void closeCommunicationFrame() {
		if(_historyVisible) {
			rightBarAnchor.getChildren().clear();
			_historyVisible = false;
		}
		
		if(_chatVisible) {
			rightBarAnchor.getChildren().clear();
			_chatVisible = false;
		}
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
						tilePane.setDropEvents(createDropEvents());
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
	
	
	private void createHand() {
		_currentHand.clear();
		_db = new DatabaseController<HandLetter>();
		
		var tableOpponent = checkPlayerIfPlayer1() ? "turnplayer2" : "turnplayer1";
		var tableMe = checkPlayerIfPlayer1() ? "turnplayer1" : "turnplayer2";
		var needsToPlaceOpponent = needsToWaitForHandLetters(tableOpponent);
		var needsToPlaceOwn = needsToWaitForHandLetters(tableMe);
		
		if(needsToPlaceOwn) {
			var handLetters = getHandLetters();
			visualizeHand(handLetters);
			return;
		}
		
		getGeneratedLetters(needsToPlaceOpponent);	
	}
	
	private void visualizeHand(ArrayList<HandLetter> handLetters) {
		paneHand.getChildren().clear();
		_currentHand.clear();
		int x = 0;
		int y = 13;

		for(var handLetter : handLetters) {
			for(var letter : handLetter.getLetters()) {
				var boardTile = new BoardTile(letter.getSymbol(), letter.getLetterId());
				boardTile.setDraggableEvents();
				boardTile.setLayoutX(x);
				boardTile.setLayoutY(y);
				boardTile.setStyle("-fx-background-color: #3B86FF; -fx-background-radius: 6");
				y += 44.5;
				boardTile.setMinWidth(39);
				boardTile.setMinHeight(39);
				
				_currentHand.add(boardTile);
			}
		};
		
		placeHand(false);		
	}
	
	private void getGeneratedLetters(boolean checkGenerated){
		if(checkGenerated) {
			_currentTurn.incrementId();
			waitForVisualizeNewHandLetters();
		}
		else {
			var scores = getScores();
			if(insertScore(scores.getKey(), scores.getValue()))
			{
				_currentTurn.incrementId();
				var handLetters = generateHandLetters();
				updatePaneWithNewLetters();
				visualizeHand(handLetters);
			}
		}
	}
	
	private boolean insertScore(int ownScore, int oppScore)
	{
		var ownInsert = false;
		var oppInsert = false;
		
		var bonus = 5;
		
		var gameId = _currentGame.getGameId();
		var turnId = _currentTurn.getTurnId();
		
		var ownPlayerNum = checkPlayerIfPlayer1() ? 1 : 2;
		
		var oppPlayerNum = ownPlayerNum == 1 ? 2 : 1;
		
		try
		{
			if(ownScore == 0 && oppScore == 0)
				return true;
			
			if(ownScore == oppScore)
			{
				var statement = String.format("UPDATE turnplayer%s "
						+ "SET bonus = %s "
						+ "WHERE game_id = %s "
						+ "AND turn_id = %s "
						+ "AND score = %s ",oppPlayerNum, bonus, gameId, turnId, oppScore);

				if(_db.Update(statement))
					oppScore += bonus;
			}
			
			var usedLetters = (ArrayList<TurnBoardLetter>)_db.SelectAll("SELECT letter_id FROM turnboardletter where game_id = " + gameId, TurnBoardLetter.class);

			if(ownScore > oppScore)
			{
				var selectStatement = String.format("SELECT letter_id, game_id, turn_id, tile_x, tile_y "
						+ "FROM boardplayer%s "
						+ "WHERE game_id = %s "
						+ "AND turn_id = %s", ownPlayerNum, gameId, turnId);
				
				ArrayList<BoardPlayer> playerBoard = (ArrayList<BoardPlayer>) 
						_db.SelectAll(selectStatement, BoardPlayer.class);
				
				ArrayList<String> insertStatements = new ArrayList<String>();
				
				for(var boardPlayer : playerBoard)
				{
					var letterId = boardPlayer.getLetterId();
					if(usedLetters.stream().filter(letter -> letter.getLetterId() == letterId).findFirst().isPresent()) 
						continue;

					var insertStatement = String.format("INSERT INTO turnboardletter "
							+ "(letter_id, game_id, turn_id, tile_x, tile_y)"
							+ "VALUES"
							+ "(%s, %s, %s, %s, %s)"
							, boardPlayer.getLetterId(), boardPlayer.getGameId(), boardPlayer.getTurnId(),
							boardPlayer.getLetterX(), boardPlayer.getLetterY());
					
					insertStatements.add(insertStatement);
				}
				
				String[] statementBoardPlayerArr = new String[insertStatements.size()];
				statementBoardPlayerArr = insertStatements.toArray(statementBoardPlayerArr);

				oppInsert = true;
				ownInsert = _db.InsertBatch(statementBoardPlayerArr);
			}
			else
			{
				var selectStatement = String.format("SELECT letter_id, game_id, turn_id, tile_x, tile_y "
						+ "FROM boardplayer%s "
						+ "WHERE game_id = %s "
						+ "AND turn_id = %s", oppPlayerNum, gameId, turnId);
				
				ArrayList<BoardPlayer> playerBoard = (ArrayList<BoardPlayer>) 
						_db.SelectAll(selectStatement, BoardPlayer.class);
				
				ArrayList<String> insertStatements = new ArrayList<String>();
				
				for(var boardPlayer : playerBoard)
				{
					var letterId = boardPlayer.getLetterId();
					if(usedLetters.stream().filter(letter -> letter.getLetterId() == letterId).findFirst().isPresent())
						continue;
					
					var insertStatement = String.format("INSERT INTO turnboardletter "
							+ "(letter_id, game_id, turn_id, tile_x, tile_y)"
							+ "VALUES"
							+ "(%s, %s, %s, %s, %s)"
							, boardPlayer.getLetterId(), boardPlayer.getGameId(), boardPlayer.getTurnId(),
							boardPlayer.getLetterX(), boardPlayer.getLetterY());
					
					insertStatements.add(insertStatement);
				}
				
				String[] statementBoardPlayerArr = new String[insertStatements.size()];
				statementBoardPlayerArr = insertStatements.toArray(statementBoardPlayerArr);
				
				oppInsert = _db.InsertBatch(statementBoardPlayerArr);
				ownInsert = true;
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return ownInsert == oppInsert;
	}
		
	private Pair<Integer, Integer> getScores()
	{
		Pair<Integer, Integer> ownOppScore = null;
		try
		{
			var gameId = _currentGame.getGameId();
			var turnId = _currentTurn.getTurnId();
			
			var ownPlayerNum = checkPlayerIfPlayer1() ? 1 : 2;
			
			var opponentPlayerNum = ownPlayerNum == 1 ? 2 : 1;
			
			var ownScore = _db.SelectCount(String.format("SELECT score FROM turnplayer%1$s "
					+ "WHERE game_id = %2$s "
					+ "AND turn_id = %3$s", ownPlayerNum, gameId, turnId));
			
			var opponentScore = _db.SelectCount(String.format("SELECT score FROM turnplayer%1$s "
					+ "WHERE game_id = %2$s "
					+ "AND turn_id = %3$s",opponentPlayerNum, gameId, turnId));
			
			ownOppScore = new Pair<>(ownScore, opponentScore);
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return ownOppScore;
	}
	
	private ArrayList<HandLetter> getHandLetters() {
		var handLetters = getExistingHandLetters();
		return handLetters.size() == 0 ? generateHandLetters() : handLetters; 
	}
	
	private int getAmountLetters(ArrayList<HandLetter> handLetters) {
		if(handLetters.isEmpty())
			return 0;
		return handLetters.get(0).getLetters().size();
	}
	
	private void waitForVisualizeNewHandLetters(){
		disableBoard();
		
		var thread = new Thread() {
			public void run() {
				var handLetters = getExistingHandLetters();
				int tries = 0;
				while(true)
				{
					// Try at least 4 times before exiting
					if(tries == 4)
					{
						break;
					}
					
					// Break loop if the handletters are correctly set
					if(getAmountLetters(handLetters) == 7)
					{
						break;
					}
					
					// Wait for the opponent
					if(getAmountLetters(handLetters) > 0)
					{
						tries ++;
					}
					
					try 
					{
						var hasResigned = _db.SelectCount("SELECT COUNT(*) FROM game WHERE (game_state = 'resigned' OR game_state = 'finished') AND game_id = " + _currentGame.getGameId()) == 1;
						
						if(hasResigned) {
							Platform.runLater(() -> {
								showGameScreen();
							});
							return;
						}
						handLetters = getExistingHandLetters();
						
						Thread.sleep(1000);
					} 
					catch (Exception e) 
					{
						e.printStackTrace();
					}
				}
				
				final var finalHandLetters = handLetters;
				
				Platform.runLater(() -> {
					updatePaneWithNewLetters();
	            	visualizeHand(finalHandLetters);
	            	enableBoard();
		        });
			}
		};
		
		thread.setDaemon(true);
		thread.start();
	}
	
	private void updatePaneWithNewLetters() {
		var exisitingTurns = getTurns();
		resetBoard();
		
		exisitingTurns.entrySet().forEach(turn -> {
			var cords = turn.getKey();
			var boardTilePane = _boardTiles.get(cords);
			var boardTile = createBoardTile(cords, turn.getValue());
			boardTilePane.setBoardTile(boardTile);
		});
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
	
	private void resetBoard() {
		_fieldHand.entrySet().forEach(tile -> {
			var cords = tile.getKey();
			var boardTilePane = _boardTiles.get(tile.getKey());
			
			boardTilePane.removeBoardTile();
			_board.updateStatus(cords, PositionStatus.Open);
		});
		
		_fieldHand.clear();
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
	
	private ArrayList<HandLetter> generateHandLetters() {
		var handLetters = new ArrayList<HandLetter>();
		
		if(!hasExisitingTurn()) {
			addTurn();
		}
		
		getLettersAndValidate();
		
		for(int i = 0; i < 7; i++) {
			var letter = createHandLetter();
			if(letter != null)
				handLetters.add(letter);
		}
		
		return handLetters;
	}
	
	private void addTurn() {		
		var turnStatement = Turn.getInsertNewTurn(_currentGame.getGameId(), _currentTurn.getTurnId());
		
		try {
			_db.Insert(turnStatement);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void placeHand(boolean shuffle) {
		int x = 10;
		int y = 13;
		
		if(shuffle) 
			Collections.shuffle(_currentHand);	
		
		for(var tile : _currentHand) {
			tile.setLayoutX(x);
			tile.setLayoutY(y);
			y += 44.5;
			paneHand.getChildren().remove(tile);
			paneHand.getChildren().add(tile);
		}
	}	
	
	private boolean hasExisitingTurn() {
		_db = new DatabaseController<Turn>();
		try {
			return _db.SelectCount("SELECT COUNT(*) FROM turn WHERE game_id = " + _currentGame.getGameId() + " AND turn_id = " + _currentTurn.getTurnId()) == 1;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
		
	private HandLetter createHandLetter() {
		var letter = createLetter();
		if(letter == null)
			return null;
		
		return new HandLetter(_currentGame, _currentTurn, letter);
	}
	
	//
	private Letter createLetter() {
		var amountOfLetters = _letters.size();
		if(amountOfLetters == 0) 
			return null;
		
		var number = _random.nextInt(amountOfLetters);
		var rndLetter = _letters.get(number);
		
		var statement = HandLetter.insertLetter(_currentGame.getGameId(), _currentTurn.getTurnId(), rndLetter.getLetterId());
		try {
			if(_db.Insert(statement)) {
				_letters.remove(number);
				return rndLetter;
			}
		} catch (SQLException e) {
		}
		return null;
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
	
	private Background getBackground(Color color) {
		var backgroundFill = new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY);
		return new Background(backgroundFill);
	}
	
	private Consumer<DragEvent> createDropEvents(){
		return (event -> {
			if(event.getGestureTarget() instanceof BoardTilePane) {
				var sourceTile = (BoardTile) event.getGestureSource();
				var boardTile = (BoardTilePane) event.getGestureTarget();
				
				var cords = boardTile.getCords();
								
				if(!_board.canPlace(cords))
					return;
				
				// Remove letter from fieldhand
				if(sourceTile.getParent() instanceof BoardTilePane) {
					var oldBoardTile = (BoardTilePane) sourceTile.getParent();
					var oldCords = oldBoardTile.getCords();						
					_board.updateStatus(oldCords, PositionStatus.Open);
					oldBoardTile.removeBoardTile();
					_fieldHand.remove(oldCords);
					_boardTiles.put(boardTile.getCords(), oldBoardTile);
				}
				
				sourceTile.setLayoutX(0);
				sourceTile.setLayoutY(0);
				boardTile.setBoardTile(sourceTile);
				
				_fieldHand.put(cords, sourceTile);
				_boardTiles.put(cords, boardTile);
				
				event.acceptTransferModes(TransferMode.ANY);
				event.setDropCompleted(true);
				_board.updateStatus(cords, PositionStatus.Taken);
								
				refreshVisualWordScore(cords, boardTile, sourceTile);	
					
				event.consume();
			}
		});
	}
	
	private void refreshVisualOnRemove(Point removePoint)
	{
		var adjacentCords = _board.getAdjacentCords(removePoint);
		
		adjacentCords.forEach(cord -> {
			var target = _boardTiles.get(cord);
			var source = _fieldHand.get(cord);
			refreshVisualWordScore(cord, target, source);
		});
	}
		
	private void refreshVisualWordScore(Point dropPoint, BoardTilePane target, BoardTile source) {
		
		var wordsData = getWordData(dropPoint);
		
		if(wordsData.size() == 0)
		{
			System.out.println("Not a word");
			
			var column = (int)dropPoint.getX();
			var row = (int)dropPoint.getY();
			
			var horStartEnd = collectLettersUntilSeperator(
					createCharArrFromCords(row, true), column, ' ').getValue();
			
			var verStartEnd = collectLettersUntilSeperator(
					createCharArrFromCords(column, false), row, ' ').getValue();
			
			var cordsToClear = new HashSet<Point>();
			
			var cordsToClearhor = collectCordsStartEnd(horStartEnd, row, true);
			var cordsToClearVer = collectCordsStartEnd(verStartEnd, column, false);
			
			for(var cord : cordsToClearhor)
			{
				cordsToClear.add(cord);
			}
			
			for(var cord : cordsToClearVer)
			{
				cordsToClear.add(cord);
			}
			
			cordsToClear.forEach(cord -> {
				var tilePane = _boardTiles.get(cord);
				tilePane.clearScores();
			});
			
		}
		else
		{
			wordsData.forEach(data -> {
				
				var letterCords = data.getLetters();
				
				letterCords.entrySet().forEach(letter -> {
					
					var cord = letter.getValue().getValue();
					var tilePane = _boardTiles.get(cord);
					tilePane.clearScores();
				});
			});
			
			var score = 0;
			
			for(var data : wordsData)
			{
				score += data.getScore();
			}
			
			if(score != 0 && source != null) {
				target.setBoardTile(source, score);
			}
		}
	}
	
	private ArrayList<Point> collectCordsStartEnd(Pair<Integer, Integer> startEnd, int colro, boolean horizontal)
	{
		var cords = new ArrayList<Point>();
		
		var start = startEnd.getKey();
		var end = startEnd.getValue();
		
		if(horizontal)
		{
			// Horizontal
			for(int i = start; i <= end; i++)
			{
				if(_boardTiles.containsKey(new Point(i, colro)))
				{
					var tile = _boardTiles.get(new Point(i, colro));
					cords.add(tile.getCords());
				}
			}
		}
		else
		{
			// Vertical
			for(int i = start; i <= end; i++)
			{
				if(_boardTiles.containsKey(new Point(colro, i)))
				{
					var tile = _boardTiles.get(new Point(colro, i));
					cords.add(tile.getCords());
				}
			}	
		}
		
		return cords;
	}
	
	private void dragOnHand() {
		paneHand.setOnDragOver(new EventHandler<DragEvent>() {

			@Override
			public void handle(DragEvent event) {
				if(event.getGestureSource() != event.getTarget()) {
					event.acceptTransferModes(TransferMode.ANY);
				}
				event.consume();
			}
		});
		
		paneHand.setOnDragDropped(new EventHandler<DragEvent>() {

			@Override
			public void handle(DragEvent event) {
				if(event.getGestureSource() instanceof BoardTile) {
					var draggedTile = (BoardTile) event.getGestureSource();
					
					if(draggedTile.getParent() instanceof BoardTilePane) {
						var oldBoardTile = (BoardTilePane) draggedTile.getParent();
						var oldCords = oldBoardTile.getCords();
						_board.updateStatus(oldCords, PositionStatus.Open);
						oldBoardTile.removeBoardTile();
						_fieldHand.remove(oldCords);
						refreshVisualOnRemove(oldCords);
					}
					
					int x = 10;
					int y = 13;
					
					for(var tile : _currentHand) {
						if(draggedTile.equals(tile)) {
							tile.setLayoutX(x);
							tile.setLayoutY(y);
							paneHand.getChildren().remove(tile);
							paneHand.getChildren().add(tile);		
						}
						y += 44.5;
					}
				}
			}
		});
	}
	
	private ArrayList<WordData> getWordData(Point cords)
	{
		var completeWordData = new ArrayList<WordData>();

		var wordsWithScore = getPlacedWordsWithScore(cords);
				
		for(var word : wordsWithScore)
		{
			completeWordData.add(new WordData(word.getKey(), word.getValue(), 
					getPlacedWordWithLetterCords(cords, word.getKey())));
		}
		
		return completeWordData;

	}
	
	private HashMap<Integer,Pair<Character, Point>> getPlacedWordWithLetterCords(Point playedCord, String placedWord)
	{
		var placedWordWithLetterCords = new HashMap<Integer,Pair<Character, Point>>();
		
		var column = (int)playedCord.getX();
		var row = (int)playedCord.getY();
		
		var wordHorWithStartEnd = collectLettersUntilSeperator(createCharArrFromCords(row, true), column, ' ');
		var wordVerWithStartEnd = collectLettersUntilSeperator(createCharArrFromCords(column, false), row, ' ');
		
		var wordsWithStartEnd = new ArrayList<Pair<Pair<String, Pair<Integer, Integer>>, Boolean>>() { 
			{ 
				add(new Pair<>(
						new Pair<>(wordHorWithStartEnd.getKey(), wordHorWithStartEnd.getValue()), true));
				add(new Pair<>(
						new Pair<>(wordVerWithStartEnd.getKey(), wordVerWithStartEnd.getValue()), false));
			} };
			
		for(var wordWithStartEnd : wordsWithStartEnd)
		{
			var word = wordWithStartEnd.getKey().getKey().toLowerCase();
			
			if(!word.equals(placedWord.toLowerCase()))
				continue;
			
			var horizontal = wordWithStartEnd.getValue();
			var startEnd = wordWithStartEnd.getKey().getValue();
			
			var wordLetterCords = horizontal ? getWordLetterCords(row, startEnd, horizontal) :
				getWordLetterCords(column, startEnd, horizontal);
			
			for(Map.Entry<Integer,Pair<Character, Point>> entry : wordLetterCords.entrySet())
			{
				var letterId = entry.getKey();
				var letterCords = entry.getValue();
				
				placedWordWithLetterCords.put(letterId, letterCords);
			}
		}
		
		return placedWordWithLetterCords;
	}
		
	private ArrayList<Pair<String, Integer>> getPlacedWordsWithScore(Point playedCord)
	{
		_db = new DatabaseController<Word>();
					
		ArrayList<Pair<String, Integer>> placedWords = new ArrayList<Pair<String, Integer>>();
		
		var column = (int)playedCord.getX();
		var row = (int)playedCord.getY();
		
		var horWordStartEnd = getPlacedWordFromChars(createCharArrFromCords(row, true), playedCord, true);
		var verWordStartEnd = getPlacedWordFromChars(createCharArrFromCords(column, false), playedCord, false);
		
		var wordsWithStartEnd = new ArrayList<Pair<Pair<String, Pair<Integer, Integer>>, Boolean>>() { 
			{ 
				add( new Pair<>(
						new Pair<>(horWordStartEnd.getKey(), 
								new Pair<>(horWordStartEnd.getValue().getKey(), horWordStartEnd.getValue().getValue())
								), true));
				add( new Pair<>(
						new Pair<>(verWordStartEnd.getKey(), 
								new Pair<>(verWordStartEnd.getValue().getKey(), verWordStartEnd.getValue().getValue())
								), false));
			} };
		
		for(var wordWithStartEnd : wordsWithStartEnd)
		{
			try 
			{
				var word = wordWithStartEnd.getKey().getKey();
								
				var statement = String.format("SELECT word, state FROM dictionary "
						+ "WHERE word = '%s'", word);
				
				var dictWordArr = (ArrayList<Word>) _db.SelectAll(statement, Word.class);
												
				if(dictWordArr.size() != 1)
					continue;
				else
				{
					var dictWord = dictWordArr.get(0);
										
					if(dictWord.getWord().equals(word.toLowerCase()) 
							&& dictWord.getWordState().equals("accepted"))
					{
						var startEnd = wordWithStartEnd.getKey().getValue();
						var horizontal = wordWithStartEnd.getValue();
						
						var wordScore = 0;
						
						wordScore = horizontal ? getWordScore(startEnd, row, horizontal) : 
							getWordScore(startEnd, column, horizontal);
						
						placedWords.add(new Pair<>(dictWord.getWord(), wordScore));
					}
				}	
			} 
			catch (SQLException e) 
			{
				e.printStackTrace();
			}
		}
									
		return placedWords;
	}

	private char[] createCharArrFromCords(int colro, boolean horizontal)
	{
		var letters = new ArrayList<Character>();
		
		if(horizontal)
		{
			// Horizontal
			for(int i = 0; i < 15; i++)
			{
				if(_boardTiles.containsKey(new Point(i, colro)))
				{
					var tile = _boardTiles.get(new Point(i, colro));
					letters.add(tile.getBoardTileSymbolAsChar());
				}
			}
		}
		else
		{
			// Vertical
			for(int i = 0; i < 15; i++)
			{
				if(_boardTiles.containsKey(new Point(colro, i)))
				{
					var tile = _boardTiles.get(new Point(colro, i));
					letters.add(tile.getBoardTileSymbolAsChar());
				}
			}	
		}
		
		return createCharArr(letters);
	}
	
	private char[] createCharArr(ArrayList<Character> arr)
	{
		return arr.stream().map(String::valueOf).collect(Collectors.joining()).toCharArray();
	}
	
	private Pair<String, Pair<Integer, Integer>> getPlacedWordFromChars(char[] letters, Point placedCord, boolean horizontal)
	{	
		Pair<String, Pair<Integer, Integer>> wordWithStartEnd;
				
		wordWithStartEnd = horizontal ? collectLettersUntilSeperator(letters, (int)placedCord.getX(), ' ') :
			collectLettersUntilSeperator(letters, (int)placedCord.getY(), ' ');
			
		return wordWithStartEnd;
	}
	
	private HashMap<Integer,Pair<Character, Point>> getWordLetterCords(int colro, Pair<Integer, Integer> startEnd, boolean horizontal)
	{
		var letterCords = new HashMap<Integer,Pair<Character, Point>>();
		
		var start = startEnd.getKey();
		var end = startEnd.getValue();
		
		for(int i = start; i <= end; i++)
		{
			Point cord = null;
			
			if(horizontal)
				cord = new Point(i, colro);
			else
				cord = new Point(colro, i);
			
			if(_boardTiles.containsKey(cord))
			{
				var tile = _boardTiles.get(cord);
				
				letterCords.put(tile.getBoardTile().getLetterId(), new Pair<>(tile.getBoardTileSymbolAsChar(), cord));
			}
		}
		
		return letterCords;
	}
	
	private int getWordScore(Pair<Integer,Integer> startEnd, int colro, boolean horizontal)
	{
		var start = startEnd.getKey();
		var end = startEnd.getValue();
				
		return calcScore(colro, start, end, horizontal, false);
	}
	
	public int calcScore(int colro , int start, int end, boolean horizontal, boolean testing)
	{
		var wordMultiTiles = new ArrayList<Object>();
		
		var tileCollection = testing ? _boardTilesTest : _boardTiles;
				
		var score = 0;
		
		var hasWordMulti = false;
		
		for(int i = start; i <= end; i++)
		{
			Point cord = null;
			
			if(horizontal)
				cord = new Point(i, colro);
			else
				cord = new Point(colro, i);
			
			{
				var tile = tileCollection.get(cord);
				
				var letterScore = testing ? ((BoardTileTest) tile).getSymbol().getValue() :
					((BoardTilePane) tile).getBoardTile().getSymbol().getValue();
				
				var bonusLetter = testing ? ((BoardTileTest) tile).getBonusLetter() : 
					((BoardTilePane) tile).getBonusLetter();
				
				var bonusMulti = testing ? ((BoardTileTest) tile).getBonusValue() :
					((BoardTilePane) tile).getBonusValue();
				
				if(bonusLetter == 'L')
					score += letterScore * bonusMulti;
				else
					score += letterScore;
				
				System.out.println(bonusLetter);
					
				if (bonusLetter == 'W' || bonusLetter == '*')
				{
					hasWordMulti = true;
					if(testing)
						wordMultiTiles.add((BoardTileTest)tile);
					else
						wordMultiTiles.add((BoardTilePane)tile);
				}
			}
		}
		
		if(hasWordMulti)
		{
			for(var multiTile : wordMultiTiles)
			{
				var tile = testing ? ((BoardTileTest) multiTile) 
						: ((BoardTilePane) multiTile);
				
				if(tile instanceof BoardTilePane)
				{
					var bonusLetter = ((BoardTilePane) tile).getBonusLetter();
					
					score = bonusLetter == '*' ? score * 3 : score * ((BoardTilePane) tile).getBonusValue();
				}
				else if (tile instanceof BoardTileTest)
				{
					var bonusLetter = ((BoardTileTest) tile).getBonusLetter();
					
					score = bonusLetter == '*' ? score * 3 : score * ((BoardTileTest) tile).getBonusValue();
				}

			}
		}
		
		return score;
	}
		
	private Pair<String,Pair<Integer,Integer>> collectLettersUntilSeperator(char[] letters, int index, char seperator)
	{
		var str = new StringBuilder();
		
		var endStr = 14;
		
		var startStr = 0;
		
		for(int i = index; i < letters.length; i++)
		{
			if(letters[i] == seperator)
			{
				endStr = i-1;
				break;
			}
		}
		
		for(int i = index; i >= 0; i--)
		{
			if(letters[i] == seperator)
			{
				startStr = i+1;
				break;
			}
		}
		
		for(int i = startStr; i <= endStr; i++)
		{
			str.append(letters[i]);
		}
		
		var wordStartEnd = new Pair<>(startStr, endStr);
		var word = str.toString().trim();
		
		return new Pair<>(word,wordStartEnd);
	}
	
}
