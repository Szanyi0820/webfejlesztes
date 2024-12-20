package frontend.security;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TokenService {
	private static final Map<String, String> issuedTokens = new HashMap<>();

	public static String generateToken( String username ) {
		String token = UUID.randomUUID()
			.toString(); // Egyedi token generálása
		issuedTokens.put( token, username ); // Token és felhasználó összerendelése
		return token;
	}

	public static boolean validateToken( String token ) {
		return issuedTokens.containsKey( token ); // Ellenőrzi, hogy létezik-e a token
	}

	public static String getUsernameFromToken( String token ) {
		return issuedTokens.get( token ); // Visszaadja a tokenhez tartozó felhasználót
	}

	public static void revokeToken( String token ) {
		issuedTokens.remove( token ); // Token visszavonása
	}
}
