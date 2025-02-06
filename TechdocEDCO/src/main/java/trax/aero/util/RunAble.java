package trax.aero.util;

import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

import javax.ejb.EJB;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.tinylog.Logger;

import trax.aero.interfaces.IModelData;
import trax.aero.model.Wo;
import trax.aero.pojo.xml.ADDATTR;
import trax.aero.pojo.xml.MODEL;
import trax.aero.pojo.xml.ROOT;



public class RunAble implements Runnable {
	
	//Variables
	@EJB IModelData data;
	int minuteCounter = 0;
	boolean sendEmail = false;
	
	public RunAble(IModelData data) {
		this.data = data;
	}
	
	private void process() throws Exception {
		
		String message = null, printer = null;
		JAXBContext jc = JAXBContext.newInstance(ROOT.class);
		Unmarshaller unmarshaller = jc.createUnmarshaller();
		
		minuteCounter++;
		if (minuteCounter >= 5) {
			sendEmail = true;
			minuteCounter = 0;
		}
		message = MqUtilities.receiveMqText(sendEmail);
		sendEmail = false;
		
		if(message != null) {
			
			Logger.info(message);
			try {
				
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
			
				//TODO save WO data to P
			}catch (Exception e) {
				Logger.error(e);
			}
		}
		
	}
	
	
	public void run() 
	{
		try {
			process();
			data.processBatFile();
		}catch(Exception e) {
			Logger.error(e);
		}		
	}
}