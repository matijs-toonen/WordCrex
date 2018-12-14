package Controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import Model.Account;
import Model.AccountRole.AccountRole;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class MainController implements Initializable {
	private static Account _currentUser;
	
	public static Account getUser() {
		return _currentUser;
	}
	
	@FXML
	private VBox menuWrapper;
	
	@FXML
	private AnchorPane screenPane;
	
	@FXML
	private Button gameButton;
	
	@FXML
	private Button challengeButton;
	
	@FXML
	private Button suggestButton;
	
	@FXML
	private Button watchGameButton;
	
	@FXML
	private Button judgeButton;
	
	@FXML
	private Button usersButton;
	
	@FXML
	private Button settingsButton;
	
	@FXML
	private Button logoutButton;
	
	@FXML
	private BorderPane rootPane;
	
	public MainController(Account account) {
		_currentUser = account;
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		loadPane("Games");
		
		menuWrapper.getChildren().clear();
		
		if (userHasRole("Player")) {
			menuWrapper.getChildren().add(gameButton);
			menuWrapper.getChildren().add(challengeButton);
			menuWrapper.getChildren().add(suggestButton);
		}
		
		if (userHasRole("Observer")) {
			menuWrapper.getChildren().add(watchGameButton);
		}
		
		if (userHasRole("Moderator")) {
			menuWrapper.getChildren().add(judgeButton);
		}
		
		if (userHasRole("Administrator")) {
			menuWrapper.getChildren().add(usersButton);
		}
		
		menuWrapper.getChildren().add(settingsButton);
		menuWrapper.getChildren().add(logoutButton);
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
	@FXML
	private void loadLogin(MouseEvent event)
	{
		_currentUser = null;
		logout();
	}
	
	private void logout()
	{
		try {
			BorderPane pane = FXMLLoader.load(getClass().getResource("/View/Login.fxml"));			
			rootPane.getChildren().setAll(pane);
		} catch(Exception e) {
			e.printStackTrace();
		}
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
		screenPane.getChildren().setAll(pane);
	}
	
	private boolean userHasRole(String role) {
		for (AccountRole userRole : _currentUser.getRoles()) {
			if (role.equals(userRole.getRole())){
				return true;
			}
		}
		return false;
	}
}
