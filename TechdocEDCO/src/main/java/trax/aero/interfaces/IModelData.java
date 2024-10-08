package trax.aero.interfaces;

import java.sql.Connection;
import java.util.List;

import trax.aero.model.Wo;
import trax.aero.pojo.Print;
import trax.aero.pojo.xml.ADDATTR;
import trax.aero.pojo.xml.MODEL;

public interface IModelData {

	public Wo issueTo(MODEL input, String xml) throws Exception;

	public String filterADDATTR(List<ADDATTR> attributes, String filter);

	
	public void print(Print input);

	public Connection getCon();

	public void sendPrint(MODEL model, String xml, Wo w);

	public void processBatFile();

	
}
