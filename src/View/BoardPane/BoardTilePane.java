package View.BoardPane;

import java.awt.Point;
import java.util.function.Consumer;

import Model.Symbol;
import Model.Tile;
import Model.TileType;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class BoardTilePane extends Pane {
	private BoardTile _boardTile;
	private Point _currentPoint;
	private TileType _type;
	private boolean _isTaken;
	private Label lblValue = new Label();
	private Label lblSymbol = new Label();
	
	public BoardTilePane(Point point, TileType type) {
		super();
		_currentPoint = point;
		_type = type;
		setTypeAsVisual();
		init();
	}
	
	public BoardTilePane(Tile tile)
	{
		// Index 0
		this(new Point(tile.getX()-1, tile.getY()-1), tile.getType());
	}
	
	private void init() {
		lblValue.setLayoutX(25);
		lblValue.getStyleClass().add("tileValue");

		lblSymbol.setLayoutX(15);
		lblSymbol.setLayoutY(8);
		lblSymbol.getStyleClass().add("tileSymbol");

		getChildren().removeAll(lblValue, lblSymbol);
		getChildren().addAll(lblValue, lblSymbol);
	}
	
	private void setTypeAsVisual()
	{
		if(_type != null)
		{
			if(_type.getValue() != 0)
				lblValue.setText(String.valueOf(_type.getValue()));
			else
				lblValue.setText("");
			
			lblSymbol.setText(String.valueOf(_type.getLetter()));
		}
	}
	
	private void showBoardTile() {
		getChildren().remove(_boardTile);
		
		if(_boardTile != null)
			getChildren().add(_boardTile);
	}
	
	public BoardTile getBoardTile() {
		return _boardTile;
	}
	
	public void setBoardTile(BoardTile newBoardTile) {
		_boardTile = newBoardTile;
		showBoardTile();
	}
	
	public void removeBoardTile() {
		_boardTile = null;
		showBoardTile();
	}
	
	public Character getBoardTileSymbolAsChar() {
		if(_boardTile != null)
			return _boardTile.getSymbolAsChar();
		return ' ';
	}
	
	public int getBonusValue()
	{
		return _type.getValue();
	}
	
	public char getBonusLetter()
	{
		return _type.getLetter();
	}
	
	public void setTaken() {
		_isTaken = true;
	}
	
	public boolean getTaken() {
		return _isTaken;
	}
	
	public Point getCords(){
		return _currentPoint;
	}
	
	public void setSymbol(Symbol symbol) {
		_boardTile.setSymbol(symbol);
	}
	
	public void setDropEvents(Consumer<DragEvent> action) {
		this.setOnDragOver(new EventHandler<DragEvent>() {

			@Override
			public void handle(DragEvent event) {
				
				if(event.getGestureSource() != event.getTarget()) {
					event.acceptTransferModes(TransferMode.ANY);
				}
				event.consume();
			}
		});
		
		this.setOnDragDropped(new EventHandler<DragEvent>() {

			@Override
			public void handle(DragEvent event) {
				action.accept(event);
			}
		});
	}
}
