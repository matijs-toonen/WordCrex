package View.Items;

import java.util.function.Consumer;

import Model.Game;
import Model.Word;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
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
		lblWoord.getStyleClass().add("normalLabel");
		
		btnAccept.setLayoutX(150);
		btnAccept.setLayoutY(5);
		btnAccept.setText(acceptText);
		btnAccept.getStyleClass().add("blueButton");
		
		btnDecline.setLayoutX(260);
		btnDecline.setLayoutY(5);
		btnDecline.setText(rejectText);
		btnDecline.getStyleClass().add("redButton");
		
		this.getChildren().addAll(lblWoord, btnAccept, btnDecline);
	}
	
	private void LoadJugdedItems()
	{	
		lblWoord.setText(_word.getWord());
		lblWoord.setLayoutX(5);
		lblWoord.getStyleClass().add("normalLabel");
		
		lblState.setText(_word.getWordState());
		lblState.setLayoutX(200);
		lblState.setPrefWidth(150);
		lblState.setAlignment(Pos.CENTER);
		
		if(lblState.getText().equals("denied")) {
			lblState.getStyleClass().add("rejectedItem");
		} else {
			lblState.getStyleClass().add("approvedItem");
		}
		
		this.getChildren().addAll(lblWoord, lblState);
	}
	
	public Word getWord() {
		return _word;
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
