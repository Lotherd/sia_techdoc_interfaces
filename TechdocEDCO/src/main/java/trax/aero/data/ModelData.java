package trax.aero.data;

import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.lang3.RandomStringUtils;

import trax.aero.controller.ModelController;
import trax.aero.interfaces.IModelData;
import trax.aero.logger.LogManager;
import trax.aero.model.BlobTable;
import trax.aero.model.BlobTablePK;
import trax.aero.model.Wo;
import trax.aero.model.WoTaskCard;
import trax.aero.model.WoTaskCardPK;
import trax.aero.pojo.Dw_Wo_Pack_Print;
import trax.aero.pojo.Print;
import trax.aero.pojo.Row;
import trax.aero.pojo.acknowledgement.PrintAck;
import trax.aero.pojo.json.EFFECTIVITY;
import trax.aero.pojo.json.JOBI;
import trax.aero.pojo.json.PANEL;
import trax.aero.pojo.json.PLI;
import trax.aero.pojo.json.SIMPLEREFERENCE;
import trax.aero.pojo.xml.ADDATTR;
import trax.aero.pojo.xml.JOBCARD;
import trax.aero.pojo.xml.MODEL;
import trax.aero.pojo.xml.OUTPUT;
import trax.aero.util.MqUtilities;
import trax.aero.util.PrinterUtilities;
import trax.aero.util.S3Utilities;
import trax.aero.util.SqsUtilities;

@Stateless(name="ModelData" , mappedName="ModelData")
public class ModelData implements IModelData {
	
	
	@PersistenceContext(unitName = "TechdocDS") private EntityManager em;
		
	Logger logger = LogManager.getLogger("TechdocEDCO");
	ArrayList<String> scoot = new ArrayList<>(Arrays.asList("300275","300276","101821")); 
	ArrayList<String> siaec = new ArrayList<>(Arrays.asList("319","320")); 

	
	public String filterADDATTR(List<ADDATTR> attributes, String filter)
	{
		ADDATTR temp = attributes.stream().filter(att -> filter.equals(att.getATTRNAME())).findFirst().orElse(null);
		
		
		if(temp == null)
			return null;
		else
			return temp.getATTRVALUE();
	}
	
	

	//MOD 16 //MOD 22
	public void issueTo (MODEL input, String xml) throws Exception {
		
		
		
		//creates temp wo with attached xml
		String company = "SIA", ac, location , site, type ;
		ac = input.getEFFECTIVITY().getREGNBR();
		location = "SIN";
		site = "";
		type = input.getEFFECTIVITY().getJOBCARD().getTYPE();
		if(siaec.contains(input.getMODELNBR())) {
			company = "SIAEC";
		}else if(scoot.contains(filterADDATTR(input.getEFFECTIVITY().getJOBCARD().getJOBI().getPLI().getADDATTR(), "BUSR06"))) {
			company = "SCOOT";
		}
		
		
		//creates temp wo task card 
		Wo w = createTempWo(type,company,location,site,ac,
				(filterADDATTR(input.getEFFECTIVITY().getJOBCARD().getJOBI().getPLI().getADDATTR(), "PRINTER-NAME")));
		createTempBlob(xml, w);
		createTempWoTaskCard(input, w); 
		
		//call wo pack print with flag TODO
		sendWorkPackPrintJob(w );
		
		sendPrintStatusAcknowledgement(input, "P", "SUCESS");
		
	}
	
	@Override
	public void print(Print print) {
		
		String printer = "", date, time, revision;
		ArrayList<PrintAck> ack = null;
		MODEL input = null;
		//TODO
		try {
			input = getXml(print.getWo());
			// creates zip file with pdf and txt file
			
			if(input == null) {
				return ;
			}
			String pdfName = genratePdfName(input);	
			ArrayList<String> txt =	genrateTxt(input);
			String txtName = genrateTxtName(input);	
			 printer = (filterADDATTR(input.getEFFECTIVITY().getJOBCARD().getJOBI().getPLI().getADDATTR(), "PRINTER-NAME"));
			
			 ack = new ArrayList<PrintAck>();
			ack.add(new PrintAck());
			
			
			if(printer.equalsIgnoreCase("ECXX") || printer.equalsIgnoreCase("ECXY")) {
				trax.aero.pojo.json.OUTPUT	json = convertXmlToJson(input);
				
				
				//gets pdfs path with wo id
				json = S3Utilities.sendEDCO(json, pdfName,txt,txtName, print.getPath(),printer );
				
				SqsUtilities.sendResend(json, pdfName);
					
				//send to physical printer TODO
				
			}else if (printer.equalsIgnoreCase("TRAX")){
				OUTPUT	xml = new OUTPUT();
				xml.setMODEL(input);
				xml.setFILENAME(pdfName);
				
				//gets pdfs path with wo id
				xml = S3Utilities.sendTrax(xml, pdfName,txt,txtName, print.getPath(),"" );
						

			}else if (printer.equalsIgnoreCase("EDXX") || printer.equalsIgnoreCase("ECXZ")){
			
				//send to virtual printer TODO
				//VIA S3 just PDF
				S3Utilities.sendVirtualPrint( print.getPath(),printer );
			}else {
				PrinterUtilities.sendPrint(printer, print.getPath());
			}
			// deletes wo and wo task card TODO
			deleteTempWoTaskCardBlob(print.getWo() );
			 
		} catch (Exception e) {
			
			date =filterADDATTR( input.getEFFECTIVITY().getJOBCARD().getJOBI().getPLI().getADDATTR(), "IDOC-DATE");
			revision = filterADDATTR( input.getEFFECTIVITY().getJOBCARD().getJOBI().getPLI().getADDATTR(), "LATEST-REVISION");
			time = filterADDATTR( input.getEFFECTIVITY().getJOBCARD().getJOBI().getPLI().getADDATTR(), "IDOC-TIME") ;
			switch(printer) {
			
			 
				case "ECXX":
				case "ECXY":
					ModelController.sendEmailEDCO( input.getEFFECTIVITY().getJOBCARD().getWPNBR()
							, revision, date, time, ack);
					break;
				case "TRAX":	
					ModelController.sendEmailTrax(input.getEFFECTIVITY().getJOBCARD().getWPNBR()
							, revision, date, time, ack);
					break;

				case "EDXX":
				case "ECXZ":	
					ModelController.sendEmailPrint(input.getEFFECTIVITY().getJOBCARD().getWPNBR()
							, revision, date, time, ack);
					break;
				default:
					break;
			}
			
			
			
			e.printStackTrace();
		} 
	}
	
