package Controller;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import Model.Game;
import Model.Word;
import View.Items.ChallengeItem;
import View.Items.ChallengePlayerItem;
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
		LoadWords();

		searchBox.textProperty().addListener((observable) -> {
			renderWords();
		});
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
		
		Word.getWordsThatContain(_wordsNeedJugding, searchBox.getText()).forEach(Word -> {
			var wordItem = new WordItem(Word);
			wordItem.setOnClickEvent(onHandleJudgeClick());
			WordsNeedJugding.getChildren().add(wordItem);
		});
		
		Word.getWordsThatContain(_wordsJugded, searchBox.getText()).forEach(Word -> {
			var wordItem = new WordItem(Word);
			JudgedWords.getChildren().add(wordItem);
		});
		
	}
	
	private Consumer<ActionEvent> onHandleJudgeClick() {
		
		return (event -> {
			
			// get item values
	    	var btnReaction = (Button) event.getSource();
	    	var type = btnReaction.getText();
	    	var wordItem = (WordItem) btnReaction.getParent();
	    	String word = wordItem.getWord().getWord(); 

	    	if (type.equals(WordItem.acceptText)) {
	    		String query = Word.UpdateAcceptWord(word);
	    		
	    		try {
	    			if(_db.Update(query))
						LoadWords();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    	}else if(type.equals(WordItem.rejectText)) {
	    		String query = Word.UpdateDeniedWord(word);
	    		
	    		try {
					if(_db.Update(query))
						LoadWords();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    	}
		});
    }
	
}
