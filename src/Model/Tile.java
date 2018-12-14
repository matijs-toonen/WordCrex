package Model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Tile {
	
	/*
	 * Props
	 */
	private Integer _x;
	private Integer _y;
	private TileType _tileType;
	
	
	/*
	 * Const
	 */
	public Tile(int x, int y) {
		_x = x;
		_y = y;
	}
	
	public Tile(int x, int y, TileType tileType) {
		this(x, y);
		_tileType = tileType;
	}
	
	public Tile(ResultSet rs, ArrayList<String> columns) {
		try {
			_x = columns.contains("x") ? rs.getInt("x") : null;
			_y = columns.contains("y") ? rs.getInt("y") : null;
			_tileType = columns.contains("tile_type") ? new TileType(rs.getString("tile_type")) : null;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
