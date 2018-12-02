package Model;

public class LetterSet {
	private String _letterCode;
	private String _description;
	
	public LetterSet(String letterCode) {
		_letterCode = letterCode;
	}
	
	public LetterSet(String letterCode, String description) {
		this(letterCode);
		_description = description;
	}
	
	public String getLetterCode() {
		return _letterCode;
	}
	
	public String getDescription() {
		return _description;
	}
}
