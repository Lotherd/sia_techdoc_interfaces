package trax.aero.data;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.lang3.RandomStringUtils;

import trax.aero.controller.ModelController;
import trax.aero.interfaces.IModelData;
import trax.aero.model.BlobTable;
import trax.aero.model.BlobTablePK;
import trax.aero.model.InterfaceAudit;
import trax.aero.model.InterfaceData;
import trax.aero.model.JournalEntriesExpenditure;
import trax.aero.model.JournalEntriesExpenditurePK;
import trax.aero.model.TaskCard;
import trax.aero.model.Wo;
import trax.aero.model.WoTaskCard;
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
import trax.aero.pojo.xml.ATTACHMENT;
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
	private Connection con;

	public Connection getCon() {
		return con;
	}
	
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
	public Wo issueToTechDocRequest (MODEL input, String xml) throws Exception {
		
		//creates temp wo with attached xml
		String company = "SIA", ac, location , site, type ;
		String printer = "", date, time, revision;
		ArrayList<PrintAck> ack = null;
		ac = input.getEFFECTIVITY().getREGNBR();
		location = "SIN";
		site = input.getEFFECTIVITY().getJOBCARD().getCENTER();
		type = input.getEFFECTIVITY().getJOBCARD().getTYPE();
		if(siaec.contains(input.getMODELNBR())) {
			company = "SIAEC";
		}else if(scoot.contains(filterADDATTR(input.getEFFECTIVITY().getJOBCARD().getJOBI().getPLI().getADDATTR(), "BUSR06"))) {
			company = "SCOOT";
		}
		
		 int status = 0;
		 Wo w = null;
		//creates temp wo task card 
		try {
			w = createTempWo(type,company,location,site,ac,
					(filterADDATTR(input.getEFFECTIVITY().getJOBCARD().getJOBI().getPLI().getADDATTR(), "PRINTER-NAME")),
					filterADDATTR(input.getEFFECTIVITY().getJOBCARD().getJOBI().getPLI().getADDATTR(), "BUSR04"),
					input);
			createTempBlob(xml, w);
			
			return w;
		}catch (Exception e) {
			e.printStackTrace();
			status = 1;
		}
		
		if(status == 0) {
			sendPrintStatusAcknowledgement(input, "P", "SUCESS");
		}else {
			sendPrintStatusAcknowledgement(input, "E", "ERROR");
			date =filterADDATTR( input.getEFFECTIVITY().getJOBCARD().getJOBI().getPLI().getADDATTR(), "IDOC-DATE");
			revision = filterADDATTR( input.getEFFECTIVITY().getJOBCARD().getJOBI().getPLI().getADDATTR(), "LATEST-REVISION");
			time = filterADDATTR( input.getEFFECTIVITY().getJOBCARD().getJOBI().getPLI().getADDATTR(), "IDOC-TIME") ;
			printer = (filterADDATTR(input.getEFFECTIVITY().getJOBCARD().getJOBI().getPLI().getADDATTR(), "PRINTER-NAME"));
			ack = new ArrayList<PrintAck>();
			ack.add(new PrintAck());	
			
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
					ModelController.sendEmailPrint(input.getEFFECTIVITY().getJOBCARD().getWPNBR()
							, revision, date, time, ack);
					break;
			}
			
		}
		return null;
	}
	
	@Override
	public void sendPrintToOutBound(Print print) throws Exception {
		
		String printer = "", date, time, revision, folder = "";
		ArrayList<PrintAck> ack = null;
		MODEL input = null;
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
			String header = genrateHeaderName(input);	
			String footer = genrateFooterName(input);	
			ArrayList<String> headerTxt = genrateHeaderTxt(input);	
			ArrayList<String> footerTxt = genrateFooterTxt(input);	
			
			folder = input.getEFFECTIVITY().getJOBCARD().getWPNBR() +"_"+
			filterADDATTR( input.getEFFECTIVITY().getJOBCARD().getJOBI().getPLI().getADDATTR(), "IDOC-DATE")
			+ filterADDATTR( input.getEFFECTIVITY().getJOBCARD().getJOBI().getPLI().getADDATTR(), "IDOC-TIME") ;

			ack = new ArrayList<PrintAck>();
			ack.add(new PrintAck());
			
			
			if(printer.equalsIgnoreCase("ECXX") || printer.equalsIgnoreCase("ECXY")) {
				trax.aero.pojo.json.OUTPUT	json = convertXmlToJson(input);
				
				
				//gets pdfs path with wo id
				json = S3Utilities.sendEDCO(json, pdfName,txt,txtName, print.getPath(),printer,
						header,footer,
						headerTxt, footerTxt,folder						
						);
				 
				SqsUtilities.sendResend(json, pdfName);
					
				//send to physical printer 
				
			}else if (printer.equalsIgnoreCase("TRAX")){
				OUTPUT	xml = new OUTPUT();
				xml.setMODEL(input);
				xml.setFILENAME(pdfName);
				
				//gets pdfs path with wo id
				xml = S3Utilities.sendTrax(xml, pdfName,txt,txtName, print.getPath(), folder,
						header,footer,
						headerTxt, footerTxt);
						

			}else if (printer.equalsIgnoreCase("EDXX") || printer.equalsIgnoreCase("ECXZ")){
			
				//send to virtual printer 
				//VIA S3 just PDF
				S3Utilities.sendVirtualPrint( print.getPath(),printer +File.separator+ folder,pdfName );
			}else {
				String side = (filterADDATTR(input.getEFFECTIVITY().getJOBCARD().getJOBI().getPLI().getADDATTR(), "ORDER-TYPE"))
				,tray = "4"; 
				
				
				if(side.equalsIgnoreCase("MCS")) {
					switch(input.getEFFECTIVITY().getJOBCARD().getJOBNBR()) {
						case"A":
						case"1":
							tray = "2";	
						case"2":
						case"3":
						case"4":
						case"B":
							tray = "3";
						default:tray = "1";
					}
				}else if(side.equalsIgnoreCase("SI")){
					switch(input.getEFFECTIVITY().getJOBCARD().getJOBNBR()) {
						case"1":tray = "2";					
						case"2":tray = "3";
						default:tray = "1";
					}
				}
				
				if(side.equalsIgnoreCase("MCS") 
						|| side.equalsIgnoreCase("SI") 
						|| side.equalsIgnoreCase("AMM/BTC")) {
					side = "DUPLEX";
				}
							
				PrinterUtilities.sendPrint(printer, print.getPath(),side,tray);
			}
			
			 
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
					ModelController.sendEmailPrint(input.getEFFECTIVITY().getJOBCARD().getWPNBR()
							, revision, date, time, ack);
					break;
			}
			
			
			
			e.printStackTrace();
		}finally {
			// deletes wo and wo task card 
			if(System.getProperty("Techdoc_DELETE") != null 
				&& !System.getProperty("Techdoc_DELETE").isEmpty()
				&& System.getProperty("Techdoc_DELETE").equalsIgnoreCase("YES")) {
				deleteTempWoTaskCardBlob(print.getWo() );
			}
		}
	}
	
	
	
	private ArrayList<String> genrateFooterTxt(MODEL input) {
		ArrayList<String> txt = new ArrayList<String>();
		JOBCARD card = input.getEFFECTIVITY().getJOBCARD();
		String count = (filterADDATTR(input.getEFFECTIVITY().getJOBCARD().getJOBI().getPLI().getADDATTR(), "COUNT"));
		int c = Integer.parseInt(count);

		txt.add( "FOOTER PAGE" );
		txt.add( "Revision Number :" +" " +card.getWPNBR());
		txt.add( "WorkOrder(s) Printed :" +" " +c);
		txt.add( "WorkOrder(s) Incomplete :" +"  0" );
		txt.add( "Total No of WorkOrder(s) :" +" "+c );

		
		return txt;
	}



	private ArrayList<String> genrateHeaderTxt(MODEL input) {
		ArrayList<String> txt = new ArrayList<String>();
		JOBCARD card = input.getEFFECTIVITY().getJOBCARD();
		
		String count = (filterADDATTR(input.getEFFECTIVITY().getJOBCARD().getJOBI().getPLI().getADDATTR(), "COUNT"));
		String user = (filterADDATTR(input.getEFFECTIVITY().getJOBCARD().getJOBI().getPLI().getADDATTR(), "USER-NAME"));
		
		txt.add( "INDEX PAGE" );
		txt.add( "Revision Number :"+" " +card.getWPNBR() );
		txt.add( "Title :" +" " +card.getWPTITLE());
		txt.add( "Date of Check :" +" " +card.getWPDATE());
		txt.add( "A/C Model :" +" " +input.getMODELNBR());
		txt.add( "Work Center :"+" " + card.getCENTER() );
		txt.add( "Registration :"+" " + input.getEFFECTIVITY().getREGNBR() );
		txt.add( "Total No of Job Cards :" +" " +count);
		txt.add( "User Id :" +" " +user);
	
		return txt;
	}



	private String genrateFooterName(MODEL input) {
		JOBCARD card = input.getEFFECTIVITY().getJOBCARD();

	    String random = RandomStringUtils.random(19, false, true);

		String txt = "footer_" + card.getWPNBR() +	"_"+ random+ ".pdf";

		return txt;
	}



	private String genrateHeaderName(MODEL input) {
		JOBCARD card = input.getEFFECTIVITY().getJOBCARD();

	    String random = RandomStringUtils.random(19, false, true);

		String txt = "index_" + card.getWPNBR() +	"_"+ random+ ".pdf";

		return txt;
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
		dwPackPrint.getRow().setWo_select("N");
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


	private void deleteTempWoTaskCardBlob(Wo w, ArrayList<WoTaskCard> taskCards, BlobTable blob  ) throws Exception {
		
		for(WoTaskCard t : taskCards) {
			 System.out.println("DELETING TEMP Task Card: " + t.getId().getTaskCard());
			deleteData(em.find(WoTaskCard.class, t.getId()));
		}
		System.out.println("DELETING TEMP WO TaskCard Pn: " + w.getWo());
		deleteWoTaskCardPn(String.valueOf( w.getWo()));
		
		 System.out.println("DELETING TEMP BLOB: " + blob.getId().getBlobNo());
		deleteData(em.find(BlobTable.class, blob.getId()));
		
		 System.out.println("DELETING TEMP WO: " + w.getWo());
		deleteData(em.find(Wo.class, w.getWo()));

		
		
		
		
	}


	private void createTempWoTaskCard(MODEL input, Wo w) throws Exception {

		ArrayList<String> taskIds = new ArrayList<String>();
		
		if( input.getEFFECTIVITY().getJOBCARD().getJOBI().getPLI().getATTACHMENT() != null
			&&  !input.getEFFECTIVITY().getJOBCARD().getJOBI().getPLI().getATTACHMENT().isEmpty()) {
			for(ATTACHMENT at : input.getEFFECTIVITY().getJOBCARD().getJOBI().getPLI().getATTACHMENT()) {
				taskIds.add(at.getID());
			}
		}else {
			taskIds.add( filterADDATTR(input.getEFFECTIVITY().getJOBCARD().getJOBI().getPLI().getADDATTR(), "TASK-NBR")); 
		}
		CallableStatement cstmt = null;

		final String sql = "{ call pkg_wo_task_card_functions.add_wo_task_card(?, ?, ?, ?, ?, ?, ?, ?)}";
		
		ArrayList<String> taskCards = getTaskCards(taskIds);
		 System.out.println("ENG TASK CARD size: " + taskCards.size());

		if(this.con == null || this.con.isClosed())
		{
			this.con = trax.aero.util.DataSourceClient.getConnection();
			 System.out.println("The connection was stablished successfully with status: " + String.valueOf(!this.con.isClosed()));
		}
		
		try {
			cstmt = con.prepareCall(sql);
		
			for(String tc: taskCards) {
				cstmt.setInt(1, Long.valueOf( w.getWo()).intValue());
				cstmt.setString(2, tc);
				cstmt.setString(3, input.getEFFECTIVITY().getREGNBR());
				cstmt.setString(4, "                                   ");
				cstmt.setString(5, "                                   ");
				cstmt.setString(6, "IFACE-SIA");
				cstmt.setNull(7, Types.VARCHAR);
				cstmt.setString(8, "N");
	
				cstmt.execute();
			}
		}catch (Exception e) {
			throw e;
		}finally {
			if(cstmt != null && !cstmt.isClosed()) {
				cstmt.close();
			}
		}	
	}

	private ArrayList<String> getTaskCards(ArrayList<String> taskIds) {
		try {
			
			ArrayList<String> taskCardStrings = new ArrayList<String>();
			for(String subTaskId  : taskIds) {
				 System.out.println("SUB TASK CARD: " + subTaskId);
				//get all tcs from tc sub fields
				try {
					List<TaskCard> cards = em.createQuery("Select t From TaskCard t where t.tcSub =:sub", TaskCard.class)
							.setParameter("sub", subTaskId)
							.getResultList();
					for(TaskCard tc: cards) {
						 System.out.println("ENG TASK CARD: " + tc.getTaskCard());
		
						taskCardStrings.add( tc.getTaskCard());
					}
				}catch (Exception e) {
					 System.out.println("NO ENG TASK CARD FOUND FOR " + subTaskId);
				}
			}
			return taskCardStrings;	
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}	
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
		blob.setBlobDescription("JOBCARD");
		blob.setCustomDescription("JOBCARD");
		
	
		blob.getId().setBlobNo(((getTransactionNo("BLOB").longValue())));
		w.setBlobNo(new BigDecimal(blob.getId().getBlobNo()));
			
		 System.out.println("INSERTING TEMP Blob : " +blob.getId().getBlobNo() );
		
		insertData(blob);
		insertData(w);
		
		return blob;
	}
	

	private Wo createTempWo(String type, String comapny, String location, 
			String site,String ac, String print, String acType, MODEL input ) {
		
			JOBCARD jc = input.getEFFECTIVITY().getJOBCARD();
			Wo wo = null;

			wo = new Wo();
			wo.setCreatedDate(new Date());
			wo.setCreatedBy("IFACE-SIA");
			
			//EMRO fields to create basic object
			wo.setManufactureOrder("N");
			wo.setAuthorization("Y");
			wo.setAuthorizationBy("TRAX_IFACE");
			wo.setGlCompany("SIAEC");				
			wo.setExpenditure(setExpenditure("General"));
			wo.setPriority("NORMAL");
			
			wo.setWoDescription(print);
			wo.setWoCategory(type);
			wo.setLocation(location);
			wo.setSite(site);
			wo.setAc(ac);
			wo.setAcSn(acType);
			wo.setGlCompany(comapny);
			
			wo.setPnDescription(input.getEFFECTIVITY().getJOBCARD().getWPTITLE());
			wo.setTaskCardNumberingSystem(new BigDecimal(0));
			wo.setAmpRevision( filterADDATTR(jc.getJOBI().getPLI().getADDATTR(), "LATEST-REVISION-DATE"));
			wo.setExternalReference(jc.getJOBI().getZONE() +"/" +jc.getPOS()
			);
			wo.setAmpTempRev(jc.getWPNBR());
			wo.setCosl(jc.getJCNBR());
			wo.setFlight(jc.getMANHRS().substring(0, 9));
			wo.setAmpMs(filterADDATTR(jc.getJOBI().getPLI().getADDATTR(), "BUSR12"));
			wo.setAuthorizationDate(convertStringToDate(jc.getWPDATE()));
			wo.setStatus("OPEN");
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
			wo.setLongTermEvent("YES");
			wo.setModifiedDate(new Date());
			wo.setExpenditure(("General"));
			wo.setWo(getTransactionNo("WOSEQ").longValue());
			
			wo.setExpenditure(setExpenditure("General"));
			wo.setPriority("NORMAL");
						
			wo.setScheduleStartHour(new BigDecimal(0));
			wo.setScheduleStartMinute(new BigDecimal(0));
			wo.setScheduleCompletionHour(new BigDecimal(0));
			wo.setScheduleCompletionMinute(new BigDecimal(0));
			
			wo.setScheduleStartDate(new Date());
			wo.setScheduleCompletionDate(new Date());
			wo.setActualStartDate(new Date());
			wo.setScheduleOrgCompletionDate(new Date());	
			
			wo.setActualStartHour(new BigDecimal(0));
			wo.setActualStartMinute(new BigDecimal(0));
			wo.setScheduleOrgCompletionHour(new BigDecimal(0));
			wo.setScheduleOrgCompletionMinute(new BigDecimal(0));
			
			
			
			
			
			 System.out.println("INSERTING TEMP WO: " + wo.getWo());
			
			insertData(wo);
		
			
			
		
		return wo;
	}


	private trax.aero.pojo.json.OUTPUT convertXmlToJson(MODEL input) throws Exception
	{
		 System.out.println("Converting XML to JSON");
		
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

		pdfName = "wo_"+ card.getWPNBR() + "_" + card.getSEQNBR() + "_" + card.getWONBR() + "_" +random + ".pdf" ;
		
		return pdfName;
	}

	
	private String genrateTxtName(MODEL input) {
		String txtNmae = "";
		
		JOBCARD card = input.getEFFECTIVITY().getJOBCARD();

		txtNmae = card.getWPNBR()+ "_" + card.getWPNBR() + "_" +filterADDATTR(input.getEFFECTIVITY().getJOBCARD().getJOBI().getPLI().getADDATTR(), "PRINTER-NAME")  ;
		
		return txtNmae;
	}


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
				+" " +	filterADDATTR(input.getEFFECTIVITY().getJOBCARD().getJOBI().getPLI().getADDATTR(), "TASK-NBR") + " " + "OK"	);
		
		
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
			
			 System.out.println("Input: " + sw.toString());
			String text = sw.toString();
			
			
			return MqUtilities.sendMqText(text);
			
		}catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}	
	
	//insert generic data from model objects
		public <T> void insertData( T data) 
		{
			try {	
				em.merge(data);
				em.flush();
			}catch (Exception e){	
				 System.err.println(e.toString());
				throw e;
			}
		}
		
		private <T> void deleteData( T data) 
		{
			try {	
				em.remove(data);
				em.flush();
			}catch (Exception e){	
				 System.err.println(e.toString());
				throw e;
			}
		}
		
		private BigDecimal getTransactionNo(String code)
		{		
			
			try
			{
				if(code.equalsIgnoreCase("WOSEQ")) {
					String sql = "SELECT R.WO FROM WO R WHERE R.WO = ? ";
					long i = 	10000;
					long max = 	29999;
					try {	
			            while (true)
			            {
			            	if(i >= max) {
			            		i = 10000;
			            	}
			            	try {
			            	  @SuppressWarnings("unused")
			            	  BigDecimal found = (BigDecimal) em.createNativeQuery(sql)
			        			.setParameter(1, i )
			        			.getSingleResult();	
			            	}  catch (NoResultException e) {
			            		return new BigDecimal(i);
			            	}   
				            i++;		                
			            }
			        } catch (Exception e) {
			        	 System.err.println("Error getting WO.");
			        	 System.err.println(e.toString());
			            throw e;
			        } 
				}else {
					BigDecimal acctBal = (BigDecimal) em.createNativeQuery("SELECT pkg_application_function.config_number ( ? ) "
							+ " FROM DUAL ").setParameter(1, code).getSingleResult();
								
					return acctBal;
				}
			}
			catch (Exception e) 
			{
				 System.err.println("An unexpected error occurred getting the sequence. " + "\nmessage: " + e.toString());
			}
			
			return null;
			
		}


		


		private void deleteTempWoTaskCardBlob(String wo) throws Exception {

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

		public void deleteWoTaskCardPn( String wo) throws Exception{
			String query = "DELETE WO_TASK_CARD_PN where WO = ?";		
			try
			{	
				em.createNativeQuery(query).setParameter(1, wo).executeUpdate();	
			}
			catch (Exception e) 
			{
				throw new Exception("An Exception occurred executing the query to delete the WO_TASK_CARD_PN. " + "\n error: " + e.toString());
			}
		}

		private BlobTable getTempBlob(BigDecimal blobNo) {
			try {
				BlobTable blob = em.createQuery("SELECT b FROM BlobTable b where b.id.blobNo = :bl and b.blobDescription = :des", BlobTable.class)
						.setParameter("bl", blobNo.longValue())
						.setParameter("des","JOBCARD" )
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
						.setParameter("des","JOBCARD" )
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
		
		private Date convertStringToDate( String string) {
			try {
				return new SimpleDateFormat("yyyyMMdd").parse(string); 
			}catch(Exception e) {
				e.printStackTrace();
				return null;
			}
		}



		



		@Override
		public void sendRequestToPrintServer(MODEL input, String xml, Wo w) {
			//creates temp wo with attached xml
			String printer = "", date, time, revision;
			ArrayList<PrintAck> ack = null;
			 int status = 0;
			
			//creates temp wo task card 
			try {
				
				createTempWoTaskCard(input, w); 
				
				//call wo pack print with flag 
				status = sendWorkPackPrintJob(w );
			}catch (Exception e) {
				e.printStackTrace();
				status = 1;
			}
			
			if(status == 0) {
				sendPrintStatusAcknowledgement(input, "P", "SUCESS");
			}else {
				sendPrintStatusAcknowledgement(input, "E", "ERROR");
				date =filterADDATTR( input.getEFFECTIVITY().getJOBCARD().getJOBI().getPLI().getADDATTR(), "IDOC-DATE");
				revision = filterADDATTR( input.getEFFECTIVITY().getJOBCARD().getJOBI().getPLI().getADDATTR(), "LATEST-REVISION");
				time = filterADDATTR( input.getEFFECTIVITY().getJOBCARD().getJOBI().getPLI().getADDATTR(), "IDOC-TIME") ;
				printer = (filterADDATTR(input.getEFFECTIVITY().getJOBCARD().getJOBI().getPLI().getADDATTR(), "PRINTER-NAME"));
				ack = new ArrayList<PrintAck>();
				ack.add(new PrintAck());	
				
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
			}
			
		}	
		
		@SuppressWarnings("unchecked")
		public void processBatFile() {
			try
			{	
				List<InterfaceAudit> interfaceAudits = em.createQuery("SELECT p FROM InterfaceAudit p WHERE p.transactionObject = :obj "
						+ "and p.messageNeedsToBeSent = :tas ")
						.setParameter("obj", "SAP_TC")
						.setParameter("tas", "Y")
						.getResultList();
				if(interfaceAudits != null && !interfaceAudits.isEmpty()) {
					 System.out.println("Interface Audit SIZE " +interfaceAudits.size());
					for(InterfaceAudit i : interfaceAudits) {
						try 
						{
							 System.out.println("Interface Audit PROCESSING " +i.getTransaction());
							for (InterfaceData id : i.getInterfaceData()) {
								S3Utilities.setDatFile(id.getClobDocument(),id.getFileName(),String.valueOf( i.getTransaction()));
							}
							if(i.getInterfaceData() != null && !i.getInterfaceData().isEmpty()) {
								ModelController.sendEmailDat(i.getInterfaceData());
							}
							em.refresh(i);
							i.setMessageNeedsToBeSent("N");
							insertData(i);
						}
						catch (Exception e) 
				        {
							
						}
					}
				}
			}
			catch (Exception e)
			{	
				e.printStackTrace();
			}
		}
		
		private String setExpenditure(String string) {
			JournalEntriesExpenditure journalEntriesExpenditure = null;
			try
			{
				journalEntriesExpenditure = em.createQuery("SELECT j FROM JournalEntriesExpenditure j WHERE j.id.categoryCode = :code AND  j.id.transaction = :tra AND j.id.category = :cat", JournalEntriesExpenditure.class)
				.setParameter("code", string)
				.setParameter("tra", "WIP")
				.setParameter("cat", "EXPENDITURE")
				.getSingleResult();
				
				
				return journalEntriesExpenditure.getId().getCategoryCode();
			}
			catch (Exception e)
			{
				journalEntriesExpenditure = new JournalEntriesExpenditure();
				JournalEntriesExpenditurePK pk = new JournalEntriesExpenditurePK();
				journalEntriesExpenditure.setId(pk);
				journalEntriesExpenditure.setModifiedBy("TRAX_IFACE");
				journalEntriesExpenditure.setModifiedDate(new Date());
				journalEntriesExpenditure.setCreatedBy("TRAX_IFACE");
				journalEntriesExpenditure.setCreatedDate(new Date());
				
				journalEntriesExpenditure.getId().setCategoryCode("DEFAULT");
				journalEntriesExpenditure.getId().setTransaction("WIP");
				journalEntriesExpenditure.getId().setCategory("EXPENDITURE");
				
				journalEntriesExpenditure.getId().setClass_("LABOR");
				
				journalEntriesExpenditure.setJournalDescription("DEFAULT");
				journalEntriesExpenditure.setExpenditureUse("PRODUCTION");
				
				
				 System.out.println("INSERTING CODE: " + journalEntriesExpenditure.getId().getCategoryCode());
				insertData(journalEntriesExpenditure);
			}
			return journalEntriesExpenditure.getId().getCategoryCode();
		}
		
}
