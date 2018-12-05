package Controller;

import Model.LoginModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class LoginController {
	LoginModel model = new LoginModel();
	
	private String _username, _password;
	
	@FXML
	private TextField txtLogin, txtPassword; 
	@FXML
	private Button btnLogin, btnRegister;
	
	public void onClickLogin(ActionEvent e)
	{
		_username = txtLogin.getText();
		_password = txtPassword.getText();
		
		if(_username.isEmpty() || _password.isEmpty())
			System.out.println("Voer een username of password in");
		else
			model.checkUsername(_username);
		
	}
	
	public void onClickRegister(ActionEvent e)
	{
		model.checkLogin(txtLogin.getText(), txtPassword.getText());
	}
}
