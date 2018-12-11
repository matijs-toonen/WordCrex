package Controller;

import java.awt.Point;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.function.Function;

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
					testGetWordCords(boardTile.getSymbolAsChar(), cords);
					event.consume();	
				}
			}
		});
	}
	
	private void testGetWordCords(char c, Pair<Integer, Integer> cord)
	{
		var test = getWordCords(null,c, cord);
	}
	
	private ArrayList<Pair<Integer,Integer>> getOccupiedInlineCords(int column, int row, ArrayList<Pair<Integer,Integer>> occupiedCords)
	{
		ArrayList<Pair<Integer, Integer>> inlineCords = new ArrayList<Pair<Integer, Integer>>();
		
		// Horizontal left
		for(int i = column; i >= 0; i--)
		{
			if(i != column)
			{
				var cord = new Pair<Integer,Integer>(i,row);
				if(occupiedCords.contains(cord))
					inlineCords.add(cord);
//				System.out.println("Left: " + i + "=" + row);
			}
		}
		
		// Horizontal right
		for(int i = column; i < 15; i++)
		{
			if(i != column)
			{
				var cord = new Pair<Integer,Integer>(i,row);
				if(occupiedCords.contains(cord))
					inlineCords.add(cord);
//				System.out.println("Right: " + i + "=" + row);
			}
		}
		
		// Vertical up
		for(int i = row; i >= 0; i--)
		{
			if(i != row)
			{
				var cord = new Pair<Integer,Integer>(column,i);
				if(occupiedCords.contains(cord))
					inlineCords.add(cord);
//				System.out.println("Up: " + column + "=" + i);
			}
		}
		
		// Vertical down
		for(int i = row; i < 15; i++)
		{
			if(i != row)
			{
				var cord = new Pair<Integer,Integer>(column,i);
				if(occupiedCords.contains(cord))
					inlineCords.add(cord);
//				System.out.println("Down: " + column + "=" + i);
			}
		}
		
		return inlineCords;
	}
	
	private String createWordFromCords(ArrayList<Pair<Integer,Integer>> cords)
	{
		var letters = new ArrayList<Character>();
		
		for(var cord : cords)
		{
			if(_boardTiles.get(new Point(cord.getKey(), cord.getValue())) != null)
			{
				var tile = _boardTiles.get(new Point(cord.getKey(), cord.getValue()));
				letters.add(tile.getSymbolAsChar());
			}
		}
		
		var word = new StringBuilder(letters.size());
		
		for(var c : letters)
		{
			word.append(c);
		}
		
		return word.toString();
	}
	
	private String checkWordHorVer(String word, char charToAdd, String dictWord)
	{
		var tempWord = "";
		dictWord = dictWord.toUpperCase();
		
		// Vertical up -> down + Horizontal right -> left
		tempWord = new StringBuilder(word).reverse().append(charToAdd).toString();
		if(tempWord.equals(dictWord))
			return tempWord;
		
		// Vertical down -> up + Horizontal left -> right
		tempWord = new StringBuilder(word).insert(0,charToAdd).reverse().toString();
		if(tempWord.equals(dictWord))
			return tempWord;
		
		return null;
	}
	
	private ArrayList<Point> getWordCords(ArrayList<String> words, char playedChar, Pair<Integer, Integer> playedCord)
	{
		// Dummy data
		words = new ArrayList<String>() { { add("Beller"); add("Belles"); add("Bellec"); add("Bel"); add("Bell"); } };
				
		ArrayList<Point> wordCords = new ArrayList<Point>();
		
		var column = playedCord.getKey();
		var row = playedCord.getValue();
		
		var connectedOccupiedCords = getOccupiedInlineCords(column, row, _board.getOccupiedPositions());
						
		for(var word : words)
		{			
			if(word.toLowerCase().indexOf(Character.toLowerCase(playedChar)) == -1)
				continue;
			
			System.out.println(checkWordHorVer(createWordFromCords(connectedOccupiedCords), playedChar, word));
		}
		
		return wordCords;
	}
}
