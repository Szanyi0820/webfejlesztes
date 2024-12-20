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
		String authHeader = requestContext.getHeaderString( "Authorization" );
		if ( authHeader == null || !authHeader.startsWith( "Bearer " ) ) {
			requestContext.abortWith( Response.status( Response.Status.UNAUTHORIZED )
				.build() );
			return;
		}

		String token = authHeader.substring( "Bearer ".length() );
		if ( !TokenService.validateToken( token ) ) {
			requestContext.abortWith( Response.status( Response.Status.UNAUTHORIZED )
				.entity( "Invalid token" )
				.build() );
		}

		// A token validált, folytathatja a kérést
	}
}
