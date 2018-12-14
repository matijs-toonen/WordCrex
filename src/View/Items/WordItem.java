package View.Items;

import Model.Account;
import Model.Word;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

public class WordItem extends AnchorPane {
	
	private Word _word;
	
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
	}

	private void LoadItems()
	{
		System.out.println(_word.getWord());
		
		lblWoord.setText(_word.getWord());
		
		btnAccept.setLayoutX(200);
		btnAccept.setLayoutY(200);
		
		btnDecline.setLayoutX(400);
		btnDecline.setLayoutY(400);
	}
}
