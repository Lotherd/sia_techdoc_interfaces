package trax.aero.application;


import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
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

import org.tinylog.Logger;

import trax.aero.interfaces.IModelData;
import trax.aero.model.Wo;
import trax.aero.pojo.Print;
import trax.aero.pojo.jdf.JDFBean;
import trax.aero.pojo.xml.MODEL;
import trax.aero.pojo.xml.ROOT;
import trax.aero.util.MqUtilities;
import trax.aero.util.PrinterUtilities;


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
			Logger.error(e);
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
			Logger.info("Print WO:" +input.getWo() + ", PATH:" +input.getPath());
			data.sendPrintToOutBound(input);
		}
		catch(Exception e)
		{
			Logger.error(e);
       }finally {
	    	   try 
	   		{
	   			if(data.getCon() != null && !data.getCon().isClosed()) {
	   				data.getCon().close();
	   			}	
	   		} 
	   		catch (SQLException e) 
	   		{ 
	   			//Logger.error(e);
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
			
			//TODO create parent WO 
			Wo parent = data.createParentWo(root.getMODELS().size());
			Logger.info("Size: " +root.getMODELS().size() );
			int count = 1;
			
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
				xml=xml.replaceAll("&amp;re;", 		"&#xA;");
				Wo w= data.issueToTechDocRequest(model,xml );
				data.linkWoToParent(w,parent,new BigDecimal( count));
				data.sendRequestToPrintServer(model, xml, w);
				 try{
					if(data.getCon() != null && !data.getCon().isClosed())
						data.getCon().close();
				}catch (SQLException e) { 
					Logger.error(e);
				}
				 count++;
			}
		}catch (Exception e) {
			Logger.error(e);
		}finally {
	    	   try 
	   		{
	   			if(data.getCon() != null && !data.getCon().isClosed())
	   				data.getCon().close();
	   		} 
	   		catch (SQLException e) 
	   		{ 
				Logger.error(e);
	   		}
       }
	   return Response.ok().build();
	}
	
	@POST
	@Path("/initJDF")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN )
	public Response initJDF(String input)
	{
		try 
        {    
			JDFBean jdf = PrinterUtilities.initJDF(input);
				
				
				JAXBContext jc = JAXBContext.newInstance(JDFBean.class);
				Marshaller marshaller = jc.createMarshaller();
				marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
				StringWriter sw = new StringWriter();
				marshaller.marshal(jdf, sw);
				
				String xml = sw.toString();
				return Response.ok(xml).build();

		}
		catch(Exception e)
		{
			Logger.error(e);
       }
	   return Response.ok().build();
	}
	
}
