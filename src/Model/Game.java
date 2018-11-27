package Model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Game {
	public String objectNaam;
	public String satellietVan;
	public Float afstand;
	public Integer diameter;
	
	public Game(ResultSet rs, ArrayList<String> columns) {
		try {
			objectNaam = columns.contains("objectnaam") ? rs.getString("objectnaam") : null;
			satellietVan = columns.contains("satellietVan") ? rs.getString("satellietVan") : null;
			afstand = columns.contains("afstand") ? rs.getFloat("afstand") : null;
			diameter = columns.contains("diameter") ? rs.getInt("diameter") : null;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
