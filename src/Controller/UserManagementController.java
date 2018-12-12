package Controller;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.function.Function;

import Model.Account;
import Model.Game;
import Model.AccountRole.AdministratorRole;
import View.Items.AccountItem;
import View.Items.GameItem;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class UserManagementController implements Initializable {

	private DatabaseController<Account> _db;
	private ArrayList<Account> _accounts;
	
	@FXML
	private VBox vboxAccounts;
	
	@FXML 
	private TextField searchBox;

	@Override
	public void initialize(URL url, ResourceBundle resources) {
		// TODO Auto-generated method stub
		getAccounts();
		
		searchBox.textProperty().addListener((observable) -> {
			renderAccounts();
		});
	}
	
	private void getAccounts() {
		_db = new DatabaseController<Account>();
		String getAccountsCommand = Account.getAllAccounts();
		
		var db = new DatabaseController<Account>();
		try {
			_accounts = (ArrayList<Account>) db.SelectWithCustomLogic(getAccountRole(), "SELECT * FROM accountrole");
			
			renderAccounts();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void renderAccounts() {
		vboxAccounts.getChildren().clear();
		
		for(var account : _accounts) {
			account.getRoles().forEach(role -> {
				
			});
			
			var accountItem = new AccountItem(account);
			vboxAccounts.getChildren().add(accountItem);
		}
	}
	
	//Custom functionality for merging account and role together
	private Function<ResultSet, ArrayList<Account>> getAccountRole(){
		return (resultSet -> {
			ArrayList<Account> accounts = new ArrayList<Account>();
			
			try {
				while(resultSet.next()) {	
					var columns = DatabaseController.getColumns(resultSet.getMetaData());
					if(columns.contains("username")) {
						var account = Account.getAccountByUsername(accounts, resultSet.getString("username"));
						if(columns.contains("role")) {
							if(account.isPresent()) {		
								account.get().addAllRoles(resultSet.getString("role"));
							}else {
								var newAccount = new Account(resultSet, columns);
								newAccount.addAllRoles(resultSet.getString("role"));
								accounts.add(newAccount);		
							}	
						}else {
							accounts.add(new Account(resultSet, columns));
						}
					}
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return accounts;
		});
	}
	
}
