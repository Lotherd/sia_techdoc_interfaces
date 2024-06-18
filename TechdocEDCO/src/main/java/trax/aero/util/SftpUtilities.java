package trax.aero.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;

import trax.aero.pojo.json.ATTACHMENT;
import trax.aero.pojo.json.OUTPUT;


public class SftpUtilities {
	
	private static String message = "";

	// ---------------------------------------------------------------------------- Send File
	
	public void sendSftpFile(InputStream inputStrem, String fileName) throws JSchException, SftpException {
		
		String sftpUsername = System.getProperty("SftpUsername");
		String sftpPassword =  System.getProperty("SftpPassword");
		String sftpRemoteHost =  System.getProperty("SftpRemoteHost");
		String sftpKnownHost =  System.getProperty("SftpKnownHost");
		String sftpDestination =  System.getProperty("SftpDestination");
		
	    ChannelSftp channelSftp = setupJsch(sftpUsername, sftpPassword, sftpRemoteHost, sftpKnownHost);
	    channelSftp.connect();
	    message = String.format("SFTP is connected: %s", channelSftp.isConnected());
	    System.out.println(message);
	    
	    SftpATTRS attrs = null;
	    try {
	    	
	    	attrs = channelSftp.stat(sftpDestination);
	    } 
	    catch (Exception e) {
		    message = String.format("SFTP Directory [%s] does no exist.", sftpDestination);
		    System.out.println(message);
	    }
	    
	    if (attrs != null) {
	    	
		    message = String.format("SFTP Directory [%s] already exists.", sftpDestination);
		    System.out.println(message);
	    }
	    else {
	    	
		    message = String.format("Creating SFTP Directory [%s].", sftpDestination);
		    System.out.println(message);
	    	channelSftp.mkdir(sftpDestination);
	    }
	    
	    
	    
	    sftpDestination = String.format("%s/%s", sftpDestination, fileName);
	    message = String.format("Putting the SFTP file in the following directory [%s].", sftpDestination);
	    System.out.println(message);
	    
	    
//	    System.out.println(sftpDestination);
//	    System.out.println(fileName);
	    
	    channelSftp.put(inputStrem, sftpDestination);
	    channelSftp.exit();
	}
	
	private ChannelSftp setupJsch(String sftpUsername, String sftpPassword, String sftpRemoteHost, String knownHost) throws JSchException {
		
	    JSch jsch = new JSch();
	    jsch.setKnownHosts(knownHost);
	    Session jschSession = jsch.getSession(sftpUsername, sftpRemoteHost);
	    jschSession.setPassword(sftpPassword);
	    jschSession.connect();
	    
	    message = String.format("Finshed setting up the session data. Returning open channel.");
	    System.out.println(message);
	    
	    return (ChannelSftp) jschSession.openChannel("sftp");
	}

	public static OUTPUT sendPdf(OUTPUT json, String pdfName, ArrayList<String> txt ,String txtName) {
		
		File folderOut = new File(System.getProperty("TECH_fileLocOut"));
		File folderPrint = new File(System.getProperty("TECH_fileLocPrint"));

		File[] print = folderPrint.listFiles();
		
		ArrayList<File> pdfs = new ArrayList<File>();
		
		try {
			
			//Print server folder logic
			if(print != null)
			{
				for(File f : print)
				{		
					if(f.isDirectory()) {
						
						//FOUND PDFS
						Path file = Paths.get(f+File.separator+ txtName+".txt");
						List<String> lines = txt;
						Files.write(file, lines, StandardCharsets.UTF_8);
						Files.move(f.toPath(), new File(folderOut+File.separator+f.getName()).toPath(), StandardCopyOption.REPLACE_EXISTING);
					
					}
				}
			}
			
			
			//output server logic 
			createZipFile();
			
			
			json.setATTACHMENT(new ArrayList<ATTACHMENT>());
			json.setFILENAME(pdfName);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		return json;
		// TODO Auto-generated method stub
		
	}
	
	
	
	public static void createZipFile() {
		
		File folder = new File(System.getProperty("TECH_fileLocOut"));
		File[] folders = folder.listFiles();
		
		for(File f : folders)
		{		
			if(f.isDirectory()) {
				String zipName = "techdoc_"+getCurrentTimeStamp();
				String destination = String.format("%s%s%s.zip", f.getPath(), File.separator, zipName);
				
				File zipFile = new File(destination);
				
				// Collects all of the files in a directory
				
				File[] files = f.listFiles();
				
				byte[] data = new byte[4092];
				
				try (ZipOutputStream out = new ZipOutputStream(new FileOutputStream(destination));) {

					for (File file : files) {
							
						out.putNextEntry(new ZipEntry(file.getName()));
						
						try (FileInputStream in = new FileInputStream(file.getAbsolutePath())) 
						{

							int len;
							while ((len = in.read(data)) > 0) {
								out.write(data, 0, len);

							}
						}
					}
					out.closeEntry();
					out.close();
					cleanUp(f.getPath());
				}catch(Exception exception) {
					exception.printStackTrace();
				}
			}
		}
				
	}
	
	public static String getCurrentTimeStamp() {
	    SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    Date now = new Date();
	    String strDate = sdfDate.format(now);
	    return strDate.replace("-", "").replace(":", "").replace(" ", "_");
	}
	
	
	public static void cleanUp(String path) {
        try {
        	
        		File file = new File(path);
	        	if(file.isDirectory()) {
					File[] cleanup = file.listFiles();
					for(File clean : cleanup) {
						if(clean.exists() && !clean.getName().toLowerCase().endsWith(".zip")){
							clean.delete();
						}
					}			
        	}
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}
