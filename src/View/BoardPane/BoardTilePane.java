package View.BoardPane;

import java.awt.Point;
import java.util.function.Consumer;

import Model.Symbol;
import Model.Tile;
import Model.TileType;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Background;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class BoardTilePane extends Pane {
	private BoardTile _boardTile;
	private Point _currentPoint;
	private TileType _type;
	private boolean _isTaken;
	private Label lblValue = new Label();
	private Label lblSymbol = new Label();
	private Label lblScore = new Label();
	
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
		
		lblScore.setAlignment(Pos.CENTER);
        lblScore.setLayoutX(18);
		lblScore.getStyleClass().add("tileScore");

		getChildren().removeAll(lblValue, lblSymbol, lblScore);
		getChildren().addAll(lblValue, lblSymbol, lblScore);
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
	
	private void showBoardTile(boolean shouldAdd) {
		getChildren().removeAll(_boardTile,lblScore);
		
		if(shouldAdd && _boardTile != null)
			getChildren().addAll(_boardTile,lblScore);
	}
	
	public void clearScore() {
		lblScore.setText("");
	}
	
	public BoardTile getBoardTile() {
		return _boardTile;
	}
	
	public void setBoardTile(BoardTile newBoardTile) {
		_boardTile = newBoardTile;
		showBoardTile(true);
	}
	
	public void setBoardTile(BoardTile newBoardTile, Integer score) {
		lblScore.setText(score.toString());
		
		setBoardTile(newBoardTile);
	}
	
	public void removeBoardTile() {
		showBoardTile(false);
		_boardTile = null;
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
