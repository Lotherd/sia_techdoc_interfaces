package trax.aero.application;

import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.tinylog.Logger;
import trax.aero.data.IModelData;
import trax.aero.model.Wo;
import trax.aero.pojo.Print;
import trax.aero.pojo.xml.MODEL;
import trax.aero.pojo.xml.ROOT;
import trax.aero.util.MqUtilities;

@Path("/Service")
public class Service {

  @EJB IModelData data;

  @POST
  @Path("/sendMqText")
  @Consumes(MediaType.TEXT_PLAIN)
  @Produces(MediaType.TEXT_PLAIN)
  public Response insertXml(String input) {
    try {
      MqUtilities.sendMqText(input);
    } catch (Exception e) {
      Logger.error(e);
    }
    return Response.ok().build();
  }

  @POST
  @Path("/print")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.TEXT_PLAIN)
  public Response print(Print input) {
    try {
      Logger.info("Print WO:" + input.getWo() + ", PATH:" + input.getPath());
      data.sendPrintToOutBound(input);
    } catch (Exception e) {
      Logger.error(e);
    } finally {
      try {
        if (data.getCon() != null && !data.getCon().isClosed()) {
          data.getCon().close();
        }
      } catch (Exception ignored) {
      }
    }
    return Response.ok().build();
  }

  @POST
  @Path("/issueToTechDocRequest")
  @Consumes(MediaType.TEXT_PLAIN)
  @Produces(MediaType.TEXT_PLAIN)
  public Response issue(String message) {

    try {
      JAXBContext jc = JAXBContext.newInstance(ROOT.class);
      Unmarshaller unmarshaller = jc.createUnmarshaller();
      message = "<ROOT>" + message + "</ROOT>";

      ROOT root;
      StringReader sr = new StringReader(message);

      root = (ROOT) unmarshaller.unmarshal(sr);
      // create parent WO
      BigDecimal COUNT =
          new BigDecimal(
              data.filterADDATTR(
                  root.getMODELS()
                      .get(0)
                      .getEFFECTIVITY()
                      .getJOBCARD()
                      .getJOBI()
                      .getPLI()
                      .getADDATTR(),
                  "COUNT"));
      // SAVE TRAX WO NUMBER
      // AS ISSUE TO TRAX IS SEPARATE REQUESTS
      String idocID =
          data.filterADDATTR(
                  root.getMODELS()
                      .get(0)
                      .getEFFECTIVITY()
                      .getJOBCARD()
                      .getJOBI()
                      .getPLI()
                      .getADDATTR(),
                  "USER-NAME")
              + data.filterADDATTR(
                  root.getMODELS()
                      .get(0)
                      .getEFFECTIVITY()
                      .getJOBCARD()
                      .getJOBI()
                      .getPLI()
                      .getADDATTR(),
                  "IDOC-DATE")
              + data.filterADDATTR(
                  root.getMODELS()
                      .get(0)
                      .getEFFECTIVITY()
                      .getJOBCARD()
                      .getJOBI()
                      .getPLI()
                      .getADDATTR(),
                  "IDOC-TIME")
              + data.filterADDATTR(
                  root.getMODELS()
                      .get(0)
                      .getEFFECTIVITY()
                      .getJOBCARD()
                      .getJOBI()
                      .getPLI()
                      .getADDATTR(),
                  "PRINTER-NAME");
      Wo parent = data.createParentWo(COUNT, idocID);
      Logger.info("Size: " + parent.getDocumentNo().intValue());
      for (MODEL model : root.getMODELS()) {
        jc = JAXBContext.newInstance(MODEL.class);
        Marshaller marshaller = jc.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        StringWriter sw = new StringWriter();
        marshaller.marshal(model, sw);

        String xml = sw.toString();
        xml = xml.replaceAll("&amp;apos;", "&apos;");
        xml = xml.replaceAll("&amp;gt;", "&gt;");
        xml = xml.replaceAll("&amp;lt;", "&lt;");
        xml = xml.replaceAll("&amp;quot;", "&quot;");
        xml = xml.replaceAll("&amp;re;", "&#xA;");
        Wo w = data.issueToTechDocRequest(model, xml);
        data.linkWoToParent(
            w, parent, new BigDecimal(model.getEFFECTIVITY().getJOBCARD().getSEQNBR()));
        data.setCountWoToParent(w, parent);
        data.sendRequestToPrintServer(model, xml, w);
        try {
          if (data.getCon() != null && !data.getCon().isClosed()) data.getCon().close();
        } catch (Exception e) {
          Logger.error(e);
        }
      }
    } catch (Exception e) {
      Logger.error(e);
    } finally {
      try {
        if (data.getCon() != null && !data.getCon().isClosed()) data.getCon().close();
      } catch (Exception e) {
        Logger.error(e);
      }
    }
    return Response.ok().build();
  }
}
