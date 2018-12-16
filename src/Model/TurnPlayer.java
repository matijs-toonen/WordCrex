package Model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import Model.TurnAction.*;

public class TurnPlayer {
	
	/*
	 * Props
	 */
	private Game _game;
	private Turn _turn;
	private Account _user;
	private Integer _bonus;
	private Integer _score;
	private TurnAction _turnAction;
	
	
	/*
	 * Const
	 */
	public TurnPlayer(Game game, Turn turn, Account user, int bonus, int score, TurnAction turnAction) {
		_game = game;
		_turn = turn;
		_user = user;
		_bonus = bonus;
		_score = score;
		_turnAction = turnAction;
	}
	
	public TurnPlayer(ResultSet rs, ArrayList<String> columns) {
		try {
			_game = columns.contains("game_id") ? new Game(rs.getInt("game_id")) : null;
			_turn = columns.contains("turn_id") ? new Turn(rs.getInt("turn_id")) : null;
			var playerNumber = checkPlayerNumber(columns);
			_user = playerNumber != null ? new Account(rs.getString("username_player" + playerNumber)) : null;
			_bonus = columns.contains("bonus") ? rs.getInt("bonus") : null;
			_score = columns.contains("score") ? rs.getInt("score") : null;
			_turnAction = columns.contains("turnaction_type") ? getTurnAction(rs.getString("turnaction_type")) : null;
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Getters
	 */
	private Integer checkPlayerNumber(ArrayList<String> columns) {
		if(columns.contains("username_player1"))
			return 1;
		else if(columns.contains("username_player2"))
			return 2;
		else
			return null;
	}
	
	private TurnAction getTurnAction(String turnAction) {
		switch(turnAction.toLowerCase()) {
		case "play":
			return new PlayTurnAction();
		case "pass":
			return new PassTurnAction();
		case "resign":
			return new ResignTurnAction();
		default:
			return null;
		}
	}
	
	public static final String hasPlacedTurn(String table, int turnId, int gameId) {
		return ("SELECT COUNT(*) FROM " + table + "  WHERE turn_id = " + turnId + " AND game_id = " + gameId);
	}
	
	public static final String insertPlayer(String table, int gameId, int turnId, String username, int bonus, int score, String type) {
		String whereUser = "username";
		if(table.equals("turnplayer2"))
			whereUser += "_player2";
		else
			whereUser += "_player1";
		
		return ("INSERT INTO " + table
				+ " (`game_id`, `turn_id`, " + whereUser + ", `bonus`, `score`, `turnaction_type`)"
				+ " VALUES (" + gameId + ", "+ turnId + ", '" + username + "', "+ bonus + ", "+ score + ", '" + type + "');");
	}
}
