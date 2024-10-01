package trax.aero.util;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import trax.aero.pojo.json.ATTACHMENT;
import trax.aero.pojo.json.OUTPUT;
import org.apache.commons.io.FilenameUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

public class S3Utilities {
	
	private static String bucketNameTrax=  System.getProperty("Techdoc_Traxbucket");

	private static String bucketNamePrint=  System.getProperty("Techdoc_Printbucket");

	private static String bucketNameEDCO =  System.getProperty("Techdoc_EDCObucket");

	
	private static String pathS3Trax =  System.getProperty("Techdoc_pathS3Trax") + File.separator;

	private static String pathS3Print =  System.getProperty("Techdoc_pathS3Print") + File.separator;
	
	private static String pathS3EDCO =  System.getProperty("Techdoc_pathS3EDCO") + File.separator;;

	private static String fileLocOut =  System.getProperty("TECH_fileLocOut") ;;


	
	// ---------------------------------------------------------------------------- Send File
	
	protected static void putS3Object( File file, String key, String bucketName) {
        try {
        	
        	
        	//Files.move(file.toPath(), new File(key+File.separator+file.getName()).toPath(), StandardCopyOption.REPLACE_EXISTING);

        	
	        	S3Client s3 = S3Client.builder().build();       
	            PutObjectRequest request = PutObjectRequest.builder()
	                .bucket(bucketName)
	                .key(key+file.getName())
	                .build();
	            s3.putObject(request, RequestBody.fromFile(file));
	            
	            System.out.println("Successfully placed " + key+file.getName() + " into bucket " + bucketName);
        } catch (S3Exception e) {
            e.printStackTrace();
            throw e;
        }catch (Exception e) {
        	 e.printStackTrace();
        	 throw e;
		}
    }

	// ---------------------------------------------------------------------------- Send Folder
	
	private static void putS3Object( String key, String bucketName) {
	        try {
	        	
	        	
	        	//Files.move(file.toPath(), new File(key+File.separator+file.getName()).toPath(), StandardCopyOption.REPLACE_EXISTING);

	        	
		        	S3Client s3 = S3Client.builder().build();       
		            PutObjectRequest request = PutObjectRequest.builder()
		                .bucket(bucketName)
		                .key(key)
		                .build();
		            s3.putObject(request, RequestBody.empty());
		            
		            System.out.println("Successfully placed " + key + " into bucket " + bucketName);
	        } catch (S3Exception e) {
	            e.printStackTrace();
	            throw e;
	        }catch (Exception e) {
	        	 e.printStackTrace();
	        	 throw e;
			}
	    }
	
	public static OUTPUT sendEDCO(OUTPUT json, String pdfName, ArrayList<String> txt, String txtName, String path, String printer,
			String header, String footer, ArrayList<String> headerTxt, ArrayList<String> footerTxt, String folder) throws Exception {
		
		
		//ArrayList<File> pdfs = new ArrayList<File>();
		
		try {
			File print = new File(path);
			
			File theDir = new File(fileLocOut+File.separator+FilenameUtils.removeExtension(print.getName()));
			if (!theDir.exists()){
			    theDir.mkdirs();
			}
			
			Files.move(print.toPath(), new File(fileLocOut+File.separator+FilenameUtils.removeExtension(print.getName())
			+File.separator +pdfName).toPath(), StandardCopyOption.REPLACE_EXISTING);

			
			Path file = Paths.get(fileLocOut+File.separator+FilenameUtils.removeExtension(print.getName())
					+File.separator+ txtName+".txt");
			List<String> lines = txt;
			Files.write(file, lines, StandardCharsets.UTF_8);
			
			//header 
			PDDocument documentHeader = new PDDocument();
			PDPage pageHeader = new PDPage();
			documentHeader.addPage(pageHeader);
			PDPageContentStream contentStreamHeader = new PDPageContentStream(documentHeader, pageHeader);

			contentStreamHeader.beginText();
			contentStreamHeader.setLeading(14.5f);

			contentStreamHeader.newLineAtOffset(25, 700);
			contentStreamHeader.setFont(PDType1Font.HELVETICA, 16);

			for(String linesheader: headerTxt)
			{
				System.out.println(linesheader);
				contentStreamHeader.showText(linesheader);
				contentStreamHeader.newLine(); 
				
			}
			contentStreamHeader.endText();
			contentStreamHeader.close();
			documentHeader.save(fileLocOut+File.separator+FilenameUtils.removeExtension(print.getName())
			+File.separator+ header);
			documentHeader.close();
			
			//footer
			PDDocument documentFooter = new PDDocument();
			PDPage pageFooter = new PDPage();
			documentFooter.addPage(pageFooter);

			PDPageContentStream contentStreamFooter = new PDPageContentStream(documentFooter, pageFooter);

			contentStreamFooter.beginText();
			contentStreamFooter.setLeading(14.5f);

			contentStreamFooter.newLineAtOffset(25, 700);
			contentStreamFooter.setFont(PDType1Font.HELVETICA, 16);

			for(String linesFooter: footerTxt)
			{
				System.out.println(linesFooter);

				contentStreamFooter.showText(linesFooter);
				contentStreamFooter.newLine(); 
			}
			contentStreamFooter.endText();

			contentStreamFooter.close();
			documentFooter.save(fileLocOut+File.separator+FilenameUtils.removeExtension(print.getName())
			+File.separator+ footer);
			documentFooter.close();
			
			
			
			
			File folderPrint = new File(fileLocOut+File.separator+FilenameUtils.removeExtension(print.getName()));

			
			File[] prints = folderPrint.listFiles();

			//Print server folder logic
			if(prints != null)
			{
				
				//FOUND PDFS
				for(File p : prints){						
					putS3Object(p,pathS3EDCO +folder+ File.separator ,bucketNameEDCO);	
					PrinterUtilities.sendPrint(printer, p.getPath());
				}
			}
			
			json.setATTACHMENT(new ArrayList<ATTACHMENT>());
			json.setFILENAME(pdfName);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}	
		
		return json;
	}

