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

    Wo issueToTechDocRequest(MODEL input, String xml) throws Exception;

    String filterADDATTR(List<ADDATTR> attributes, String filter);


    void sendPrintToOutBound(Print input) throws Exception;

    Connection getCon();

    void sendRequestToPrintServer(MODEL model, String xml, Wo w) throws Exception;

    void processBatFile();

    Wo createParentWo(BigDecimal COUNT, String wpTitle);

    void linkWoToParent(Wo w, Wo parent, BigDecimal count);

    boolean lockAvailable(String notificationType);


    void lockTable(String notificationType);

    void unlockTable(String notificationType);

    void cleanUpTemp();

    void setCountWoToParent(Wo w, Wo parent);

}
