package Model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ChatLine {
	
	public static final String getQuery(int gameId) {
		return String.format("SELECT * FROM chatline WHERE game_id = '%d' ORDER BY moment", gameId);
	}
	
	public static final String insertQuery(String username, int gameId, String message) {
		return String.format("INSERT INTO chatline (username, game_id, moment, message) VALUES ('%s', '%d', NOW(), '%s');", username, gameId, message);
	}
	
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
	
	public String getUsername() {
		return _username;
	}
	
	public String getTime() {
		SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss");
        return DATE_FORMAT.format(_moment);
	}
}
