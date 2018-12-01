package Model;

import Model.TurnAction.TurnAction;

public class TurnPlayer {
	private Game _game;
	private Turn _turn;
	private Account _user;
	private int _bonus;
	private int _score;
	private TurnAction _turnAction;
	
	public TurnPlayer(Game game, Turn turn, Account user, int bonus, int score, TurnAction turnAction) {
		_game = game;
		_turn = turn;
		_user = user;
		_bonus = bonus;
		_score = score;
		_turnAction = turnAction;
	}
}
