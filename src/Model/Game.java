package Model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import Controller.MainController;
import Model.Answer.*;

public class Game {
 	/*
 	 * Props
 	 */
	private Integer _gameId, _zetPlayer1, _zetPlayer2;
	private GameStatus _gameStatus;
	private LetterSet _letterSet;
	private String _usernamePlayer1;
	private String _usernamePlayer2;
	private Answer _answerPlayer2;
	private String _opponent;
	private String _winner;
	
	
	/*
	 * Const
	 */
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
	
	/*
	 * Getters
	 */
	
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
	
	
	/*
	 * Filters
	 */
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
	
	
	/*
	 * Queries
	 */
	public static final String getWinnerQuery(String username) {
		return ("SELECT game_id,username_winner, " +
				"(SELECT IF(username_player1 = '"+username+"', username_player2, username_player1)) AS opponent " +
				"FROM game " +
				"WHERE (username_player1 = '"+username+"' OR username_player2 = '"+username+"') " +
				"AND (username_winner is not null)");
	}
	
 	public static final String getActiveQuery(String username) {
 		return String.format(
 				"SELECT (SELECT IF(g.username_player1 = '%s', g.username_player2, g.username_player1)) AS 'opponent',\n" + 
 				"       g.game_id,\n" + 
 				"       g.game_state,\n" + 
 				"       IFNULL(MAX(tp1.turn_id), 0)                                                       AS player1_zet,\n" + 
 				"       g.username_player1                                                                AS username_player1,\n" + 
 				"       IFNULL(MAX(tp2.turn_id), 0)                                                       AS player2_zet,\n" + 
 				"       g.username_player2                                                                AS username_player2\n" + 
 				"FROM game g\n" + 
 				"       LEFT JOIN turnplayer1 tp1 ON g.game_id = tp1.game_id\n" + 
 				"       LEFT JOIN turnplayer2 tp2 ON g.game_id = tp2.game_id\n" + 
 				"WHERE (g.username_player1 = '%s' OR g.username_player2 = '%s')\n" + 
 				"  AND (g.game_state = '%s')\n" + 
 				"GROUP BY g.game_id;", username, username, username, GameStatus.getGameStatus(GameStatus.Playing));
 	}
 	
 	public static final String getTurnFromActiveGame(int gameId) {
 		return ("SELECT IF(MAX(turn_id) IS NULL, 1, MAX(turn_id)) " + 
 				"FROM handletter " + 
 				"WHERE game_id = " + gameId);
 	}
 	
 	public static final String getExistingTiles(int gameId, int turnId) {
 		return ("SELECT * FROM turnboardletter NATURAL JOIN letter NATURAL JOIN symbol WHERE game_id = " + gameId + " AND turn_id < " + turnId);
 	}
 	
 	public static final String getPlayedTiles(int gameId, int turnId, String table) {
 		return ("SELECT * FROM " + table + " NATURAL JOIN letter NATURAL JOIN symbol WHERE game_id = " + gameId + " AND turn_id = " + turnId);
 	}
 	
 	public static final String getExisitingHandLetters(int gameId, int turnId) {
 		return("SELECT * FROM handletter NATURAL JOIN letter NATURAL JOIN symbol where game_id = " + gameId + " AND turn_id = " + turnId);
 	}
 	
 	public static final String getActiveQueryObserver() {
		return ("select game_id, game_state, username_player1, username_player2 "
				+ "from game "
				+ "where game_state = 'playing'");
	}
 	
	public static final String getWinnerQueryObserver() {
		return ("SELECT game_id, username_winner, username_player1, username_player2 " + 
				"FROM game " + 
				"WHERE (username_winner is not null);");
		
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
 		if (awnser.equals("accepted")) {
 			return String.format("UPDATE game SET answer_player2 = 'accepted', game_state = 'playing' WHERE game_id = %d", gameId);
 		}else if (awnser.equals("rejected")) {
 			return String.format("UPDATE game SET answer_player2 = 'rejected', game_state = 'finished' WHERE game_id = %d", gameId);
 		}else {
 			return "";
 		}
 		
 	}

 	public static final String getUninvitedUsersQuery(String username) {
 		return String.format(
 				"SELECT *\n" + 
 				"FROM account\n" + 
 				"WHERE username NOT IN (SELECT username_player1 FROM game WHERE username_player2 = '%s' AND (game_state = 'request' OR game_state = 'playing'))\n" + 
 				"  AND username NOT IN (SELECT username_player2 FROM game WHERE username_player1 = '%s' AND (game_state = 'request' OR game_state = 'playing'))\n" + 
 				"  AND username <> '%s'"
 				, username, username, username);
 	}
 	
 	public static final String getRequestGameQuery(String usernameFrom, String usernameTo) {
 		return String.format(
 				"INSERT INTO game (game_state, letterset_code, username_player1, username_player2, answer_player2)\n" + 
 				"VALUES ('request', 'NL', '%s', '%s', 'unknown');"
 				, usernameFrom, usernameTo);
 	}
 	
 	public static final String getResignQuery(int gameId, String opponent) {
 		return ("UPDATE game " + 
 				"SET " + 
 				"game_state = 'resigned', username_winner = '" + opponent + "' " + 
 				"WHERE " + 
 				"game_id = " + gameId);
 	}
 	
 	public static final String getNewTurnQuery(int gameId) {
 		return String.format(
 				"INSERT INTO turn (turn.game_id, turn.turn_id)\n" + 
 				"VALUES (%d, (SELECT IFNULL(max(t.turn_id), 0) + 1 FROM turn AS t WHERE t.game_id = %d));", gameId, gameId);
 	}
 	
 	public static final String getPassQuery(int gameId, int turnId, String username, boolean isFirstPlayer) {
 		String tableName = isFirstPlayer ? "turnplayer1" : "turnplayer2";
 		String columnName = isFirstPlayer ? "username_player1" : "username_player2";
 		
 		return String.format(
 				"INSERT INTO %s\n" + 
 				"  (game_id, turn_id, %s, bonus, score, turnaction_type)\n" + 
 				"VALUES ('%d', '%d', '%s', '0', '0', 'pass')", tableName, columnName, gameId, turnId, username);
 	}
}
