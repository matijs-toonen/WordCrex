package Controller;

import java.awt.Point;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import Model.Account;
import Model.HandLetter;
import Model.Letter;
import Model.Tile;
import Model.Turn;
import Model.Word;
import Model.Board.Board;
import Model.Board.PositionStatus;
import Model.WordState.WordState;
import View.BoardPane.BoardTile;
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
	private LinkedHashMap<Point, BoardTile> _boardTiles; // 15 by 15
	private Board _board;
	
	@FXML
	private Label lblScore1, lblScore2, lblPlayer1, lblPlayer2;
	
	@FXML
	private Button btnTest;
	
	@FXML
	private Pane panePlayField, paneHand;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		_boardTiles = new LinkedHashMap<Point, BoardTile>();
		lblPlayer1.setText("SCHRUKTURK");
		lblPlayer2.setText("BOEDER");
		lblScore1.setText("1");
		lblScore2.setText("9");
		_board = new Board();
		createField();
		createHand();
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
		_db = new DatabaseController<HandLetter>();
		try {
			var count = _db.SelectCount("SELECT COUNT(*) FROM handletter");
			var handLetters = (ArrayList<HandLetter>) _db.SelectWithCustomLogic(getHandLetter(), "SELECT * FROM handletter NATURAL JOIN letter NATURAL JOIN symbol where turn_id = 1");
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
				}
			};
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
			
			if(event.getGestureTarget() instanceof BoardTile) {

				var boardTile = (BoardTile) event.getGestureTarget();
				var cords = boardTile.getCords();
				
				if(!_board.canPlace(cords))
					return;
				
				var sourceTile = (BoardTile) event.getGestureSource();
				var symbol = sourceTile.getSymbol();
				boardTile.setSymbol(symbol);
				boardTile.setBackground(getBackground(Color.PINK));
				
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
		
	private ArrayList<Pair<String, Integer>> getPlacedWordsWithScore(Pair<Integer, Integer> playedCord)
	{
		_db = new DatabaseController<Word>();
				
		ArrayList<Pair<String, Integer>> placedWords = new ArrayList<Pair<String, Integer>>();
		
		var column = playedCord.getKey();
		var row = playedCord.getValue();
		
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
			var score = getWordScore(wordWithStartEnd.getValue(), placedCord.getValue(), horizontal);
			wordWithScore = new Pair<String, Integer>(word, score);
		}
		else
		{
			var wordWithStartEnd = collectLettersUntilSeperator(letters, placedCord.getValue(), ' ');
			var word = wordWithStartEnd.getKey();
			var score = getWordScore(wordWithStartEnd.getValue(), placedCord.getKey(), horizontal);
			wordWithScore = new Pair<String, Integer>(word, score);
		}
			
		return wordWithScore;
	}
	
	public int getWordScore(Pair<Integer,Integer> endStart, int colro, boolean horizontal)
	{
		var start = endStart.getKey();
		var end = endStart.getValue();
		
		var score = 0;
		
		var hasWordMulti = false;
		
		var wordMultiTiles = new ArrayList<BoardTile>();
		
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
