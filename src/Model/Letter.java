package Model;

public class Letter {
	private int _letterId;
	private Game _game;
	private LetterSet _letterSet;
	private char _symbol;
	
	public Letter(int letterId, Game game, LetterSet letterSet, char symbol) {
		_letterId = letterId;
		_game = game;
		_letterSet = letterSet;
		_symbol = symbol;
	}
}
