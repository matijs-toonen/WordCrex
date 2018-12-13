package Controller;

import java.awt.Point;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.LinkedHashMap;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import Model.HandLetter;
import Model.Letter;
import Model.Tile;
import Model.LetterSet;
import Model.Turn;
import Model.Board.Board;
import Model.Board.PositionStatus;
import View.BoardPane.BoardTile;
import View.BoardPane.BoardTilePane;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
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
	private HashMap<Point, BoardTilePane> _tiles;
	private Board _board;
    private ArrayLis2t<BoardTile> _currentHand;
    private HashMap<Point, BoardTile> _fieldHand;
	
	@FXML
	private Label lblScore1, lblScore2, lblPlayer1, lblPlayer2;
	
	@FXML
	private Button btnTest;
	
	@FXML
	private Pane panePlayField, paneHand;
	
	public BoardController(Game game) {
		_board = new Board();
		_tiles = new HashMap<Point, BoardTilePane>();
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
		_tiles = new HashMap<Point, BoardTile>();
		lblPlayer1.setText("SCHRUKTURK");
		lblPlayer2.setText("BOEDER");
		lblScore1.setText("1");
		lblScore2.setText("9");
		createField();
		createHand();
		createOnButtonTestClick();
	}
	
	private void createField() 
	{
		_db = new DatabaseController<Tile>();
		try 
		{
			var allTiles = (ArrayList<Tile>) _db.SelectAll("SELECT * FROM tile", Tile.class);
			
			int x = 1;
			for(int i = 0; i < 15; i++) {
				int y = 1;
				for(int j = 0; j < 15; j++) {
				var tile = new BoardTile(i, j);
					
					Tile tile = null;
					
					try
					{
						tile = getTileFromCollection(allTiles, i+1, j+1);
					}
					catch (Exception e)
					{
//				tile.createOnClickEvent(creatOnClickEvent());
				_tiles.put(new Point(i, j), tile);
						System.err.println("Tile(s) does/do not not exist");
					}
					
					if(tile == null)
						throw new Exception("Tile == null");
						
					
					var boardTile = new BoardTile(tile);
					boardTile.setDropEvents(createDropEvents());
					boardTile.setBackground(getBackground(Color.CHOCOLATE));
					boardTile.setLayoutX(x);
					boardTile.setLayoutY(y);
					boardTile.setMinWidth(30);
					boardTile.setMinHeight(30);
					_boardTiles.put(new Point(i, j), boardTile);
					y += 32;
					panePlayField.getChildren().add(boardTile);
				}
				x += 32;
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
		int x = 12;
		int y = 0;

		for(var handLetter : handLetters) {
			for(var letter : handLetter.getLetters()) {
				var boardTile = new BoardTile(letter.getSymbol());
				boardTile.setDraggableEvents();
				boardTile.setBackground(getBackground(Color.LIGHTPINK));
				boardTile.setLayoutX(x);
				boardTile.setLayoutY(y);
				y += 32;
				boardTile.setMinWidth(30);
				boardTile.setMinHeight(30);
				paneHand.getChildren().add(boardTile);
				_currentHand.add(boardTile);
			}
		};
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
			var tile = handLetter.getValue();
			
			var boardTile = _tiles.get(cords);
//			boardTile.setBackground(getBackground(Color.CHOCOLATE));
//			boardTile.resetTile();
			_board.updateStatus(cords, PositionStatus.Open);
			tile.setPaneVisible(true);
		});	
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
		Letter letter = null;
		var rndLetter = _letters.get(_random.nextInt(_letters.size()));
		
		var statement = "INSERT INTO handletter VALUES (" + _currentGame.getGameId() + ", " + _currentTurn.getTurnId() + ", " + rndLetter.getLetterId() + ")"; 
		
		try {
			if(_db.Insert(statement)) {
				return rndLetter;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
				var boardTile = (BoardTilePane) event.getGestureTarget();

				var cords = boardTile.getCords();
				
				if(!_board.canPlace(cords))
					return;
				
				var sourceTile = (BoardTile) event.getGestureSource();
//				sourceTile.setCords(boardTile.getCords());
//				boardTile.setDraggableEvents();
				
				var symbol = sourceTile.getSymbol();
				boardTile.setSymbol(symbol);
				var bg = sourceTile.getBackground();
				boardTile.setBackground(bg);
				_fieldHand.put(boardTile.getCords(), sourceTile);
				
				Dragboard db = event.getDragboard();
				
				if(db.hasString()) {
					event.acceptTransferModes(TransferMode.ANY);
					event.setDropCompleted(true);
					_board.updateStatus(cords, PositionStatus.Taken);
					// TODO Add word collection from database and do something with the placed words
					showPlacedWords(cords); // Only for testing purposes can remove after
					event.consume();	
				}
			}
		});
	}
	
	private void showPlacedWords(Pair<Integer, Integer> cords)
	{
		var words = getWords(null, cords);
		
		for(var word : words)
		{
			System.out.println(word);
		}
	}
		
	private ArrayList<String> getWords(ArrayList<String> words, Pair<Integer, Integer> playedCord)
	{
		// Dummy data
		words = new ArrayList<String>() { { add("Beller"); add("Belles"); add("Bellec"); add("Bel"); add("Bell"); } };
				
		ArrayList<String> placedWords = new ArrayList<String>();
		
		});
	}
	
	//Dummy
	private void createOnButtonTestClick() {
		btnTest.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				resetHand();
			}
		var column = playedCord.getKey();
		var row = playedCord.getValue();
		
		var horWordScore = getPlacedWordFromChars(createCharArrFromCords(row, true), playedCord, true);
		var verWordScore = getPlacedWordFromChars(createCharArrFromCords(column, false), playedCord, false);
		
		var verWord = horWordScore.getKey();
		var horWord = verWordScore.getKey();
										
		for(var word : words)
		{
			if(!word.toUpperCase().equals(horWord) && !word.toUpperCase().equals(verWord))
				continue;
			
			if(word.toUpperCase().equals(horWord) && word.toUpperCase().equals(verWord))
			{
				placedWords.add(word);
				placedWords.add(word);
			}
			else
				placedWords.add(word);
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
					letters.add(tile.getSymbolAsChar());
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
					letters.add(tile.getSymbolAsChar());
				}
			}	
		}
		
		return createCharArr(letters);
	}
	
	private char[] createCharArr(ArrayList<Character> arr)
	{
		return arr.stream().map(String::valueOf).collect(Collectors.joining()).toCharArray();
	}
	
	private Pair<String, Integer> getPlacedWordFromChars(char[] letters, Pair<Integer, Integer> placedCord, boolean horizontal)
	{	
		Pair<String, Integer> wordWithScore;
		
		if(horizontal)			
		{
			var wordWithStartEnd = collectLettersUntilSeperator(letters, placedCord.getKey(), ' ');
			var word = wordWithStartEnd.getKey();
			var score = getWordScore(wordWithStartEnd.getValue(), placedCord.getValue());
			wordWithScore = new Pair<String, Integer>(word, score);
		}
		else
		{
			var wordWithStartEnd = collectLettersUntilSeperator(letters, placedCord.getValue(), ' ');
			var word = wordWithStartEnd.getKey();
			var score = getWordScore(wordWithStartEnd.getValue(), placedCord.getKey());
			wordWithScore = new Pair<String, Integer>(word, score);
		}
			
		return wordWithScore;
	}
	
	private int getWordScore(Pair<Integer,Integer> endStart, int colro)
	{
		var start = endStart.getKey();
		var end = endStart.getValue();
		
		for(int i = start; i <= end; i++)
		{
			System.out.println(i + "=" + colro);
		}
		
		return 0;
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
