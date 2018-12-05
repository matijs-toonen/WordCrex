package Controller;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import Model.Account;
import Model.Game;
import Model.LetterSet;
import Model.Word;
import Model.WordState.AcceptedWordState;
import Model.WordState.DeniedWordState;
import Model.WordState.PendingWordState;
import Model.WordState.WordState;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class SuggestController implements Initializable{
	
	private DatabaseController _db;
	
	@FXML
	private TextField textfieldAddWord;
	
	@FXML
	private ImageView plusButton;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		_db = new DatabaseController<Word>();
		
		plusButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) ->  addWord() );
	}
	
	public void textfieldConfirm(ActionEvent e) {
		addWord();
	}
	
	public void addWord() {
		
		// TODO: validate word
		String wordValue = textfieldAddWord.getText().toLowerCase();
		
		if(wordValue == null || wordValue.isEmpty()) {
			return;
		}
		
		textfieldAddWord.setText("");
		
		LetterSet letterSet = new LetterSet("NL", "Nederlands");
		WordState wordState = getWordState("pending");
		Account account = new Account("test-player", "123");
		Word word = new Word(wordValue, letterSet, wordState, account);
		
		try {
			String statement = "INSERT INTO dictionary (word, letterset_code, state, username) VALUES ('" + wordValue +"', 'NL', 'pending', 'test-player');";
			_db.Insert(statement);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("Added word to database!");
		
	}
	
	private WordState getWordState(String state) {
		switch(state.toLowerCase()) {
		case "accepted":
			return new AcceptedWordState();
		case "denied":
			return new DeniedWordState();
		case "pending":
			return new PendingWordState();
		default:
			return null;
		}
	}
	
}
