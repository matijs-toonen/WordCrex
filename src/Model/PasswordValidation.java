package Model;

public class PasswordValidation {
	public static boolean isValid(String password)
	{
		return password.length() >= 5 && password.length() <= 25;
	}
}
