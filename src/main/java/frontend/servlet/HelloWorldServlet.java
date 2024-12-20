package frontend.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDatabase;
import com.arangodb.entity.BaseDocument;

import frontend.db.DBUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet( value = "/helloWorld", name = "HelloWorldServlet", displayName = "HelloWorldServlet", urlPatterns = "/helloWorld" )
public class HelloWorldServlet extends HttpServlet {

	private static final long serialVersionUID = -4694315101350916258L;

	@Override
	protected void doGet( HttpServletRequest req, HttpServletResponse resp ) throws ServletException, IOException {
		resp.setHeader( "Access-Control-Allow-Origin", "http://localhost:4200" );
		resp.setHeader( "Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS" );
		resp.setHeader( "Access-Control-Allow-Headers", "Content-Type, Authorization" );
		resp.setHeader( "Access-Control-Allow-Credentials", "true" );

		ServletOutputStream printStream = resp.getOutputStream();

		try {
			ArangoDatabase db = DBUtil.getDataBase();
			if ( db == null ) {
				printStream.println( "Database connection error!" );
				return;
			}

			String collectionName = "Cars";
			if ( !db.collection( collectionName )
				.exists() ) {
				DBUtil.createCollection( collectionName );
			}

			String aqlQueryString = "FOR r IN @@collectionName RETURN r";
			Map<String, Object> bindVars = new HashMap<>();
			bindVars.put( "@collectionName", collectionName );
			ArangoCursor<BaseDocument> retCursor = db.query( aqlQueryString, bindVars, null, BaseDocument.class );

			List<BaseDocument> result = retCursor.asListRemaining();

			// Building HTML content
			StringBuilder htmlBuilder = new StringBuilder( "<html><head><meta charset=\"UTF-8\"><title>Car List</title></head><body>" );
			htmlBuilder.append( "<h1>Car List</h1>" );
			htmlBuilder.append( "<table border='1'>" )
				.append( "<tr><th>Brand Name</th><th>Color</th><th>Fuel Type</th><th>Max Speed</th><th>Price</th><th>Type Name</th><th>Factory Date</th><th>Plate Number</th></tr>" );

			for ( BaseDocument doc:result ) {
				Map<String, Object> carType = (Map<String, Object>) doc.getAttribute( "carType" );
				String factoryDate = (String) doc.getAttribute( "factoryDate" );
				String plateNumber = (String) doc.getAttribute( "plateNumber" );

				htmlBuilder.append( "<tr>" )
					.append( "<td>" )
					.append( carType.get( "brandName" ) )
					.append( "</td>" )
					.append( "<td>" )
					.append( carType.get( "color" ) )
					.append( "</td>" )
					.append( "<td>" )
					.append( carType.get( "fuelType" ) )
					.append( "</td>" )
					.append( "<td>" )
					.append( carType.get( "maxSpeed" ) )
					.append( "</td>" )
					.append( "<td>" )
					.append( carType.get( "price" ) )
					.append( "</td>" )
					.append( "<td>" )
					.append( carType.get( "typeName" ) )
					.append( "</td>" )
					.append( "<td>" )
					.append( factoryDate )
					.append( "</td>" )
					.append( "<td>" )
					.append( plateNumber )
					.append( "</td>" )
					.append( "</tr>" );
			}

			htmlBuilder.append( "</table></body></html>" );

			// Sending HTML content as response
			resp.setContentType( "text/html" );
			printStream.println( htmlBuilder.toString() );
		} catch ( Exception e ) {
			e.printStackTrace();
			printStream.println( "Error occurred while processing your request!" );
		}
	}
}
