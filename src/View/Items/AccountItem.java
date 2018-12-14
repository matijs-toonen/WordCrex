package View.Items;

import java.util.ArrayList;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import Controller.MainController;
import Model.Account;
import Model.AccountRole.AccountRole;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.AnchorPane;

public class AccountItem extends AnchorPane {

	private Account _currentAccount;
	
	private final Separator separator = new Separator();
	private Label lblUser = new Label();
	private CheckBox chbAdministrator = new CheckBox("Administrator");
	private CheckBox chbModerator = new CheckBox("Moderator");
	private CheckBox chbObserver = new CheckBox("Observer");
	private CheckBox chbPlayer = new CheckBox("Speler");
	
	public AccountItem(Account account) {
		super();
		_currentAccount = account;

		init();
	}
	
	private void init()
	{
		setUserLabel();
		setCheckboxes();
		
		this.getChildren().addAll(separator, lblUser,chbAdministrator,chbModerator,chbObserver,chbPlayer);
	}
	
	private void setUserLabel() {
		var userText = _currentAccount.getUsername();
		lblUser.setText(userText);
		lblUser.setLayoutX(20);
		lblUser.setLayoutY(40);
		lblUser.getStyleClass().add("text");
		lblUser.setStyle("-fx-font-size: 14px");
		
		separator.setPrefWidth(300);
		this.setTopAnchor(separator, 0.0);
	    this.setLeftAnchor(separator, 15.0);
	    this.setRightAnchor(separator, 30.0);
	}
	
	private void setCheckboxes()
	{
		ArrayList<AccountRole> _roles = _currentAccount.getRoles();
		
		for(var role : _roles)
		{
			String _role = role.getRole();
			if(_role.equals("Administrator")) {
				chbAdministrator.setSelected(true);
			} else if(_role.equals("Moderator")) {
				chbModerator.setSelected(true);
			} else if(_role.equals("Observer")) {
				chbObserver.setSelected(true);
			} else if(_role.equals("Player")) {
				chbPlayer.setSelected(true);
			}
		}
		
		if(chbAdministrator.isSelected()) {
			chbAdministrator.setDisable(true);
			chbAdministrator.getStyleClass().add("disabled");
		}
		
		chbAdministrator.setLayoutX(200);
		chbAdministrator.setLayoutY(20);
		chbAdministrator.getStyleClass().add("checkbox");
		
		chbModerator.setLayoutX(200);
		chbModerator.setLayoutY(55);
		chbModerator.getStyleClass().add("checkbox");
		
		chbObserver.setLayoutX(200);
		chbObserver.setLayoutY(90);
		chbObserver.getStyleClass().add("checkbox");
		
		chbPlayer.setLayoutX(200);
		chbPlayer.setLayoutY(125);
		chbPlayer.getStyleClass().add("checkbox");
		
		chbPlayer.setStyle("-fx-padding: 0 0 20 0");
	}

	public void setUpdateEvent(Consumer<String> action) {
		
		chbAdministrator.selectedProperty().addListener((ChangeListener<? super Boolean>)((observer, oldVal, newVal) -> {
			var query = changed(observer, newVal, "administrator");
			action.accept(query);
			chbAdministrator.setDisable(true);
			chbAdministrator.getStyleClass().add("disabled");
		}));
		
		chbModerator.selectedProperty().addListener((ChangeListener<? super Boolean>)((observer, oldVal, newVal) -> {
			var query = changed(observer, newVal, "moderator");
			action.accept(query);
		}));
		
		chbObserver.selectedProperty().addListener((ChangeListener<? super Boolean>)((observer, oldVal, newVal) -> {
			var query = changed(observer, newVal, "observer");
			action.accept(query);
		}));
		
		chbPlayer.selectedProperty().addListener((ChangeListener<? super Boolean>)((observer, oldVal, newVal) -> {
			var query = changed(observer, newVal, "player");
			action.accept(query);
		}));

	}
	
	private String changed(ObservableValue<? extends Boolean> observable, Boolean newValue, String role) {
		String username = _currentAccount.getUsername();
		if (newValue) {
			return Account.getAddRoleQuery(username, role);
		}else {
			return Account.getRemoveRoleQuery(username, role);
		}
    }
	
}