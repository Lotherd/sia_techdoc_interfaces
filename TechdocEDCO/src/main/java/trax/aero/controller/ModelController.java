package trax.aero.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Logger;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.SimpleEmail;

import trax.aero.logger.LogManager;
import trax.aero.pojo.acknowledgement.PrintAck;

public class ModelController {
	
	
	static Logger logger = LogManager.getLogger("TechdocEDCO");
	
	static String errors = "";
	public ModelController()
	{
		
	}
	
	
	
	//MOD 19 
	public static void sendEmail(String wo,
			String revision,
			String date,
			String time,
			ArrayList<PrintAck> pAcks)
	{
		try
		{
			
			String fromEmail = System.getProperty("fromEmail");
			String host = System.getProperty("fromHost");
			String port = System.getProperty("fromPort");
			String toEmail = System.getProperty("Techdoc_toEmail");
			
			ArrayList<String>  emailsList = new ArrayList<String> (Arrays.asList(toEmail.split(",")));
			
			
			
			Email email = new SimpleEmail();
			email.setHostName(host);
			email.setSmtpPort(Integer.valueOf(port));
			email.setFrom(fromEmail);
			email.setSubject("TECHDOC-TRAX - PDF attachment not printed["+wo+"] - ALERT");
			for(String emails: emailsList)
	        {
				email.addTo(emails);
	        }
			
			//TODO
			email.setMsg("Hi All,\r\n" + 
					"Please find below PDF attachment details for not printed :-\r\n" + 
					"\r\n" + 
					"1. SAP Revision: "+revision+"\r\n" + 
					"2. iDOC Date: "+date+"\r\n" + 
					"3. iDOC Time: "+time+"\r\n" + 
					""+
					"Attachment Missing\r\n" + 
					"\r\n" + 
					"\r\n" + 
					"\r\n" + 
					"");
			
			email.send();
			logger.info("SUCCESS");
		}
		catch(Exception e)
		{
			
			logger.severe(e.getMessage());
			logger.severe(e.toString());
			logger.severe("Email not found");
			
		}
		finally
		{
			errors = "";
		}
	}
}
