package View.Items;

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
	
	private Game _currentGame;
	private Label lblUser = new Label();
	private Label lblTime = new Label();
	private ImageView imgStatus = new ImageView();
	
	public GameItem(Game game) {
		super();
		_currentGame = game;
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
		imgStatus.setImage(image);
	}
	
	private void setUserLabel() {
		lblUser.setText(_currentGame.getUser2());
		lblUser.getStyleClass().addAll("text", "title");
	}
	
	private void setSubLabel() {
		lblTime.setText(String.valueOf(_currentGame.getSatus()));
		lblTime.getStyleClass().addAll("text", "subtitle");
	}
	
	public Game getGame() {
		return _currentGame;
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