	private int sendWorkPackPrintJob(Wo w) {

		Dw_Wo_Pack_Print dwPackPrint =  new Dw_Wo_Pack_Print();

		dwPackPrint.setRow(new Row());
		dwPackPrint.getRow().setTask_card_check_list_portrait("N");
		dwPackPrint.getRow().setCorrosion("N");
		dwPackPrint.getRow().setDoc_control_index_to_txt("N");
		dwPackPrint.getRow().setType("W");
		dwPackPrint.getRow().setTask_card_item_select("N");
		dwPackPrint.getRow().setPnl_inspect_req("NO");
		dwPackPrint.getRow().setWo_category_sel("N");
		dwPackPrint.getRow().setZone("N");
		dwPackPrint.getRow().setWo_status("ALL");
		dwPackPrint.getRow().setPrint_version("0");
		
		dwPackPrint.getRow().setAll_wo("N");
		dwPackPrint.getRow().setTask_card_sort("N");
		dwPackPrint.getRow().setEc("N");
		dwPackPrint.getRow().setWo_release_form("N");
		dwPackPrint.getRow().setWo_select("Y");
		dwPackPrint.getRow().setComponent_print("N");
		dwPackPrint.getRow().setWo_export_to_wics("N");
		dwPackPrint.getRow().setWo_task_card_panel_images("NO");
		dwPackPrint.getRow().setWo_documents("N");
		dwPackPrint.getRow().setWo_sort_with_status("N");
		dwPackPrint.getRow().setManual_ac_time_on_control_sheet("N");
		dwPackPrint.getRow().setWo_index_sorted_taskcard("N");
		dwPackPrint.getRow().setSkill_cost("N");
		dwPackPrint.getRow().setWo_index("N");
		dwPackPrint.getRow().setManuals("N");
		dwPackPrint.getRow().setWo_select_only_on_cond_tc("N");
		dwPackPrint.getRow().setAttachments("ALL");
		dwPackPrint.getRow().setS_all_wo("N");
		dwPackPrint.getRow().setTask_card_check_list_portrait_sort("N");
		dwPackPrint.getRow().setWo_sort("N");
		dwPackPrint.getRow().setWo_send_to_idmr("NO");
		dwPackPrint.getRow().setTask_card_check_list_sort("N");
		dwPackPrint.getRow().setWo_index_sorted_panel("N");
		
		dwPackPrint.getRow().setSsid("N");
		dwPackPrint.getRow().setTools("N");
		dwPackPrint.getRow().setWo_bid("N");
		dwPackPrint.getRow().setResequence("RESEQUENCE");
		dwPackPrint.getRow().setMulti_wo_selected("N");
		dwPackPrint.getRow().setSkill("N");
		dwPackPrint.getRow().setWo(String.valueOf( w.getWo()));
		dwPackPrint.getRow().setWo_sort_only_on_cond_tc("N");
		dwPackPrint.getRow().setPanel("N");
		dwPackPrint.getRow().setWo_tc_panel_index("N");
		dwPackPrint.getRow().setManual_ac_time_control_status("N");
		
		dwPackPrint.getRow().setTask_card_select("Y");
		dwPackPrint.getRow().setPrint_multiple_files("N");
		dwPackPrint.getRow().setSkill_control("N");
		dwPackPrint.getRow().setWo_index_sorted_skill("N");
		
		dwPackPrint.getRow().setWo_select_nr_only_on_cond_tc("N");
		dwPackPrint.getRow().setTask_card_check_list("N");
		dwPackPrint.getRow().setWo_select_with_status("N");
		dwPackPrint.getRow().setWo_record_sheet("N");
		dwPackPrint.getRow().setWo_index_sorted_zone("N");
		dwPackPrint.getRow().setWo_index_sorted("N");
		dwPackPrint.getRow().setWo_select_nr("ALL");
		dwPackPrint.getRow().setPrint_copy("NO");
		dwPackPrint.getRow().setDoc_control_index("Y");
		dwPackPrint.getRow().setPn("N");
		dwPackPrint.getRow().setNdt("N");
		dwPackPrint.getRow().setTask_card_item_sort("N");
						
		return PrinterUtilities.sendWorkPackPrintJob("w_wo_pack_print", dwPackPrint);
	}


