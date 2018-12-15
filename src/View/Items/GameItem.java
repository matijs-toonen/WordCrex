package View.Items;

import java.io.InputStream;
import java.util.function.Consumer;

import Controller.MainController;
import Model.Game;
import Model.GameStatus;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
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
	private ImageView rightArrow = new ImageView();
	
	public GameItem(Game game) {
		super();
		_currentGame = game;
		setUserLabel();
		setSubLabel();
		setImage();
		setRightArrow();
		this.getChildren().addAll(lblTime, imgStatus, lblUser, rightArrow);
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
		InputStream is = null;
		
		var currentPlayer = MainController.getUser();
		String username = currentPlayer.getUsername();
		int zetUser1, zetUser2; //User1 will always be the user who's logged in, User2 is always his opponent.
		String opponent = _currentGame.getOpponent();
	
		if(_currentGame.getUser1().equals(username)) {
			zetUser1 = _currentGame.getZettenPlayer1();
			zetUser2 = _currentGame.getZettenPlayer2();
		}
		else {
			zetUser1 = _currentGame.getZettenPlayer2();
			zetUser2 = _currentGame.getZettenPlayer1();
		}
		
		if(zetUser1 <= zetUser2){
			is = this.getClass().getResourceAsStream("/Resources/request.png");
			lblTime.setText(opponent + " wacht op jou...");
		}
		else {
			is = this.getClass().getResourceAsStream("/Resources/playing.png");
			lblTime.setText("We wachten op " + opponent);
		}
    
		return new Image(is);
	}
	
	private Image getPlayedImage() {
		InputStream is = null;
		
		var currentPlayer = MainController.getUser();
		String username = currentPlayer.getUsername();
		String winner = _currentGame.getWinner();
		
		//Determine won/lost
		if(username.equals(winner)) {
			is = this.getClass().getResourceAsStream("/Resources/finished.png");
			lblTime.setText("Je hebt gewonnen!");
		}
		else
		{
			is = this.getClass().getResourceAsStream("/Resources/resigned.png");
			lblTime.setText("Je hebt verloren..");
		}
		
		return new Image(is);
	}
	
	private void setUserLabel() { 
		var userText = _currentGame.getOpponent();
		lblUser.setText(userText);
		lblUser.getStyleClass().add("text");
		lblUser.setStyle("-fx-padding: 0 0 0 50; -fx-font-size: 14px; -fx-text-fill: #4D4F5C; -fx-font-weight: bold;");
	}
	
	private void setSubLabel() {
		lblUser.getStyleClass().add("text");
		lblTime.setStyle("-fx-padding: 20 0 40 50; -fx-font-size: 13px; -fx-text-fill: #ABABB1");
	}
	
	private void setRightArrow() {
		InputStream is = this.getClass().getResourceAsStream("/Resources/rightArrow.png");
		Image image = new Image(is);
		
		rightArrow.setLayoutX(300);
		rightArrow.setLayoutY(5);
		rightArrow.setFitHeight(30);
		rightArrow.setFitWidth(30);
		rightArrow.setImage(image);
		rightArrow.setCursor(Cursor.HAND);
		rightArrow.getStyleClass().add("rightArrow");
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
