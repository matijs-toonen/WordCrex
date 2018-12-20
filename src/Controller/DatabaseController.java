package Controller;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;

public class DatabaseController <T> {
	
	/*
	 * PROPS
	 */

//	private static String _url = "jdbc:mysql://databases.aii.avans.nl/";
//    private static String _schema = "2018_vsoprj2_wordcrex_abcd";
//    private static String _user = "42in02vtsod";
//    private static String _password = "duurzaamheid";
    
    private static String _url = "jdbc:mysql://digitalbyali.nl/";
    private static String _schema = "u17091p12601_wordcrex";
    private static String _user = "u17091p12601_groepd";
    private static String _password = "P@s5w0rd!";
	
	
	public DatabaseController(String url, String schema, String user, String password) {
		_url = url != null ? url : _url;
		_user = user != null ? user : _user;
		_password = password != null ? password : _password;
		_schema = schema != null ? schema : _schema;
	}
	
	public DatabaseController() {
		// Default values
	}
	
	private Connection OpenConnection() throws SQLException
	{
		return DriverManager.getConnection(_url + _schema, _user, _password);
	}
	
	private void CloseConnection(Connection conn, Statement state) throws SQLException
	{
		state.close();
		conn.close();
	}
	
	public int SelectCount(String statement) throws SQLException {
		Connection conn = OpenConnection();
		Statement state = conn.createStatement();
		ResultSet resultSet = state.executeQuery(statement);
		
		resultSet.first();
		
		int total = resultSet.getInt(1);
		
		resultSet.close();
		CloseConnection(conn, state);
		return total;
	}

	public T SelectLast(String statement, Class<T> type) throws SQLException {
		Connection conn = OpenConnection();
		Statement state = conn.createStatement();
		ResultSet resultSet = state.executeQuery(statement);
		
		T item = null;
		
		resultSet.last();
		
		try {
			item = type.getDeclaredConstructor(ResultSet.class, ArrayList.class).newInstance(resultSet, getColumns(resultSet.getMetaData()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		resultSet.close();
		CloseConnection(conn, state);
		
		return item;
	}
	
	public T SelectFirst(String statement, Class<T> type) throws SQLException {
		Connection conn = OpenConnection();
		Statement state = conn.createStatement();
		ResultSet resultSet = state.executeQuery(statement);
		
		T item = null;
		
		resultSet.first();
		
		try {
			item = type.getDeclaredConstructor(ResultSet.class, ArrayList.class).newInstance(resultSet, getColumns(resultSet.getMetaData()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		resultSet.close();
		CloseConnection(conn, state);
		
		return item;
	}
	
	public Collection<T> SelectAll(String statement, Class<T> type) throws SQLException {
		Connection conn = OpenConnection();
		Statement state = conn.createStatement();
		ResultSet resultSet = state.executeQuery(statement);
		ArrayList<T> items = new ArrayList<T>();
		
		while(resultSet.next()) {	
			try {
				items.add(type.getDeclaredConstructor(ResultSet.class, ArrayList.class).newInstance(resultSet, getColumns(resultSet.getMetaData())));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		resultSet.close();
		CloseConnection(conn, state);
		
		return items;
	}
	
	public Collection<T> SelectWithCustomLogic(Function<ResultSet, ArrayList<T>> customWay, String statement) throws SQLException {
		Connection conn = OpenConnection();
		Statement state = conn.createStatement();
		ResultSet resultSet = state.executeQuery(statement);
		
		var customLogic = customWay.apply(resultSet);
		
		resultSet.close();
		CloseConnection(conn, state);
		
		return customLogic;
	}
  
	public static ArrayList<String> getColumns(ResultSetMetaData metaData) throws SQLException{
		ArrayList<String> columns = new ArrayList<String>();
		for(int i = 1; i <= metaData.getColumnCount(); i++) {
			columns.add(metaData.getColumnLabel(i));
		}
		 
		return columns;
	}
	
	public boolean Update (String statement) throws SQLException
	{
		Connection conn = OpenConnection();
		Statement state = conn.createStatement();
		
		int results = state.executeUpdate(statement);
		
		CloseConnection(conn, state);
		
		return results > 0;
	}
	
	public boolean UpdateBatch (String... statements) throws SQLException
	{
		Connection conn = OpenConnection();
		Statement state = conn.createStatement();
		
		for(var statement : statements) {
			state.addBatch(statement);
		}
		
		int[] results = state.executeBatch();
		
		CloseConnection(conn, state);
		
		return results.length > 0;
	}
	
	public T UpdateWithReturnKeys(String statement, Function<ResultSet, T> action) throws SQLException {
		Connection conn = OpenConnection();
		PreparedStatement preparedState = conn.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS);
		
		int results = preparedState.executeUpdate();
		if(results == 0) 
			return null;
		
		var generatedKeys = preparedState.getGeneratedKeys();
		var returnValue = action.apply(generatedKeys);
		
		generatedKeys.close();
		CloseConnection(conn, preparedState);
		return returnValue;
	}
	
	public T DeleteWithReturnKeys(String statement, Function<ResultSet, T> action) throws SQLException {
		return UpdateWithReturnKeys(statement, action);
	}
	
	public T InsertWithReturnKeys(String statement, Function<ResultSet, T> action) throws SQLException {
		return UpdateWithReturnKeys(statement, action);
	}
	
	public boolean DeleteBatch (String... statements) throws SQLException
	{
		return UpdateBatch(statements);
	}
	
	public boolean InsertBatch (String... statements) throws SQLException
	{
		return UpdateBatch(statements);
	}
	
	public boolean Delete (String statement) throws SQLException
	{
		return Update(statement);
	}
	
	public boolean Insert (String statement) throws SQLException
	{
		return Update(statement);
	}
}
