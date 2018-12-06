package View.BoardPane;

import java.util.function.Consumer;

import Model.Symbol;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Label;
import javafx.scene.image.WritableImage;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Pair;

public class BoardTile extends Pane {
	private Symbol _symbol;
	private int _column;
	private int _row;
	private Label lblValue = new Label();

	public BoardTile(boolean isDraggable) {
		super();
		lblValue.setLayoutX(20);
		lblValue.setLayoutX(20);
		lblValue.setTextFill(Color.GREEN);
		lblValue.setText("5");
		
		if(isDraggable)
			setDraggableEvents();
		else 
			setDragEvents();

		getChildren().addAll(lblValue);
	}
	
	public BoardTile(boolean isDraggable, int column, int row, Symbol symbol) {
		this(isDraggable);
		_column = column;
		_row = row;
		_symbol = symbol;
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
	
	private void setDraggableEvents() {
		this.setOnDragDetected(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				var node = (Pane) event.getSource();
				Dragboard db = node.startDragAndDrop(TransferMode.ANY);
				
				var content = new ClipboardContent();
				db.setDragView(createSnapshot(node));
				content.putString(lblValue.getText());
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
	
	public void createOnClickEvent(Consumer<MouseEvent> action) {
		this.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				action.accept(event);	
			}
		});
	}
}
