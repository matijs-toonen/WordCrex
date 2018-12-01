package Model;

public class HandLetter {
	private Game _game;
	private Turn _turn;
	private Letter _letter;
	
	public HandLetter(Game game, Turn turn, Letter letter) {
		_game = game;
		_turn = turn;
		_letter = letter;
	}
}
