package Model;

public class Turn {
	private int _turnId;
	private Game _gameId;
	
	public Turn(Game gameId, int turnId) {
		_gameId = gameId;
		_turnId = turnId;
	}
}
