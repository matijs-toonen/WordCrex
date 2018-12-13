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

import Model.Account;
import Model.Game;
import Model.HandLetter;
import Model.Letter;
import Model.LetterSet;
import Model.Symbol;
import Model.Turn;
import Model.Board.Board;
import Model.Board.PositionStatus;
import View.BoardPane.BoardTile;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class BoardController implements Initializable {
	
	private DatabaseController _db;
	private HashMap<Point, BoardTile> _tiles;
	private Board _board;
	private ArrayList<BoardTile> _currentHand;
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
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		_tiles = new HashMap<Point, BoardTile>();

		lblPlayer1.setText("BaderAli99");
		lblPlayer1.setStyle("-fx-font-size: 28");
		lblPlayer2.setText("SchurkTurk");
		lblPlayer2.setStyle("-fx-font-size: 28");
		lblScore1.setText("1");
		lblScore1.setStyle("-fx-font-size: 20; -fx-background-color: #F4E4D3; -fx-background-radius: 25 0 0 25");
		lblScore2.setText("9");
		lblScore2.setStyle("-fx-font-size: 20; -fx-background-color: #F4E4D3; -fx-background-radius: 0 25 25 0");
    
		_currentHand = new ArrayList<BoardTile>();
		_board = new Board();
		createField();
		createHand();
	}
	
	public void shuffle(){
		placeHand();
	}
	
	public void reset() {
		reset.setVisible(false);
	}
	
	public void openHistory() throws IOException{
		if(!_historyVisible) {
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
			Parent chatFrame = FXMLLoader.load(getClass().getResource("/View/Chat.fxml"));
			
			rightBarAnchor.getChildren().setAll(chatFrame);
			_chatVisible = true;
		}
		else {
			rightBarAnchor.getChildren().clear();
			_chatVisible = false;
		}
	}
	
	private void createField() {
		int x = 13;
		for(int i = 0; i < 15; i++) {
			int y = 13;
			for(int j = 0; j < 15; j++) {
				var tile = new BoardTile(i, j);
				tile.setDropEvents(createDropEvents());
				tile.setBackground(getBackground(Color.CHOCOLATE));
				tile.setLayoutX(x);
				tile.setLayoutY(y);
				tile.setMinWidth(39);
				tile.setMinHeight(39);
				tile.setStyle("-fx-background-color: #E8E9EC; -fx-background-radius: 6");
				tile.createOnClickEvent(creatOnClickEvent());
				_tiles.put(new Point(x, y), tile);
				y += 44.5;
				panePlayField.getChildren().add(tile);
			}
			x += 44.5;
		}
	}
	
	private void createHand() {
		_currentHand.clear();
		_db = new DatabaseController<HandLetter>();
		try {
			var count = _db.SelectCount("SELECT COUNT(*) FROM handletter");
			System.out.println(count);
			var handLetters = (ArrayList<HandLetter>) _db.SelectWithCustomLogic(getHandLetter(), "SELECT * FROM handletter NATURAL JOIN letter NATURAL JOIN symbol where turn_id = 1 AND game_id = 500");
			int x = 10;
			int y = 13;

			for(var handLetter : handLetters) {
				for(var letter : handLetter.getLetters()) {
					System.out.println(letter.getSymbol().getChar());
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
			placeHand();
		} 
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void placeHand() {
		int x = 12;
		int y = 0;
		
		Collections.shuffle(_currentHand);
		
		for(var tile : _currentHand) {
			tile.setLayoutX(x);
			tile.setLayoutY(y);
			y += 32;
			paneHand.getChildren().remove(tile);
			paneHand.getChildren().add(tile);
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
				var sourceTile = (BoardTile) event.getGestureSource();
				var symbol = sourceTile.getSymbol();
				boardTile.setSymbol(symbol);
				boardTile.setStyle("-fx-background-color: pink; -fx-background-radius: 6");
				reset.setVisible(true);
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
}
