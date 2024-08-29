package trax.aero.interfaces;

import java.sql.Connection;
import java.util.List;

import trax.aero.pojo.Print;
import trax.aero.pojo.xml.ADDATTR;
import trax.aero.pojo.xml.MODEL;

public interface IModelData {

	public void issueTo(MODEL input, String xml) throws Exception;

	public String filterADDATTR(List<ADDATTR> attributes, String filter);

	
	public void print(Print input);

	public Connection getCon();

	
}
