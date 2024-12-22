package frontend.security;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TokenService {
	private static final Map<String, UserInfo> issuedTokens = new HashMap<>();

	private static class UserInfo {
		private final String username;
		private final String role;

		public UserInfo( String username, String role ) {
			this.username = username;
			this.role = role;
		}

		public String getUsername( ) {
			return username;
		}

		public String getRole( ) {
			return role;
		}
	}

	public static String generateToken( String username, String role ) {
		String token = UUID.randomUUID()
			.toString();
		issuedTokens.put( token, new UserInfo( username, role ) );
		return token;
	}

	public static boolean validateToken( String token ) {
		return issuedTokens.containsKey( token );
	}

	public static String getUsernameFromToken( String token ) {
		UserInfo userInfo = issuedTokens.get( token );
		return userInfo != null ? userInfo.getUsername() : null;
	}

	public static String getRoleFromToken( String token ) {
		UserInfo userInfo = issuedTokens.get( token );
		return userInfo != null ? userInfo.getRole() : null;
	}

	public static void revokeToken( String token ) {
		issuedTokens.remove( token );
	}
}