	private void deleteTempWoTaskCardBlob(Wo w, ArrayList<WoTaskCard> taskCards, BlobTable blob) {
		
		for(WoTaskCard t : taskCards) {
			logger.info("DELETING TEMP Task Card: " + t.getId().getTaskCard());
			deleteData(em.find(WoTaskCard.class, t.getId()));
		}	
		
		logger.info("DELETING TEMP BLOB: " + blob.getId().getBlobNo());
		deleteData(em.find(BlobTable.class, blob.getId()));
		
		logger.info("DELETING TEMP WO: " + w.getWo());
		deleteData(em.find(Wo.class, w.getWo()));

		
		
		
		
	}


	private ArrayList<WoTaskCard> createTempWoTaskCard(MODEL input, Wo w) {

		WoTaskCard woCard = new WoTaskCard();
		
		woCard.setId(new WoTaskCardPK());
		woCard.getId().setAc(input.getEFFECTIVITY().getREGNBR());
		woCard.getId().setPn("                                   ");
		woCard.getId().setPnSn("                                   ");
		woCard.getId().setTaskCard(filterADDATTR(input.getEFFECTIVITY().getJOBCARD().getJOBI().getPLI().getADDATTR(), "TASK-NBR"));
		woCard.getId().setWo(w.getWo());

		
		woCard.setCreatedBy("IFACE-SIA");
		woCard.setCreatedDate(new Date());
		woCard.setStatus("OPEN");
		woCard.setNdt("N");
		woCard.setSellMaterialMethod("STANDARD");
		woCard.setSellMaterialAmount(new BigDecimal(0));
		woCard.setSellLaborMethod("STANDARD");
		woCard.setSellLaborAmount(new BigDecimal(0));
		woCard.setSellOtherMethod("STANDARD");
		woCard.setSellOtherAmount(new BigDecimal(0));
		
		woCard.setAuthorization("AUTHORIZED");
		woCard.setAuthorizationBy("IFACE-SIA");
		woCard.setAuthorizationDate(new Date());
		woCard.setWeightOn(new BigDecimal(0));
		woCard.setWeightOff(new BigDecimal(0));
		woCard.setScheduleStartHour(new BigDecimal(0));
		woCard.setScheduleStartMinute(new BigDecimal(0));
		
		woCard.setParagraph(new BigDecimal(0));
		woCard.setEditorUsed("NONE");
		woCard.setScheduleEndHour(new BigDecimal(0));
		woCard.setScheduleEndMinute(new BigDecimal(0));
		woCard.setNoOfPrint(new BigDecimal(0));
		woCard.setInsuranceClaim("NO");
		woCard.setFaultConfirm("PENDING");
		woCard.setPnRequired("NO");
		
		woCard.setRectifiedByEngineering("N");
		woCard.setAircraftJacked("NO");
		woCard.setElectricalPowerReq("OPTIONAL");
		woCard.setHydraulicPowerReq("OPTIONAL");
		woCard.setNonRoutine("N");
		
		logger.info("INSERTING TEMP Task Card: " + woCard.getId().getTaskCard());
		insertData(woCard);
		
		ArrayList<WoTaskCard>  temps = new ArrayList<WoTaskCard>();
		temps.add(woCard);
		return temps;
	}

	private BlobTable createTempBlob(String xml, Wo w) {
		BlobTable blob = null;
    	
		
		BlobTablePK pk = new BlobTablePK();
		blob = new BlobTable();
		blob.setCreatedDate(new Date());
		blob.setCreatedBy("IFACE-SIA");
		blob.setId(pk);
			
		blob.setPrintFlag("YES");
		blob.setDocType("TASKCARD");
		blob.getId().setBlobLine(1);
		
		
		blob.setModifiedBy("IFACE-SIA");
		blob.setModifiedDate(new Date());
		blob.setBlobItem(xml.getBytes());
		blob.setBlobDescription("MODEL");
		blob.setCustomDescription("MODEL");
		
	
		blob.getId().setBlobNo(((getTransactionNo("BLOB").longValue())));
		w.setBlobNo(new BigDecimal(blob.getId().getBlobNo()));
			
		logger.info("INSERTING TEMP WO : " + w.getWo());
		
		insertData(blob);
		insertData(w);
		
		return blob;
	}
	

	private Wo createTempWo(String type, String comapny, String location, String site,String ac, String print) {
		
			Wo wo = null;

			wo = new Wo();
			wo.setCreatedDate(new Date());
			wo.setCreatedBy("IFACE-SIA");
			
			//EMRO fields to create basic object
			wo.setWoDescription(print);
			wo.setWoCategory(type);
			wo.setLocation(location);
			wo.setSite(site);
			wo.setAc(ac);
			wo.setGlCompany(comapny);
			wo.setOrderType("W/O");
			wo.setModule("PRODUCTION");
			wo.setPaperChecked("NO");
			wo.setAuthorization("Y");
			wo.setNrReqItem("N");
			wo.setRestrictActual("N");
			wo.setNrAllow("YES");
			wo.setExcludeMhPlanner("N");
			wo.setThirdPartyWo("Y");
			wo.setModifiedBy("IFACE-SIA");
			wo.setModifiedDate(new Date());
			wo.setExpenditure(("General"));
			wo.setWo(getTransactionNo("WOSEQ").longValue());
			logger.info("INSERTING TEMP WO: " + wo.getWo());
			
			insertData(wo);
		
			
			
		
		return wo;
	}


