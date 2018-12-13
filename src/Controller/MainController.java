package Controller;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import Model.Account;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

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
		loadPane("Games");
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
