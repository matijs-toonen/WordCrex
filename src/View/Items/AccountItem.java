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
			String _role = role.getAccountType();
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
		chbAdministrator.selectedProperty().addListener(new ChangeListener<Boolean>() {
		    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
		    	
		    	String Query = null;
		    	String username = _currentAccount.getUsername();
		    	if(newValue == false)
		    		Query = "DELETE FROM accountrole WHERE username = '"+username+"' AND role = 'administrator'";
		    	else if(newValue == true)
		    		Query = "INSERT INTO accountrole (username, role) VALUES ('"+username+"','administrator')";

		    	action.accept(Query);
		    	
		    	chbAdministrator.setDisable(true);
				chbAdministrator.getStyleClass().add("disabled");
		    }
		});
		chbModerator.selectedProperty().addListener(new ChangeListener<Boolean>() {
		    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
		    	
		    	String Query = null;
		    	String username = _currentAccount.getUsername();
		    	if(newValue == false)
		    		Query = "DELETE FROM accountrole WHERE username = '"+username+"' AND role = 'moderator'";
		    	else if(newValue == true)
		    		Query = "INSERT INTO accountrole (username, role) VALUES ('"+username+"','moderator')";

		    	action.accept(Query);
		    }
		});
		chbObserver.selectedProperty().addListener(new ChangeListener<Boolean>() {
		    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
		    	
		    	String Query = null;
		    	String username = _currentAccount.getUsername();
		    	if(newValue == false)
		    		Query = "DELETE FROM accountrole WHERE username = '"+username+"' AND role = 'observer'";
		    	else if(newValue == true)
		    		Query = "INSERT INTO accountrole (username, role) VALUES ('"+username+"','observer')";

		    	action.accept(Query);
		    }
		});
		chbPlayer.selectedProperty().addListener(new ChangeListener<Boolean>() {
		    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
		    	
		    	String Query = null;
		    	String username = _currentAccount.getUsername();
		    	if(newValue == false)
		    		Query = "DELETE FROM accountrole WHERE username = '"+username+"' AND role = 'player'";
		    	else if(newValue == true)
		    		Query = "INSERT INTO accountrole (username, role) VALUES ('"+username+"','player')";

		    	action.accept(Query);
		    }
		});
	}
	
}