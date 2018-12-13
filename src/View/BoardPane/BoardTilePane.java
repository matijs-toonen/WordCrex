package View.BoardPane;

import java.awt.Point;
import java.util.function.Consumer;

import javafx.event.EventHandler;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;

public class BoardTilePane extends Pane{
	private BoardTile _boardTile;
	private Point _currentPoint;
	
	public BoardTilePane(Point point) {
		super();
		_currentPoint = point;
	}
	
	public BoardTilePane(BoardTile boardTile, Point point) {
		this(point);
		_boardTile = boardTile;
	}
	
	public BoardTile getBoardTile() {
		return _boardTile;
	}
	
	public Point getCords(){
		return _currentPoint;
	}
	
	public void setBoardTile(BoardTile newBoardTile) {
		_boardTile = newBoardTile;
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
