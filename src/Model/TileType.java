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
	
	public char getLetter()
	{
		var type = _tileType.toCharArray();
		
		if(type.length < 2)
		{
			if(type[0] != '*')
				return ' ';
			
			return type[0];
		}
		else
		{
			if(type[0] == '-')
				return ' ';
			
			return type[1];
		}
	}
	
	public int getValue()
	{
	    try 
	    {
	    	return Integer.parseInt(Character.toString(_tileType.toCharArray()[0]));
	    } 
	    catch(NumberFormatException e) 
	    { 
	        return 0; 
	    } 
	    catch(NullPointerException e) 
	    {
	        return 0;
	    }
	}
}
