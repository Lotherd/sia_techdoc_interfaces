package trax.aero.util;

import java.io.InputStream;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;


public class SftpUtilities {
	
	private static String message = "";

	// ---------------------------------------------------------------------------- Send File
	
	public void sendSftpFile(InputStream inputStrem, String fileName) throws JSchException, SftpException {
		
		String sftpUsername = System.getProperty("Techdoc_sftpUsername");
		String sftpPassword = System.getProperty("Techdoc_sftpPassword");
		String sftpRemoteHost = System.getProperty("Techdoc_sftpRemoteHost");
		String sftpKnownHost = System.getProperty("Techdoc_sftpKnownHost");
		String sftpDestination = System.getProperty("Techdoc_sftpDestination");
		
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
}
