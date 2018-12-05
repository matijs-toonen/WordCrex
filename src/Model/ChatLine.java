package Model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

public class ChatLine {
	private String _username;
	private Game _game;
	private Date _moment;
	private String _message;
	
	public ChatLine(String username, int game_id, Date moment, String message) {
		_username = username;
		_game = new Game(game_id);
		_moment = moment;
		_message = message;
	}
	
	public ChatLine(ResultSet rs, ArrayList<String> columns) {
		try {
			_username = columns.contains("username") ? rs.getString("username") : null;
			_game = columns.contains("game_id") ? new Game(rs.getInt("game_id")) : null;
			_moment = columns.contains("moment") ? rs.getTimestamp("moment") : null;
			_message = columns.contains("message") ? rs.getString("message") : null;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getMessage() {
		return _message;
	}
}
