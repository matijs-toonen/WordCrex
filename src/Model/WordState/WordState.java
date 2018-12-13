package Model.WordState;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class WordState {
	
	private String _state;
	
	public WordState(String state)
	{
		_state = state;
	}

	public WordState(ResultSet rs, ArrayList<String> columns)
	{
		try {
			_state = columns.contains("state") ? rs.getString("state") : null;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getState()
	{
		return _state;
	}
}
