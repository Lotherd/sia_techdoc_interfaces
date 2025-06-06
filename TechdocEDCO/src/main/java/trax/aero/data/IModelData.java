package trax.aero.data;

import trax.aero.model.Wo;
import trax.aero.pojo.Print;
import trax.aero.pojo.xml.ADDATTR;
import trax.aero.pojo.xml.MODEL;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.List;

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
