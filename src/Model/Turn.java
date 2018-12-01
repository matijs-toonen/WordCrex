package Model;

public class Turn {
	private int _turnId;
	private Game _game;
	
	public Turn(Game game, int turnId) {
		_game = game;
		_turnId = turnId;
	}
}
