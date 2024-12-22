package frontend.security;

import java.io.IOException;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

@Provider
public class JwtAuthFilter implements ContainerRequestFilter {

	@Override
	public void filter( ContainerRequestContext requestContext ) throws IOException {
		String path = requestContext.getUriInfo()
			.getPath();
		System.out.println( "Path: " + path );

		// Bypass token validation for the login endpoint
		if ( path.equals( "auth/login" ) || path.equals( "auth/register" ) ) {
			System.out.println( "Skipping token validation for login endpoint" );
			return;
		}

		String authHeader = requestContext.getHeaderString( "Authorization" );
		System.out.println( "Authorization Header: " + authHeader );

		if ( authHeader == null || !authHeader.startsWith( "Bearer " ) ) {
			System.out.println( "Missing or invalid Authorization header" );
			requestContext.abortWith( Response.status( Response.Status.UNAUTHORIZED )
				.build() );
			return;
		}

		String token = authHeader.substring( "Bearer ".length() );
		System.out.println( "Token: " + token );

		if ( !TokenService.validateToken( token ) ) {
			System.out.println( "Invalid token" );
			requestContext.abortWith( Response.status( Response.Status.UNAUTHORIZED )
				.entity( "Invalid token" )
				.build() );
		} else {
			System.out.println( "Token is valid" );
		}
	}

}