	private trax.aero.pojo.json.OUTPUT convertXmlToJson(MODEL input) throws Exception
	{
		logger.info("Converting XML to JSON");
		
		trax.aero.pojo.json.OUTPUT json = new trax.aero.pojo.json.OUTPUT();
		json.setMODEL(new trax.aero.pojo.json.MODEL());
		
		//model
		json.getMODEL().setMODELNBR(input.getMODELNBR());
		
		
		//EFFECTIVITY
		json.getMODEL().setEFFECTIVITY(new EFFECTIVITY());
		json.getMODEL().getEFFECTIVITY().setCONFIG(input.getEFFECTIVITY().getCONFIG());
		json.getMODEL().getEFFECTIVITY().setREGNBR(input.getEFFECTIVITY().getREGNBR());
		
		//JOBCARD
		JOBCARD xmlJc =input.getEFFECTIVITY().getJOBCARD();
		
		json.getMODEL().getEFFECTIVITY().setJOBCARD(new trax.aero.pojo.json.JOBCARD());
		json.getMODEL().getEFFECTIVITY().getJOBCARD().setJCNBR(xmlJc.getJCNBR());
		json.getMODEL().getEFFECTIVITY().getJOBCARD().setJOBNBR(xmlJc.getJOBNBR());
		json.getMODEL().getEFFECTIVITY().getJOBCARD().setJCTITLE(xmlJc.getJCTITLE());
		json.getMODEL().getEFFECTIVITY().getJOBCARD().setCENTER(xmlJc.getCENTER());
		json.getMODEL().getEFFECTIVITY().getJOBCARD().setISSUENBR(xmlJc.getISSUENBR());
		json.getMODEL().getEFFECTIVITY().getJOBCARD().setISSUEDATE(xmlJc.getISSUEDATE());
		json.getMODEL().getEFFECTIVITY().getJOBCARD().setWPID(xmlJc.getWPID());
		json.getMODEL().getEFFECTIVITY().getJOBCARD().setWPNBR(xmlJc.getWPNBR());
		json.getMODEL().getEFFECTIVITY().getJOBCARD().setWPTITLE(xmlJc.getWPTITLE());
		json.getMODEL().getEFFECTIVITY().getJOBCARD().setWPDATE(xmlJc.getWPDATE());
		json.getMODEL().getEFFECTIVITY().getJOBCARD().setENGINESN(xmlJc.getENGINESN());
		
		json.getMODEL().getEFFECTIVITY().getJOBCARD().setENGINEPOS(xmlJc.getENGINEPOS());
		json.getMODEL().getEFFECTIVITY().getJOBCARD().setAPU(xmlJc.getAPU());
		json.getMODEL().getEFFECTIVITY().getJOBCARD().setAPUSN(xmlJc.getAPUSN());
		json.getMODEL().getEFFECTIVITY().getJOBCARD().setORIGINATOR(xmlJc.getORIGINATOR());
		json.getMODEL().getEFFECTIVITY().getJOBCARD().setSEQNBR(xmlJc.getSEQNBR());
		json.getMODEL().getEFFECTIVITY().getJOBCARD().setAREACODE(xmlJc.getAREACODE());
		json.getMODEL().getEFFECTIVITY().getJOBCARD().setAREA(xmlJc.getAREA());
		json.getMODEL().getEFFECTIVITY().getJOBCARD().setTRADE(xmlJc.getTRADE());
		json.getMODEL().getEFFECTIVITY().getJOBCARD().setTRADECODE(xmlJc.getTRADECODE());

		json.getMODEL().getEFFECTIVITY().getJOBCARD().setPOS(xmlJc.getPOS());
		json.getMODEL().getEFFECTIVITY().getJOBCARD().setMANHRS(xmlJc.getMANHRS());
		json.getMODEL().getEFFECTIVITY().getJOBCARD().setWONBR(xmlJc.getWONBR());

		json.getMODEL().getEFFECTIVITY().getJOBCARD().setLANGUAGE(xmlJc.getLANGUAGE());
		json.getMODEL().getEFFECTIVITY().getJOBCARD().setCREWSIZE(xmlJc.getCREWSIZE());
		json.getMODEL().getEFFECTIVITY().getJOBCARD().setDURATION(xmlJc.getDURATION());
		json.getMODEL().getEFFECTIVITY().getJOBCARD().setINTERVAL(xmlJc.getINTERVAL());
		json.getMODEL().getEFFECTIVITY().getJOBCARD().setHARDTIME(xmlJc.getHARDTIME());
		json.getMODEL().getEFFECTIVITY().getJOBCARD().setDIMENSION(xmlJc.getDIMENSION());
		json.getMODEL().getEFFECTIVITY().getJOBCARD().setOPPORTUNITY(xmlJc.getOPPORTUNITY());
		json.getMODEL().getEFFECTIVITY().getJOBCARD().setTYPE(xmlJc.getTYPE());
		
		json.getMODEL().getEFFECTIVITY().getJOBCARD().setJOBI(new JOBI());
		
		//JOBI	
		json.getMODEL().getEFFECTIVITY().getJOBCARD().getJOBI().setJOBINBR(xmlJc.getJOBI().getJOBINBR());
		json.getMODEL().getEFFECTIVITY().getJOBCARD().getJOBI().setJOBITITLE(xmlJc.getJOBI().getJOBITITLE());
		json.getMODEL().getEFFECTIVITY().getJOBCARD().getJOBI().setLANGUAGE(xmlJc.getJOBI().getLANGUAGE());
		json.getMODEL().getEFFECTIVITY().getJOBCARD().getJOBI().setZONE(xmlJc.getJOBI().getZONE());
		json.getMODEL().getEFFECTIVITY().getJOBCARD().getJOBI().setINTERVAL(xmlJc.getJOBI().getINTERVAL());
		json.getMODEL().getEFFECTIVITY().getJOBCARD().getJOBI().setHARDTIME(xmlJc.getJOBI().getHARDTIME());
		json.getMODEL().getEFFECTIVITY().getJOBCARD().getJOBI().setDIMENSION(xmlJc.getJOBI().getDIMENSION());
		
		
		//PANEL
		json.getMODEL().getEFFECTIVITY().getJOBCARD().getJOBI().setPANEL(new PANEL());
		json.getMODEL().getEFFECTIVITY().getJOBCARD().getJOBI().getPANEL().setPANELNBR(xmlJc.getJOBI().getPANEL().get(0).getPANELNBR());
		json.getMODEL().getEFFECTIVITY().getJOBCARD().getJOBI().getPANEL().setPANELSTATE(xmlJc.getJOBI().getPANEL().get(0).getPANELSTATE());
		json.getMODEL().getEFFECTIVITY().getJOBCARD().getJOBI().getPANEL().setPANELTEXT(xmlJc.getJOBI().getPANEL().get(0).getPANELTEXT());
		
		//PLI
		json.getMODEL().getEFFECTIVITY().getJOBCARD().getJOBI().setPLI(new PLI());
		json.getMODEL().getEFFECTIVITY().getJOBCARD().getJOBI().getPLI().setCHAPNBR(xmlJc.getJOBI().getPLI().getCHAPNBR());
		json.getMODEL().getEFFECTIVITY().getJOBCARD().getJOBI().getPLI().setSECTNBR(xmlJc.getJOBI().getPLI().getSECTNBR());
		json.getMODEL().getEFFECTIVITY().getJOBCARD().getJOBI().getPLI().setSUBJNBR(xmlJc.getJOBI().getPLI().getSUBJNBR());
		json.getMODEL().getEFFECTIVITY().getJOBCARD().getJOBI().getPLI().setPLINBR(xmlJc.getJOBI().getPLI().getPLINBR());
		json.getMODEL().getEFFECTIVITY().getJOBCARD().getJOBI().getPLI().setSKILL(xmlJc.getJOBI().getPLI().getSKILL());
		json.getMODEL().getEFFECTIVITY().getJOBCARD().getJOBI().getPLI().setLANGUAGE(xmlJc.getJOBI().getPLI().getLANGUAGE());
		json.getMODEL().getEFFECTIVITY().getJOBCARD().getJOBI().getPLI().setPLITEXT(xmlJc.getJOBI().getPLI().getPLITEXT());
		
		
		//SIMPLEREFERENCE
		json.getMODEL().getEFFECTIVITY().getJOBCARD().getJOBI().getPLI().setSIMPLEREFERENCE(new SIMPLEREFERENCE());
		json.getMODEL().getEFFECTIVITY().getJOBCARD().getJOBI().getPLI().getSIMPLEREFERENCE().setREFERENCENAME(xmlJc.getJOBI().getPLI().getSIMPLEREFERENCE().get(0).getREFERENCENAME());
		json.getMODEL().getEFFECTIVITY().getJOBCARD().getJOBI().getPLI().getSIMPLEREFERENCE().setREFTEXT(xmlJc.getJOBI().getPLI().getSIMPLEREFERENCE().get(0).getREFTEXT());
		
		//ADDATTR
		json.getMODEL().getEFFECTIVITY().getJOBCARD().getJOBI().getPLI().setADDATTR(new ArrayList<trax.aero.pojo.json.ADDATTR>());
		
		
		json.getMODEL().getEFFECTIVITY().getJOBCARD().getJOBI().getPLI().getADDATTR().add(addAddAttr("COUNT", xmlJc.getJOBI().getPLI().getADDATTR()));
		json.getMODEL().getEFFECTIVITY().getJOBCARD().getJOBI().getPLI().getADDATTR().add(addAddAttr("EQUI-NUMBER", xmlJc.getJOBI().getPLI().getADDATTR()));
		json.getMODEL().getEFFECTIVITY().getJOBCARD().getJOBI().getPLI().getADDATTR().add(addAddAttr( "NOTI-NUMBER", xmlJc.getJOBI().getPLI().getADDATTR()));
		json.getMODEL().getEFFECTIVITY().getJOBCARD().getJOBI().getPLI().getADDATTR().add(addAddAttr("MAT-TYPE", xmlJc.getJOBI().getPLI().getADDATTR()));
		json.getMODEL().getEFFECTIVITY().getJOBCARD().getJOBI().getPLI().getADDATTR().add(addAddAttr("ORDER-TYPE", xmlJc.getJOBI().getPLI().getADDATTR()));
		json.getMODEL().getEFFECTIVITY().getJOBCARD().getJOBI().getPLI().getADDATTR().add(addAddAttr("PLANT", xmlJc.getJOBI().getPLI().getADDATTR()));
		json.getMODEL().getEFFECTIVITY().getJOBCARD().getJOBI().getPLI().getADDATTR().add(addAddAttr("PRINTER-NAME", xmlJc.getJOBI().getPLI().getADDATTR()));
		json.getMODEL().getEFFECTIVITY().getJOBCARD().getJOBI().getPLI().getADDATTR().add(addAddAttr("STATUS", xmlJc.getJOBI().getPLI().getADDATTR()));
		json.getMODEL().getEFFECTIVITY().getJOBCARD().getJOBI().getPLI().getADDATTR().add(addAddAttr("REMARK", xmlJc.getJOBI().getPLI().getADDATTR()));
		json.getMODEL().getEFFECTIVITY().getJOBCARD().getJOBI().getPLI().getADDATTR().add(addAddAttr("USER-NAME", xmlJc.getJOBI().getPLI().getADDATTR()));
		json.getMODEL().getEFFECTIVITY().getJOBCARD().getJOBI().getPLI().getADDATTR().add(addAddAttr("IDOC-DATE", xmlJc.getJOBI().getPLI().getADDATTR()));
		json.getMODEL().getEFFECTIVITY().getJOBCARD().getJOBI().getPLI().getADDATTR().add(addAddAttr("IDOC-TIME", xmlJc.getJOBI().getPLI().getADDATTR()));
		json.getMODEL().getEFFECTIVITY().getJOBCARD().getJOBI().getPLI().getADDATTR().add(addAddAttr("MAFNO", xmlJc.getJOBI().getPLI().getADDATTR()));

		json.getMODEL().getEFFECTIVITY().getJOBCARD().getJOBI().getPLI().getADDATTR().add(addAddAttr("MOD_CLAS", xmlJc.getJOBI().getPLI().getADDATTR()));
		json.getMODEL().getEFFECTIVITY().getJOBCARD().getJOBI().getPLI().getADDATTR().add(addAddAttr("WBS", xmlJc.getJOBI().getPLI().getADDATTR()));
		json.getMODEL().getEFFECTIVITY().getJOBCARD().getJOBI().getPLI().getADDATTR().add(addAddAttr("DOWN_TIME", xmlJc.getJOBI().getPLI().getADDATTR()));
		json.getMODEL().getEFFECTIVITY().getJOBCARD().getJOBI().getPLI().getADDATTR().add(addAddAttr("COMP", xmlJc.getJOBI().getPLI().getADDATTR()));
		json.getMODEL().getEFFECTIVITY().getJOBCARD().getJOBI().getPLI().getADDATTR().add(addAddAttr("PART_NO1", xmlJc.getJOBI().getPLI().getADDATTR()));
		json.getMODEL().getEFFECTIVITY().getJOBCARD().getJOBI().getPLI().getADDATTR().add(addAddAttr("SN", xmlJc.getJOBI().getPLI().getADDATTR()));
		json.getMODEL().getEFFECTIVITY().getJOBCARD().getJOBI().getPLI().getADDATTR().add(addAddAttr("SB_TITLE", xmlJc.getJOBI().getPLI().getADDATTR()));
		json.getMODEL().getEFFECTIVITY().getJOBCARD().getJOBI().getPLI().getADDATTR().add(addAddAttr("TBOH", xmlJc.getJOBI().getPLI().getADDATTR()));
		
		
		json.getMODEL().getEFFECTIVITY().getJOBCARD().getJOBI().getPLI().getADDATTR().add(addAddAttr("TBOC", xmlJc.getJOBI().getPLI().getADDATTR()));
		json.getMODEL().getEFFECTIVITY().getJOBCARD().getJOBI().getPLI().getADDATTR().add(addAddAttr("TBOD", xmlJc.getJOBI().getPLI().getADDATTR()));
		json.getMODEL().getEFFECTIVITY().getJOBCARD().getJOBI().getPLI().getADDATTR().add(addAddAttr("TTOH", xmlJc.getJOBI().getPLI().getADDATTR()));
		json.getMODEL().getEFFECTIVITY().getJOBCARD().getJOBI().getPLI().getADDATTR().add(addAddAttr("TTOC", xmlJc.getJOBI().getPLI().getADDATTR()));
		json.getMODEL().getEFFECTIVITY().getJOBCARD().getJOBI().getPLI().getADDATTR().add(addAddAttr("TTOD", xmlJc.getJOBI().getPLI().getADDATTR()));
		json.getMODEL().getEFFECTIVITY().getJOBCARD().getJOBI().getPLI().getADDATTR().add(addAddAttr("SERIAL", xmlJc.getJOBI().getPLI().getADDATTR()));
		json.getMODEL().getEFFECTIVITY().getJOBCARD().getJOBI().getPLI().getADDATTR().add(addAddAttr("QTY", xmlJc.getJOBI().getPLI().getADDATTR()));
		json.getMODEL().getEFFECTIVITY().getJOBCARD().getJOBI().getPLI().getADDATTR().add(addAddAttr("PART_NO2", xmlJc.getJOBI().getPLI().getADDATTR()));

		json.getMODEL().getEFFECTIVITY().getJOBCARD().getJOBI().getPLI().getADDATTR().add(addAddAttr("PRIORITY", xmlJc.getJOBI().getPLI().getADDATTR()));
		json.getMODEL().getEFFECTIVITY().getJOBCARD().getJOBI().getPLI().getADDATTR().add(addAddAttr("AD_NO", xmlJc.getJOBI().getPLI().getADDATTR()));
		json.getMODEL().getEFFECTIVITY().getJOBCARD().getJOBI().getPLI().getADDATTR().add(addAddAttr("DUPLICATE", xmlJc.getJOBI().getPLI().getADDATTR()));
		json.getMODEL().getEFFECTIVITY().getJOBCARD().getJOBI().getPLI().getADDATTR().add(addAddAttr("TASK-NBR", xmlJc.getJOBI().getPLI().getADDATTR()));
		json.getMODEL().getEFFECTIVITY().getJOBCARD().getJOBI().getPLI().getADDATTR().add(addAddAttr("BUSR01", xmlJc.getJOBI().getPLI().getADDATTR()));
		json.getMODEL().getEFFECTIVITY().getJOBCARD().getJOBI().getPLI().getADDATTR().add(addAddAttr("BUSR02", xmlJc.getJOBI().getPLI().getADDATTR()));

		json.getMODEL().getEFFECTIVITY().getJOBCARD().getJOBI().getPLI().getADDATTR().add(addAddAttr("BUSR03", xmlJc.getJOBI().getPLI().getADDATTR()));
		json.getMODEL().getEFFECTIVITY().getJOBCARD().getJOBI().getPLI().getADDATTR().add(addAddAttr("BUSR04", xmlJc.getJOBI().getPLI().getADDATTR()));
		json.getMODEL().getEFFECTIVITY().getJOBCARD().getJOBI().getPLI().getADDATTR().add(addAddAttr("BUSR05", xmlJc.getJOBI().getPLI().getADDATTR()));
		json.getMODEL().getEFFECTIVITY().getJOBCARD().getJOBI().getPLI().getADDATTR().add(addAddAttr("BUSR06", xmlJc.getJOBI().getPLI().getADDATTR()));

		json.getMODEL().getEFFECTIVITY().getJOBCARD().getJOBI().getPLI().getADDATTR().add(addAddAttr("BUSR07", xmlJc.getJOBI().getPLI().getADDATTR()));
		json.getMODEL().getEFFECTIVITY().getJOBCARD().getJOBI().getPLI().getADDATTR().add(addAddAttr("BUSR08", xmlJc.getJOBI().getPLI().getADDATTR()));
		json.getMODEL().getEFFECTIVITY().getJOBCARD().getJOBI().getPLI().getADDATTR().add(addAddAttr("BUSR09", xmlJc.getJOBI().getPLI().getADDATTR()));
		json.getMODEL().getEFFECTIVITY().getJOBCARD().getJOBI().getPLI().getADDATTR().add(addAddAttr("BUSR10", xmlJc.getJOBI().getPLI().getADDATTR()));

		json.getMODEL().getEFFECTIVITY().getJOBCARD().getJOBI().getPLI().getADDATTR().add(addAddAttr("BUSR11", xmlJc.getJOBI().getPLI().getADDATTR()));
		json.getMODEL().getEFFECTIVITY().getJOBCARD().getJOBI().getPLI().getADDATTR().add(addAddAttr("BUSR12", xmlJc.getJOBI().getPLI().getADDATTR()));
		json.getMODEL().getEFFECTIVITY().getJOBCARD().getJOBI().getPLI().getADDATTR().add(addAddAttr("LATEST-REVISION", xmlJc.getJOBI().getPLI().getADDATTR()));
		json.getMODEL().getEFFECTIVITY().getJOBCARD().getJOBI().getPLI().getADDATTR().add(addAddAttr("LATEST-REVISION-DATE", xmlJc.getJOBI().getPLI().getADDATTR()));

		json.getMODEL().getEFFECTIVITY().getJOBCARD().getJOBI().getPLI().getADDATTR().add(addAddAttr("DOC-TYPE", xmlJc.getJOBI().getPLI().getADDATTR()));
		json.getMODEL().getEFFECTIVITY().getJOBCARD().getJOBI().getPLI().getADDATTR().add(addAddAttr("DOC-NUMBER", xmlJc.getJOBI().getPLI().getADDATTR()));
		json.getMODEL().getEFFECTIVITY().getJOBCARD().getJOBI().getPLI().getADDATTR().add(addAddAttr("DOC-DESC", xmlJc.getJOBI().getPLI().getADDATTR()));
		
		return json;
	}
	
	
	private trax.aero.pojo.json.ADDATTR addAddAttr(String ATTRNAME , List<ADDATTR> attributes ) {
		
		trax.aero.pojo.json.ADDATTR addattr = new trax.aero.pojo.json.ADDATTR();
		addattr.setATTRNAME(ATTRNAME);
		addattr.setATTRVALUE(filterADDATTR(attributes, ATTRNAME));
		
		return addattr;
		
	}
	
