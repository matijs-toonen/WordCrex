package Controller;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

import Model.Account;
import Model.PasswordValidation;
import Model.AccountRole.AccountRole;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.text.Text;

public class SettingsController implements Initializable {

	PasswordValidation pass = new PasswordValidation();
	private DatabaseController<Account> _db = new DatabaseController<Account>();
	
	@FXML
	private Text lblUser;
	
	@FXML
	private Text lblRoles;
	
	@FXML
	private TextField passwordOld;
	
	@FXML
	private TextField passwordNew;
	
	@FXML
	private Button btnSavePassword;
	
	@FXML
	private final Tooltip tooltip = new Tooltip();
	
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		LoadUserData();
		
		passwordNew.textProperty().addListener((observable, oldValue, newValue) -> {
			if(!pass.isValid(newValue)) {
				if(!passwordNew.getStyleClass().contains("Invalid")) {
					passwordNew.getStyleClass().remove("Valid");
					passwordNew.getStyleClass().add("Invalid");
					passwordNew.setTooltip(tooltip);
					
					btnSavePassword.setDisable(true);
				}
			}
			else if(pass.isValid(newValue)) {
				if(passwordNew.getStyleClass().contains("Invalid")) {
					passwordNew.getStyleClass().remove("Invalid");
					passwordNew.getStyleClass().add("Valid");
					passwordNew.setTooltip(null);
					
					btnSavePassword.setDisable(false);
				}
			}
		});
		
		btnSavePassword.setOnAction(new EventHandler<ActionEvent>() {
		    @Override public void handle(ActionEvent e) {
		    	updatePassword(passwordNew.getText().trim().toString());		    
	    	}
		});
	}
	
	private void LoadUserData() {
		var currentUser = MainController.getUser();
		String username = currentUser.getUsername();
		ArrayList<String> Roles = new ArrayList<String>();
		
		lblUser.setText(username);
		currentUser.getRoles().forEach(item -> {Roles.add(item.getRole());});
		lblRoles.setText("");
		Roles.forEach(item ->{AddRoleText(item);});
		lblRoles.setText(lblRoles.getText().substring(0, lblRoles.getText().length() -2));
		btnSavePassword.setDisable(true);
		
		tooltip.setText(
			    "Het wachtwoord voldoet niet aan onderstaande voorwaarden:\n" +
			    "- Minimaal 5 tekens lang\n"  +
			    "- Maximaal 25 tekens lang\n"
			);
	}
	
	private void AddRoleText(String role)
	{
		String RolesText = lblRoles.getText();
		
		lblRoles.setText(RolesText + role + ", ");
	}
	
	private void updatePassword(String password)
	{
		var currentUser = MainController.getUser();
		
		String query = Account.updatePassword(password, currentUser.getUsername());
		
		try {
           boolean succes = _db.Update(query);
           
           System.out.println(succes);
           
           if(succes) {
        	   //TODO: Martijn Logout
           }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
		
	}
	
}
