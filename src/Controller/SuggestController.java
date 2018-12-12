package Controller;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

import Model.Account;
import Model.Game;
import Model.LetterSet;
import Model.Word;
import Model.WordState.AcceptedWordState;
import Model.WordState.DeniedWordState;
import Model.WordState.PendingWordState;
import Model.WordState.WordState;
import View.Items.ChallengePlayerItem;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class SuggestController implements Initializable{
	
	private DatabaseController<Word> _db;
	private ArrayList<Word> _words;
	
	@FXML
	private TextField textfieldAddWord;
	
	@FXML
	private ImageView plusButton;
	
	@FXML
	private VBox wordList;
	
	@FXML 
	private TextField searchBox;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		_db = new DatabaseController<Word>();
		
		searchBox.textProperty().addListener((observable) -> {
			//showPlayersToChallenge();
			System.out.println("Test");
			showSuggestedWords();
		});
		
		plusButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) ->  addWord() );
		showSuggestedWords();
	}
	
	public void textfieldConfirm(ActionEvent e) {
		addWord();
	}
	
	public void addWord() {
		
		String wordValue = textfieldAddWord.getText().toLowerCase();
		
		if(wordValue == null || wordValue.isEmpty()) {
			return;
		}
		
		wordValue = wordValue.replace(" ", "");
		
		textfieldAddWord.setText("");
		
		try {
			String statement = Word.insterQuery(wordValue, MainController.getUser().getUsername());
			System.out.println(statement);
			_db.Insert(statement);
		} catch (SQLException e) {
			System.out.println("Error");
		}
		
		showSuggestedWords();
	}
	
	
	private void showSuggestedWords() {
		
		wordList.getChildren().clear();
		
		String selectQuery = Word.selectQuery(MainController.getUser().getUsername());
		try {
			_words = (ArrayList<Word>) _db.SelectAll(selectQuery, Word.class);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		}
		
		Word.getAllWordsThatContain(_words, searchBox.getText()).forEach(word -> {
			
			HBox wordBox = new HBox();
		
			Label wordState = new Label();
			
			switch (word.getWordState()) {
			case "accepted":
				wordState.setText("Goed Gekeurd");
				wordState.setStyle("-fx-background-color: green");
				break;
			case "pending":
				wordState.setText("In Beoordeling...");
				wordState.setStyle("-fx-background-color: gray");
				break;
			case "denied":
				wordState.setText("Af Gekeurd");
				wordState.setStyle("-fx-background-color: red");
				break;
			default:
				break;
			}
			
			Label wordLabel = new Label();
			wordLabel.setText(word.getWord());
			
			wordBox.getChildren().addAll(wordLabel, wordState);
			
			wordList.getChildren().add(wordBox);
		});
		
	}
	
}
