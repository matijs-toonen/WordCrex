package Controller;

import java.awt.Point;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.function.Function;

import Model.Game;
import Model.HandLetter;
import Model.Letter;
import Model.Symbol;
import Model.Turn;
import Model.Board.Board;
import Model.Board.PositionStatus;
import View.BoardPane.BoardTile;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class BoardController implements Initializable {
	
	private DatabaseController _db;
	private Random _random = new Random();
	private ArrayList<Letter> _letters = new ArrayList<Letter>();
	private Turn _currentTurn;
	private Game _currentGame;
	private HashMap<Point, BoardTile> _tiles;
	private Board _board;
    private ArrayList<BoardTile> _currentHand;
    private HashMap<Point, BoardTile> _fieldHand;
	
	@FXML
	private Label lblScore1, lblScore2, lblPlayer1, lblPlayer2;
	
	@FXML
	private Button btnTest;
	
	@FXML
	private Pane panePlayField, paneHand;
	
	public BoardController(Game game) {
		_board = new Board();
		_tiles = new HashMap<Point, BoardTile>();
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
		lblPlayer1.setText("SCHRUKTURK");
		lblPlayer2.setText("BOEDER");
		lblScore1.setText("1");
		lblScore2.setText("9");
		createField();
		createHand();
		createOnButtonTestClick();
	}
	
	private void createField() {
		int x = 1;
		for(int i = 0; i < 15; i++) {
			int y = 1;
			for(int j = 0; j < 15; j++) {
				var tile = new BoardTile(new Point(i, j));
				tile.setDropEvents(createDropEvents());
				tile.setBackground(getBackground(Color.CHOCOLATE));
				tile.setLayoutX(x);
				tile.setLayoutY(y);
				tile.setMinWidth(30);
				tile.setMinHeight(30);
				tile.createOnClickEvent(creatOnClickEvent());
				_tiles.put(new Point(i, j), tile);
				y += 32;
				panePlayField.getChildren().add(tile);
			}
			x += 32;
		}
	}
	
	private void createHand() {
        _currentHand.clear();
		_db = new DatabaseController<HandLetter>();
		var handLetters = getHandLetters();
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
			boardTile.setBackground(getBackground(Color.CHOCOLATE));
			boardTile.resetTile();
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
			if(event.getGestureTarget() instanceof BoardTile) {
				var boardTile = (BoardTile) event.getGestureTarget();
				var sourceTile = (BoardTile) event.getGestureSource();
				sourceTile.setCords(boardTile.getCords());
				boardTile.setDraggableEvents();
				var symbol = sourceTile.getSymbol();
				boardTile.setSymbol(symbol);
				var bg = sourceTile.getBackground();
				boardTile.setBackground(bg);
				_fieldHand.put(boardTile.getCords(), sourceTile);
			}
			
			Dragboard db = event.getDragboard();
			if(db.hasString()) {
				event.acceptTransferModes(TransferMode.ANY);
				event.setDropCompleted(true);
				event.consume();	
			}
		});
	}
	
	private Consumer<MouseEvent> creatOnClickEvent(){
		return (event -> {
			var tile = (BoardTile) event.getSource();
			var cords = tile.getCords();
			if(!_board.canPlace(cords))
				return;
			
			_board.updateStatus(cords, PositionStatus.Taken);
			tile.setBackground(getBackground(Color.YELLOW));
		});
	}
	
	//Dummy
	private void createOnButtonTestClick() {
		btnTest.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				resetHand();
			}
		});
	}
}
