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
	
	private String _username;
	private String _password;
	private ArrayList<AccountRole> _roles = new ArrayList<AccountRole>();
	
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
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
	
	public ArrayList<AccountRole> getRoles(){
		return _roles;
	}
	
	public static Optional<Account> getAccountByUsername(ArrayList<Account> accounts, String username){
		return accounts.stream().filter(account -> account.getUsername().equals(username)).findFirst();
	}
	
	public static List<Account> getAllAccountsByUsername(ArrayList<Account> accounts, String username){
		return accounts.stream().filter(account -> account.getUsername().contains(username)).collect(Collectors.toList());
	}
	
}
