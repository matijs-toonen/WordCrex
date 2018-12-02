package Model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class TurnBoardLetter {
	private Letter _letter;
	private Game _game;
	private Turn _turn;
	private Tile _tile;
	
	public TurnBoardLetter(Letter letter, Game game, Turn turn, Tile tile) {
		_letter = letter;
		_game = game;
		_turn = turn;
		_tile = tile;
	}
	
	public TurnBoardLetter(ResultSet rs, ArrayList<String> columns) {
		try {
			_letter = columns.contains("letter_id") ? new Letter(rs.getInt("letter_id")) : null;
			_game = columns.contains("game_id") ? new Game(rs.getInt("game_id")) : null;
			_turn = columns.contains("turn_id") ? new Turn(rs.getInt("turn_id")) : null;
			_tile = columns.contains("tile_x") && columns.contains("tile_y") ? new Tile(rs.getInt("tile_x"), rs.getInt("tile_y")) : null;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
