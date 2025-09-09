/*
* This source code file is the intellectual property of TRAX USA Corp.
* Copyright (c) 2025 TRAX USA Corp. All rights reserved.
*/
package trax.aero.api.controller;

import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.tinylog.Logger;
import trax.aero.data.ITechDocData;
import trax.aero.messaging.mq.MqUtilities;
import trax.aero.model.Wo;
import trax.aero.pojo.GroupBuffer;
import trax.aero.pojo.Print;
import trax.aero.pojo.xml.MODEL;
import trax.aero.pojo.xml.ROOT;
import trax.aero.utilities.StringUtilities;

@Path("/Service")
public class TechDocController {

    @EJB ITechDocData data;

    @GET
    @Path("/health")
    @Produces(MediaType.TEXT_PLAIN)
    public Response health(String input) {
        try {
            return Response.ok(data.health()).build();
        } catch (Exception e) {
            return Response.status(Status.BAD_REQUEST)
                    .type(MediaType.TEXT_PLAIN)
                    .entity(e.getMessage())
                    .build();
        }
    }

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
            long totalCount =
                    new BigDecimal(
                                    data.filterADDATTR(
                                            root.getMODELS()
                                                    .get(0)
                                                    .getEFFECTIVITY()
                                                    .getJOBCARD()
                                                    .getJOBI()
                                                    .getPLI()
                                                    .getADDATTR(),
                                            "COUNT"))
                            .longValue();
            BigDecimal seqNbr =
                    new BigDecimal(root.getMODELS().get(0).getEFFECTIVITY().getJOBCARD().getSEQNBR());
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
            data.getGroup().computeIfAbsent(idocID, k -> new GroupBuffer());
            GroupBuffer buffer = data.getGroup().get(idocID);
            buffer.setTotalCount(totalCount);
            Logger.info("nextExpectedSeq start" +buffer.getNextExpectedSeq());
            if (seqNbr.longValue() == buffer.getNextExpectedSeq()) {
                try {
                    Logger.info("deliver");
                    // deliver
                    processXmlMessage(root);
                } catch (Exception e){
                    Logger.error(e);
                }finally {
                    buffer.setNextExpectedSeq(buffer.getNextExpectedSeq()+1);
                    Logger.info("nextExpectedSeq deliver" +buffer.getNextExpectedSeq());
                    data.getGroup().put(idocID,buffer);
                    // flush
                    flushContiguous(idocID);
                }
            } else {
                Logger.info("save");
                // save
                buffer.getBuffer().put(seqNbr.longValue(), root);
                data.getGroup().put(idocID,buffer);
                // flush
                flushContiguous(idocID);
            }
            // reset
            Logger.info("Size  " +data.getGroup().get(idocID).getBuffer());
            Logger.info("NextExpectedSeq end" +data.getGroup().get(idocID).getNextExpectedSeq());
            if (data.getGroup().get(idocID).getNextExpectedSeq() > totalCount
                    && data.getGroup().get(idocID).getBuffer().isEmpty()) {
                Logger.info("reset " + data.getGroup().get(idocID).getBuffer().size());
                Logger.info("nextExpectedSeq reset" +data.getGroup().get(idocID).getNextExpectedSeq());
                data.getGroup().remove(idocID);
            }
        } catch (Exception e) {
            Logger.error(e);
        }
        return Response.ok().build();
    }

    private void flushContiguous(String idocID) {
        while (!data.getGroup().get(idocID).getBuffer().isEmpty()) {
            ROOT root = data.getGroup().get(idocID).getBuffer().get(data.getGroup().get(idocID).getNextExpectedSeq());
            if (root == null) break;
            try {
                Logger.info("deliver flush");
                processXmlMessage(root);
            } catch (Exception e){
                Logger.error(e);
            } finally {
                GroupBuffer buffer = data.getGroup().get(idocID);
                buffer.getBuffer().remove(data.getGroup().get(idocID).getNextExpectedSeq());
                buffer.setNextExpectedSeq(data.getGroup().get(idocID).getNextExpectedSeq() + 1);
                data.getGroup().put(idocID,buffer);
            }
        }
    }

    private void processXmlMessage(ROOT root) throws Exception {
        Thread.sleep(1000);
        JAXBContext jc;
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

            String xml = StringUtilities.xmlCleaning(sw.toString());
            Wo w = data.issueToTechDocRequest(model, xml);
            data.linkWoToParent(
                    w, parent, new BigDecimal(model.getEFFECTIVITY().getJOBCARD().getSEQNBR()));
            data.sendRequestToPrintServer(model, xml, w);
        }
    }
}
