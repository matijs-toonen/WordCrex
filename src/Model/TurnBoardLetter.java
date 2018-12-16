package Model;

import java.awt.Point;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class TurnBoardLetter {
	
	/*
	 * Props
	 */
	private Letter _letter;
	private Game _game;
	private Turn _turn;
	private Tile _tile;
	
	
	/*
	 * Const
	 */
	public TurnBoardLetter(Letter letter, Game game, Turn turn, Tile tile) {
		_letter = letter;
		_game = game;
		_turn = turn;
		_tile = tile;
	}
	
	public TurnBoardLetter(ResultSet rs, ArrayList<String> columns) {
		try {
			_letter = columns.contains("letter_id") ? new Letter(rs, columns) : null;
			_game = columns.contains("game_id") ? new Game(rs, columns) : null;
			_turn = columns.contains("turn_id") ? new Turn(rs, columns) : null;
			_tile = columns.contains("tile_x") && columns.contains("tile_y") ? new Tile(rs.getInt("tile_x"), rs.getInt("tile_y")) : null;
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public Point getTileCords() {
		return new Point(_tile.getX(), _tile.getY());
	}
	
	public Symbol getSymbol() {
		return _letter.getSymbol();
	}
	
	public int getLetterId() {
		return _letter.getLetterId();
	}
}
