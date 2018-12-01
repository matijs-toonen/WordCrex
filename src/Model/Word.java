package Model;

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
}
