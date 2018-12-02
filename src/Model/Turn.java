package Model;

public class Turn {
	private int _turnId;
	private Game _game;
	
	public Turn(int turnId) {
		_turnId = turnId;
	}
	
	public Turn(int turnId, Game game) {
		this(turnId);
		_game = game;
	}
}
