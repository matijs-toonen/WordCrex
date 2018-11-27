package Model;

public enum GameStatus {
	Requested,
	Playing,
	Finished,
	Resigned;
	
	public static GameStatus getGameStatus(String state) {
		switch(state) {
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
}
