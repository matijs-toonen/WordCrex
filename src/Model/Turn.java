package Model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Turn {
	
	private Integer _turnId;
	private Game _game;
	
	public Turn(int turnId) {
		_turnId = turnId;
	}
	
	public Turn(int turnId, Game game) {
		this(turnId);
		_game = game;
	}
	
	public Turn(ResultSet rs, ArrayList<String> columns) {
		try {
			_turnId = columns.contains("turn_id") ? rs.getInt("turn_id") : null;
			_game = columns.contains("game_id") ? new Game(rs.getInt("game_id")) : null;
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