	private String genratePdfName(MODEL input) {
		String pdfName = "wo_";
		
		JOBCARD card = input.getEFFECTIVITY().getJOBCARD();
	    String random = RandomStringUtils.random(19, false, true);

		pdfName = "wo_"+ card.getWPNBR() + "_" + card.getSEQNBR() + "_" + card.getWONBR() +random + ".pdf" ;
		
		return pdfName;
	}

	
	private String genrateTxtName(MODEL input) {
		String txtNmae = "";
		
		JOBCARD card = input.getEFFECTIVITY().getJOBCARD();

		txtNmae = card.getWPNBR()+ "_" + card.getWPNBR() + "_" +filterADDATTR(input.getEFFECTIVITY().getJOBCARD().getJOBI().getPLI().getADDATTR(), "PRINTER-NAME")  ;
		
		return txtNmae;
	}


	//TODO
	private ArrayList<String> genrateTxt(MODEL input) {
		
		ArrayList<String> txt = new ArrayList<String>();
	    SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

		
		txt.add( "CDM Revision: " +input.getEFFECTIVITY().getJOBCARD().getWPNBR() + System.lineSeparator() + System.lineSeparator());
		txt.add( "SAP Revision: " +input.getEFFECTIVITY().getJOBCARD().getWPNBR() + System.lineSeparator() + System.lineSeparator());
		txt.add( "Aircraft Registration: " +input.getEFFECTIVITY().getREGNBR() + System.lineSeparator() + System.lineSeparator());
		txt.add( "Aircraft Type: " +input.getMODELNBR() + System.lineSeparator() + System.lineSeparator());
		txt.add( "Printer: " +filterADDATTR(input.getEFFECTIVITY().getJOBCARD().getJOBI().getPLI().getADDATTR(), "PRINTER-NAME")
		+ System.lineSeparator() + System.lineSeparator());
		txt.add( "Date: " +dateFormatter.format(new Date()) + System.lineSeparator() + System.lineSeparator());
		
		
		
		txt.add( "=========================================================="  + System.lineSeparator() + System.lineSeparator());

		txt.add( "Print Order	WorkOrder	JobCardTrade	Status	Seq		Number"  + System.lineSeparator() + System.lineSeparator());

		txt.add(input.getEFFECTIVITY().getJOBCARD().getSEQNBR()+" " +input.getEFFECTIVITY().getJOBCARD().getWONBR()
				+" " +	filterADDATTR(input.getEFFECTIVITY().getJOBCARD().getJOBI().getPLI().getADDATTR(), "TASK-NBR")	);
		
		
		txt.add( "=========================================================="  + System.lineSeparator() + System.lineSeparator());

		txt.add( "- END OF REPORT -") ;

		
		return txt;
	}

