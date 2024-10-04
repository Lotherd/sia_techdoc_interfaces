package trax.aero.application;


import java.io.StringReader;
import java.io.StringWriter;
import java.sql.SQLException;

import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import trax.aero.interfaces.IModelData;
import trax.aero.model.Wo;
import trax.aero.pojo.Print;
import trax.aero.pojo.xml.MODEL;
import trax.aero.pojo.xml.ROOT;
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
			System.out.println("Print WO:" +input.getWo() + ", PATH:" +input.getPath());
			data.print(input);
		}
		catch(Exception e)
		{
			e.printStackTrace();
       }finally {
	    	   try 
	   		{
	   			if(data.getCon() != null && !data.getCon().isClosed()) {
	   				data.getCon().close();
	   			}	
	   		} 
	   		catch (SQLException e) 
	   		{ 
	   			//e.printStackTrace();
	   		}
       }
	   return Response.ok().build();
	}

	@POST
	@Path("/issue")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN )
	public Response issue(String message)
	{
		String printer = null;
		
		
		try {
			JAXBContext jc = JAXBContext.newInstance(ROOT.class);
			Unmarshaller unmarshaller = jc.createUnmarshaller();
			message = "<ROOT>"+ message + "</ROOT>";
			
			ROOT  root = null;
			StringReader sr = new StringReader(message);

			root = (ROOT) unmarshaller.unmarshal(sr);	
			
			for(MODEL model : root.getMODELS()) {
				printer = data.filterADDATTR(model.getEFFECTIVITY().getJOBCARD().getJOBI().getPLI().getADDATTR(), "PRINTER-NAME");
				jc = JAXBContext.newInstance(MODEL.class);
				Marshaller marshaller = jc.createMarshaller();
				marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
				StringWriter sw = new StringWriter();
				marshaller.marshal(model, sw);
				
				String xml = sw.toString();
				xml=xml.replaceAll("&amp;apos;", 	"&apos;");
				xml=xml.replaceAll("&amp;gt;", 		"&gt;");
				xml=xml.replaceAll("&amp;lt;", 		"&lt;");
				xml=xml.replaceAll("&amp;quot;", 	"&quot;");
				xml=xml.replaceAll("&amp;re;", 		"");
				Wo w= data.issueTo(model,xml );
				data.sendPrint(model, xml, w);
				 try{
					if(data.getCon() != null && !data.getCon().isClosed())
						data.getCon().close();
				}catch (SQLException e) { 
					e.printStackTrace();
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
	    	   try 
	   		{
	   			if(data.getCon() != null && !data.getCon().isClosed())
	   				data.getCon().close();
	   		} 
	   		catch (SQLException e) 
	   		{ 
	   			e.printStackTrace();
	   		}
       }
	   return Response.ok().build();
	}
	
}
