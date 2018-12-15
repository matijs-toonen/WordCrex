package Model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.stream.Collectors;


import java.util.List;

import Model.WordState.*;

public class Word {
	
	/*
	 * Props
	 */
	private String _word;
	private LetterSet _letterSet;
	private WordState _state;
	private Account _user;
	
	/*
	 * COnst
	 */
	public Word(String word, LetterSet letterSet, WordState wordState, Account user) {
		_word = word;
		_letterSet = letterSet;
		_state = wordState;
		_user = user;
	}
	
	public Word(ResultSet rs, ArrayList<String> columns) {
		try {
			_word = columns.contains("word") ? rs.getString("word") : null;
			_letterSet = columns.contains("letterset_code") ? new LetterSet(rs.getString("letterset_code")) : null;
			_state = columns.contains("state") ? getWordState(rs.getString("state")) : null;
			_user = columns.contains("username") ? new Account(rs.getString("username")) : null;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*
	 * Getters
	 */
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
	
	public String getWordState() {
		return _state.getState();
	}
	
	public String getWord() {
		return _word;
	}
	
	public static List<Word> getWordsThatContain(ArrayList<Word> words, String search){
		return words.stream().filter(word -> word.getWord().contains(search)).collect(Collectors.toList());
	}
	
	
	/*
	 * Queries
	 */
	public static String insertQuery(String word, String username) {
		return String.format("INSERT INTO dictionary (word, letterset_code, state, username) VALUES ('%s', 'NL', 'pending', '%s');", word, username);
	}
	
	public static String selectQuery(String username) {
		return String.format("SELECT * FROM dictionary WHERE username = '%s'", username);
	}
	
	public static String selectAllQuery() {
		return ("SELECT * FROM dictionary WHERE state = 'pending'");
	}
	
	public static String selectAllJugded() {
		return ("SELECT word, state FROM dictionary WHERE state != 'pending' AND username != 'bookowner'");
	}
	
	public static String UpdateAcceptWord (String word) {
		return ("UPDATE dictionary SET state = 'accepted' WHERE word = '"+word+"'");
	}
	
	public static String UpdateDeniedWord (String word) {
		return ("UPDATE dictionary SET state = 'denied' WHERE word = '"+word+"'");
	}
	
	public static List<Word> getAllWordsThatContain (ArrayList<Word> words, String searchText){
		return words.stream().filter(word -> word.getWord().contains(searchText)).collect(Collectors.toList());
	}
}
