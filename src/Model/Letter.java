package Model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Letter {
	private Integer _letterId;
	private Game _game;
	private LetterSet _letterSet;
	private Symbol _symbol;
	
	public Letter(int letterId) {
		_letterId = letterId;
	}
	
	public Letter(int letterId, Game game, LetterSet letterSet, Symbol symbol) {
		this(letterId);
		_game = game;
		_letterSet = letterSet;
		_symbol = symbol;
	}
	
	public Letter(ResultSet rs, ArrayList<String> columns) {
		try {
			_letterId = columns.contains("letter_id") ? rs.getInt("letter_id") : null;
			_game = columns.contains("game_id") ? new Game(rs.getInt("game_id")) : null;
			_letterSet = columns.contains("symbol_letterset_code") ? new LetterSet(rs.getString("symbol_letterset_code")) : null;
			_symbol = columns.contains("symbol") ? new Symbol(rs, columns) : null;
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public Game getGame() {
		return _game;
	}
	
	public Symbol getSymbol() {
		return _symbol;
	}
	
	public int getLetterId() {
		return _letterId;
	}
	
	
	public static final String getLetterQuery(int gameId, int turnId, int letterId) {		
		return "INSERT INTO handletter VALUES (" + gameId + ", " + turnId + ", " + letterId + ")"; 
	}
	
	public static final String getUnusedLetters(int gameId) {
		return ("SELECT * FROM letter NATURAL JOIN symbol WHERE game_id = "+ gameId + " AND letter_id NOT IN (SELECT letter_id FROM handletter WHERE game_id = " + gameId + ")");
	}
}
