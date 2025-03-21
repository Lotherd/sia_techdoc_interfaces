package trax.aero.util;

import java.io.File;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.apache.commons.io.FilenameUtils;
import org.tinylog.Logger;

import trax.aero.pojo.Dw_Wo_Pack_Print;
import trax.types.PrintQueueJob;

public class PrinterUtilities {


	static ArrayList<String> heavyPrinters = new ArrayList<String>(Arrays.asList("EC61", "EC62", "EC63", "SINW","SIO1", "EC1O","SIOP"));
	static ArrayList<String> heavyPrintersRicoh = new ArrayList<String>(Arrays.asList("EC1O","SIOP"));
	static ArrayList<String> heavyPrintersOcepdf = new ArrayList<String>(Arrays.asList("EC61", "EC62", "EC63", "SINW","SIO1"));
	
	public static int sendWorkPackPrintJob(String printWindow, Dw_Wo_Pack_Print dwSel) throws Exception {
		int job = 0;
		String s_wo_print = "";

		

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

			Logger.info("Print Job " +  dwSel.getRow().getWo() + " Has been successfuly sent to the print queue");

		

		return job;
	}
	
	private static void addJobToJMSQueue(PrintQueueJob propJob, String isWPP) throws Exception {
		propJob.setIsWPP(isWPP);

		

		String url = "Y".equals(isWPP) ? System.getProperty("Trax_Print_WPP_URL") + "rest/print/printwpp"
				: System.getProperty("Trax_Print_URL") + "rest/print/print";
		Client client = null;

		try {

			client = ClientBuilder.newClient();
			Logger.info("Calling Print web service : " + "'" + url + "'");

			client.target(url).request().post(Entity.entity(propJob, MediaType.APPLICATION_JSON));

			Logger.info("After Print web service ");

		} catch (Exception e) {
			throw e;
		} finally {
			if (client != null)
				client.close();
		}
	}
	
	public static void sendToPrinterHeavyLP(String printService, File file, String side, String tray) throws Exception {

		if (file != null && printService != null && file.exists() && file.isFile()) {
			Logger.info("Job received for printer: " + printService);

			try {
				String ricohCommands = "";
				String oceCommands = "";
				if(heavyPrintersOcepdf.contains(printService)) {
					oceCommands += " -o \"StapleWhen=EndOfSet\"" + " -o \"OCStaple=TopLeftPortrait\"";
					
					//InputSlot=1Tray  2Tray 3Tray OCE
					switch(tray){
		            	case "1": tray = "Plain";
		            	case "2": tray = "TabInsert"; 
		            	case "3": tray = "Tab";             		
		            	case "4": tray = "PrePunched";
		            	default: tray = "PrePunched";
					}
					oceCommands += " -o \"InputSlot="+tray+"\"";
					//Duplex=DuplexNoTumble OCE 
					if(side.equalsIgnoreCase("DUPLEX")) {
						side = "DuplexNoTumble";
		            }else {
		            	side = "None";
		            }
					oceCommands+=" -o \"Duplex="+side+"\"";
				}else if(heavyPrintersRicoh.contains(printService)) {
					ricohCommands += " -o \"StapleLocation=UpperLeft\"";
					
					//InputSlot=1Tray  2Tray 3Tray RICHO
					switch(tray){
		            	case "1": tray = "1Tray";
		            	case "2": tray = "2Tray"; 
		            	case "3": tray = "3Tray";             		
		            	case "4": tray = "MultiTray";
		            	default: tray = "MultiTray";
					}
					ricohCommands += " -o \"InputSlot="+tray+"\"";
					//Duplex=DuplexNoTumble RICHO 
					if(side.equalsIgnoreCase("DUPLEX")) {
						side = "DuplexNoTumble";
		            }else {
		            	side = "None";
		            }
					ricohCommands+=" -o \"Duplex="+side+"\"";
				}
				// Command to print the document with duplex and tray options using lp on Linux
	            String command = "lp -d "+printService+oceCommands+ricohCommands+" " + file.getAbsolutePath();
	            Logger.info("Command: " + command);
	            // Create the process builder
	            ProcessBuilder processBuilder = new ProcessBuilder(command.split(" "));

	            // Start the process (print)
	            Process process = processBuilder.start();

	            // Wait for the process to finish
	            int exitCode = process.waitFor();
	            if (exitCode == 0) {
	                Logger.info("Print job submitted successfully.");
	            } else {
	                Logger.info("Error occurred during printing: " + exitCode);
	            }		      

			} catch (Exception e) {
				Logger.info("Exception printing JOB***************************");
				Logger.error(e);
				throw e;
			}
		}
	}
	
	
	public static boolean greaterThan(int p1, int p2) {

		return p1 > p2;
	}
	
	
	
	
	public static void  sendPrint(String printer, String path, String side, String tray) throws Exception {
		
		//ArrayList<File> pdfs = new ArrayList<File>();
		String fileLocOut =  System.getProperty("TECH_fileLocOut") ;
		try {
			
			
			//PDF
			File print = new File(path);
			
			File theDir = new File(fileLocOut+File.separator+FilenameUtils.removeExtension(print.getName()));
			if (!theDir.exists()){
			    theDir.mkdirs();
			}
			Logger.info("MOVE " +print.toPath() + " TO " 
			+fileLocOut+File.separator+FilenameUtils.removeExtension(print.getName())+File.separator +print.getName());
			Files.move(print.toPath(), new File(fileLocOut+File.separator+FilenameUtils.removeExtension(print.getName())
			+File.separator +print.getName()).toPath(), StandardCopyOption.REPLACE_EXISTING);
			
			//Print server folder logic
			File localPrint = new File(fileLocOut+File.separator+FilenameUtils.removeExtension(print.getName())+File.separator +print.getName());
			//sendToPrinter(printer, print);
			if(heavyPrinters.contains(printer)) {
				sendToPrinterHeavyLP(printer, localPrint,side,tray);
			}else {
				sendToPrinterLP(printer, localPrint);
			}
		} catch (Exception e) {
			Logger.error(e);
			throw e;
		}		
		return;
	}

	private static void sendToPrinterLP(String printer, File print) throws Exception {
		 try {
	            // Create a ProcessBuilder to run the 'lp' command
	            ProcessBuilder processBuilder = new ProcessBuilder("lp", "-d", printer, print.getPath());
	            
	            // Start the process
	            Process process = processBuilder.start();
	            
	            // Wait for the process to complete
	            int exitCode = process.waitFor();
	            if (exitCode == 0) {
	                Logger.info("Printing completed successfully!");
	            } else {
	                Logger.info("Error occurred while printing. Exit code: " + exitCode);
	                throw new Exception("Error occurred while printing. Exit code: " + exitCode);
	            }
	        } catch (Exception exc) {
	        	exc.printStackTrace();
				Logger.info("Exception printing JOB***************************");
				Logger.info(exc.getMessage());
				throw exc;
	        }
		
	}
	
}
