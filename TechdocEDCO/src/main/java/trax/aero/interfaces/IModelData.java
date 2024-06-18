package trax.aero.interfaces;

import java.util.List;

import trax.aero.pojo.Print;
import trax.aero.pojo.xml.ADDATTR;
import trax.aero.pojo.xml.MODEL;

public interface IModelData {

	public void issueToEDCO(MODEL input, String xml) throws Exception;

	public String filterADDATTR(List<ADDATTR> attributes, String filter);

	public void issueToTRAX(MODEL input, String xml) throws Exception;

	public void issueToPrinter(MODEL input, String xml) throws Exception;

	public void print(Print input);

	
}
