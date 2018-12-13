package Model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import Model.AccountRole.*;

public class Account {
	public static final String getAllAccounts() {
		return ("select username, role from accountrole order by username");
	}
	
	public static final String getLoginQuery(String username, String password) {
		return String.format("SELECT username FROM account WHERE username = '%s' AND password = '%s'", username, password);
	}
	
	public static final String getNewUserQuery(String username, String password) {
		return String.format(
				"INSERT INTO account (username, password)\n" + 
				"SELECT *\n" + 
				"FROM (SELECT '%s', '%s') AS tmp\n" + 
				"WHERE NOT EXISTS(SELECT username FROM account WHERE username = '%s')\n" + 
				"LIMIT 1;"
				, username, password, username);
	}
	
	public static final String getInsertDefaultRoleQuery(String username) {
		return String.format("INSERT INTO accountrole (username, role) VALUES ('%s','player')", username);
	}
	
	public static final String getRolesFromUserQuery(String username) {
		return String.format("SELECT * FROM accountrole WHERE username = '%s'", username);
	}
	
	public static final String getRolesQuery() {
		return "SELECT * FROM accountrole";
	}
	
	private String _username;
	private String _password;
	private ArrayList<AccountRole> _roles = new ArrayList<AccountRole>();
	
	public static final String updatePassword(String password, String username) {
		return ("UPDATE account SET password = '" + password + "' WHERE username = '" + username + "'");
	}
	
	public Account(String username) {
		_username = username;
	}
	
	public Account(String username, String password) {
		this(username);
		_password = password;
	}
	
	public Account(String username, String password, String... roles) {
		this(username, password);
		addAllRoles(roles);
	}
	
	public Account(ResultSet rs, ArrayList<String> columns) {
		try {
			_username = columns.contains("username") ? rs.getString("username") : null;
			_password = columns.contains("password") ? rs.getString("password") : null;
		} catch (SQLException e) {}
	}
	
	public void addAllRoles(String... roles) {
		for(var role : roles) {
			_roles.add(role == null ? new PlayerRole() : getRole(role));	
		}
	}
	
	private AccountRole getRole(String role) {
		switch(role.toLowerCase()) {
		case "administrator":
			return new AdministratorRole();
		case "observer":
			return new ObserverRole();
		case "player":
			return new PlayerRole();
		case "moderator":
			return new ModeratorRole();
		default:
			return null;
		}
	}

	public String getUsername() {
		return _username;
	}

  
	public ArrayList<AccountRole> getRoles()
	{
		return _roles;
	}
	
	public String getPassword() 
	{
		return _password;
	}
	
	public static Optional<Account> getAccountByUsername(ArrayList<Account> accounts, String username){
		return accounts.stream().filter(account -> account.getUsername().equals(username)).findFirst();
	}
	
	public static List<Account> getAllAccountsByUsername(ArrayList<Account> accounts, String username){
		return accounts.stream().filter(account -> account.getUsername().contains(username)).collect(Collectors.toList());
	}
	
}
