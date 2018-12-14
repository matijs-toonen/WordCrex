package Controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
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
		loadPaneWithParam("Games", GameController.class);
	}
	
	@FXML
	private void loadGames(MouseEvent event)
	{
		loadPaneWithParam("Games", GameController.class);
	}
	
	@FXML
	private void loadChallenge(MouseEvent event)
	{
		loadPaneWithParam("Challenge", ChallengeController.class);
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
	
	private <T> void loadPaneWithParam(String paneName, Class<T> controller) {
		
		//Parent root = null;
		AnchorPane pane = null;
		try {
			T con = null;
			
			if(controller.equals(ChallengeController.class)) 
				con = (T) new ChallengeController(rootPane);
			
			else if(controller.equals(GameController.class)) 
				con = (T) new GameController(rootPane);
			
			var panes = new FXMLLoader(getClass().getResource("/View/" + paneName + ".fxml"));
			panes.setController(con);
			pane = panes.load();
			
		}
		catch(Exception ex) {
			Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
		}
		//borderPane.setCenter(root);
		rootPane.getChildren().setAll(pane);
	}
	
	private void loadPane(String paneName) {
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
