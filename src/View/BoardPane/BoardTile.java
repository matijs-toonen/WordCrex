package View.BoardPane;

import java.util.function.Consumer;

import Model.Symbol;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Pair;

public class BoardTile extends Pane {
	private Symbol _symbol;
	private int _column;
	private int _row;
	private Rectangle _rectangle;
	private Label lblValue = new Label();

	public BoardTile(Rectangle rectangle, int column, int row, Symbol symbol) {
		super();
		lblValue.setLayoutX(20);
		lblValue.setLayoutX(20);
		lblValue.setTextFill(Color.GREEN);
		lblValue.setText("5");
		_column = column;
		_row = row;
		_rectangle = rectangle;
		getChildren().addAll(rectangle, lblValue);
	}	
	
	public BoardTile(Rectangle rectangle, int column, int row) {
		this(rectangle, column, row, null);
	}
	
	public Symbol getSymbol() {
		return _symbol;
	}
	
	public Rectangle getRectangle() {
		return _rectangle;
	}
	
	public Pair<Integer, Integer> getCords(){
		return new Pair<Integer, Integer>(_column, _row);
	}
	
	public void createOnClickEvent(Consumer<MouseEvent> action) {
		this.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				action.accept(event);	
			}
		});
	}
}
