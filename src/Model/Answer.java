package Model;

public enum Answer {
	Accepted,
	Rejected,
	Unknown;
	
	public static Answer getAnswer(String answer) {
		switch(answer.toLowerCase()) {
		case "accepted":
			return Accepted;
		case "rejected":
			return Rejected;
		case "unknown":
			return Unknown;
		default:
			return null;
		}
	}
}