	//MOD 18
	private Boolean sendPrintStatusAcknowledgement(MODEL input, String status, String remarks) {
		
		try {
			trax.aero.pojo.acknowledgement.JOBCARD ack = new trax.aero.pojo.acknowledgement.JOBCARD();
			ack.setIdocdate(filterADDATTR(input.getEFFECTIVITY().getJOBCARD().getJOBI().getPLI().getADDATTR(), "IDOC-DATE"));
			ack.setIdoctime(filterADDATTR(input.getEFFECTIVITY().getJOBCARD().getJOBI().getPLI().getADDATTR(), "IDOC-TIME"));
			ack.setLoginname("TRAXIFACE");
			ack.setRemarks(remarks);
			ack.setSeqnbr(input.getEFFECTIVITY().getJOBCARD().getSEQNBR());
			ack.setStatus(status);
			ack.setWpnbr(input.getEFFECTIVITY().getJOBCARD().getWPNBR());
			
			JAXBContext jc = JAXBContext.newInstance(trax.aero.pojo.acknowledgement.JOBCARD.class);
			Marshaller marshaller = jc.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			StringWriter sw = new StringWriter();
			marshaller.marshal(ack, sw);
			
			logger.info("Input: " + sw.toString());
			String text = sw.toString();
			
			
			return MqUtilities.sendMqText(text);
			
		}catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}	
	
