package trax.aero.util;

import java.io.StringReader;
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.xml.bind.JAXBContext;
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
					
					switch(printer){
						case "ECXX": 
							logger.info("Issue To EDCO");
							data.issueToEDCO(model,message );
							break;
						case "TRAX":
							logger.info("Issue To TRAX");
							data.issueToTRAX(model ,message);
							break;
						default:
							logger.info("Issue To Printer " + printer );
							data.issueToPrinter(model,message);
							break;	
					}
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