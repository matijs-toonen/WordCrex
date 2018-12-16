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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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
	private Label lblScore1, lblScore2, lblPlayer1, lblPlayer2;
	
	@FXML
	private Button btnTest, btnShuffle;
	
	@FXML
	private Pane panePlayField, paneHand, boardPane, waitingPane;
	
	@FXML
	private AnchorPane rightBarAnchor;
	
	@FXML
	private ImageView reset, accept;
	
	@FXML
	private AnchorPane screenPane;	
	
	public BoardController(Game game, Turn turn) {
		_currentGame = game;
		_currentTurn = turn;
		_board = new Board();
		_boardTiles = new HashMap<Point, BoardTilePane>();
        _currentHand = new ArrayList<BoardTile>();
        _fieldHand = new HashMap<Point, BoardTile>();
        getLetters();
	}
	

	
	public BoardController(AnchorPane rootPane) {
		_rootPane = rootPane;
	}
	
	public BoardController(Game game) {
		this(game, new Turn(1));
	}

	private void getLetters() {
		_db = new DatabaseController<Symbol>();
		var statement = Letter.getUnusedLetters(_currentGame.getGameId());
		
		try {
			_letters = (ArrayList<Letter>)_db.SelectAll(statement, Letter.class);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		lblPlayer1.setText(MainController.getUser().getUsername());
		lblPlayer1.setStyle("-fx-font-size: 28");
		lblPlayer2.setText(_currentGame.getOpponent());
		lblPlayer2.setStyle("-fx-font-size: 28");
		lblScore1.setText("1");
		lblScore1.setStyle("-fx-font-size: 20; -fx-background-color: #F4E4D3; -fx-background-radius: 25 0 0 25");
		lblScore2.setText("9");
		lblScore2.setStyle("-fx-font-size: 20; -fx-background-color: #F4E4D3; -fx-background-radius: 0 25 25 0");
		
		scoreRefreshThread();

		createField(false);
		createHand(false);
		dragOnHand();
	}
	
	/**
	 * disable the board and show waiting animation
	 */
	private void disableBoard() {
		boardPane.setDisable(true);
		waitingPane.setVisible(true);
		boardPane.setStyle("-fx-opacity: 0.3");
	}
	
	/**
	 * disable the board and show waiting animation
	 */
	private void enableBoard() {
		boardPane.setDisable(false);
		waitingPane.setVisible(false);
		boardPane.setStyle("-fx-opacity: 1");
	}
	
	private void scoreRefreshThread() {
		
		Thread chatThread = new Thread(){
		    public void run(){
		    	
		    	while(true) {
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
			System.out.println(e.getMessage());
		}
		
		Platform.runLater(() -> {
			lblScore1.setText(Integer.toString(_currentScore.getOwnScore()));
			lblScore2.setText(Integer.toString(_currentScore.getOpponentScore()));
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
		if(!hasNotPlacedFirstMid())
		{	
			if(!_board.allChainedToMiddle())
				System.err.println("Not all tiles connected to the middle");
			else
			{
				var wordsData = getUniqueWordData();
				
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
				
				for(var statement : statementBoardPlayerArr)
				{
					System.out.println(statement);
				}
	
				// TODO uncomment
//				try 
//				{
//					if(_db.Insert(statementTurnPlayer)) 
//					{
//						if(_db.InsertBatch(statementBoardPlayerArr)) 
//						{
//							renewHand();
//						}
//					}
//				}
//				catch(SQLException e) {
//					e.printStackTrace();
//				}
			}
		}
		else
			System.err.println("Middle field not used");
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
		
		for(int i = 0; i < uniqueWordsData.size(); i++)
		{
			for(int j = 0; j < uniqueWordsData.size(); j++)
			{
				if(uniqueWordsData.get(i).hasSameLetterIds(uniqueWordsData.get(j).getLetterIds()))
					uniqueWordsData.remove(j);
			}
		}
		
		return uniqueWordsData;
	}
	
	private boolean hasNotPlacedFirstMid()
	{		
		if(_currentTurn.getTurnId() == 1)
			return _board.canPlace(_board.getMiddle());
		else
			return false;
	}
	
	public void clickSkipTurn() {
		System.out.println("User clicked skip turn");
		String insertQuery = Game.getPassQuery(_currentGame.getGameId(), _currentTurn.getTurnId(), MainController.getUser().getUsername(), checkPlayer());
		
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
			var updateStatement = TurnPlayer.getTurnPlayerUpdateQuery(checkPlayer(), "resign", _currentGame.getGameId(), _currentTurn.getTurnId(), MainController.getUser().getUsername());
			
			_db.Update(updateStatement);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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
	
	public void reset() {
		resetFieldHand();
		reset.setVisible(false);
	}
	
	public void renewHand() {
		createHand(true);
	}
	private boolean needsToWaitForHandLetters() {
		var table = checkPlayer() ? "turnplayer2" : "turnplayer1";
		var query = TurnPlayer.hasPlacedTurn(table, _currentTurn.getTurnId(), _currentGame.getGameId());
		System.out.println(query);
		try {
			var shoud = _db.SelectCount(query) == 0;
			System.out.println(shoud + " table " + table + " turn " + _currentTurn.getTurnId());
			return shoud;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	private boolean checkPlayer() {
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
		
	private boolean checkPlayerIfPlayer1()
	{		
		return MainController.getUser().getUsername().equals(_currentGame.getUser1());
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
							boardTile = new BoardTile(turn.getSymbol(), turn.getLetterId());
							boardTile.setMinWidth(39);
							boardTile.setMinHeight(39);
							boardTile.setStyle("-fx-background-color: pink; -fx-background-radius: 6");
							_board.updateStatus(cords, PositionStatus.Taken);
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
	
	private HashMap<Point, TurnBoardLetter> getTurns() {
		_db = new DatabaseController<TurnBoardLetter>();
		var turns = new HashMap<Point, TurnBoardLetter>();
		
		String tileQuery = Game.getExistingTiles(_currentGame.getGameId(), _currentTurn.getTurnId());
		try {
			((ArrayList<TurnBoardLetter>)_db.SelectAll(tileQuery, TurnBoardLetter.class)).forEach(turn -> {
				turns.put(turn.getTileCords(), turn);	
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
	
	
	private void createHand(boolean checkGenerated) {
		_currentHand.clear();
		_db = new DatabaseController<HandLetter>();
		var handLetters = new ArrayList<HandLetter>();
		
		if(checkGenerated) {
			var check = needsToWaitForHandLetters();
			_currentTurn.incrementId();
			getGeneratedLetters(check);
			return;
		}
		
		handLetters = getHandLetters();
		visualizeHand(handLetters);
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
	
	// TODO HERE
	private void getGeneratedLetters(boolean checkGenerated){
		if(checkGenerated) {
			waitForVisualizeNewHandLetters();
		}
		else {
			var handLetters = generateHandLetters();
			visualizeHand(handLetters);
		}
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
	
	// TODO HERE
	private void waitForVisualizeNewHandLetters(){	
		var thread = new Thread() {
			public void run() {
				var handLetters = getExistingHandLetters();
				int tries = 0;
//				while(true) {
//					Thread.sleep(1000);
//					handLetters = getExistingHandLetters();
//					
//					var amount = getAmountLetters(handLetters);
//					if(amount == 0 || amount != 7 || tries < 4) {
//						
//					}
//					break;
//				}
				while(getAmountLetters(handLetters) == 0 || getAmountLetters(handLetters) != 7 || tries < 4) {
	    			try {
						Thread.sleep(1000);
						handLetters = getExistingHandLetters();
						
						if(getAmountLetters(handLetters) > 0) {
							tries++;	
						}
						
						if (getAmountLetters(handLetters) == 0)
							tries = 0;
					
					} catch (Exception e) {
						System.out.println(e.getMessage());
					}
				}
				final var finalHandLetters = handLetters;
				Platform.runLater(() -> {
	            	visualizeHand(finalHandLetters);
		        });
			}
		};
		
		thread.setDaemon(true);
		thread.start();
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
		
		for(int i = 0; i < 7; i++) {
			handLetters.add(createHandLetter());
		}
		
		getLetters();
		
		return handLetters;
	}
	
	private void resetFieldHand() {
		_fieldHand.entrySet().forEach(handLetter -> {
			var cords = handLetter.getKey();
			
			var tilePane = _boardTiles.get(cords);
			tilePane.removeBoardTile();
			_board.updateStatus(cords, PositionStatus.Open);
		});	

		placeHand(false);
		_fieldHand.clear();
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
			System.out.println(e.getMessage());
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
				
				reset.setVisible(true);
				_fieldHand.put(cords, sourceTile);
				_boardTiles.put(cords, boardTile);
				
				event.acceptTransferModes(TransferMode.ANY);
				event.setDropCompleted(true);
				_board.updateStatus(cords, PositionStatus.Taken);
				
				playTileSound();					
				event.consume();
			}
		});
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
					
				if (bonusLetter == 'W')
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
			for(var tile : wordMultiTiles)
			{
				var multi = testing ? ((BoardTileTest) tile).getBonusValue() 
						: ((BoardTilePane) tile).getBonusValue();
				
				score = score * multi;
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
	
	private void playTileSound() {
		String bip = "src/Resources/tileMove.mp3";
		Media hit = new Media(new File(bip).toURI().toString());
		MediaPlayer mediaPlayer = new MediaPlayer(hit);
		mediaPlayer.play();
	}
}
