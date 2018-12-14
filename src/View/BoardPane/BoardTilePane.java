package View.BoardPane;

import java.awt.Point;
import java.util.function.Consumer;

import Model.Symbol;
import Model.Tile;
import Model.TileType;
import javafx.event.EventHandler;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Background;
import javafx.scene.layout.Pane;

public class BoardTilePane extends Pane {
	private BoardTile _boardTile;
	private Point _currentPoint;
	private TileType _type;
	
	public BoardTilePane(Background color) {
		super();
		setBackground(color);
	}
	
	public BoardTilePane(Point point) {
		super();
		_currentPoint = point;
	}
	
	public BoardTilePane(Point point, TileType type, BoardTile boardTile) {
		this(point);
		_boardTile = boardTile;
		_type = type;
	}
	
	public BoardTilePane(Tile tile)
	{
		// Index 0
		this(new Point(tile.getX()-1, tile.getY()-1), tile.getType(), null);
	}
	
	public BoardTile getBoardTile() {
		return _boardTile;
	}
	
	public void setBoardTile(BoardTile newBoardTile) {
		_boardTile = newBoardTile;
	}
	
	public Point getCords(){
		return _currentPoint;
	}
	
	public int getBonusValue()
	{
		return _type.getValue();
	}
	
	public char getBonusLetter()
	{
		return _type.getLetter();
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
