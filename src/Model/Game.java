package Model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Game {
	public Integer gameId;
	public GameStatus gameStatus;
	public LetterSet letterSetCode;
	public String usernamePlayer1;
	public String usernamePlayer2;
	public Answer answerPlayer2;
	
	public Game(ResultSet rs, ArrayList<String> columns) {
		try {
			gameId = columns.contains("game_id") ? rs.getInt("game_id") : null;
			gameStatus = columns.contains("game_state") ? GameStatus.getGameStatus(rs.getString("game_state")) : null;
			letterSetCode = columns.contains("letterset_code") ? new LetterSet(rs.getString("letterset_code")) : null;
			usernamePlayer1 = columns.contains("username_player1") ? rs.getString("username_player1") : null;
			usernamePlayer2 = columns.contains("username_player2") ? rs.getString("username_player2") : null;
			answerPlayer2 = columns.contains("answer_player2") ? Answer.getAnswer(rs.getString("answer_player2")) : null;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static List<Game> hasGameWithUsername(List<Game> games, String username){
		return games.stream().filter(game -> game.usernamePlayer1.toLowerCase().contains(username) || 
				game.usernamePlayer2.toLowerCase().contains(username))
				.collect(Collectors.toList());
	}
}
