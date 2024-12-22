package frontend.rest;

import org.glassfish.jersey.server.ResourceConfig;

import frontend.security.JwtAuthFilter;

public class RestApplication extends ResourceConfig {
	public RestApplication( ) {
		super();
		register( JsonResource.class );
		register( CarResource.class );
		register( JwtAuthFilter.class );
		register( AuthResource.class );
	}

}
