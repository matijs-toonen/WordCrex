package Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class LoginController {
	Model.loginModel model = new Model.loginModel();
	
	@FXML
	private TextField txtLogin, txtPassword; 
	@FXML
	private Button btnLogin, btnRegister;
	
	public void onClickLogin(ActionEvent e)
	{
		model.checkUsername(txtLogin.getText());
		model.checkLogin(txtLogin.getText(), txtPassword.getText());
	}
}
