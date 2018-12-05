package View.BoardPane;

import java.util.function.Consumer;

import Model.Symbol;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
import javafx.util.Pair;

public class BoardTile extends Rectangle {
	private Symbol _symbol;
	private int _column;
	private int _row;

	public BoardTile(int column, int row, Symbol symbol) {
		super();
		_column = column;
		_row = row;
	}	
	
	public BoardTile(int column, int row) {
		this(column, row, null);
	}
	
	public Symbol getSymbol() {
		return _symbol;
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
