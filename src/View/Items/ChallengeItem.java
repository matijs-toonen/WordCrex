package View.Items;

import java.util.function.Consumer;

import Model.Game;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

public class ChallengeItem extends AnchorPane{
	public final static String acceptText = "Accepteren";
	public final static String rejectText = "Weigeren";
	
	//User2 Point of view
	private Game _currentGame;
	private Label lblUsername = new Label();
	private ImageView imgStatus = new ImageView();
	private Button btnAccept = new Button();
	private Button btnReject = new Button();
	
	public ChallengeItem(Game game) {
		super();
		_currentGame = game;
		setImage();
		setLabel();
		setButtons();
		getChildren().addAll(lblUsername, imgStatus, btnAccept, btnReject);
	}
	
	private void setImage() {
		var is = this.getClass().getResourceAsStream("/Resources/request.png");
		var image = new Image(is);
		imgStatus.setFitHeight(40);
		imgStatus.setFitWidth(40);
		imgStatus.setImage(image);
	}
	
	private void setLabel() {
		lblUsername.setText(_currentGame.getUser2());
		lblUsername.setLayoutX(50);
		lblUsername.setStyle("-fx-padding: 10 0 48 0; -fx-font-size: 14px; -fx-font-weight: bold");
	}
	
	private void setButtons() {
		btnAccept.setText(acceptText);
		btnAccept.setLayoutX(150);
		btnAccept.setLayoutY(5);
		btnAccept.setCursor(Cursor.HAND);
		btnAccept.setStyle("-fx-background-color: #3B86FF; -fx-text-fill: white; -fx-font-weight: bold; -fx-pref-width: 80");
		btnReject.setText(rejectText);
		btnReject.setLayoutX(240);
		btnReject.setLayoutY(5);
		btnReject.setCursor(Cursor.HAND);
		btnReject.setStyle("-fx-background-color: #FF6565; -fx-text-fill: white; -fx-font-weight: bold; -fx-pref-width: 80");
	}
	
	public Game getGame() {
		return _currentGame;
	}
	
	public void setOnClickEvent(Consumer<ActionEvent> action) {
		btnAccept.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				action.accept(event);
			}
		});
		
		btnReject.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				action.accept(event);
			}
		});
	}
}