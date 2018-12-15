package Model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class LetterSet {
	
	/*
	 * Props
	 */
	private String _letterCode;
	private String _description;
	
	/*
	 * Const
	 */
	public LetterSet(String letterCode) {
		_letterCode = letterCode;
	}
	
	public LetterSet(String letterCode, String description) {
		this(letterCode);
		_description = description;
	}
	
	public LetterSet(ResultSet rs, ArrayList<String> columns) {
		try {
			_letterCode = columns.contains("code") ? rs.getString("code") : null;
			_description = columns.contains("description") ? rs.getString("description") : null;
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Getters
	 */
	public String getLetterCode() {
		return _letterCode;
	}
	
	public String getDescription() {
		return _description;
	}
}
