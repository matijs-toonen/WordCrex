package Model;

import java.awt.Point;
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
		
	public int getX()
	{
		return _x;
	}
	
	public int getY()
	{
		return _y;
	}
	
	public TileType getType()
	{
		return _tileType;
	}
	
	public boolean isAtPoint(Point point)
	{
		return point.equals(new Point(getX(), getY()));
	}
}
