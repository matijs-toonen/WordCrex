package Controller;

import java.awt.Point;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
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
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.WritableImage;
import javafx.scene.input.ClipboardContent;
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
	private HashMap<Point, BoardTile> _tiles;
	private Board _board;
	
	@FXML
	private Label lblScore1, lblScore2, lblPlayer1, lblPlayer2;
	
	@FXML
	private Button btnTest;
	
	@FXML
	private Pane panePlayField, paneHand;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		_tiles = new HashMap<Point, BoardTile>();
		lblPlayer1.setText("SCHRUKTURK");
		lblPlayer2.setText("BOEDER");
		lblScore1.setText("1");
		lblScore2.setText("9");
		_board = new Board();
		createField();
		createHand();
		setDragEvents();
	}
	
	private void createField() {
		int x = 1;
		for(int i = 0; i < 15; i++) {
			int y = 1;
			for(int j = 0; j < 15; j++) {
				var tile = new BoardTile(false, i, j);
				tile.setBackground(getBackground(Color.CHOCOLATE));
				tile.setLayoutX(x);
				tile.setLayoutY(y);
				tile.setMinWidth(30);
				tile.setMinHeight(30);
				tile.createOnClickEvent(creatOnClickEvent());
				_tiles.put(new Point(x, y), tile);
				y += 32;
				panePlayField.getChildren().add(tile);
			}
			x += 32;
		}
	}
	
	private void createHand() {
		_db = new DatabaseController<HandLetter>();
		try {
			var handLetters = (ArrayList<HandLetter>) _db.SelectWithCustomLogic(getHandLetter(), "SELECT * FROM handletter NATURAL JOIN letter where turn_id = 1");
			int x = 12;
			int y = 0;

			for(var handLetter : handLetters) {
				for(var letter : handLetter.getLetters()) {
					System.out.println(letter.getSymbol().getChar());
					var boardTile = new BoardTile(true, letter.getSymbol());
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
	
	private void setDragEvents() {		
		btnTest.setOnDragDetected(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				var node = (Button) event.getSource();
				Dragboard db = node.startDragAndDrop(TransferMode.ANY);
				
				var content = new ClipboardContent();
				db.setDragView(createSnapshot(node));
				content.putString(btnTest.getText());
				db.setContent(content);
				event.consume();
			}
		});
	}
	
	private WritableImage createSnapshot(Node item) {
		var params = new SnapshotParameters();
		params.setFill(Color.TRANSPARENT);
		return item.snapshot(params, null);
	}
	
	private Background getBackground(Color color) {
		var backgroundFill = new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY);
		return new Background(backgroundFill);
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
