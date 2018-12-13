package Controller;

import java.sql.SQLException;

import Model.Account;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
			
			if(!checkLogin())
			{
				lblError.setText("Het username en wachtwoord komt niet overeen.");
				lblError.setVisible(true);
			}
			else
				loadSidebar();
		}
	}
	
	private boolean checkLogin() {	
		var db = new DatabaseController<Account>();
		String sqlStatement = "SELECT username FROM account WHERE username = '" + _username + "' AND password = '" + _password + "'";

		try {
			_account = (Account) db.SelectFirst(sqlStatement, Account.class);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		if(_account.getUsername() != null)
			return true;

		return false;
	}

	public void onClickRegister(ActionEvent e)
	{
		_username = txtLogin.getText().toLowerCase();
		_password = txtPassword.getText().toLowerCase();
		
		if(_username.isEmpty() || _password.isEmpty())
		{
			lblError.setText("Voer een username en of wachtwoord in.");
			lblError.setVisible(true);
		}
		else if(_username.length() < 5 || _password.length() < 5)
		{
			lblError.setText("Uw username en wachtwoord moet minimaal 5 tekens lang zijn.");
			lblError.setVisible(true);
		}
		else 
		{
			lblError.setVisible(false);

			if(!register())
			{
				lblError.setText("Deze gebruikersnaam bestaat al, kies een andere gebruikersnaam.");
				lblError.setVisible(true);
			}
			else
				loadSidebar();
		}
	}

	private boolean register() {
		var db = new DatabaseController<Account>();
		String sqlStatement = "INSERT INTO account (username, password) SELECT * FROM (SELECT '" + _username + "', '" + _password + "') AS tmp WHERE NOT EXISTS ( SELECT username FROM account WHERE username = '" + _username + "' ) LIMIT 1";
		boolean register = false;
		
		try {
			register = db.Insert(sqlStatement);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		if(register)
		{
			sqlStatement = "insert into accountrole (username, role) values ('" + _username + "','player')";
			try {
				register = db.Insert(sqlStatement);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return true;
		}

		return false;
	}
	
    private void loadSidebar() 
    {        
    	BorderPane pane = null;         
    	
    	try {             
	    		var con = new MainController(_account);             
	    		var panes = new FXMLLoader(getClass().getResource("/View/Sidebar.fxml"));             
	    		panes.setController(con);             
	    		pane = panes.load();         
    		}         
    	catch(Exception ex) {             
    			ex.printStackTrace();      
    		}             	
    	loginPane.getChildren().setAll(pane);
    }
}
