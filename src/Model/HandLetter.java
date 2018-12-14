package Model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class HandLetter {
	
	/*
	 * Props
	 */
	private Game _game;
	private Turn _turn;
	private Letter _letter;
	
	
	/*
	 * Const
	 */
	public HandLetter(Game game, Turn turn, Letter letter) {
		_game = game;
		_turn = turn;
		_letter = letter;
	}
	
	public HandLetter(ResultSet rs, ArrayList<String> columns) {
		try {
			_game = columns.contains("game_id") ? new Game(rs.getInt("game_id")) : null;
			_turn = columns.contains("turn_id") ? new Turn(rs.getInt("turn_id")) : null;
			_letter = columns.contains("letter_id") ? new Letter(rs.getInt("letter_id")) : null;
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
