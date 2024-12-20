package frontend.rest;

import frontend.security.TokenService;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path( "/auth" )
public class AuthResource {

	@POST
	@Path( "/login" )
	@Consumes( MediaType.APPLICATION_JSON )
	@Produces( MediaType.APPLICATION_JSON )
	public Response login( UserLoginRequest loginRequest ) {
		// Egyszerű felhasználó ellenőrzés (hardcoded adatokkal)
		if ( "user".equals( loginRequest.getUsername() ) && "password".equals( loginRequest.getPassword() ) ) {
			String token = TokenService.generateToken( loginRequest.getUsername() );
			return Response.ok( new TokenResponse( token ) )
				.build();
		}
		return Response.status( Response.Status.UNAUTHORIZED )
			.entity( "Invalid credentials" )
			.build();
	}

	public static class UserLoginRequest {
		private String username;
		private String password;

		public String getUsername( ) {
			return username;
		}

		public void setUsername( String username ) {
			this.username = username;
		}

		public String getPassword( ) {
			return password;
		}

		public void setPassword( String password ) {
			this.password = password;
		}
	}

	public static class TokenResponse {
		private String token;

		public TokenResponse( String token ) {
			this.token = token;
		}

		public String getToken( ) {
			return token;
		}
	}
}
