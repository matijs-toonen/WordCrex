package Model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Symbol {
	
	/*
	 * Props
	 */
	private char _symbol;
	private LetterSet _letterSet;
	private Integer _value;
	private Integer _counted;
	
	/*
	 * Const
	 */
	public Symbol(char symbol) {
		_symbol = symbol;
	}
	
	public Symbol(char symbol, int value)
	{
		_symbol = symbol;
		_value = value;
	}
	
	public Symbol(char symbol, LetterSet letterSet, int value, int counted) {
		this(symbol);
		_letterSet = letterSet;
		_value = value;
		_counted = counted;
	}
	
	public Symbol(ResultSet rs, ArrayList<String> columns) {
		try {
			_symbol = columns.contains("symbol") ? rs.getString("symbol").charAt(0) : null;
			_letterSet = columns.contains("letterset_code") ? new LetterSet(rs.getString("letterset_code")) : null;
			_value = columns.contains("value") ? rs.getInt("value") : null;
			_counted = columns.contains("counted") ? rs.getInt("counted") : null;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public char getChar() {
		return _symbol;
	}
	
	public int getValue() {
		return _value != null ? _value : 0; 
	}
	
	public int getAmount() {
		return _counted;
	}
}
