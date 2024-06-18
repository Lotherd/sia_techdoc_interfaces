package trax.aero.util;

import java.io.StringWriter;
import java.util.logging.Logger;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import trax.aero.logger.LogManager;
import trax.aero.pojo.Dw_Wo_Pack_Print;
import trax.types.PrintQueueJob;

public class PrinterUtilities {

	static Logger logger = LogManager.getLogger("TechdocEDCO");

	
	public static int sendWorkPackPrintJob(String printWindow, Dw_Wo_Pack_Print dwSel) {
		int job = 0;
		String s_wo_print = "";

		try {

			StringWriter sw = new StringWriter();
			JAXBContext jc = JAXBContext.newInstance(Dw_Wo_Pack_Print.class);
			Marshaller marshaller = jc.createMarshaller();
			marshaller.marshal(dwSel, sw);
			String xml = sw.toString();
					
			xml = xml.replaceAll("(<\\?xml.*?\\?>)","");
		

			if ("Y".equalsIgnoreCase(dwSel.getRow().getMulti_wo_selected()))
				s_wo_print = "Work Order Pack Print Multi Wo : " + dwSel.getRow().getL_wo_multi();
			else
				s_wo_print = "Work Order Pack Print Wo: " + dwSel.getRow().getWo();

			PrintQueueJob propJob = new PrintQueueJob();
			propJob.setPrintWindow(printWindow);
			propJob.setPrintTitle(s_wo_print);
			propJob.setPrintParameters(xml.getBytes());
			propJob.setUser("ADM");

			addJobToJMSQueue(propJob, "Y");

			logger.info("Print Job " +  dwSel.getRow().getWo() + " Has been successfuly sent to the print queue");

		} catch (Exception e) {
			e.printStackTrace();
		}

		return job;
	}
	
	private static void addJobToJMSQueue(PrintQueueJob propJob, String isWPP) throws Exception {
		propJob.setIsWPP(isWPP);

		

		String url = "Y".equals(isWPP) ? System.getProperty("Trax_Print_WPP_URL") + "rest/print/printwpp"
				: System.getProperty("Trax_Print_URL") + "rest/print/print";
		Client client = null;

		try {

			client = ClientBuilder.newClient();
			System.out.println("Calling Print web service : " + "'" + url + "'");

			client.target(url).request().post(Entity.entity(propJob, MediaType.APPLICATION_JSON));

			System.out.println("After Print web service ");

		} catch (Exception e) {
			throw e;
		} finally {
			if (client != null)
				client.close();
		}
	}
	
	
	public static boolean greaterThan(int p1, int p2) {

		return p1 > p2;
	}
	
	
}
