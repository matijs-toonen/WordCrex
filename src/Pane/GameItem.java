package Pane;

import java.util.function.Consumer;
import Model.Game;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

public class GameItem extends AnchorPane {
	
	private Game currentGame;
	private Label lblUser = new Label();
	private Label lblTime = new Label();
	private ImageView imgStatus = new ImageView();
	
	public GameItem(Game game) {
		super();
		currentGame = game;
		setUserLabel();
		setSubLabel();
		setImage();
		this.getChildren().addAll(lblTime, imgStatus, lblUser);
	}
	
	private void setImage() {
		var is = this.getClass().getResourceAsStream("/Resources/logo.png");
		var image = new Image(is);
		imgStatus.setFitHeight(20);
		imgStatus.setFitWidth(20);
		imgStatus.setStyle("-fx-height: 10px; -fx-width: 10px");
		imgStatus.setImage(image);
	}
	
	private void setUserLabel() {
		lblUser.setText(currentGame.usernamePlayer2);
		lblUser.setStyle("-fx-padding: 0 0 0 25; -fx-font-weight: bold");
	}
	
	private void setSubLabel() {
		lblTime.setText(String.valueOf(currentGame.answerPlayer2));
		lblTime.setStyle("-fx-padding: 10 0 5 25; -fx-text-fill: #cdcdb1");
	}

	public void setThing(Consumer<MouseEvent> action) {
		
		lblUser.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				action.accept(event);
			}
		});
	}
}
