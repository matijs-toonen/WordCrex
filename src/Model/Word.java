package Model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import Model.WordState.WordState;

public class Word {
	private String _word;
	private LetterSet _letterSet;
	private WordState _wordState;
	private Account _user;
	
	public Word(String word, LetterSet letterSet, WordState wordState, Account user) {
		_word = word;
		_letterSet = letterSet;
		_wordState = wordState;
		_user = user;
	}
	
	public Word(ResultSet rs, ArrayList<String> columns)
	{
		try {
			_word = columns.contains("word") ? rs.getString("word") : null;
			_letterSet = columns.contains("letterset_code") ? new LetterSet(rs.getString("letterset_code")) : null;
			_wordState = columns.contains("state") ? new WordState(rs.getString("state")) : null;
			_user = columns.contains("username") ? new Account(rs.getString("username")) : null;
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getWord()
	{
		return _word;
	}
	
	public WordState getWordState()
	{
		return _wordState;
	}
}
