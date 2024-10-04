package trax.aero.util;

import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Logger;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.MediaTray;
import javax.print.attribute.standard.OrientationRequested;
import javax.print.attribute.standard.Sides;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.io.FilenameUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPrintable;
import org.apache.pdfbox.printing.Scaling;

import trax.aero.logger.LogManager;
import trax.aero.pojo.Dw_Wo_Pack_Print;
import trax.aero.pojo.jdf.AuditPool;
import trax.aero.pojo.jdf.Comment;
import trax.aero.pojo.jdf.Contact;
import trax.aero.pojo.jdf.Created;
import trax.aero.pojo.jdf.CustomerInfo;
import trax.aero.pojo.jdf.CustomerInfoLink;
import trax.aero.pojo.jdf.DigitalPrintingParams;
import trax.aero.pojo.jdf.DigitalPrintingParamsLink;
import trax.aero.pojo.jdf.FoldingParams;
import trax.aero.pojo.jdf.FoldingParamsLink;
import trax.aero.pojo.jdf.HoleMakingParams;
import trax.aero.pojo.jdf.HoleMakingParamsLink;
import trax.aero.pojo.jdf.InterpretingParams;
import trax.aero.pojo.jdf.InterpretingParamsLink;
import trax.aero.pojo.jdf.JDF;
import trax.aero.pojo.jdf.LayoutElement;
import trax.aero.pojo.jdf.LayoutPreparationParams;
import trax.aero.pojo.jdf.LayoutPreparationParamsLink;
import trax.aero.pojo.jdf.MailboxDetails;
import trax.aero.pojo.jdf.MailboxDetailsLink;
import trax.aero.pojo.jdf.Media;
import trax.aero.pojo.jdf.MediaLink;
import trax.aero.pojo.jdf.RenderingParams;
import trax.aero.pojo.jdf.RenderingParamsLink;
import trax.aero.pojo.jdf.ResourceLinkPool;
import trax.aero.pojo.jdf.ResourcePool;
import trax.aero.pojo.jdf.RunList;
import trax.aero.pojo.jdf.RunListLink;
import trax.aero.pojo.jdf.ScreeningParams;
import trax.aero.pojo.jdf.ScreeningParamsLink;
import trax.aero.pojo.jdf.StitchingParams;
import trax.aero.pojo.jdf.StitchingParamsLink;
import trax.aero.pojo.xml.MODEL;
import trax.aero.pojo.xml.ROOT;
import trax.types.PrintQueueJob;

public class PrinterUtilities {

	static Logger logger = LogManager.getLogger("TechdocEDCO");

	static ArrayList<String> heavyPrinters = new ArrayList<String>(Arrays.asList("EC61", "EC62", "EC63", "SINW","SIO1"));
	
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
	
