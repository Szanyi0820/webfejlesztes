package frontend.rest;

import com.fasterxml.jackson.databind.ObjectMapper;

import cars.Owner;

public class Ownertest {
	public static void main( String[] args ) throws Exception {
		String jsonInput = """
				{
				    "properties": {
				        "plateNumber": "CPR-395",
				        "name": "Sarah Johnson",
				        "contact_number": "555-456-7890test",
				        "email": "sarah.johnson@email.com"
				    }
				}
				""";

		ObjectMapper mapper = new ObjectMapper();
		Owner owner = mapper.readValue( jsonInput, Owner.class );

		System.out.println( "Deserialized owner properties: " + owner.getProperties() );
		System.out.println( "Contact Number: " + owner.getProperties()
			.getContactNumber() );
	}
}
