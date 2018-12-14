package View.Items;

import Model.Account;
import Model.Word;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

public class WordItem extends AnchorPane {
	
	private Word _word;
	
	@FXML
	private Label lblWoord = new Label();
	private Button btnAccept = new Button("Accepteren");
	private Button btnDecline = new Button("Weigeren");
	
	public WordItem(Word word) {
		super();
		_word = word;
		
		init();
	}
	
	private void init() {
		LoadItems();
		
		this.getChildren().addAll(lblWoord, btnAccept, btnDecline);
	}

	private void LoadItems()
	{	
		lblWoord.setText(_word.getWord());
		lblWoord.setLayoutX(5);
		lblWoord.getStyleClass().add("text");
		
		btnAccept.setLayoutX(200);
		
		btnDecline.setLayoutX(300);
	}
}
