package trax.aero.application;


import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import trax.aero.interfaces.IModelData;
import trax.aero.pojo.Print;
import trax.aero.util.MqUtilities;



@Path("/Service")
public class Service {
	
	@EJB IModelData data;

	
	@POST
	@Path("/insertString")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN )
	public Response insertXml(String input)
	{
		try 
        {    
			MqUtilities.sendMqText(input);
		}
		catch(Exception e)
		{
			e.printStackTrace();
       }
	   return Response.ok().build();
	}
	
	@POST
	@Path("/print")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN )
	public Response print(Print input)
	{
		try 
        {    
			data.print(input);
		}
		catch(Exception e)
		{
			e.printStackTrace();
       }
	   return Response.ok().build();
	}
	
}