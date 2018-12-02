package Model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class BoardPlayer {
	private Game _game;
	private Account _user;
	private Turn _turn;
	private Letter _letter;
	private Tile _tile;
	
	public BoardPlayer(Game game, Account user, Turn turn, Letter letter, Tile tile) {
		_game = game;
		_user = user;
		_turn = turn;
		_letter = letter;
		_tile = tile;
	}
	
	public BoardPlayer(ResultSet rs, ArrayList<String> columns) {
		try {
			_game = columns.contains("game_id") ? new Game(rs.getInt("game_id")) : null;
			_user = columns.contains("username") ? new Account(rs.getString("username")) : null;
			_turn = columns.contains("turn_id") ? new Turn(rs.getInt("turn_id")) : null;
			_letter = columns.contains("letter_id") ? new Letter(rs.getInt("letter_id")) : null;
			_tile = columns.contains("tile_x") && columns.contains("tile_y") ? new Tile(rs.getInt("tile_x"), rs.getInt("tile_y")) : null;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
