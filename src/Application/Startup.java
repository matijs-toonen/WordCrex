package Application;
	
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;


public class Startup extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			BorderPane root = new BorderPane();
			Scene scene = new Scene(root,700,700);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			Font.loadFont(getClass().getResource("../Fonts/SourceSansPro-Regular.ttf").toExternalForm(), 10);
			Font.loadFont(getClass().getResource("../Fonts/SourceSansPro-Bold.ttf").toExternalForm(), 10);
			primaryStage.setScene(scene);
			
			Parent mainFrame = FXMLLoader.load(getClass().getResource("/View/Login.fxml"));
			
//			primaryStage.setMaximized(true);	@ToDo aan einde weer toevoegen !!!
			primaryStage.getIcons().add(new Image("/Resources/logo.png"));
			primaryStage.setScene(new Scene(mainFrame));
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
