package Controller;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;

public class DatabaseController <T> {
	private static String _url = "jdbc:mysql://localhost/";
	private static String _user = "root";
	private static String _password = "1234";
	private static String _schema = "outerspace";
	
	public DatabaseController(String url, String schema, String user, String password) {
		_url = url != null ? url : _url;
		_user = user != null ? user : _user;
		_password = password != null ? password : _password;
		_schema = schema != null ? schema : _schema;
	}
	

	public T SelectLast(String statement, Class<T> type) throws SQLException {
		Connection conn = DriverManager.getConnection(_url + _schema, _user, _password);
		Statement state = conn.createStatement();
		ResultSet resultSet = state.executeQuery(statement);
		
		T item = null;
		
		resultSet.last();
		
		try {
			item = type.getDeclaredConstructor(ResultSet.class, ArrayList.class).newInstance(resultSet, setColumns(resultSet.getMetaData()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		resultSet.close();
		state.close();
		conn.close();
		return item;
	}
	
	public Collection<T> SelectAll(String statement, Class<T> type) throws SQLException {
		Connection conn = DriverManager.getConnection(_url + _schema, _user, _password);
		Statement state = conn.createStatement();
		ResultSet resultSet = state.executeQuery(statement);
		ArrayList<T> items = new ArrayList<T>();
		
		while(resultSet.next()) {	
			try {
				items.add(type.getDeclaredConstructor(ResultSet.class, ArrayList.class).newInstance(resultSet, setColumns(resultSet.getMetaData())));
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