	//insert generic data from model objects
		private <T> void insertData( T data) 
		{
			try {	
				em.merge(data);
				em.flush();
			}catch (Exception e){	
				logger.severe(e.toString());
				throw e;
			}
		}
		
		private <T> void deleteData( T data) 
		{
			try {	
				em.remove(data);
				em.flush();
			}catch (Exception e){	
				logger.severe(e.toString());
				throw e;
			}
		}
		
		private BigDecimal getTransactionNo(String code)
		{		
			try
			{
				BigDecimal acctBal = (BigDecimal) em.createNativeQuery("SELECT pkg_application_function.config_number ( ? ) "
						+ " FROM DUAL ").setParameter(1, code).getSingleResult();
							
				return acctBal;			
			}
			catch (Exception e) 
			{
				logger.severe("An unexpected error occurred getting the sequence. " + "\nmessage: " + e.toString());
			}
			
			return null;
			
		}


		


		private void deleteTempWoTaskCardBlob(String wo) {

			Wo w = getTempWo(wo);
			BlobTable blob = getTempBlob(w.getBlobNo());
			//creates temp wo task card 
			ArrayList<WoTaskCard> taskCards =new ArrayList<WoTaskCard>(w.getWoTaskCards())  ; 

			
			deleteTempWoTaskCardBlob(w, taskCards, blob);
		}


