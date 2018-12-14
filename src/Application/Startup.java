package Application;
	
import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;


public class Startup extends Application {
	
	private static Stage _primaryStage;
	
	@Override
	public void start(Stage primaryStage) {
		
		_primaryStage = primaryStage;
		
		try {
			BorderPane root = new BorderPane();
			Scene scene = new Scene(root,700,700);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			Font.loadFont(getClass().getResourceAsStream("../Fonts/SourceSansPro-Regular.ttf"), 10);	
			Font.loadFont(getClass().getResourceAsStream("../Fonts/SourceSansPro-Bold.ttf"), 10);
			primaryStage.setScene(scene);
			
			Parent mainFrame = FXMLLoader.load(getClass().getResource("/View/Login.fxml"));
			
//			primaryStage.setMaximized(true);	TODO aan einde weer toevoegen !!!
			primaryStage.getIcons().add(new Image("/Resources/logo.png"));
			primaryStage.setScene(new Scene(mainFrame));
			primaryStage.show();
			
			loadView("M");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	public void loadView(String viewName) {
		try {
			Parent mainFrame = FXMLLoader.load(getClass().getResource("/View/Login.fxml"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
