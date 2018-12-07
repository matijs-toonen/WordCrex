package Model;

import java.sql.ResultSet;
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
		_game = columns.contains("game_id") ? new Game(rs, columns) : null;
		_turn = columns.contains("turn_id") ? new Turn(rs, columns) : null;
		_letters.add(columns.contains("letter_id") ? new Letter(rs, columns) : null);
	}
	
	public int getGameId() {
		return _game.getGameId();
	}
	
	public int getTurnId() {
		return _turn.getTurnId();
	}
	
	public ArrayList<Letter> getLetters() {
		return _letters;
	}
	
	public void addLetters(Letter... letters) {
		for(var letter : letters) 
			_letters.add(letter);
	}
	
	public static Optional<HandLetter> getHandByGameAndTurn(ArrayList<HandLetter> handLetters, int gameId, int turnId){
		return handLetters.stream().filter(handLetter -> handLetter.getGameId() == gameId && handLetter.getTurnId() == turnId).findFirst();
	}
}
