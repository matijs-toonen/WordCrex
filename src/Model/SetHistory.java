package Model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class SetHistory {
	
	private Game _game;
	private int _turn_id;
	private String _player1;
	private int _score1;
	private String _woorddeel1;
	private String _player2;
	private int _score2;
	private String _woorddeel2;
	
	public static final String getQuery(int gameId) {
		String query = "SELECT\r\n" + 
				"    tp1.game_id,\r\n" + 
				"    tp1.turn_id,\r\n" + 
				"    tp1.username_player1 as player1,\r\n" + 
				"    tp1.score + tp1.bonus AS score1,\r\n" + 
				"    gp1.woorddeel as woorddeel1,\r\n" + 
				"    tp2.username_player2 as player2,\r\n" + 
				"    tp2.score + tp2.bonus AS score2,\r\n" + 
				"    gp2.woorddeel as woorddeel2\r\n" + 
				"FROM\r\n" + 
				"    turnplayer1 AS tp1\r\n" + 
				"        INNER JOIN\r\n" + 
				"    gelegdplayer1 AS gp1 ON tp1.game_id = gp1.game_id\r\n" + 
				"        AND tp1.turn_id = gp1.turn_id\r\n" + 
				"        INNER JOIN\r\n" + 
				"    turnplayer2 AS tp2 ON tp1.game_id = tp2.game_id\r\n" + 
				"        AND tp1.turn_id = tp2.turn_id\r\n" + 
				"        INNER JOIN\r\n" + 
				"    gelegdplayer2 AS gp2 ON tp2.game_id = gp2.game_id\r\n" + 
				"        AND tp2.turn_id = gp2.turn_id\r\n" + 
				"WHERE tp1.game_id = " + gameId + ";";
		return query;
	}
	
	public SetHistory(int game_id, int turn_id, String player1, int score1, String woorddeel1, String player2, int score2, String woorddeel2) {
		_game = new Game(game_id);
		_turn_id = turn_id;
		_player1 = player1;
		_score1 = score1;
		_woorddeel1 = woorddeel1;
		_player2 = player2;
		_score2 = score2;
		_woorddeel2 = woorddeel2;
	}
	
	public SetHistory(ResultSet rs, ArrayList<String> columns) {
		try {
			_game = columns.contains("game_id") ? new Game(rs.getInt("game_id")) : null;
			_turn_id = columns.contains("turn_id") ? rs.getInt("turn_id") : null;
			_player1 = columns.contains("player1") ? rs.getString("player1") : null;
			_score1 = columns.contains("score1") ? rs.getInt("score1") : null;
			_woorddeel1 = columns.contains("woorddeel1") ? rs.getString("woorddeel1") : null;
			_player2 = columns.contains("player2") ? rs.getString("player2") : null;
			_score2 = columns.contains("score2") ? rs.getInt("score2") : null;
			_woorddeel2 = columns.contains("woorddeel2") ? rs.getString("woorddeel2") : null;
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public int getTurnID() {
		return _turn_id;
	}
	
	public String getPlayer1() {
		return _player1;
	}
	
	public String getPlayer2() {
		return _player2;
	}
	
	public int getScore1() {
		return _score1;
	}
	
	public int getScore2() {
		return _score2;
	}
	
	public String getWoord1() {
		return _woorddeel1.replace(",", "").replace(" ", "");
	}
	
	public String getWoord2() {
		return _woorddeel2.replace(",", "").replace(" ", "");
	}
}
