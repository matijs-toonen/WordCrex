package Controller;

import java.awt.Point;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import Model.Game;
import Model.HandLetter;
import Model.Letter;
import Model.Symbol;
import Model.Tile;
import Model.Turn;
import Model.Word;
import Model.Board.Board;
import Model.Board.PositionStatus;
import Tests.BoardTileTest;
import View.BoardPane.BoardTile;
import View.BoardPane.BoardTilePane;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
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
	private boolean _chatVisible;
	private boolean _historyVisible;
	
	@FXML
	private Label lblScore1, lblScore2, lblPlayer1, lblPlayer2;
	
	@FXML
	private Button btnTest, btnShuffle;
	
	@FXML
	private Pane panePlayField, paneHand;
	
	@FXML
	private AnchorPane rightBarAnchor;
	
	@FXML
	private ImageView reset;
	
	public BoardController(Game game) {
		_board = new Board();
		_boardTiles = new HashMap<Point, BoardTilePane>();
        _currentHand = new ArrayList<BoardTile>();
        _fieldHand = new HashMap<Point, BoardTile>();
		_currentGame = game;
		_currentTurn = new Turn(1);
		getLetters();
	}
	
	private void getLetters() {
		_db = new DatabaseController<Symbol>();
		var statement = "SELECT * FROM letter WHERE game_id = " + _currentGame.getGameId();
		
		try {
			_letters = (ArrayList<Letter>)_db.SelectAll(statement, Letter.class);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		lblPlayer1.setText("BaderAli99");
		lblPlayer1.setStyle("-fx-font-size: 28");
		lblPlayer2.setText("SchurkTurk");
		lblPlayer2.setStyle("-fx-font-size: 28");
		lblScore1.setText("1");
		lblScore1.setStyle("-fx-font-size: 20; -fx-background-color: #F4E4D3; -fx-background-radius: 25 0 0 25");
		lblScore2.setText("9");
		lblScore2.setStyle("-fx-font-size: 20; -fx-background-color: #F4E4D3; -fx-background-radius: 0 25 25 0");

		createField(false);
		createHand();
		dragOnHand();
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
	
	public void reset() {
		resetHand();
		reset.setVisible(false);
	}
	
	public void openHistory() throws IOException{
		if(!_historyVisible) {
			closeCommunicationFrame();
			Parent historyFrame = FXMLLoader.load(getClass().getResource("/View/SetHistory.fxml"));
			
			rightBarAnchor.getChildren().setAll(historyFrame);
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
			Parent chatFrame = FXMLLoader.load(getClass().getResource("/View/Chat.fxml"));
			
			rightBarAnchor.getChildren().setAll(chatFrame);
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
						var tilePane = new BoardTilePane(tile);
						tilePane.setDropEvents(createDropEvents());
						tilePane.setLayoutX(x);
						tilePane.setLayoutY(y);
						tilePane.setMinWidth(39);
						tilePane.setMinHeight(39);
						tilePane.setStyle("-fx-background-color: #E8E9EC; -fx-background-radius: 6");

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
	
	private Tile getTileFromCollection(ArrayList<Tile> collection, int x, int y)
	{
		var allTiles = collection.stream().filter(t -> t.isAtPoint(new Point(x,y))).collect(Collectors.toList());
		return  allTiles.size() == 1 ? allTiles.get(0) : null;
	}
	
	private void createHand() {
		_currentHand.clear();
		_db = new DatabaseController<HandLetter>();
		var handLetters = getHandLetters();
		int x = 0;
		int y = 13;

		for(var handLetter : handLetters) {
			for(var letter : handLetter.getLetters()) {
				var boardTile = new BoardTile(letter.getSymbol());
				boardTile.setDraggableEvents();
				boardTile.setLayoutX(x);
				boardTile.setLayoutY(y);
				boardTile.setStyle("-fx-background-color: pink; -fx-background-radius: 6");
				y += 44.5;
				boardTile.setMinWidth(39);
				boardTile.setMinHeight(39);
				paneHand.getChildren().add(boardTile);
				_currentHand.add(boardTile);
			}
		};
		placeHand(false);
	}
	
	private ArrayList<HandLetter> getHandLetters() {
		var handLetters = getExistingHandLetters();
		return handLetters.size() == 0 ? generateHandLetters() : handLetters; 
	}
	
	private ArrayList<HandLetter> getExistingHandLetters() {
		_db = new DatabaseController<HandLetter>();
		
		var statement = "SELECT * FROM handletter NATURAL JOIN letter NATURAL JOIN symbol where game_id = " + _currentGame.getGameId() + " AND turn_id = " + _currentTurn.getTurnId();
		
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
		
		return handLetters;
	}
	
	private void resetHand() {
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
		var turnStatement = "INSERT INTO turn VALUES(" + _currentGame.getGameId() + ", " + _currentTurn.getTurnId() + ")";
		
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
		return new HandLetter(_currentGame, _currentTurn, createLetter());
	}
	
	private Letter createLetter() {
		var number = _random.nextInt(_letters.size());
		var rndLetter = _letters.get(number);
		
		var statement = "INSERT INTO handletter VALUES (" + _currentGame.getGameId() + ", " + _currentTurn.getTurnId() + ", " + rndLetter.getLetterId() + ")"; 
		
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
	
	private Function<ResultSet, Integer> getNewLetter(){
		return (generatedKeys -> {
			try {
				if(generatedKeys != null && generatedKeys.next()) {
					var letterId = generatedKeys.getInt(0);
					return letterId;
				}
			}catch(SQLException e) {
				System.out.println(e.getMessage());
			}
			return null;
		});
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

				// TODO Add word collection from database and do something with the placed words
				showPlacedWords(cords); // Only for testing purposes can remove after

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
	
	private void showPlacedWords(Point cords)
	{
		try
		{
			var words = getPlacedWordsWithScore(cords);
			
			for(var word : words)
			{
				System.out.println("Word: " + word.getKey() + " Score: " + word.getValue());
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}
		
	private ArrayList<Pair<String, Integer>> getPlacedWordsWithScore(Point playedCord)
	{
		_db = new DatabaseController<Word>();
		
		// TODO add that this only gets checked after placing first letter on board
		if(!_board.placedConnected(playedCord))
			System.out.println("No tile connected");
		else
			System.out.println("Tile connected");
				
		ArrayList<Pair<String, Integer>> placedWords = new ArrayList<Pair<String, Integer>>();
		
		var column = (int)playedCord.getX();
		var row = (int)playedCord.getY();
		
		var horWordScore = getPlacedWordFromChars(createCharArrFromCords(row, true), playedCord, true);
		var verWordScore = getPlacedWordFromChars(createCharArrFromCords(column, false), playedCord, false);
		
		var wordsWithScore = new ArrayList<Pair<String, Integer>>() { 
			{ add(new Pair<>(verWordScore.getKey(), verWordScore.getValue()));
				add(new Pair<>(horWordScore.getKey(), horWordScore.getValue())); } };
		
		for(var wordWithScore : wordsWithScore)
		{
			try 
			{
				var word = wordWithScore.getKey();
				var score = wordWithScore.getValue();
				
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
						placedWords.add(new Pair<>(dictWord.getWord(), score));
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
	
	private Pair<String, Integer> getPlacedWordFromChars(char[] letters, Point placedCord, boolean horizontal)
	{	
		Pair<String, Integer> wordWithScore;
		
		if(horizontal)			
		{
			var wordWithStartEnd = collectLettersUntilSeperator(letters, (int)placedCord.getX(), ' ');
			var word = wordWithStartEnd.getKey();
			var score = getWordScore(wordWithStartEnd.getValue(), (int)placedCord.getY(), horizontal);
			wordWithScore = new Pair<String, Integer>(word, score);
		}
		else
		{
			var wordWithStartEnd = collectLettersUntilSeperator(letters, (int)placedCord.getY(), ' ');
			var word = wordWithStartEnd.getKey();
			var score = getWordScore(wordWithStartEnd.getValue(), (int)placedCord.getX(), horizontal);
			wordWithScore = new Pair<String, Integer>(word, score);
		}
			
		return wordWithScore;
	}
	
	public Pair<String, Integer> getPlacedWordFromChars(char[] letters, Pair<Integer, Integer> placedCord, boolean horizontal, boolean test)
	{	
		Pair<String, Integer> wordWithScore;
		
		if(horizontal)			
		{
			var wordWithStartEnd = collectLettersUntilSeperator(letters, placedCord.getKey(), ' ');
			var word = wordWithStartEnd.getKey();
			var score = getWordScore(wordWithStartEnd.getValue(), placedCord.getValue(), horizontal, test);
			wordWithScore = new Pair<String, Integer>(word, score);
		}
		else
		{
			var wordWithStartEnd = collectLettersUntilSeperator(letters, placedCord.getValue(), ' ');
			var word = wordWithStartEnd.getKey();
			var score = getWordScore(wordWithStartEnd.getValue(), placedCord.getKey(), horizontal, test);
			wordWithScore = new Pair<String, Integer>(word, score);
		}
			
		return wordWithScore;
	}
	
	private int getWordScore(Pair<Integer,Integer> endStart, int colro, boolean horizontal, boolean Testing)
	{
		var start = endStart.getKey();
		var end = endStart.getValue();
		
		var score = 0;
		
		var hasWordMulti = false;
		
		var wordMultiTiles = new ArrayList<BoardTileTest>();
		
		for(int i = start; i <= end; i++)
		{
			Point cord = null;
			
			if(horizontal)
				cord = new Point(i, colro);
			else
				cord = new Point(colro, i);
			
			if(_boardTilesTest.containsKey(cord))
			{
				var tile = _boardTilesTest.get(cord);
				
				var letterScore = tile.getSymbol().getValue();
				var bonusLetter = tile.getBonusLetter();
				var bonusMulti = tile.getBonusValue();
				
				if(bonusMulti != 0 && bonusLetter != 'W')
					score += letterScore * bonusMulti;
				else
					score += letterScore;
					
				if (bonusLetter == 'W')
				{
					hasWordMulti = true;
					wordMultiTiles.add(tile);
				}
			}
		}
		
		if(hasWordMulti)
		{
			for(var tile : wordMultiTiles)
			{
				score += score * tile.getBonusValue();
			}
		}
		
		return score;
	}
	
	private int getWordScore(Pair<Integer,Integer> endStart, int colro, boolean horizontal)
	{
		var start = endStart.getKey();
		var end = endStart.getValue();
		
		var score = 0;
		
		var hasWordMulti = false;
		
		var wordMultiTiles = new ArrayList<BoardTilePane>();
		
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
				
				var letterScore = tile.getBoardTile().getSymbol().getValue();
				var bonusLetter = tile.getBonusLetter();
				var bonusMulti = tile.getBonusValue();
				
				if(bonusMulti != 0 && bonusLetter != 'W')
					score += letterScore * bonusMulti;
				else
					score += letterScore;
					
				if (bonusLetter == 'W')
				{
					hasWordMulti = true;
					wordMultiTiles.add(tile);
				}
			}
		}
		
		if(hasWordMulti)
		{
			for(var tile : wordMultiTiles)
			{
				score += score * tile.getBonusValue();
			}
		}
		
		return score;
	}
	
	private Pair<String,Pair<Integer,Integer>> collectLettersUntilSeperator(char[] letters, int index, char seperator)
	{
		var str = new StringBuilder();
		
		var endStr = 0;
		
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
