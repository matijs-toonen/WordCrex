package Model;

public class PasswordValidation {
	public boolean isValid(String password)
	{
		boolean isValid = false;
		
		if(password.length() >= 5 && password.length() <= 25)
		{
			isValid = true;
		}
		
		return isValid;
	}
}
