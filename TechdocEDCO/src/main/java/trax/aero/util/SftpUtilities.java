package trax.aero.util;

import com.jcraft.jsch.*;
import java.io.InputStream;
import org.tinylog.Logger;

public class SftpUtilities {

  private static String message = "";

  // ---------------------------------------------------------------------------- Send File

  public void sendSftpFile(InputStream inputStream, String fileName)
      throws JSchException, SftpException {

    String sftpUsername = System.getProperty("Techdoc_sftpUsername");
    String sftpPassword = System.getProperty("Techdoc_sftpPassword");
    String sftpRemoteHost = System.getProperty("Techdoc_sftpRemoteHost");
    String sftpKnownHost = System.getProperty("Techdoc_sftpKnownHost");
    String sftpDestination = System.getProperty("Techdoc_sftpDestination");

    ChannelSftp channelSftp = setupJsch(sftpUsername, sftpPassword, sftpRemoteHost, sftpKnownHost);
    channelSftp.connect();
    message = String.format("SFTP is connected: %s", channelSftp.isConnected());
    Logger.info(message);

    SftpATTRS attrs = null;
    try {

      attrs = channelSftp.stat(sftpDestination);
    } catch (Exception e) {
      message = String.format("SFTP Directory [%s] does no exist.", sftpDestination);
      Logger.info(message);
    }

    if (attrs != null) {

      message = String.format("SFTP Directory [%s] already exists.", sftpDestination);
      Logger.info(message);
    } else {

      message = String.format("Creating SFTP Directory [%s].", sftpDestination);
      Logger.info(message);
      channelSftp.mkdir(sftpDestination);
    }

    sftpDestination = String.format("%s/%s", sftpDestination, fileName);
    message =
        String.format("Putting the SFTP file in the following directory [%s].", sftpDestination);
    Logger.info(message);

    //	    Logger.info(sftpDestination);
    //	    Logger.info(fileName);

    channelSftp.put(inputStream, sftpDestination);
    channelSftp.exit();
  }

  private ChannelSftp setupJsch(
      String sftpUsername, String sftpPassword, String sftpRemoteHost, String knownHost)
      throws JSchException {

    JSch jsch = new JSch();
    jsch.setKnownHosts(knownHost);
    Session jschSession = jsch.getSession(sftpUsername, sftpRemoteHost);
    jschSession.setPassword(sftpPassword);
    jschSession.connect();

    message = "Finished setting up the session data. Returning open channel.";
    Logger.info(message);

    return (ChannelSftp) jschSession.openChannel("sftp");
  }
}
