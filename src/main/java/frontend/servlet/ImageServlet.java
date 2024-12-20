package frontend.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import frontend.resources.ResourceLoader;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class ImageServlet
 */
@WebServlet( urlPatterns = { "/ImageServlet" } )
public class ImageServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor.
	 */
	public ImageServlet( ) {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
		// TODO Auto-generated method stub
		InputStream iS = ResourceLoader.class.getResourceAsStream( "nyilatkozat.pdf" );
		OutputStream oS = response.getOutputStream();
		iS.transferTo( oS );
		iS.close();
		oS.close();
	}

}
