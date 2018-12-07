package View.BoardPane;

import java.util.function.Consumer;

import Model.Symbol;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Label;
import javafx.scene.image.WritableImage;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Pair;

public class BoardTile extends Pane {
	private Symbol _symbol;
	private int _column;
	private int _row;
	private Label lblValue = new Label();
	private Label lblSymbol = new Label();

	public BoardTile(boolean isDraggable, Symbol symbol) {
		super();
		_symbol = symbol;
		lblValue.setLayoutX(20);
		lblValue.setTextFill(Color.GREEN);

		lblSymbol.setLayoutX(12);
		lblSymbol.setLayoutY(8);
		lblSymbol.setTextFill(Color.BLUE);
		
		if(isDraggable) {
			setDraggableEvents();
			lblValue.setText(String.valueOf(_symbol.getValue()));
			lblSymbol.setText(String.valueOf(_symbol.getChar()));
		}
		else {
			setDragEvents();
			_symbol = new Symbol('h');
//			lblValue.setText("4");
//			lblSymbol.setText("H");
		}
			

		getChildren().addAll(lblValue, lblSymbol);
	}
	
	public BoardTile(boolean isDraggable, int column, int row, Symbol symbol) {
		this(isDraggable, symbol);
		_column = column;
		_row = row;
	}	
	
	public BoardTile(boolean isDraggable, int column, int row) {
		this(isDraggable, column, row, null);
	}
	
	public Symbol getSymbol() {
		return _symbol;
	}
	
	public Pair<Integer, Integer> getCords(){
		return new Pair<Integer, Integer>(_column, _row);
	}
	
	private void setDragEvents() {
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
				if(event.getTarget() instanceof BoardTile) {
					System.out.println("testDropped");
					var boardTile = (BoardTile) event.getGestureTarget();
					var sourceTile = (BoardTile) event.getGestureSource();
					var symbol = sourceTile.getSymbol().getChar();
					boardTile.setSymbol(String.valueOf(symbol));
					boardTile.setBackground(getBackground(Color.PINK));
				}
				
				Dragboard db = event.getDragboard();
				if(db.hasString()) {
					event.acceptTransferModes(TransferMode.ANY);
					event.setDropCompleted(true);
					event.consume();	
				}
			}
		});
	}
	
	private Background getBackground(Color color) {
		var backgroundFill = new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY);
		return new Background(backgroundFill);
	}
	
	private void setDraggableEvents() {
		this.setOnDragDetected(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				var node = (BoardTile) event.getSource();
				Dragboard db = node.startDragAndDrop(TransferMode.ANY);
				
				var content = new ClipboardContent();
				db.setDragView(createSnapshot(node));
				content.putString(lblSymbol.getText());
				db.setContent(content);
				event.consume();
			}
		});
	}
	
	public void setSymbol(String text) {
		lblSymbol.setTextFill(Color.BLUE);
		lblSymbol.setText(text);
	}
	
	private WritableImage createSnapshot(Node item) {
		var params = new SnapshotParameters();
		params.setFill(Color.TRANSPARENT);
		return item.snapshot(params, null);
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
