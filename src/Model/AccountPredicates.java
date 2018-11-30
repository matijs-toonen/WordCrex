package Model;

import java.util.function.Predicate;

public class AccountPredicates {
	public static Predicate<Account> getAccountByUsername(String username){
		return account -> account.getUsername().equals(username);
	}
}
