package frontend.servlet;

import static java.lang.String.join;
import static java.lang.System.lineSeparator;
import static org.apache.commons.lang3.StringUtils.wrap;

import java.io.IOException;
import java.util.Enumeration;
import java.util.StringJoiner;
import java.util.logging.Logger;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CrossOriginResourceSharingFilter implements Filter {

	private static final Logger logger = Logger.getLogger( CrossOriginResourceSharingFilter.class.getName() );

	@Override
	public void doFilter( ServletRequest request, ServletResponse response, FilterChain chain ) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;

		// Add CORS headers if CORS is enabled
		if ( isCORSEnabled() ) {
			addCORSHeaders( req, res );
		}

		String uri = req.getRequestURI();
		if ( uri != null && !uri.contains( "/version/" ) && !"OPTIONS".equals( req.getMethod() ) ) {
			logger.info( ( ) -> {
				StringJoiner requestLog = new StringJoiner( lineSeparator() ).add( join( " ", req.getMethod(), uri ) )
					.add( req.getQueryString() )
					.add( req.getHeader( "authorization" ) );
				for ( Enumeration<String> headerNames = req.getHeaderNames(); headerNames.hasMoreElements(); ) {
					String headerName = headerNames.nextElement();
					requestLog.add( headerName + " : " + req.getHeader( headerName ) );
				}
				return wrap( wrap( requestLog.toString(), lineSeparator() ), "----" );
			} );
		}

		// For OPTIONS request, we handle preflight here and return immediately
		if ( "OPTIONS".equals( req.getMethod() ) ) {
			res.setStatus( HttpServletResponse.SC_OK );
			return;
		}

		chain.doFilter( request, response );
	}

	private boolean isCORSEnabled( ) {
		String corsProperty = System.getProperty( "cors" );
		return corsProperty == null || "true".equalsIgnoreCase( corsProperty );
	}

	private void addCORSHeaders( HttpServletRequest req, HttpServletResponse res ) {
		String origin = req.getHeader( "Origin" );

		if ( origin != null ) {
			// Allow only your frontend's origin for security
			if ( origin.equals( "http://localhost:4200" ) ) {
				res.addHeader( "Access-Control-Allow-Origin", origin );
			} else {
				// Optional: Log or handle unauthorized origin here.
				res.addHeader( "Access-Control-Allow-Origin", "*" );
			}

			// Allow credentials and expose necessary headers
			res.addHeader( "Access-Control-Allow-Credentials", "true" );
			res.addHeader( "Access-Control-Expose-Headers", "Selected-Abydos-Role" );

			// Set allowed headers, including Authorization and Content-Type for API requests
			res.addHeader( "Access-Control-Allow-Headers", "Authorization,Content-Type,Selected-Abydos-Role,Expect" );

			// Allow methods for CORS
			res.addHeader( "Access-Control-Allow-Methods", "PUT, POST, GET, DELETE, OPTIONS" );
		}
	}

	@Override
	public void init( FilterConfig paramFilterConfig ) throws ServletException {
		// Optional initialization logic (not needed in this case)
	}

	@Override
	public void destroy( ) {
		// Optional cleanup logic (not needed in this case)
	}
}
