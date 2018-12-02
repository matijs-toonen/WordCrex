package Model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;

import Model.AccountRole.*;

public class Account {
	private String _username;
	private String _password;
	private ArrayList<AccountRole> _roles = new ArrayList<AccountRole>();;
	
	public Account(String username) {
		_username = username;
	}
	
	public Account(String username, String password) {
		this(username);
		_password = password;
	}
	
	public Account(String username, String password, String role) {
		this(username, password);
		addRole(role);
	}
	
	public Account(ResultSet rs, ArrayList<String> columns) {
		try {
			_username = columns.contains("username") ? rs.getString("username") : null;
			_password = columns.contains("password") ? rs.getString("password") : null;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void addRole(String role) {
		_roles.add(role == null ? new PlayerRole() : getRole(role));
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
	
	public static Optional<Account> getAccountByUsername(ArrayList<Account> accounts, String username){
		return accounts.stream().filter(account -> account.getUsername().equals(username)).findFirst();
	}
}
