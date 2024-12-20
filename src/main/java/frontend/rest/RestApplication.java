package frontend.rest;

import org.glassfish.jersey.server.ResourceConfig;

public class RestApplication extends ResourceConfig {
	public RestApplication( ) {
		super();
		register( JsonResource.class );
		register( CarResource.class );
	}

}
