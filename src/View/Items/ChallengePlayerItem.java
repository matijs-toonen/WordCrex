package View.Items;

import java.util.function.Consumer;

import Model.Account;
import Model.Game;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

public class ChallengePlayerItem extends AnchorPane{
	public final static String challengeText = "Uitdagen";
	
	//User2 Point of view
	private Account _challengePlayer;
	private Label lblUsername = new Label();
	private ImageView imgStatus = new ImageView();
	private Button btnChallenge = new Button();
	
	public ChallengePlayerItem(Account player) {
		super();
		_challengePlayer = player;
		setImage();
		setLabel();
		setButtons();
		getChildren().addAll(lblUsername, imgStatus, btnChallenge);
	}
	
	private void setImage() {
		var is = this.getClass().getResourceAsStream("/Resources/playing.png");
		var image = new Image(is);
		imgStatus.setImage(image);
	}
	
	private void setLabel() {
		lblUsername.setText(_challengePlayer.getUsername());
		lblUsername.setLayoutX(50);
		lblUsername.setStyle("-fx-padding: 10 0 48 0; -fx-font-size: 14px; -fx-font-weight: bold");
	}
	
	private void setButtons() {
		btnChallenge.setText(challengeText);
		btnChallenge.setLayoutX(240);
		btnChallenge.setLayoutY(5);
		btnChallenge.setCursor(Cursor.HAND);
		btnChallenge.getStyleClass().add("blueButton");
		btnChallenge.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-pref-width: 80");
	}
	
	public Account getOpponent() {
		return _challengePlayer;
	}
	
	/**
	 * On Label Click
	 * @param action
	 */
	public void setOnClickEvent(Consumer<ActionEvent> action) {
		btnChallenge.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				action.accept(event);
			}
		});
	}
}
