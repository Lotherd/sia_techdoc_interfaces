package trax.aero.util;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import trax.aero.interfaces.IModelData;
import trax.aero.logger.LogManager;
import trax.aero.pojo.xml.ADDATTR;
import trax.aero.pojo.xml.MODEL;
import trax.aero.pojo.xml.ROOT;



public class RunAble implements Runnable {
	
	//Variables
	Logger logger = LogManager.getLogger("TechdocEDCO");
	@EJB IModelData data;

	
	public RunAble(IModelData data) {
		this.data = data;
	}
	
	private void process() throws Exception {
		
		String message = null, printer = null;
		JAXBContext jc = JAXBContext.newInstance(ROOT.class);
		Unmarshaller unmarshaller = jc.createUnmarshaller();
		
		
		message = MqUtilities.receiveMqText();
		
		if(message != null) {
			
			logger.info(message);
			try {
				
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
					data.issueTo(model,xml );
				}
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
	
	public void run() 
	{
		try {
			process();
		}catch(Exception e) {
			e.printStackTrace();
		}		
	}
}