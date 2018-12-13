package Controller;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import Model.Account;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

public class MainController implements Initializable {
	
	private static Account _currentUser;
	
	public static Account getUser() {
		return _currentUser;
	}
	
	@FXML
	private AnchorPane rootPane;
	
	public MainController(Account account) {
		_currentUser = account;
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		accountStub();
		accountRoleStub();
	}
	
	private void accountStub() {
		//Dummy
		var accounts = new ArrayList<Account>();
		accounts.add(new Account("henk"));
		var acc = Account.getAccountByUsername(accounts, "henk");
		
		if(acc.isPresent()) 
			System.out.println(acc.get().getUsername());
		
		//With db
		var db = new DatabaseController<Account>();
		try {
			accounts = (ArrayList<Account>) db.SelectAll("SELECT * FROM account", Account.class);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void accountRoleStub() {
		//Dummy
		var accounts = new ArrayList<Account>();
		accounts.add(new Account("henk"));
		var acc = Account.getAccountByUsername(accounts, "henk");
		
		if(acc.isPresent()) 
			acc.get().addRole("player");
		
		//With db
		var db = new DatabaseController<Account>();
		try {
			accounts = (ArrayList<Account>) db.SelectWithCustomLogic(getAccountRole(), "SELECT * FROM accountrole");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
								account.get().addRole(resultSet.getString("role"));
							}else {
								var newAccount = new Account(resultSet, columns);
								newAccount.addRole(resultSet.getString("role"));
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
	
	@FXML
	private void loadGames(MouseEvent event)
	{
		loadPane("Games");
	}
	
	@FXML
	private void loadChallenge(MouseEvent event)
	{
		loadPane("Challenge");
	}
	
	@FXML
	private void loadSuggest(MouseEvent event)
	{
		loadPane("Suggest");
	}
	
	@FXML
	private void loadWatch(MouseEvent event)
	{
		loadPane("Watch");
	}
	
	@FXML
	private void loadJudge(MouseEvent event)
	{
		loadPane("Judge");
	}
	
	@FXML
	private void loadManage(MouseEvent event)
	{
		loadPane("Manage");
	}
	
	@FXML
	private void loadSettings(MouseEvent event)
	{
		loadPane("Settings");
	}
	
	private void loadPane(String paneName) {
		
		//Parent root = null;
		AnchorPane pane = null;
		try {
			pane = FXMLLoader.load(getClass().getResource("/View/" + paneName + ".fxml"));
		}
		catch(IOException ex) {
			Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
		}
		//borderPane.setCenter(root);
		rootPane.getChildren().setAll(pane);
	}
}
