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
	private String _inhoud;
	
	public static final String getQuery(int gameId) {
		
		return "SELECT \n" + 
				"    tp1.game_id,\n" + 
				"    tp1.turn_id,\n" + 
				"    tp1.username_player1 AS player1,\n" + 
				"    tp1.score + tp1.bonus AS score1,\n" + 
				"    gp1.woorddeel AS woorddeel1,\n" + 
				"    tp2.username_player2 AS player2,\n" + 
				"    tp2.score + tp2.bonus AS score2,\n" + 
				"    gp2.woorddeel AS woorddeel2,\n" + 
				"    H.inhoud\n" + 
				"FROM\n" + 
				"    turnplayer1 AS tp1\n" + 
				"        left JOIN\n" + 
				"    gelegdplayer1 AS gp1 ON tp1.game_id = gp1.game_id\n" + 
				"        AND tp1.turn_id = gp1.turn_id\n" + 
				"        left JOIN\n" + 
				"    turnplayer2 AS tp2 ON tp1.game_id = tp2.game_id\n" + 
				"        AND tp1.turn_id = tp2.turn_id\n" + 
				"        left JOIN\n" + 
				"    gelegdplayer2 AS gp2 ON tp2.game_id = gp2.game_id\n" + 
				"        AND tp2.turn_id = gp2.turn_id\n" + 
				"        left join hand as H\n" + 
				"        on H.game_id =  tp1.game_id\n" + 
				"        and H.turn_id = tp1.turn_id\n" + 
				"WHERE tp1.game_id = " + gameId + ";";
	}
	
	public SetHistory(int game_id, int turn_id, String player1, int score1, String woorddeel1, String player2, int score2, String woorddeel2, String inhoud) {
		_game = new Game(game_id);
		_turn_id = turn_id;
		_player1 = player1;
		_score1 = score1;
		_woorddeel1 = woorddeel1;
		_player2 = player2;
		_score2 = score2;
		_woorddeel2 = woorddeel2;
		_inhoud = inhoud;
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
			_inhoud = columns.contains("inhoud") ? rs.getString("inhoud") : null;
			
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
		if (_woorddeel1 == null) {
			return null;
		}
		return _woorddeel1.replace(",", "").replace(" ", "");
	}
	
	public String getWoord2() {
		if (_woorddeel2 == null) {
			return null;
		}
		return _woorddeel2.replace(",", "").replace(" ", "");
	}
	
	public String getInhoud() {
		return _inhoud;
	}
}
