package Model;

public class Letter {
	private int _letterId;
	private Game _game;
	private LetterSet _letterSet;
	private Symbol _symbol;
	
	public Letter(int letterId, Game game, LetterSet letterSet, Symbol symbol) {
		_letterId = letterId;
		_game = game;
		_letterSet = letterSet;
		_symbol = symbol;
	}
}
