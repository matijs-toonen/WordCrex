package controller;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;

public class DataBaseController <T> {
	
	private static final String _url = "jdbc:mysql://localhost/";
	private static final String _user = "root";
	private static final String _password = "1234";
	private static final String _schema = "outerspace";
	
	public T SelectLast(String statement, Class<T> cls) throws SQLException {
		Connection conn = DriverManager.getConnection(_url + _schema, _user, _password);
		Statement state = conn.createStatement();
		ResultSet resultSet = state.executeQuery(statement);
		
		T item = null;
		
		resultSet.last();
		
		try {
			item = cls.getDeclaredConstructor(ResultSet.class, ArrayList.class).newInstance(resultSet, setColumns(resultSet.getMetaData()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		resultSet.close();
		state.close();
		conn.close();
		return item;
	}
	
	public Collection<T> SelectAll(String statement, Class<T> cls) throws SQLException {
		Connection conn = DriverManager.getConnection(_url + _schema, _user, _password);
		Statement state = conn.createStatement();
		ResultSet resultSet = state.executeQuery(statement);
		ArrayList<T> items = new ArrayList<T>();
		
		while(resultSet.next()) {	
			try {
				items.add(cls.getDeclaredConstructor(ResultSet.class, ArrayList.class).newInstance(resultSet, setColumns(resultSet.getMetaData())));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		resultSet.close();
		state.close();
		conn.close();
		return items;
	}
	
	private ArrayList<String> setColumns(ResultSetMetaData metaData) throws SQLException{
		ArrayList<String> columns = new ArrayList<String>();
		for(int i = 1; i <= metaData.getColumnCount(); i++) {
			columns.add(metaData.getColumnName(i));
		}
		return columns;
	}
}
