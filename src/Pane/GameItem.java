package Pane;

import java.util.function.Function;
import java.util.function.Predicate;

import Model.Game;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

public class GameItem extends AnchorPane {
	
	private Game currentGame;
	private Label lblTitle = new Label();
	private Label lblSubTitle = new Label();
	private ImageView imgStatus = new ImageView();
	
	public GameItem(Game game) {
		super();
		currentGame = game;
		setUserLabel();
		setSubLabel();
		setImage();
		this.getChildren().addAll(lblTitle, lblSubTitle, imgStatus);
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
		lblTitle.setText(currentGame.usernamePlayer2);
		lblTitle.setStyle("-fx-padding: 0 0 0 25; -fx-font-weight: bold");
		
		lblTitle.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
		    @Override
		    public void handle(MouseEvent mouseEvent) {
		        System.out.println("mouse click detected! " + mouseEvent.getSource());
		    }
		});
		
//		lblTitle.setOnMouseClicked(new EventHandler<MouseEvent>() {
//			
//			@Override
//			public void handle(MouseEvent event) {
//				System.out.println("KANKER ZOOI");
//			}
//		});
	}
	
	public void callEvent(MouseEvent mouseEvent) {
		System.out.println("mouse click detected! " + mouseEvent.getSource());
	}
	
	private void setThing(Predicate<MouseEvent> action) {
		action.test(null);
	}
	
	private void setSubLabel() {
		lblSubTitle.setText(String.valueOf(currentGame.answerPlayer2));
		lblSubTitle.setStyle("-fx-padding: 10 0 5 25; -fx-text-fill: #cdcdb1");
	}
}
