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
import trax.aero.pojo.jdf.ComChannelBean;
import trax.aero.pojo.jdf.CommentBean;
import trax.aero.pojo.jdf.ComponentBean;
import trax.aero.pojo.jdf.ComponentLinkBean;
import trax.aero.pojo.jdf.ContactBean;
import trax.aero.pojo.jdf.ContactRefBean;
import trax.aero.pojo.jdf.CreatedBean;
import trax.aero.pojo.jdf.CustomerInfoBean;
import trax.aero.pojo.jdf.CustomerInfoLinkBean;
import trax.aero.pojo.jdf.DigitalPrintingParamsBean;
import trax.aero.pojo.jdf.DigitalPrintingParamsLinkBean;
import trax.aero.pojo.jdf.FileSpecBean;
import trax.aero.pojo.jdf.FoldingParamsBean;
import trax.aero.pojo.jdf.FoldingParamsLinkBean;
import trax.aero.pojo.jdf.HoleMakingParamsBean;
import trax.aero.pojo.jdf.HoleMakingParamsLinkBean;
import trax.aero.pojo.jdf.InterpretingParamsBean;
import trax.aero.pojo.jdf.InterpretingParamsLinkBean;
import trax.aero.pojo.jdf.JDFBean;
import trax.aero.pojo.jdf.LayoutElementBean;
import trax.aero.pojo.jdf.LayoutElementRefBean;
import trax.aero.pojo.jdf.LayoutPreparationParamsBean;
import trax.aero.pojo.jdf.LayoutPreparationParamsLinkBean;
import trax.aero.pojo.jdf.MailboxDetailsBean;
import trax.aero.pojo.jdf.MailboxDetailsLinkBean;
import trax.aero.pojo.jdf.MediaBean;
import trax.aero.pojo.jdf.MediaLinkBean;
import trax.aero.pojo.jdf.MediaRefBean;
import trax.aero.pojo.jdf.ObjectResolutionBean;
import trax.aero.pojo.jdf.RenderingParamsBean;
import trax.aero.pojo.jdf.RenderingParamsLinkBean;
import trax.aero.pojo.jdf.ResourceLinkPoolBean;
import trax.aero.pojo.jdf.ResourcePoolBean;
import trax.aero.pojo.jdf.RunListBean;
import trax.aero.pojo.jdf.RunListLinkBean;
import trax.aero.pojo.jdf.ScreenSelectorBean;
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
		jdf.getResourcePoolBean().getContactBean().setClass("Parameter");
		jdf.getResourcePoolBean().getContactBean().setComChannelBean(new ComChannelBean());
		jdf.getResourcePoolBean().getContactBean().getComChannelBean().setChannelType("ComputerName");
		jdf.getResourcePoolBean().getContactBean().getComChannelBean().setLocator("NB10832");		
		jdf.getResourcePoolBean().getContactBean().setContactTypes("Owner Delivery");
		jdf.getResourcePoolBean().getContactBean().setID("contact_1");
		jdf.getResourcePoolBean().getContactBean().setStatus("Available");
		jdf.getResourcePoolBean().getContactBean().setUserID("LA04533");
		
		jdf.getResourcePoolBean().setCustomerInfoBean(new CustomerInfoBean());
		jdf.getResourcePoolBean().getCustomerInfoBean().setClass("Parameter");
		jdf.getResourcePoolBean().getCustomerInfoBean().setContactRefBean(new ContactRefBean());
		jdf.getResourcePoolBean().getCustomerInfoBean().getContactRefBean().setRRef("contact_1");
		jdf.getResourcePoolBean().getCustomerInfoBean().setCustomerJobName("jc684990001697300773176947073.ps");
		jdf.getResourcePoolBean().getCustomerInfoBean().setID("info_1");
		jdf.getResourcePoolBean().getCustomerInfoBean().setStatus("Available");
		
		
		jdf.getResourcePoolBean().setDigitalPrintingParamsBean(new DigitalPrintingParamsBean());
		jdf.getResourcePoolBean().getDigitalPrintingParamsBean().setMediaRefBean(new MediaRefBean());
		jdf.getResourcePoolBean().getDigitalPrintingParamsBean().getMediaRefBean().setRRef("04 - ");
		jdf.getResourcePoolBean().getDigitalPrintingParamsBean().setRunIndex("0 ~ -1");
		
		jdf.getResourcePoolBean().setFoldingParamsBean(new FoldingParamsBean());
		jdf.getResourcePoolBean().getFoldingParamsBean().setClass("Parameter");
		jdf.getResourcePoolBean().getFoldingParamsBean().setID("ProcessID_8");
		jdf.getResourcePoolBean().getFoldingParamsBean().setNoOp("true");
		jdf.getResourcePoolBean().getFoldingParamsBean().setStatus("Available");
		
		
		jdf.getResourcePoolBean().setHoleMakingParamsBean(new HoleMakingParamsBean());
		jdf.getResourcePoolBean().getHoleMakingParamsBean().setClass("Parameter");
		jdf.getResourcePoolBean().getHoleMakingParamsBean().setID("ProcessID_7");
		jdf.getResourcePoolBean().getHoleMakingParamsBean().setNoOp("true");
		jdf.getResourcePoolBean().getHoleMakingParamsBean().setStatus("Available");
		
		jdf.getResourcePoolBean().setInterpretingParamsBean(new InterpretingParamsBean());
		jdf.getResourcePoolBean().getInterpretingParamsBean().setClass("Parameter");
		jdf.getResourcePoolBean().getInterpretingParamsBean().setID("ProcessID_2");
		jdf.getResourcePoolBean().getInterpretingParamsBean().setStatus("Available");
		
		
		jdf.getResourcePoolBean().setLayoutElementBean(new LayoutElementBean());
		jdf.getResourcePoolBean().getLayoutElementBean().setClass("Parameter");
		jdf.getResourcePoolBean().getLayoutElementBean().setFileSpecBean(new FileSpecBean());
		jdf.getResourcePoolBean().getLayoutElementBean().getFileSpecBean().setMimeType("postscript");
		jdf.getResourcePoolBean().getLayoutElementBean().getFileSpecBean().setURL("cid:doc@hostname.com");		
		jdf.getResourcePoolBean().getLayoutElementBean().setID("file_1");
		jdf.getResourcePoolBean().getLayoutElementBean().setStatus("Available");
		
		jdf.getResourcePoolBean().setLayoutPreparationParamsBean(new LayoutPreparationParamsBean());
		jdf.getResourcePoolBean().getLayoutPreparationParamsBean().setRunIndex("0 ~ -1");
		jdf.getResourcePoolBean().getLayoutPreparationParamsBean().setSides("OneSidedFront");
		
		
		jdf.getResourcePoolBean().setMailboxDetailsBean(new MailboxDetailsBean());
		jdf.getResourcePoolBean().getMailboxDetailsBean().setClass("Parameter");
		jdf.getResourcePoolBean().getMailboxDetailsBean().setID("ProcessID_6");
		jdf.getResourcePoolBean().getMailboxDetailsBean().setStatus("Available");
		jdf.getResourcePoolBean().getMailboxDetailsBean().setUseMailbox("false");
		
		
		jdf.getResourcePoolBean().setMediaBean(new MediaBean());
		jdf.getResourcePoolBean().getMediaBean().setClass("Consumable");
		jdf.getResourcePoolBean().getMediaBean().setCustomMediaType("Plain paper");
		jdf.getResourcePoolBean().getMediaBean().setDescriptiveName("");
		jdf.getResourcePoolBean().getMediaBean().setDimension("595.3 841.9");
		jdf.getResourcePoolBean().getMediaBean().setHoleType("None");
		jdf.getResourcePoolBean().getMediaBean().setID("04 - ");
		jdf.getResourcePoolBean().getMediaBean().setMediaColorNameDetails("White");
		jdf.getResourcePoolBean().getMediaBean().setMediaSetCount("1");
		jdf.getResourcePoolBean().getMediaBean().setStatus("Available");
		jdf.getResourcePoolBean().getMediaBean().setWeight("80");
		jdf.getResourcePoolBean().getMediaBean().setMediaSetCount("1");
		
		
		
		jdf.getResourcePoolBean().setRenderingParamsBean(new RenderingParamsBean());
		jdf.getResourcePoolBean().getRenderingParamsBean().setClass("Parameter");
		jdf.getResourcePoolBean().getRenderingParamsBean().setID("ProcessID_3");
		jdf.getResourcePoolBean().getRenderingParamsBean().setObjectResolutionBean(new ObjectResolutionBean());
		jdf.getResourcePoolBean().getRenderingParamsBean().getObjectResolutionBean().setResolution("600 2400");
		jdf.getResourcePoolBean().getRenderingParamsBean().setStatus("Available");
		
		
		jdf.getResourcePoolBean().setRunListBean(new RunListBean());
		jdf.getResourcePoolBean().getRunListBean().setClass("Parameter");
		jdf.getResourcePoolBean().getRunListBean().setID("runlist_id");
		jdf.getResourcePoolBean().getRunListBean().setLayoutElementRefBean(new LayoutElementRefBean());
		jdf.getResourcePoolBean().getRunListBean().getLayoutElementRefBean().setRRef("file_1");
		jdf.getResourcePoolBean().getRunListBean().setStatus("Available");
		
		jdf.getResourcePoolBean().setScreeningParamsBean(new ScreeningParamsBean());
		jdf.getResourcePoolBean().getScreeningParamsBean().setClass("Parameter");
		jdf.getResourcePoolBean().getScreeningParamsBean().setID("ProcessID_4");
		jdf.getResourcePoolBean().getScreeningParamsBean().setScreenSelectorBean(new ScreenSelectorBean());
		jdf.getResourcePoolBean().getScreeningParamsBean().getScreenSelectorBean().setFrequency("200");
		jdf.getResourcePoolBean().getScreeningParamsBean().getScreenSelectorBean().setScreeningType("AM");
		jdf.getResourcePoolBean().getScreeningParamsBean().getScreenSelectorBean().setSpotFunction("Line");
		jdf.getResourcePoolBean().getScreeningParamsBean().setStatus("Incomplete");
		
		jdf.getResourcePoolBean().setStitchingParamsBean(new StitchingParamsBean());
		jdf.getResourcePoolBean().getStitchingParamsBean().setClass("Parameter");
		jdf.getResourcePoolBean().getStitchingParamsBean().setID("ProcessID_8");
		jdf.getResourcePoolBean().getStitchingParamsBean().setNumberOfStitches("1");
		jdf.getResourcePoolBean().getStitchingParamsBean().setStitchType("Corner");
		jdf.getResourcePoolBean().getStitchingParamsBean().setStatus("Available");
		
		jdf.getResourcePoolBean().setComponentBean(new ArrayList<ComponentBean>());
		jdf.getResourcePoolBean().getComponentBean().add(new ComponentBean());
		jdf.getResourcePoolBean().getComponentBean().get(0).setClass("Quantity");
		jdf.getResourcePoolBean().getComponentBean().get(0).setComponentType("PartialProduct");
		jdf.getResourcePoolBean().getComponentBean().get(0).setID("Product_5");
		jdf.getResourcePoolBean().getComponentBean().get(0).setStatus("Unavailable");

		
		jdf.getResourcePoolBean().getComponentBean().add(new ComponentBean());
		jdf.getResourcePoolBean().getComponentBean().get(1).setClass("Quantity");
		jdf.getResourcePoolBean().getComponentBean().get(1).setComponentType("PartialProduct");
		jdf.getResourcePoolBean().getComponentBean().get(1).setID("Product_6");
		jdf.getResourcePoolBean().getComponentBean().get(1).setStatus("Unavailable");
		
		jdf.getResourcePoolBean().getComponentBean().add(new ComponentBean());
		jdf.getResourcePoolBean().getComponentBean().get(2).setClass("Quantity");
		jdf.getResourcePoolBean().getComponentBean().get(2).setComponentType("PartialProduct");
		jdf.getResourcePoolBean().getComponentBean().get(2).setID("Product_7");
		jdf.getResourcePoolBean().getComponentBean().get(2).setStatus("Unavailable");
		
		jdf.getResourcePoolBean().getComponentBean().add(new ComponentBean());
		jdf.getResourcePoolBean().getComponentBean().get(3).setClass("Quantity");
		jdf.getResourcePoolBean().getComponentBean().get(3).setComponentType("PartialProduct");
		jdf.getResourcePoolBean().getComponentBean().get(3).setID("Product_8");
		jdf.getResourcePoolBean().getComponentBean().get(3).setStatus("Unavailable");
		
		jdf.getResourcePoolBean().getComponentBean().add(new ComponentBean());
		jdf.getResourcePoolBean().getComponentBean().get(4).setClass("Quantity");
		jdf.getResourcePoolBean().getComponentBean().get(4).setComponentType("PartialProduct");
		jdf.getResourcePoolBean().getComponentBean().get(4).setID("Product_9");
		jdf.getResourcePoolBean().getComponentBean().get(4).setStatus("Unavailable");
		
		jdf.getResourcePoolBean().getComponentBean().add(new ComponentBean());
		jdf.getResourcePoolBean().getComponentBean().get(5).setClass("Quantity");
		jdf.getResourcePoolBean().getComponentBean().get(5).setComponentType("FinalProduct");
		jdf.getResourcePoolBean().getComponentBean().get(5).setID("Product_Final");
		jdf.getResourcePoolBean().getComponentBean().get(5).setStatus("Unavailable");
		
				
		//DONE
		jdf.getResourceLinkPoolBean().setCustomerInfoLinkBean(new CustomerInfoLinkBean());
		jdf.getResourceLinkPoolBean().getCustomerInfoLinkBean().setRRef("info_1");
		jdf.getResourceLinkPoolBean().getCustomerInfoLinkBean().setUsage("Input");
		
		
		jdf.getResourceLinkPoolBean().setDigitalPrintingParamsLinkBean(new DigitalPrintingParamsLinkBean());
		jdf.getResourceLinkPoolBean().getDigitalPrintingParamsLinkBean().setUsage("Input");
		jdf.getResourceLinkPoolBean().getDigitalPrintingParamsLinkBean().setRRef("ProcessID_5");
		
		jdf.getResourceLinkPoolBean().setFoldingParamsLinkBean(new FoldingParamsLinkBean());
		jdf.getResourceLinkPoolBean().getFoldingParamsLinkBean().setUsage("Input");
		jdf.getResourceLinkPoolBean().getFoldingParamsLinkBean().setRRef("ProcessID_8");
		
		jdf.getResourceLinkPoolBean().setHoleMakingParamsLinkBean(new HoleMakingParamsLinkBean());
		jdf.getResourceLinkPoolBean().getHoleMakingParamsLinkBean().setUsage("Input");
		jdf.getResourceLinkPoolBean().getHoleMakingParamsLinkBean().setRRef("ProcessID_7");
		
		jdf.getResourceLinkPoolBean().setInterpretingParamsLinkBean(new InterpretingParamsLinkBean());
		jdf.getResourceLinkPoolBean().getInterpretingParamsLinkBean().setRRef("ProcessID_2");
		jdf.getResourceLinkPoolBean().getInterpretingParamsLinkBean().setUsage("Input");
		
		jdf.getResourceLinkPoolBean().setLayoutPreparationParamsLinkBean(new LayoutPreparationParamsLinkBean());
		jdf.getResourceLinkPoolBean().getLayoutPreparationParamsLinkBean().setRRef("ProcessID_0");
		jdf.getResourceLinkPoolBean().getLayoutPreparationParamsLinkBean().setUsage("Input");
		
		jdf.getResourceLinkPoolBean().setMailboxDetailsLinkBean(new MailboxDetailsLinkBean());
		jdf.getResourceLinkPoolBean().getMailboxDetailsLinkBean().setRRef("ProcessID_6");
		jdf.getResourceLinkPoolBean().getMailboxDetailsLinkBean().setUsage("Input");
		
		jdf.getResourceLinkPoolBean().setMediaLinkBean(new MediaLinkBean());
		jdf.getResourceLinkPoolBean().getMediaLinkBean().setRRef("04 - ");
		jdf.getResourceLinkPoolBean().getMediaLinkBean().setUsage("Input");
		
		jdf.getResourceLinkPoolBean().setRenderingParamsLinkBean(new RenderingParamsLinkBean());
		jdf.getResourceLinkPoolBean().getRenderingParamsLinkBean().setRRef("ProcessID_3");
		jdf.getResourceLinkPoolBean().getRenderingParamsLinkBean().setUsage("Input");
		
		jdf.getResourceLinkPoolBean().setRunListLinkBean(new RunListLinkBean());
		jdf.getResourceLinkPoolBean().getRunListLinkBean().setRRef("runlist_id");
		jdf.getResourceLinkPoolBean().getRunListLinkBean().setUsage("Input");
		
		jdf.getResourceLinkPoolBean().setScreeningParamsLinkBean(new ScreeningParamsLinkBean());
		jdf.getResourceLinkPoolBean().getScreeningParamsLinkBean().setUsage("Input");
		jdf.getResourceLinkPoolBean().getScreeningParamsLinkBean().setRRef("ProcessID_4");
		
		jdf.getResourceLinkPoolBean().setStitchingParamsLinkBean(new StitchingParamsLinkBean());
		jdf.getResourceLinkPoolBean().getStitchingParamsLinkBean().setUsage("Input");
		jdf.getResourceLinkPoolBean().getStitchingParamsLinkBean().setRRef("ProcessID_9");
		
		jdf.getResourceLinkPoolBean().setComponentLinkBean(new ArrayList<ComponentLinkBean>());
		jdf.getResourceLinkPoolBean().getComponentLinkBean().add(new ComponentLinkBean());
		
		jdf.getResourceLinkPoolBean().getComponentLinkBean().get(0).setCombinedProcessIndex("9");
		jdf.getResourceLinkPoolBean().getComponentLinkBean().get(0).setOrientation("Flip90");
		jdf.getResourceLinkPoolBean().getComponentLinkBean().get(0).setRRef("Product_8");
		jdf.getResourceLinkPoolBean().getComponentLinkBean().get(0).setUsage("Input");
		
		jdf.getResourceLinkPoolBean().getComponentLinkBean().add(new ComponentLinkBean());
		jdf.getResourceLinkPoolBean().getComponentLinkBean().get(1).setCombinedProcessIndex("9");
		jdf.getResourceLinkPoolBean().getComponentLinkBean().get(1).setOrientation("Flip90");
		jdf.getResourceLinkPoolBean().getComponentLinkBean().get(1).setRRef("Product_9");
		jdf.getResourceLinkPoolBean().getComponentLinkBean().get(1).setUsage("Output");
		
		jdf.getResourceLinkPoolBean().getComponentLinkBean().add(new ComponentLinkBean());
		jdf.getResourceLinkPoolBean().getComponentLinkBean().get(2).setAmount("1");
		jdf.getResourceLinkPoolBean().getComponentLinkBean().get(2).setRRef("Product_Final");
		jdf.getResourceLinkPoolBean().getComponentLinkBean().get(2).setUsage("Output");
		
		
		
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
