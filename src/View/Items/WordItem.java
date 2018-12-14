package View.Items;

import java.util.function.Consumer;

import Model.Word;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

public class WordItem extends AnchorPane {
	
	private Word _word;
	
	public final static String acceptText = "Accepteren";
	public final static String rejectText = "Weigeren";
	
	@FXML
	private Label lblWoord = new Label();
	private Label lblState = new Label();
	private Button btnAccept = new Button();
	private Button btnDecline = new Button();
	
	public WordItem(Word word) {
		super();
		_word = word;
		
		init();	
	}
	
	private void init() {
		
		if(_word.getWordState().equals("denied") || _word.getWordState().equals("accepted"))
			LoadJugdedItems();
		else
			LoadItems();
	}

	private void LoadItems()
	{	
		lblWoord.setText(_word.getWord());
		lblWoord.setLayoutX(5);
		lblWoord.getStyleClass().add("text");
		
		btnAccept.setLayoutX(200);
		btnAccept.setText(acceptText);
		
		btnDecline.setLayoutX(300);
		btnDecline.setText(rejectText);
		
		this.getChildren().addAll(lblWoord, btnAccept, btnDecline);
	}
	
	private void LoadJugdedItems()
	{	
		lblWoord.setText(_word.getWord());
		lblWoord.setLayoutX(5);
		lblWoord.getStyleClass().add("text");
		
		lblState.setText(_word.getWordState());
		lblState.setLayoutX(200);
		
		this.getChildren().addAll(lblWoord, lblState);
	}
	
	public void setOnClickEvent(Consumer<ActionEvent> action) {
		btnAccept.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				action.accept(event);
			}
		});
		
		btnDecline.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				action.accept(event);
			}
		});
	}
}
