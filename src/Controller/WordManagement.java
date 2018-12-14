package Controller;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import Model.Game;
import Model.Word;
import View.Items.ChallengeItem;
import View.Items.WordItem;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

public class WordManagement implements Initializable {

	private DatabaseController<Word> _db = new DatabaseController<Word>();
	private ArrayList<Word> _wordsNeedJugding;
	private ArrayList<Word> _wordsJugded;
	
	@FXML
	private VBox WordsNeedJugding;
	
	@FXML
	private VBox JudgedWords;
	
	@FXML 
	private TextField searchBox;
	
	@Override
	public void initialize(URL url, ResourceBundle resources) {
		// TODO Auto-generated method stub
		init();

		searchBox.textProperty().addListener((observable) -> {
			
		});
	}
	
	private void init() {
		LoadWords();
	}
	
	private void LoadWords() {
		String queryNeedsJugding = Word.selectAllQuery();
		String queryJuged = Word.selectAllJugded();
		
		try {
			_wordsNeedJugding = (ArrayList<Word>) _db.SelectAll(queryNeedsJugding, Word.class);
			_wordsJugded = (ArrayList<Word>) _db.SelectAll(queryJuged, Word.class);
			
			renderWords();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void renderWords() {
		WordsNeedJugding.getChildren().clear();
		JudgedWords.getChildren().clear();
		
		_wordsNeedJugding.forEach(Word -> {
			var wordItem = new WordItem(Word);
			wordItem.setOnClickEvent(onHandleChallengeClick());
			WordsNeedJugding.getChildren().add(wordItem);
		});
		
		_wordsJugded.forEach(Word -> {
			var wordItem = new WordItem(Word);
			JudgedWords.getChildren().add(wordItem);
		});
		
	}
	
	
	/**
	 * Click handler that handels accept and reject
	 * in the challengeItem
	 * 
	 * @return
	 */
	private Consumer<ActionEvent> onHandleChallengeClick() {
		
		return (event -> {
			
			// get item values
	    	var btnReaction = (Button) event.getSource();
	    	var type = btnReaction.getText();
	    	
	    	if (type.equals(WordItem.acceptText)) {
	    		System.out.println("Accepted Click");
	    	}else if(type.equals(WordItem.rejectText)) {
	    		System.out.println("Reject ClickT");
	    	}
			
			
		});
    }
	
}
