package Model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import Controller.MainController;

public class Score {
	
	/*
	 * Props
	 */
	private int _gameId;
	private String _username1;
	private int _score1;
	private int _bonus1;
	private String _username2;
	private int _score2;
	private int _bonus2;
	
	/*
	 * Const
	 */
	public Score(int gameId, String username1, int scorePlayer1, int bonusPlayer1, String username2, int scorePlayer2, int bonusPlayer2) {
		_gameId = gameId;
		_username1 = username1;
		_score1 = scorePlayer1;
		_bonus1 = bonusPlayer1;
		_username2 = username2;
		_score2 = scorePlayer2;
		_bonus2 = bonusPlayer2;
	}
	
	public Score(ResultSet rs, ArrayList<String> columns) {
		try {
			_gameId = columns.contains("game_id") ? rs.getInt("game_id") : null;
			_username1 = columns.contains("username_player1") ? rs.getString("username_player1") : null;
			_score1 = columns.contains("score1") ? rs.getInt("score1") : null;
			_bonus1 = columns.contains("bonus1") ? rs.getInt("bonus1") : null;
			_username2 = columns.contains("username_player2") ? rs.getString("username_player2") : null;
			_score2 = columns.contains("score2") ? rs.getInt("score2") : null;
			_bonus2 = columns.contains("bonus2") ? rs.getInt("bonus2") : null;
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static final String getScoreFromGameQuery(int gameId) {
		return String.format("SELECT * FROM score WHERE game_id = %d", gameId);
	}
	
	public String getUser1() {
		return _username1;
	}
	
	public String getUser2() {
		return _username2;
	}
	
	public String getOpponent() {
		return MainController.getUser().getUsername().equals(_username1) ? _username2 : _username1;
	}
	
	public int getOwnScore() {
		String myUsername = MainController.getUser().getUsername();
		
		if (_username1.equals(myUsername)) {
			return _score1 + _bonus1;
		}else {
			return _score2 + _bonus2;
		}
	}
	
	public int getOpponentScore() {
		String myUsername = MainController.getUser().getUsername();
		
		if (_username1.equals(myUsername)) {
			return _score2 + _bonus2;
		}else {
			return _score1 + _bonus1;
		}
	}
}
