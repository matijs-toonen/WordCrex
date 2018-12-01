package Model;

public class BoardPlayer {
	private Game _game;
	private Account _user;
	private Turn _turn;
	private Letter _letter;
	private Tile _tile;
	
	public BoardPlayer(Game game, Account user, Turn turn, Letter letter, Tile tile) {
		_game = game;
		_user = user;
		_turn = turn;
		_letter = letter;
		_tile = tile;
	}
}
