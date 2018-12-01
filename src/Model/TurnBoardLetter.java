package Model;

public class TurnBoardLetter {
	private Letter _letter;
	private Game _game;
	private Turn _turn;
	private Tile _tile;
	
	public TurnBoardLetter(Letter letter, Game game, Turn turn, Tile tile) {
		_letter = letter;
		_game = game;
		_turn = turn;
		_tile = tile;
	}
}
