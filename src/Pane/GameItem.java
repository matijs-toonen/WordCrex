package Pane;

import java.io.InputStream;
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
		InputStream is = null;
		
		switch (lblTime.getText().toLowerCase()) {
		case "requested":
			is = this.getClass().getResourceAsStream("/Resources/request.png");
			lblTime.setText("Wachten op een zet...");
			break;
		case "playing":
			is = this.getClass().getResourceAsStream("/Resources/playing.png");
			lblTime.setText("Plaats een zet!");
			break;
		case "resigned":
			is = this.getClass().getResourceAsStream("/Resources/resigned.png");
			lblTime.setText("Spel is afgelopen.");
			break;
		case "finished":
			is = this.getClass().getResourceAsStream("/Resources/finished.png");
			lblTime.setText("Spel is afgelopen.");
			break;
		default:
			is = this.getClass().getResourceAsStream("/Resources/playing.png");
		}
		
		var image = new Image(is);
		imgStatus.setFitHeight(40);
		imgStatus.setFitWidth(40);
		imgStatus.setStyle("-fx-height: 10px; -fx-width: 10px");
		imgStatus.setImage(image);
	}
	
	private void setUserLabel() {
		lblUser.setText(currentGame.usernamePlayer2);
		lblUser.getStyleClass().add("text");
		lblUser.setStyle("-fx-padding: 0 0 0 50; -fx-font-size: 14px; -fx-text-fill: #4D4F5C; -fx-font-weight: bold;");
	}
	
	private void setSubLabel() {
		lblTime.setText(String.valueOf(currentGame.gameStatus));
		lblUser.getStyleClass().add("text");
		lblTime.setStyle("-fx-padding: 20 0 40 50; -fx-font-size: 13px; -fx-text-fill: #ABABB1");
	}

	public void setUserOnClickEvent(Consumer<MouseEvent> action) {
		
		lblUser.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				action.accept(event);
			}
		});
	}
}
