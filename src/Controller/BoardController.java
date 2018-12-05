package Controller;

import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import Model.Board.Board;
import Model.Board.PositionStatus;
import View.BoardPane.BoardTile;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Pair;

public class BoardController implements Initializable {
	
	private HashMap<Pair<Integer, Integer>, BoardTile> _rectangles;
	private HashMap<Pair<Integer, Integer>, Pair<Integer, Integer>> _positions;
	private Board _board;
	
	@FXML
	private Label lblScore1, lblScore2, lblPlayer1, lblPlayer2;
	
	@FXML
	private Pane panePlayField;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		_rectangles = new HashMap<Pair<Integer,Integer>, BoardTile>();
		_positions = new HashMap<Pair<Integer,Integer>, Pair<Integer,Integer>>();
		lblPlayer1.setText("SCHRUKTURK");
		lblPlayer2.setText("BOEDER");
		lblScore1.setText("1");
		lblScore2.setText("9");
		_board = new Board();
		createField();
	}
	
	private void createField() {
		int x = 1;
		for(int i = 0; i < 15; i++) {
			int y = 1;
			for(int j = 0; j < 15; j++) {
				var rectangle = new Rectangle();
				rectangle.setFill(Color.CHOCOLATE);
				rectangle.setWidth(30);
				rectangle.setHeight(30);
				rectangle.setStroke(Color.BLACK);
				
				var tile = new BoardTile(rectangle, i, j);
				tile.setLayoutX(x);
				tile.setLayoutY(y);
				tile.createOnClickEvent(creatOnClickEvent());
				_rectangles.put(new Pair<Integer, Integer>(i, j), tile);
				y += 32;
				panePlayField.getChildren().add(tile);
				_positions.put(new Pair<Integer, Integer>(i, j), new Pair<Integer, Integer>(x, y));
			}
			x += 32;
		}
	}
	
	private Consumer<MouseEvent> creatOnClickEvent(){
		return (event -> {
			var tile = (BoardTile) event.getSource();
			var cords = tile.getCords();
			if(!_board.canPlace(cords))
				return;
			
			_board.updateStatus(cords, PositionStatus.Taken);
			tile.getRectangle().setFill(Color.YELLOW);
		});
	}
}
