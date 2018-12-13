package Model;

public enum GameStatus {
	Requested,
	Playing,
	Finished,
	Resigned;
	
	public static GameStatus getGameStatus(String state) {
		switch(state.toLowerCase()) {
		case "request":
			return Requested;
		case "playing":
			return Playing;
		case "finished":
			return Finished;
		case "resigned":
			return Resigned;
		default:
			return null;
		}
	}
	
	public static String getGameStatus(GameStatus state) {
		switch(state) {
		case Requested:
			return "request";
		case Playing:
			return "playing";
		case Finished:
			return "finished";
		case Resigned:
			return "resigned";
		default:
			return null;
		}
	}
}
