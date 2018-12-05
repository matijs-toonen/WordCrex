package Model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import Controller.MainController;
import Model.Answer.*;

public class Game {
	public static final String getWinnerQuery(String username) {
		return ("SELECT game_id,username_winner, " +
				"(SELECT IF(username_player1 = '"+username+"', username_player2, username_player1)) AS opponent " +
				"FROM game " +
				"WHERE (username_player1 = '"+username+"' OR username_player2 = '"+username+"') " +
				"AND (username_winner is not null)");
	}
	
	private Integer _gameId;
	private GameStatus _gameStatus;
	private LetterSet _letterSet;
	private String _usernamePlayer1;
	private String _usernamePlayer2;
	private Answer _answerPlayer2;
	private String _opponent;
	private String _winner;
	
	public Game(int gameId) {
		_gameId = gameId;
	}
	
	public Game(ResultSet rs, ArrayList<String> columns) {
		try {
			_gameId = columns.contains("game_id") ? rs.getInt("game_id") : null;
			_gameStatus = columns.contains("game_state") ? GameStatus.getGameStatus(rs.getString("game_state")) : null;
			_letterSet = columns.contains("letterset_code") ? new LetterSet(rs.getString("letterset_code")) : null;
			_usernamePlayer1 = columns.contains("username_player1") ? rs.getString("username_player1") : null;
			_usernamePlayer2 = columns.contains("username_player2") ? rs.getString("username_player2") : null;
			_answerPlayer2 = columns.contains("answer_player2") ? getAnswer(rs.getString("answer_player2")) : null;
			_opponent = columns.contains("opponent") ? rs.getString("opponent"): null;
			_winner = columns.contains("username_winner") ? rs.getString("username_winner") : null;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private Answer getAnswer(String answer) {
		switch(answer.toLowerCase()) {
		case "accepted":
			return new AcceptedAnswer();
		case "rejected":
			return new RejectedAnswer();
		case "unknown":
			return new UnknownAnswer();
		default:
			return null;
		}
	}
	
	public String getUser1() {
		return _usernamePlayer1;
	}
	
	public String getOpponent() {
		return _opponent;
	}
	
	public String getUser2() {
		return _usernamePlayer2;
	}
	
	public GameStatus getSatus() {
		return _gameStatus;
	}
	
	public String getWinner() {
		return _winner;
	}
	
	public static List<Game> hasWinnerWithUsername(List<Game> games, String username){
		return games.stream().filter(game -> game._opponent.toLowerCase().contains(username) || 
				game._winner.toLowerCase().contains(username))
				.collect(Collectors.toList());
	}
	
	public static List<Game> hasGameWithUsername(List<Game> games, String username){

		return games.stream().filter(game -> game._usernamePlayer1.toLowerCase().contains(username) || 
				game._usernamePlayer2.toLowerCase().contains(username))
				.collect(Collectors.toList());
	}
}
