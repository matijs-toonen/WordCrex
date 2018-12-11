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

import Model.HandLetter;
import Model.Letter;
import Model.Turn;
import Model.Board.Board;
import Model.Board.PositionStatus;
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
	private LinkedHashMap<Point, BoardTile> _tiles;
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
		_tiles = new LinkedHashMap<Point, BoardTile>();
		_boardTiles = new LinkedHashMap<Point, BoardTile>();
		lblPlayer1.setText("SCHRUKTURK");
		lblPlayer2.setText("BOEDER");
		lblScore1.setText("1");
		lblScore2.setText("9");
		_board = new Board();
		createField();
		createHand();
	}
	
	private void createField() {
		int x = 1;
		for(int i = 0; i < 15; i++) {
			int y = 1;
			for(int j = 0; j < 15; j++) {
				var tile = new BoardTile(i, j);
				tile.setDropEvents(createDropEvents());
				tile.setBackground(getBackground(Color.CHOCOLATE));
				tile.setLayoutX(x);
				tile.setLayoutY(y);
				tile.setMinWidth(30);
				tile.setMinHeight(30);
				_tiles.put(new Point(x, y), tile);
				_boardTiles.put(new Point(i, j), tile);
				y += 32;
				panePlayField.getChildren().add(tile);
			}
			x += 32;
		}
	}
	
	private void createHand() {
		_db = new DatabaseController<HandLetter>();
		try {
			var count = _db.SelectCount("SELECT COUNT(*) FROM handletter");
			System.out.println(count);
			var handLetters = (ArrayList<HandLetter>) _db.SelectWithCustomLogic(getHandLetter(), "SELECT * FROM handletter NATURAL JOIN letter NATURAL JOIN symbol where turn_id = 1");
			int x = 12;
			int y = 0;

			for(var handLetter : handLetters) {
				for(var letter : handLetter.getLetters()) {
					System.out.println(letter.getSymbol().getChar());
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
					testGetWordCords(cords);
					event.consume();	
				}
			}
		});
	}
	
	private void testGetWordCords(Pair<Integer, Integer> cord)
	{
		var test = getWordCords(null, cord);
	}

	// Horizontal
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
		
		return letters.stream().map(String::valueOf).collect(Collectors.joining()).toCharArray();
	}
	
	private String getPlacedWordFromChars(char[] letters, Pair<Integer, Integer> placedCord)
	{		
		var playedChar = _boardTiles.get(new Point(placedCord.getKey(), placedCord.getValue())).getSymbolAsChar();
				
		System.out.println("Column: " + placedCord.getKey() + " " + letters[placedCord.getKey()] + " played: " + playedChar);
		
		System.out.println("Row: " + placedCord.getValue() + " " + letters[placedCord.getValue()] + " played: " + playedChar);

		return new String(letters);
	}
	
	private ArrayList<Point> getWordCords(ArrayList<String> words, Pair<Integer, Integer> playedCord)
	{
		// Dummy data
		words = new ArrayList<String>() { { add("Beller"); add("Belles"); add("Bellec"); add("Bel"); add("Bell"); } };
				
		ArrayList<Point> wordCords = new ArrayList<Point>();
		
		var column = playedCord.getKey();
		var row = playedCord.getValue();
		
		var horWord = getPlacedWordFromChars(createCharArrFromCords(row, true), playedCord);
		var verWord = getPlacedWordFromChars(createCharArrFromCords(column, false), playedCord);
		
		System.out.println("Horizontal: " + horWord);
		
		System.out.println("Vertical: " + verWord);
								
		for(var word : words)
		{			
			if(!word.toUpperCase().equals(horWord) && !word.toUpperCase().equals(verWord))
				continue;
			
			System.out.println(word);
		}
		
		return wordCords;
	}
}
