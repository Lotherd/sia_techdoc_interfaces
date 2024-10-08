package trax.aero.util;

import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
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
import javax.print.attribute.standard.Media;
import javax.print.attribute.standard.MediaTray;
import javax.print.attribute.standard.OrientationRequested;
import javax.print.attribute.standard.Sides;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.apache.commons.io.FilenameUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPrintable;
import org.apache.pdfbox.printing.Scaling;

import trax.aero.logger.LogManager;
import trax.aero.pojo.Dw_Wo_Pack_Print;
import trax.aero.pojo.jdf.AuditPoolBean;
import trax.aero.pojo.jdf.CommentBean;
import trax.aero.pojo.jdf.ContactBean;
import trax.aero.pojo.jdf.CreatedBean;
import trax.aero.pojo.jdf.CustomerInfoBean;
import trax.aero.pojo.jdf.CustomerInfoLinkBean;
import trax.aero.pojo.jdf.DigitalPrintingParamsBean;
import trax.aero.pojo.jdf.DigitalPrintingParamsLinkBean;
import trax.aero.pojo.jdf.FoldingParamsBean;
import trax.aero.pojo.jdf.FoldingParamsLinkBean;
import trax.aero.pojo.jdf.HoleMakingParamsBean;
import trax.aero.pojo.jdf.HoleMakingParamsLinkBean;
import trax.aero.pojo.jdf.InterpretingParamsBean;
import trax.aero.pojo.jdf.InterpretingParamsLinkBean;
import trax.aero.pojo.jdf.JDFBean;
import trax.aero.pojo.jdf.LayoutElementBean;
import trax.aero.pojo.jdf.LayoutPreparationParamsBean;
import trax.aero.pojo.jdf.LayoutPreparationParamsLinkBean;
import trax.aero.pojo.jdf.MailboxDetailsBean;
import trax.aero.pojo.jdf.MailboxDetailsLinkBean;
import trax.aero.pojo.jdf.MediaBean;
import trax.aero.pojo.jdf.MediaLinkBean;
import trax.aero.pojo.jdf.RenderingParamsBean;
import trax.aero.pojo.jdf.RenderingParamsLinkBean;
import trax.aero.pojo.jdf.ResourceLinkPoolBean;
import trax.aero.pojo.jdf.ResourcePoolBean;
import trax.aero.pojo.jdf.RunListBean;
import trax.aero.pojo.jdf.RunListLinkBean;
import trax.aero.pojo.jdf.ScreeningParamsBean;
import trax.aero.pojo.jdf.ScreeningParamsLinkBean;
import trax.aero.pojo.jdf.StitchingParamsBean;
import trax.aero.pojo.jdf.StitchingParamsLinkBean;
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
	
	
	public static void sendToPrinterHeavy(String printService, File file, String side, String tray) {

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
		            switch(tray){
		            	case "1": attributes.add(MediaTray.TOP);
		            		
		            	case "2": attributes.add(MediaTray.MIDDLE);
		            	
		            	case "3": attributes.add(MediaTray.BOTTOM);
		            	
		            	case "4": attributes.add(MediaTray.ENVELOPE);
		            		
		            	default: attributes.add(MediaTray.TOP);	
		       
		            }
		            
		            
		           

		            
		            if(side.equalsIgnoreCase("DUPLEX")) {
		            	attributes.add(Sides.DUPLEX);  
		            }else {
		            	attributes.add(Sides.ONE_SIDED);  		            	
		            }

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
	
	
	
	
	public static void  sendPrint(String printer, String path, String side, String tray) {
		
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
				sendToPrinterHeavy(printer, localPrint,side,tray);
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
		
		 JDFBean jdf = initJDF(print.getName());
		
		
		JAXBContext jc = JAXBContext.newInstance(JDFBean.class);
		Marshaller marshaller = jc.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		StringWriter sw = new StringWriter();
		marshaller.marshal(jdf, sw);
		
		String xml = sw.toString();
		logger.info(xml);
		logger.info(print.getPath());
	 	//Files.write(Paths.get(print.getPath()), xml.getBytes(), StandardOpenOption.);
		prependPrefix(print, xml);
		 
		 
	}
	
	public static JDFBean initJDF(String fileName) {
		
		JDFBean jdf = new JDFBean();
		//DONE
		jdf.setActivation("Active");
		jdf.setAuditPoolBean(new AuditPoolBean());
		jdf.setCategory("DigitalPrinting");
		jdf.setCommentBean(new CommentBean());
		jdf.setDescriptiveName(fileName);
		jdf.setID("jdf_1");
		jdf.setResourceLinkPoolBean(new ResourceLinkPoolBean());
		jdf.setResourcePoolBean(new ResourcePoolBean());
		jdf.setStatus("Waiting");
		jdf.setType("Combined");
		jdf.setTypes("LayoutPreparation Imposition Interpreting Rendering Screening DigitalPrinting oce:Mailbox HoleMaking Folding Stitching");
		jdf.setVersion("1.3");
		
		//DONE
		jdf.getCommentBean().setName("oce:TicketVersion");
		jdf.getCommentBean().setComment("4.00");
		
		//TODO
		jdf.getResourcePoolBean().setContactBean(new ContactBean());
		//jdf.getResourcePoolBean().getContact().set
		
		jdf.getResourcePoolBean().setCustomerInfoBean(new CustomerInfoBean());
		//jdf.getResourcePoolBean().getCustomerInfo().set
		
		jdf.getResourcePoolBean().setDigitalPrintingParamsBean(new DigitalPrintingParamsBean());
		
		jdf.getResourcePoolBean().setFoldingParamsBean(new FoldingParamsBean());
		
		jdf.getResourcePoolBean().setHoleMakingParamsBean(new HoleMakingParamsBean());
		
		jdf.getResourcePoolBean().setInterpretingParamsBean(new InterpretingParamsBean());
		
		jdf.getResourcePoolBean().setLayoutElementBean(new LayoutElementBean());
		
		jdf.getResourcePoolBean().setLayoutPreparationParamsBean(new LayoutPreparationParamsBean());
		
		jdf.getResourcePoolBean().setMailboxDetailsBean(new MailboxDetailsBean());
		
		jdf.getResourcePoolBean().setMediaBean(new MediaBean());
		
		jdf.getResourcePoolBean().setRenderingParamsBean(new RenderingParamsBean());
		
		jdf.getResourcePoolBean().setRunListBean(new RunListBean());
		
		jdf.getResourcePoolBean().setScreeningParamsBean(new ScreeningParamsBean());
		
		jdf.getResourcePoolBean().setStitchingParamsBean(new StitchingParamsBean());
		
		
		
		
		//TODO
		jdf.getResourceLinkPoolBean().setCustomerInfoLinkBean(new CustomerInfoLinkBean());
		
		jdf.getResourceLinkPoolBean().setDigitalPrintingParamsLinkBean(new DigitalPrintingParamsLinkBean());
		
		jdf.getResourceLinkPoolBean().setFoldingParamsLinkBean(new FoldingParamsLinkBean());
		
		jdf.getResourceLinkPoolBean().setHoleMakingParamsLinkBean(new HoleMakingParamsLinkBean());
		
		jdf.getResourceLinkPoolBean().setInterpretingParamsLinkBean(new InterpretingParamsLinkBean());
		
		jdf.getResourceLinkPoolBean().setLayoutPreparationParamsLinkBean(new LayoutPreparationParamsLinkBean());
		
		jdf.getResourceLinkPoolBean().setMailboxDetailsLinkBean(new MailboxDetailsLinkBean());
		
		jdf.getResourceLinkPoolBean().setMediaLinkBean(new MediaLinkBean());
		
		jdf.getResourceLinkPoolBean().setRenderingParamsLinkBean(new RenderingParamsLinkBean());
		
		jdf.getResourceLinkPoolBean().setRunListLinkBean(new RunListLinkBean());
		
		jdf.getResourceLinkPoolBean().setScreeningParamsLinkBean(new ScreeningParamsLinkBean());
		
		jdf.getResourceLinkPoolBean().setStitchingParamsLinkBean(new StitchingParamsLinkBean());
		
		//DONE
		jdf.getAuditPoolBean().setCreatedBean(new CreatedBean());
		jdf.getAuditPoolBean().getCreatedBean().setAgentName("varioPRINT 135 PS3 GB ");
		jdf.getAuditPoolBean().getCreatedBean().setAgentVersion("15.2.96.66 cmrepro@RD-AS65");
		jdf.getAuditPoolBean().getCreatedBean().setID("audit_1");
		jdf.getAuditPoolBean().getCreatedBean().setTimeStamp("2014-09-19T06:54:18Z");
		
		return jdf;
	}
	
	public static void prependPrefix(File input, String prefix) throws IOException {

		FileInputStream fis = new FileInputStream(input);
        byte[] originalBytes = new byte[(int) input.length()];
        fis.read(originalBytes);
        fis.close();

        // Convert the string to bytes
        byte[] stringBytes = prefix.getBytes();

        // Create a new byte array for the new PDF
        byte[] newBytes = new byte[stringBytes.length + originalBytes.length];
        
        // Copy the string bytes to the new array
        System.arraycopy(stringBytes, 0, newBytes, 0, stringBytes.length);
        // Copy the original PDF bytes to the new array
        System.arraycopy(originalBytes, 0, newBytes, stringBytes.length, originalBytes.length);

        // Write the new PDF to a file
        FileOutputStream fos = new FileOutputStream(input);
        fos.write(newBytes);
        fos.close();
	}
	
}