	public static trax.aero.pojo.xml.OUTPUT sendTrax(trax.aero.pojo.xml.OUTPUT xml, String pdfName,
			ArrayList<String> txt, String txtName, String path, String folder, String header, String footer,
			ArrayList<String> headerTxt, ArrayList<String> footerTxt) throws Exception {
	
		//ArrayList<File> pdfs = new ArrayList<File>();
		
		try {
			//PDF
			File print = new File(path);
			
			File theDir = new File(fileLocOut+File.separator+FilenameUtils.removeExtension(print.getName()));
			if (!theDir.exists()){
			    theDir.mkdirs();
			}
			System.out.println("MOVE " +print.toPath() + " TO " 
			+fileLocOut+File.separator+FilenameUtils.removeExtension(print.getName())+File.separator +pdfName);
			Files.move(print.toPath(), new File(fileLocOut+File.separator+FilenameUtils.removeExtension(print.getName())
			+File.separator +pdfName).toPath(), StandardCopyOption.REPLACE_EXISTING);

			
			//txt
			Path text = Paths.get(fileLocOut+File.separator+FilenameUtils.removeExtension(print.getName())
			+File.separator+ txtName+".txt");
			List<String> lines = txt;
			Files.write(text, lines, StandardCharsets.UTF_8);
			
			//xml TODO
			File output = new File(fileLocOut+File.separator+FilenameUtils.removeExtension(print.getName())
			+File.separator+ txtName+".xml");
			
			
			//header 
			PDDocument documentHeader = new PDDocument();
			PDPage pageHeader = new PDPage();
			documentHeader.addPage(pageHeader);
			PDPageContentStream contentStreamHeader = new PDPageContentStream(documentHeader, pageHeader);

			contentStreamHeader.beginText();
			contentStreamHeader.setLeading(14.5f);

			contentStreamHeader.newLineAtOffset(25, 700);
			contentStreamHeader.setFont(PDType1Font.HELVETICA, 16);

			for(String linesheader: headerTxt)
			{
				System.out.println(linesheader);
				contentStreamHeader.showText(linesheader);
				contentStreamHeader.newLine(); 
				
			}
			contentStreamHeader.endText();
			contentStreamHeader.close();
			documentHeader.save(fileLocOut+File.separator+FilenameUtils.removeExtension(print.getName())
			+File.separator+ header);
			documentHeader.close();
			
			//footer
			PDDocument documentFooter = new PDDocument();
			PDPage pageFooter = new PDPage();
			documentFooter.addPage(pageFooter);

			PDPageContentStream contentStreamFooter = new PDPageContentStream(documentFooter, pageFooter);

			contentStreamFooter.beginText();
			contentStreamFooter.setLeading(14.5f);

			contentStreamFooter.newLineAtOffset(25, 700);
			contentStreamFooter.setFont(PDType1Font.HELVETICA, 16);

			for(String linesFooter: footerTxt)
			{
				System.out.println(linesFooter);

				contentStreamFooter.showText(linesFooter);
				contentStreamFooter.newLine(); 
			}
			contentStreamFooter.endText();

			contentStreamFooter.close();
			documentFooter.save(fileLocOut+File.separator+FilenameUtils.removeExtension(print.getName())
			+File.separator+ footer);
			documentFooter.close();
			
			JAXBContext jc = JAXBContext.newInstance(trax.aero.pojo.xml.OUTPUT.class);
			Marshaller marshall = jc.createMarshaller();
			marshall.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			marshall.marshal(xml, output);
			
			
			File folderPrint = new File(fileLocOut+File.separator+FilenameUtils.removeExtension(print.getName()));

			File[] prints = folderPrint.listFiles();
			
			//Print server folder logic
			if(prints != null)
			{
				//putS3Object(pathS3Trax+folder+ File.separator, bucketNameTrax);
				//FOUND PDFS
				for(File p : prints){	
					putS3Object(p,pathS3Trax+folder+ File.separator,bucketNameTrax);	
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}		
		return xml;
	}

	public static void sendVirtualPrint(String path, String folder, String pdfName) throws Exception {
		
		try {
			File print = new File(path);
			File theDir = new File(fileLocOut+File.separator+FilenameUtils.removeExtension(print.getName()));
			if (!theDir.exists()){
			    theDir.mkdirs();
			}
			System.out.println("MOVE " +print.toPath() + " TO " 
			+fileLocOut+File.separator+FilenameUtils.removeExtension(print.getName())+File.separator +pdfName);
			Files.move(print.toPath(), new File(fileLocOut+File.separator+FilenameUtils.removeExtension(print.getName())
			+File.separator +pdfName).toPath(), StandardCopyOption.REPLACE_EXISTING);

			//putS3Object(pathS3Print +folder +File.separator,bucketNamePrint);	
			//putS3Object(print,pathS3Print +folder +File.separator,bucketNamePrint);
			File folderPrint = new File(fileLocOut+File.separator+FilenameUtils.removeExtension(print.getName()));

			File[] prints = folderPrint.listFiles();
			
			//Print server folder logic
			if(prints != null)
			{
				//putS3Object(pathS3Trax+folder+ File.separator, bucketNameTrax);
				//FOUND PDFS
				for(File p : prints){	
					putS3Object(p,pathS3Print+folder+ File.separator,bucketNamePrint);	
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}	
	}
	
	
}
