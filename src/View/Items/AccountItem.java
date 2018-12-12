package View.Items;

import java.util.ArrayList;

import Model.Account;
import Model.AccountRole.AccountRole;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

public class AccountItem extends AnchorPane {

	private Account _currentAccount;
	
	private Label lblUser = new Label();
	private CheckBox chbSpeler = new CheckBox("Speler");
	private CheckBox chbAdministrator = new CheckBox("Administrator");
	private CheckBox chbModerator = new CheckBox("Moderator");
	private CheckBox chbObserver = new CheckBox("Observer");
	
	public AccountItem(Account account) {
		super();
		_currentAccount = account;
		
		setUserLabel();
		setCheckboxes();
		
		chbSpeler.selectedProperty().addListener(new ChangeListener<Boolean>() {
		    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
		        System.out.println("test");
		    }
		});
		
		this.getChildren().addAll(lblUser);
		this.getChildren().add(chbSpeler);
		this.getChildren().add(chbAdministrator);
		this.getChildren().add(chbModerator);
		this.getChildren().add(chbObserver);
	}
	
	private void setUserLabel() {
		var userText = _currentAccount.getUsername();
		lblUser.setText(userText);
		lblUser.getStyleClass().add("text");
	}
	
	private void setCheckboxes()
	{
		ArrayList<AccountRole> _roles = _currentAccount.getRoles();
		
		for(var role : _roles)
		{
			String _role = role.getAccountType();
			if(_role.equals("Administrator")) {
				chbSpeler.setSelected(true);
			} else if(_role.equals("Moderator")) {
				chbAdministrator.setSelected(true);
			} else if(_role.equals("Observer")) {
				chbModerator.setSelected(true);
			} else if(_role.equals("Player")) {
				chbObserver.setSelected(true);
			}
		}
		
		chbSpeler.setLayoutX(200);
		chbSpeler.setLayoutY(0);
		
		chbAdministrator.setLayoutX(200);
		chbAdministrator.setLayoutY(30);
		
		chbModerator.setLayoutX(200);
		chbModerator.setLayoutY(60);
		
		chbObserver.setLayoutX(200);
		chbObserver.setLayoutY(90);
		
	}

	private void updateUser(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue)
	{
		System.out.println("Test");
	}
}
