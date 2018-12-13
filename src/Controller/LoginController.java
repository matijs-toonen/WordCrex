package Controller;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.function.Function;

import Model.Account;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

public class LoginController {
	private String _username, _password;
	private Account _account;

	@FXML
	private TextField txtLogin, txtPassword; 
	@FXML
	private Button btnLogin, btnRegister;
	@FXML
	private Label lblError;
	@FXML
	private BorderPane loginPane;
	
	
	/**
	 * When user clicks on button
	 * 
	 * @param e
	 */
	public void onClickLogin(ActionEvent e)
	{
		_username = txtLogin.getText().toLowerCase();
		_password = txtPassword.getText().toLowerCase();
		
		if(_username.isEmpty() || _password.isEmpty())
		{
			lblError.setText("Voer een username en of wachtwoord in.");
			lblError.setVisible(true);
		}
		else
		{
			lblError.setVisible(false);
			
			if(userInDatabase())
			{
				loadSidebar();
			}
			else {
				lblError.setText("Het username en wachtwoord komt niet overeen.");
				lblError.setVisible(true);
			}
		}
	}
	
	
	/**
	 * Check if user exists in the database
	 * @return
	 */
	private boolean userInDatabase() {	
		var db = new DatabaseController<Account>();
		String sqlStatement = "SELECT username FROM account WHERE username = '" + _username + "' AND password = '" + _password + "'";

		try {
			_account = (Account) db.SelectFirst(sqlStatement, Account.class);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		if(_account.getUsername() == null)
			return false;

		return true;
	}
	
	
	/**
	 * User clicks on register
	 * 
	 * @param e
	 */
	public void onClickRegister(ActionEvent e)
	{
		// get valies
		_username = txtLogin.getText().toLowerCase();
		_password = txtPassword.getText().toLowerCase();
		
		// check if values are empty
		if(_username.isEmpty() || _password.isEmpty())
		{
			lblError.setText("Voer een username en of wachtwoord in.");
			lblError.setVisible(true);
			return;
		}
		
		// check if minimum req are met
		if(_username.length() < 5 || _password.length() < 5)
		{
			lblError.setText("Uw username en wachtwoord moet minimaal 5 tekens lang zijn.");
			lblError.setVisible(true);
			return;
		}
		
		
		// check if maximum req are met
		if(_username.length() > 25 || _password.length() > 25)
		{
			lblError.setText("Uw username en wachtwoord max maximaal 25 tekens lang zijn.");
			lblError.setVisible(true);
			return;
		}
		
		// validation passed
		lblError.setVisible(false);
		if(registerUser())
		{
			loadSidebar();
		}
		else 
		{
			lblError.setText("Deze gebruikersnaam bestaat al, kies een andere gebruikersnaam.");
			lblError.setVisible(true);
		}
	}

	
	/**
	 * Register the user with default role to the database
	 * @return
	 */
	private boolean registerUser() {
		
		// create basebase conn
		var db = new DatabaseController<Account>();
		
		// insert the user
		String acountInsertQuery = "INSERT INTO account (username, password) SELECT * FROM (SELECT '" + _username + "', '" + _password + "') AS tmp WHERE NOT EXISTS ( SELECT username FROM account WHERE username = '" + _username + "' ) LIMIT 1";
		String roleInsertQuery = "INSERT INTO accountrole (username, role) VALUES ('" + _username + "','player')";
		
		try {
			db.Insert(acountInsertQuery);
			db.Insert(roleInsertQuery);
			return true;
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	
	
    private void loadSidebar() 
    {
    	try {
    		loadUserRols();
    		MainController mainController = new MainController(_account);        
    		FXMLLoader root = new FXMLLoader(getClass().getResource("/View/Sidebar.fxml"));
    		root.setController(mainController);
    		BorderPane pane = root.load();
    		loginPane.getChildren().setAll(pane);
    		System.out.println("Loading Sidebar View");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    private void loadUserRols() {
		var db = new DatabaseController<String>();
		try {
			var roles = (ArrayList<String>) db.SelectWithCustomLogic(getAccountRole(), "SELECT * FROM accountrole WHERE username = '" + _account.getUsername() + "'");
			roles.forEach(role -> {
				_account.addAllRoles(role);
			});
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
	//Custom functionality for merging account and role together
	private Function<ResultSet, ArrayList<String>> getAccountRole(){
		return (resultSet -> {
			var roles = new ArrayList<String>();
			var accounts = new ArrayList<Account>();
			
			try {
				while(resultSet.next()) {	
					var columns = DatabaseController.getColumns(resultSet.getMetaData());
					if(columns.contains("username")) {
						var account = Account.getAccountByUsername(accounts, resultSet.getString("username"));
						if(account.isPresent()) {	
							roles.add(resultSet.getString("role"));
						}else {
							var newAccount = new Account(resultSet, columns);
							accounts.add(newAccount);
							roles.add(resultSet.getString("role"));
						}	
					}
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return roles;
		});
	}
}
