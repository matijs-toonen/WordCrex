package Tests;

import Model.Symbol;
import Model.Tile;
import Model.TileType;
import javafx.util.Pair;

public class BoardTileTest {
	private Symbol _symbol;
	private TileType _type;
	private int _column;
	private int _row;
	
	public BoardTileTest(Symbol symbol) {
		_symbol = symbol;
	}
	
	public BoardTileTest(int column, int row, Symbol symbol) {
		this(symbol);
		_column = column;
		_row = row;
	}
	
	public BoardTileTest(int column, int row, TileType type)
	{
		this(column, row);
		_type = type;
	}
	
	public BoardTileTest(int column, int row) {
		this(column, row, (Symbol)null);
	}
	
	public BoardTileTest(Tile tile)
	{
		// Index 0
		this(tile.getX()-1, tile.getY()-1, tile.getType());
	}
	
	public Symbol getSymbol() {
		return _symbol;
	}
	
	public Character getSymbolAsChar() {
		
		if(_symbol != null)
			return String.valueOf(_symbol.getChar()).toCharArray()[0];
		else
			return ' ';
	}
	
	public int getBonusValue()
	{
		return _type.getValue();
	}
	
	public char getBonusLetter()
	{
		return _type.getLetter();
	}
	
	public Pair<Integer, Integer> getCords(){
		return new Pair<Integer, Integer>(_column, _row);
	}
	
	
	public void setSymbol(Symbol symbol) {
		_symbol = symbol;
	}	
}
