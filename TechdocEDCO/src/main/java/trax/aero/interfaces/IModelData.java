package trax.aero.interfaces;

import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import org.tinylog.Logger;

import trax.aero.model.InterfaceLockMaster;
import trax.aero.model.Wo;
import trax.aero.pojo.Print;
import trax.aero.pojo.xml.ADDATTR;
import trax.aero.pojo.xml.MODEL;

public interface IModelData {

	public Wo issueToTechDocRequest(MODEL input, String xml) throws Exception;

	public String filterADDATTR(List<ADDATTR> attributes, String filter);

	
	public void sendPrintToOutBound(Print input) throws Exception;

	public Connection getCon();

	public void sendRequestToPrintServer(MODEL model, String xml, Wo w) throws Exception;

	public void processBatFile();

	public Wo createParentWo(BigDecimal COUNT, String wpTitle);

	public void linkWoToParent(Wo w, Wo parent, BigDecimal count);
	
	public boolean lockAvailable(String notificationType);
	
	
	public void lockTable(String notificationType);
	
	public void unlockTable(String notificationType);
	
}
