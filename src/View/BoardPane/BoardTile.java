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

	public BoardTile(Symbol symbol) {
		super();
		_symbol = symbol;
		lblValue.setLayoutX(20);
		lblValue.setTextFill(Color.GREEN);

		lblSymbol.setLayoutX(12);
		lblSymbol.setLayoutY(8);
		lblSymbol.setTextFill(Color.BLUE);
		
		if(_symbol == null) {
//			lblValue.setText("4");
			lblSymbol.setText("H");
			
		}else {
			lblValue.setText(String.valueOf(_symbol.getValue()));
			lblSymbol.setText(String.valueOf(_symbol.getChar()));
		}

		getChildren().addAll(lblValue, lblSymbol);
	}
	
	public BoardTile(int column, int row, Symbol symbol) {
		this(symbol);
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
	
	public void setDraggableEvents() {
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
	
	public void setSymbol(Symbol symbol) {
		lblSymbol.setTextFill(Color.BLUE);
		lblSymbol.setText(String.valueOf(symbol.getChar()));
		lblValue.setText(String.valueOf(symbol.getValue()));
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
