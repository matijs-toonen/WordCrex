package Model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import Model.WordState.*;

public class Word {
	private String _word;
	private LetterSet _letterSet;
	private WordState _state;
	private Account _user;
	
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
