package Controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.Window;

public class MainController implements Initializable {

	@FXML
	private Label lblText;
	
	@FXML
	private Pane paneChild;
	
	public void showChild(ActionEvent actionEvent) {
		//Type van de frame die je wilt inladen (graag alles Pane maken)
		Pane endFrame = null;
		try {
			endFrame = (Pane) FXMLLoader.load(getClass().getResource("/View/TestFrame.fxml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(endFrame != null) {
			paneChild.getChildren().setAll(endFrame);
		}
	}

	
	public void showMessage(ActionEvent actionEvent) {
		lblText.setText("Nieuwe test value");
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		
	}
}
