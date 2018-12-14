package Model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class TileType {
	
	private String _tileType;
	
	public TileType(String tileType) {
		_tileType = tileType;
	}
	
	public TileType(ResultSet rs, ArrayList<String> columns) {
		try {
			_tileType = columns.contains("type") ? rs.getString("type") : null;
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
