package Model;

public class Symbol {
	private char _symbol;
	private LetterSet _letterSet;
	private int _value;
	private int _counted;
	
	public Symbol(char symbol) {
		_symbol = symbol;
	}
	
	public Symbol(char symbol, LetterSet letterSet, int value, int counted) {
		this(symbol);
		_letterSet = letterSet;
		_value = value;
		_counted = counted;
	}
}
