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
	
	public static final String getWinnerQueryObserver() {
		return ("SELECT game_id, username_winner, username_player1, username_player2 " + 
				"FROM game " + 
				"WHERE (username_winner is not null);");
		
	}
	
 	public static final String getAcitveQuery(String username) {
		return ("SELECT " +
				"(SELECT IF(g.username_player1 = '" + username + "', g.username_player2, g.username_player1)) AS 'opponent', " +
				"g.game_id, g.game_state, " +
				"MAX(tp1.turn_id) AS player1_zet, tp1.username_player1 AS username_player1, " +
				"MAX(tp2.turn_id) AS player2_zet, tp2.username_player2 AS username_player2 " +
				"FROM game g " +
				"LEFT JOIN turnplayer1 tp1 ON g.game_id = tp1.game_id " +
				"LEFT JOIN turnplayer2 tp2 ON g.game_id = tp2.game_id " +
				"WHERE (g.username_player1 = '"+username+"' OR g.username_player2 = '"+username+"') " +
				"AND (g.game_state = '" + GameStatus.getGameStatus(GameStatus.Playing) + "')" +
				"GROUP BY g.game_id");
	}
 	
 	public static final String getAcitveQueryObserver() {
		return ("select game_id, game_state, username_player1, username_player2 "
				+ "from game "
				+ "where game_state = 'playing'");
	}
 	
 	public static final String getChallengeQuery(String username) {
 		String statement = String.format(
 				"SELECT game_id,\n" + 
				"       username_player1,\n" + 
 				"       username_player2,\n" + 
 				"       answer_player2\n" + 
 				"FROM game\n" + 
 				"WHERE (username_player2 = '%s' OR username_player1 = '%s')\n" + 
 				"  AND answer_player2 = 'unknown';", username, username);
 		return statement;
 	}
 	
 	public static final String getChallengeAwnserQuery(Integer gameId, String awnser) {
 		return String.format("UPDATE game SET answer_player2 = '%s' WHERE game_id = %d", awnser, gameId);
 	}

 	public static final String getUninvitedUsersQuery(String username) {
 		return String.format(
 				"SELECT *\n" + 
 				"FROM account\n" + 
 				"WHERE username NOT IN (SELECT username_player1 FROM game WHERE username_player2 = '%s' AND game_state <> 'finished')\n" + 
 				"  AND username NOT IN (SELECT username_player2 FROM game WHERE username_player1 = '%s' AND game_state <> 'finished')\n" + 
 				"  AND username <> '%s'"
 				, username, username, username);
 	}
 	
 	public static final String getRequestGameQuery(String usernameFrom, String usernameTo) {
 		return String.format(
 				"INSERT INTO game (game_state, letterset_code, username_player1, username_player2, answer_player2)\n" + 
 				"VALUES ('request', 'NL', '%s', '%s', 'unknown');"
 				, usernameFrom, usernameTo);
 	}
 	
	private Integer _gameId, _zetPlayer1, _zetPlayer2;
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
			_zetPlayer1 = columns.contains("player1_zet") ? rs.getInt("player1_zet") : null;
			_zetPlayer2 = columns.contains("player2_zet") ? rs.getInt("player2_zet") : null;
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
	
	public int getZettenPlayer1(){
		return _zetPlayer1;
	}
	
	public int getZettenPlayer2(){
		return _zetPlayer2;
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
	
	public Answer getAnswer() {
		return _answerPlayer2;
	}
	
	public Integer getGameId() {
		return _gameId;
	}
	
	public GameStatus getSatus() {
		return _gameStatus;
	}
	
	public String getWinner() {
		return _winner;
	}
	
	public static List<Game> hasWinnerWithUsername(List<Game> games, String username){
		return games.stream()
				.filter(game -> game._opponent.toLowerCase().contains(username.toLowerCase()) || 
				game._winner.toLowerCase().contains(username.toLowerCase()))
				.collect(Collectors.toList());
	}
	
	public static List<Game> hasGameWithUsername(List<Game> games, String username){

		return games.stream().filter(game -> game._usernamePlayer1.toLowerCase().contains(username.toLowerCase()) || 
				game._usernamePlayer2.toLowerCase().contains(username.toLowerCase()))
				.collect(Collectors.toList());
	}
}
