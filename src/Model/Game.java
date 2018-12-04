package Model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import Model.Answer.*;

public class Game {
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
			_opponent = _usernamePlayer2;
			_winner = _usernamePlayer1;
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
	
	public String getUser2() {
		return _usernamePlayer2;
	}
	
	public GameStatus getSatus() {
		return _gameStatus;
	}
	
	public static List<Game> hasGameWithUsername(List<Game> games, String username){

		return games.stream().filter(game -> game._usernamePlayer1.toLowerCase().contains(username) || 
				game._usernamePlayer2.toLowerCase().contains(username))
				.collect(Collectors.toList());
	}
}
