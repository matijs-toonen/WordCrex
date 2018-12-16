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

public class BoardTile extends Pane {
	private Symbol _symbol;
	private Label lblValue = new Label();
	private Label lblSymbol = new Label();

	public BoardTile(Symbol symbol) {
		super();
		init(symbol);
	}
	
	private void init(Symbol symbol) {
		_symbol = symbol;
		lblValue.setLayoutX(25);
		lblValue.getStyleClass().add("tileValue");

		lblSymbol.setLayoutX(12);
		lblSymbol.setLayoutY(8);
		lblSymbol.getStyleClass().add("tileSymbol");
		
		if(_symbol == null) {
			lblSymbol.setText("#");
			
		}else {
			var value = _symbol.getValue();
			if(value != 0) 
				lblValue.setText(String.valueOf(value));	
			
			lblSymbol.setText(String.valueOf(_symbol.getChar()));
		}

		getChildren().removeAll(lblValue, lblSymbol);
		getChildren().addAll(lblValue, lblSymbol);
	}
	
	public Symbol getSymbol() {
		return _symbol;
	}
	
	public void resetTile() {
		init(null);
		lblValue.setText("");
	};
	
	public Character getSymbolAsChar() {
		if(_symbol != null)
			return String.valueOf(_symbol.getChar()).toCharArray()[0];
		else
			return ' ';
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
				setPaneVisible(false);
				event.consume();
			}
		});
		
		this.setOnDragDone(new EventHandler<DragEvent>() {

			@Override
			public void handle(DragEvent event) {
				var target = event.getGestureTarget();
				if(!(target instanceof BoardTile)) {
					setPaneVisible(true);
					event.consume();
				}
			}
		});
	}
	
	public void setSymbol(Symbol symbol) {
		_symbol = symbol;
		lblSymbol.setTextFill(Color.BLUE);
		lblSymbol.setText(String.valueOf(symbol.getChar()));
		lblValue.setText(String.valueOf(symbol.getValue()));
	}
	
	private WritableImage createSnapshot(Node item) {
		var params = new SnapshotParameters();
		params.setFill(Color.TRANSPARENT);
		return item.snapshot(params, null);
	}
	
	public void setPaneVisible(boolean visible) {
		setVisible(visible);
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