		private Wo getTempWo(String wo) {
			try {
				Wo w = em.createQuery("Select w From Wo w where w.id.wo =:work", Wo.class)
						.setParameter("work", Long.parseLong(wo))
						.getSingleResult();
				return w;	
			}catch (Exception e) {
				e.printStackTrace();
				return null;
			}	
		}


		private BlobTable getTempBlob(BigDecimal blobNo) {
			try {
				BlobTable blob = em.createQuery("SELECT b FROM BlobTable b where b.id.blobNo = :bl and b.blobDescription = :des", BlobTable.class)
						.setParameter("bl", blobNo.longValue())
						.setParameter("des","MODEL" )
						.getSingleResult();
				return blob;	
			}catch (Exception e) {
				e.printStackTrace();
				return null;
			}	
		}


		private MODEL getXml(String wo) {
			try {
				Wo w = em.createQuery("Select w From Wo w where w.id.wo =:work", Wo.class)
						.setParameter("work", Long.parseLong(wo))
						.getSingleResult();
				BlobTable blob = em.createQuery("SELECT b FROM BlobTable b where b.id.blobNo = :bl and b.blobDescription = :des", BlobTable.class)
						.setParameter("bl", w.getBlobNo().longValue())
						.setParameter("des","MODEL" )
						.getSingleResult();
				
				;
				String xml = new String(blob.getBlobItem(), StandardCharsets.UTF_8); 
				System.out.println(xml);
				
				JAXBContext jc = JAXBContext.newInstance(MODEL.class);
				Unmarshaller unmarshaller = jc.createUnmarshaller();
				StringReader sr = new StringReader(xml);
				
				return (MODEL) unmarshaller.unmarshal(sr);
			}catch (Exception e) {
				e.printStackTrace();
				return null;
			}		
		}
		
}
