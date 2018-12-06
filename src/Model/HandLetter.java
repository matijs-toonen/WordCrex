package Model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;

public class HandLetter {
	private Game _game;
	private Turn _turn;
	private ArrayList<Letter> _letters = new ArrayList<Letter>();
	
	public HandLetter(Game game, Turn turn, Letter... letters) {
		_game = game;
		_turn = turn;
		addLetters(letters);
	}
	
	public HandLetter(ResultSet rs, ArrayList<String> columns) {
		try {
			_game = columns.contains("game_id") ? new Game(rs.getInt("game_id")) : null;
			_turn = columns.contains("turn_id") ? new Turn(rs.getInt("turn_id")) : null;
			_letters.add(columns.contains("letter_id") ? new Letter(rs.getInt("letter_id")) : null);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public int getGameId() {
		return _game.getGameId();
	}
	
	public int getTurnId() {
		return _turn.getTurnId();
	}
	
	public void addLetters(Letter... letters) {
		for(var letter : letters) 
			_letters.add(letter);
	}
	
	public static Optional<HandLetter> getHandByGameAndTurn(ArrayList<HandLetter> handLetters, int gameId, int turnId){
		return handLetters.stream().filter(handLetter -> handLetter.getGameId() == gameId && handLetter.getTurnId() == turnId).findFirst();
	}
}