	public static void sendToPrinter(String printService, File file) {

		if (file != null && printService != null && file.exists() && file.isFile()) {
			System.out.println("Job received for printer: " + printService);

			try {

	            PDDocument document = PDDocument.load(file);

	            // Create a PDF printable instance
	            PDFPrintable pdfPrintable = new PDFPrintable(document, Scaling.SHRINK_TO_FIT);

				PrintService printServ = null;
	            
	            // Create a PrinterJob
	            PrinterJob job = PrinterJob.getPrinterJob();
	            job.setPrintable(pdfPrintable);

	            // Find a CUPS printer
	            PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
	            
				System.out.println("Printer services avaible:");

				if (printServices != null && printServices.length > 0) {
					for (int i = 0; i < printServices.length; i++) {
						PrintService servIter = printServices[i];

						System.out.println(servIter.getName());

						if (servIter.getName().equalsIgnoreCase(printService))
							printServ = servIter;
					}
				} else
					System.out.println("Not printer services avaible");

				if (printServ != null) {
					
					job.setPrintService(printServ);

		            // Set print attributes (like orientation, number of copies, etc.)
		            PrintRequestAttributeSet attributes = new HashPrintRequestAttributeSet();
		            attributes.add(new Copies(1)); // Number of copies
		            attributes.add(OrientationRequested.PORTRAIT); // Portrait or landscape

		            // Print the document
		            job.print(attributes);

		            // Close the document
		            document.close();

		            System.out.println("Printing completed!");
					

				}

			} catch (Exception exc) {
				exc.printStackTrace();
				System.out.println("Exception printing JOB***************************");
				System.out.println(exc.getMessage());
			}
		}
	}
	
	
	public static void sendToPrinterHeavy(String printService, File file) {

		if (file != null && printService != null && file.exists() && file.isFile()) {
			System.out.println("Job received for printer: " + printService);

			try {

				addJdfToPdf(printService, file);
				
	            PrintService printServ = null;
	            
	            

	            // Find a CUPS printer
	            PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
	            
				System.out.println("Printer services avaible:");

				if (printServices != null && printServices.length > 0) {
					for (int i = 0; i < printServices.length; i++) {
						PrintService servIter = printServices[i];

						System.out.println(servIter.getName());

						if (servIter.getName().equalsIgnoreCase(printService))
							printServ = servIter;
					}
				} else
					System.out.println("Not printer services avaible");

				if (printServ != null) {
					
					 // Create a print job
		            DocPrintJob printJob = printServ.createPrintJob();

		            InputStream is = new FileInputStream(file);

					// Create a document to print (in this case, a PDF)
		            Doc doc = new SimpleDoc(is, DocFlavor.INPUT_STREAM.PDF, null);
					
		            // Set print attributes (like orientation, number of copies, etc.)
		            PrintRequestAttributeSet attributes = new HashPrintRequestAttributeSet();
		            // Set tray selection to Tray 2
		            attributes.add(MediaTray.BOTTOM);

		            // Enable duplex printing (double-sided)
		            attributes.add(Sides.DUPLEX);  // Sides.DUPLEX is for double-sided, Sides.ONE_SIDED for single-sided

		            // Set other optional attributes (e.g., number of copies, orientation)
		            attributes.add(new Copies(1));  // Set the number of copies to 1
		            attributes.add(OrientationRequested.PORTRAIT);  // Portrait orientation

		            // Send the print job with the specified attributes
		            printJob.print(doc, attributes);


		            is.close();
		            

		            System.out.println("Printing completed!");
					

				}

			} catch (Exception exc) {
				exc.printStackTrace();
				System.out.println("Exception printing JOB***************************");
				System.out.println(exc.getMessage());
			}
		}
	}
	
	
	public static boolean greaterThan(int p1, int p2) {

		return p1 > p2;
	}
	
	
	
	
	public static void  sendPrint(String printer, String path) {
		
		//ArrayList<File> pdfs = new ArrayList<File>();
		String fileLocOut =  System.getProperty("TECH_fileLocOut") ;
		try {
			
			
			//PDF
			File print = new File(path);
			
			File theDir = new File(fileLocOut+File.separator+FilenameUtils.removeExtension(print.getName()));
			if (!theDir.exists()){
			    theDir.mkdirs();
			}
			System.out.println("MOVE " +print.toPath() + " TO " 
			+fileLocOut+File.separator+FilenameUtils.removeExtension(print.getName())+File.separator +print.getName());
			Files.move(print.toPath(), new File(fileLocOut+File.separator+FilenameUtils.removeExtension(print.getName())
			+File.separator +print.getName()).toPath(), StandardCopyOption.REPLACE_EXISTING);
			
			//Print server folder logic
			File localPrint = new File(fileLocOut+File.separator+FilenameUtils.removeExtension(print.getName())+File.separator +print.getName());
			//sendToPrinter(printer, print);
			if(heavyPrinters.contains(printer)) {
				sendToPrinterHeavy(printer, localPrint);
			}else {
				sendToPrinterLP(printer, localPrint);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}		
		return;
	}

	private static void sendToPrinterLP(String printer, File print) {
		 try {
	            // Create a ProcessBuilder to run the 'lp' command
	            ProcessBuilder processBuilder = new ProcessBuilder("lp", "-d", printer, print.getPath());
	            
	            // Start the process
	            Process process = processBuilder.start();
	            
	            // Wait for the process to complete
	            int exitCode = process.waitFor();
	            if (exitCode == 0) {
	                System.out.println("Printing completed successfully!");
	            } else {
	                System.out.println("Error occurred while printing. Exit code: " + exitCode);
	            }
	        } catch (Exception exc) {
	        	exc.printStackTrace();
				System.out.println("Exception printing JOB***************************");
				System.out.println(exc.getMessage());
	        }
		
	}
	
	
	private static void addJdfToPdf(String printer, File print) throws Exception {
		
		 JDF jdf = initJDF(print.getName());
		
		
		JAXBContext jc = JAXBContext.newInstance(JDF.class);
		jc = JAXBContext.newInstance(MODEL.class);
		Marshaller marshaller = jc.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		StringWriter sw = new StringWriter();
		marshaller.marshal(jdf, sw);
		
		String xml = sw.toString();
		Files.write(Paths.get(print.getPath()), xml.getBytes(), StandardOpenOption.APPEND);
		 
		 
		 
	}
	
	private static JDF initJDF(String fileName) {
		
		JDF jdf = new JDF();
		//DONE
		jdf.setActivation("Active");
		jdf.setAuditPool(new AuditPool());
		jdf.setCategory("DigitalPrinting");
		jdf.setComment(new Comment());
		jdf.setDescriptiveName(fileName);
		jdf.setID("jdf_1");
		jdf.setResourceLinkPool(new ResourceLinkPool());
		jdf.setResourcePool(new ResourcePool());
		jdf.setStatus("Waiting");
		jdf.setType("Combined");
		jdf.setTypes("LayoutPreparation Imposition Interpreting Rendering Screening DigitalPrinting oce:Mailbox HoleMaking Folding Stitching");
		jdf.setVersion("1.3");
		
		//DONE
		jdf.getComment().set_Name("oce:TicketVersion");
		jdf.getComment().set__text("4.00");
		
		//TODO
		jdf.getResourcePool().setContact(new Contact());
		
		jdf.getResourcePool().setCustomerInfo(new CustomerInfo());
		
		jdf.getResourcePool().setDigitalPrintingParams(new DigitalPrintingParams());
		
		jdf.getResourcePool().setFoldingParams(new FoldingParams());
		
		jdf.getResourcePool().setHoleMakingParams(new HoleMakingParams());
		
		jdf.getResourcePool().setInterpretingParams(new InterpretingParams());
		
		jdf.getResourcePool().setLayoutElement(new LayoutElement());
		
		jdf.getResourcePool().setLayoutPreparationParams(new LayoutPreparationParams());
		
		jdf.getResourcePool().setMailboxDetails(new MailboxDetails());
		
		jdf.getResourcePool().setMedia(new Media());
		
		jdf.getResourcePool().setRenderingParams(new RenderingParams());
		
		jdf.getResourcePool().setRunList(new RunList());
		
		jdf.getResourcePool().setScreeningParams(new ScreeningParams());
		
		jdf.getResourcePool().setStitchingParams(new StitchingParams());
		
		
		
		
		//TODO
		jdf.getResourceLinkPool().setCustomerInfoLink(new CustomerInfoLink());
		
		jdf.getResourceLinkPool().setDigitalPrintingParamsLink(new DigitalPrintingParamsLink());
		
		jdf.getResourceLinkPool().setFoldingParamsLink(new FoldingParamsLink());
		
		jdf.getResourceLinkPool().setHoleMakingParamsLink(new HoleMakingParamsLink());
		
		jdf.getResourceLinkPool().setInterpretingParamsLink(new InterpretingParamsLink());
		
		jdf.getResourceLinkPool().setLayoutPreparationParamsLink(new LayoutPreparationParamsLink());
		
		jdf.getResourceLinkPool().setMailboxDetailsLink(new MailboxDetailsLink());
		
		jdf.getResourceLinkPool().setMediaLink(new MediaLink());
		
		jdf.getResourceLinkPool().setRenderingParamsLink(new RenderingParamsLink());
		
		jdf.getResourceLinkPool().setRunListLink(new RunListLink());
		
		jdf.getResourceLinkPool().setScreeningParamsLink(new ScreeningParamsLink());
		
		jdf.getResourceLinkPool().setStitchingParamsLink(new StitchingParamsLink());
		
		//DONE
		jdf.getAuditPool().setCreated(new Created());
		jdf.getAuditPool().getCreated().set_AgentName("varioPRINT 135 PS3 GB ");
		jdf.getAuditPool().getCreated().set_AgentVersion("15.2.96.66 cmrepro@RD-AS65");
		jdf.getAuditPool().getCreated().set_ID("audit_1");
		jdf.getAuditPool().getCreated().set_TimeStamp("2014-09-19T06:54:18Z");
		
		return jdf;
	}
	
	
}
