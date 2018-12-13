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
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

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
			String statement = Word.insertQuery(wordValue, MainController.getUser().getUsername());
			System.out.println(statement);
			_db.Insert(statement);
		} catch (SQLException e) {
			System.err.println(e);
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
			
			GridPane contentGrid = new GridPane();
		
			Label wordState = new Label();
			
			wordState.setPrefWidth(150);
			wordState.setAlignment(Pos.CENTER);
			
			switch (word.getWordState()) {
			case "accepted":
				wordState.setText("Goedgekeurd");
				wordState.setStyle("-fx-padding: 5 0; -fx-background-color: #E0F9EC; -fx-background-radius: 25; -fx-font-size: 15px; -fx-font-family: 'Source Sans Pro'");
				break;
			case "pending":
				wordState.setText("In beoordeling");
				wordState.setStyle("-fx-padding: 5 0; -fx-background-color: #b3b3b3; -fx-background-radius: 25; -fx-font-size: 15px; -fx-font-family: 'Source Sans Pro'");
				break;
			case "denied":
				wordState.setText("Afgekeurd");
				wordState.setStyle("-fx-padding: 5 0; -fx-background-color: #FFE2E6; -fx-background-radius: 25; -fx-font-size: 15px; -fx-font-family: 'Source Sans Pro'");
				break;
			default:
				break;
			}
			
			Label wordLabel = new Label();
			wordLabel.setText(word.getWord());
			wordLabel.setStyle("-fx-padding: 30 20; -fx-font-size: 15px; -fx-font-family: 'Source Sans Pro';"); 
			
			wordBox.getChildren().addAll(wordLabel, wordState);
			
			contentGrid.add(wordLabel, 0, 0);
			contentGrid.add(wordState, 1, 0);
			
			contentGrid.setHalignment(wordState, HPos.RIGHT);
			
			ColumnConstraints cc = new ColumnConstraints();
			 
			cc.setPercentWidth(50);
			cc.setHgrow(Priority.ALWAYS);
			contentGrid.getColumnConstraints().add(cc);
			contentGrid.getColumnConstraints().add(cc);
			
			wordList.getChildren().add(contentGrid);
		});
		
	}
	
}
