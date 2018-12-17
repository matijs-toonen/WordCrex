package View.Items;

import java.io.InputStream;
import java.util.function.Consumer;

import Controller.MainController;
import Model.Game;
import Model.GameStatus;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

public class GameItemObserver extends AnchorPane {
	
	private Game _currentGame;
	private Label lblUser = new Label();
	private Label lblTime = new Label();
	private ImageView imgStatus = new ImageView();
	
	public GameItemObserver(Game game) {
		super();
		_currentGame = game;
		setUserLabel();
		setSubLabel();
		setImage();
		this.getChildren().addAll(lblTime, imgStatus, lblUser);
	}
	
	private void setImage() {
		
		Image image = null;
		if(_currentGame.getSatus() == GameStatus.Playing) {
			image = getActiveImage();
		}
		else {
			image = getPlayedImage();
		}
		
		imgStatus.setFitHeight(40);
		imgStatus.setFitWidth(40);
		imgStatus.setStyle("-fx-height: 10px; -fx-width: 10px");
		imgStatus.setImage(image);
	}
	
	private Image getActiveImage() {
		InputStream is = this.getClass().getResourceAsStream("/Resources/playing.png");
		lblTime.setText("Game wordt gespeeld");
		
    
		return new Image(is);
	}
	
	private Image getPlayedImage() {
		InputStream is = null;
		
		String player1 = _currentGame.getUser1();
		String winner = _currentGame.getWinner();
		
		//Determine won/lost
		if(player1.equals(winner)) {
			is = this.getClass().getResourceAsStream("/Resources/finished.png");
			lblTime.setText(player1 + " heeft gewonnen!");
		}
		else
		{
			is = this.getClass().getResourceAsStream("/Resources/resigned.png");
			lblTime.setText(player1 + " heeft verloren.");
		}
		
		return new Image(is);
	}
	
	private void setUserLabel() { 
		var userText = _currentGame.getUser1() + " - " + _currentGame.getUser2();
		
		lblUser.setText(userText);
		lblUser.getStyleClass().add("text");
		lblUser.setStyle("-fx-padding: 0 0 0 50; -fx-font-size: 14px; -fx-text-fill: #4D4F5C; -fx-font-weight: bold;");
	}
	
	private void setSubLabel() {
		lblUser.getStyleClass().add("text");
		lblTime.setStyle("-fx-padding: 20 0 40 50; -fx-font-size: 13px; -fx-text-fill: #ABABB1");
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
