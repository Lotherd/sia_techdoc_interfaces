/*
* This source code file is the intellectual property of TRAX USA Corp.
* Copyright (c) 2025 TRAX USA Corp. All rights reserved.
*/
package trax.aero.core.scheduler;

import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import javax.ejb.EJB;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.tinylog.Logger;
import trax.aero.data.ITechDocData;
import trax.aero.messaging.mq.MqUtilities;
import trax.aero.model.Wo;
import trax.aero.pojo.GroupBuffer;
import trax.aero.pojo.xml.MODEL;
import trax.aero.pojo.xml.ROOT;
import trax.aero.utilities.StringUtilities;

public class TechDocProcessor implements Runnable {

    // Variables
    @EJB ITechDocData data;

    int minuteCounter = 0;
    boolean sendEmail = false;

    public TechDocProcessor(ITechDocData data) {
        this.data = data;
    }

    private void process() throws Exception {

        String message;
        JAXBContext jc = JAXBContext.newInstance(ROOT.class);
        Unmarshaller unmarshaller = jc.createUnmarshaller();

        minuteCounter++;
        if (minuteCounter >= 60) {
            sendEmail = true;
            minuteCounter = 0;
        }
        try {
            message = MqUtilities.receiveMqText(sendEmail);
            sendEmail = false;

            if (message != null) {

                Logger.info(message);
                try {

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
                    GroupBuffer buffer = data.getGroup().get(idocID);
                    if (buffer == null) {
                        data.getGroup().put(idocID, buffer = new GroupBuffer());
                    }
                    buffer.setTotalCount(totalCount);
                    Logger.info("Total count is " + totalCount);
                    Logger.info("nextExpectedSeq start" + buffer.getNextExpectedSeq());
                    if (seqNbr.longValue() == buffer.getNextExpectedSeq()) {
                        try {
                            Logger.info("deliver");
                            // deliver
                            processXmlMessage(root);
                        } catch (Exception e) {
                            Logger.error(e);
                        } finally {
                            buffer.setNextExpectedSeq(buffer.getNextExpectedSeq() + 1);
                            Logger.info("nextExpectedSeq deliver" + buffer.getNextExpectedSeq());
                            data.getGroup().put(idocID, buffer);
                            // flush
                            flushContiguous(idocID);
                        }
                    } else {
                        Logger.info("save");
                        // save
                        buffer.getBuffer().put(seqNbr.longValue(), root);
                        data.getGroup().put(idocID, buffer);
                        // flush
                        flushContiguous(idocID);
                    }
                    // reset
                    Logger.info("Size  " + data.getGroup().get(idocID).getBuffer());
                    Logger.info("NextExpectedSeq end" + data.getGroup().get(idocID).getNextExpectedSeq());
                    if (data.getGroup().get(idocID).getNextExpectedSeq() > totalCount
                            && data.getGroup().get(idocID).getBuffer().isEmpty()) {
                        Logger.info("reset " + data.getGroup().get(idocID).getBuffer().size());
                        Logger.info("nextExpectedSeq reset" + data.getGroup().get(idocID).getNextExpectedSeq());
                        data.getGroup().remove(idocID);
                    }
                } catch (Exception e) {
                    Logger.error(e);
                }
            }
        } catch (Exception e) {
            Logger.error(e);
        } finally {
            sendEmail = false;
        }
    }

    private void flushContiguous(String idocID) {
        while (!data.getGroup().get(idocID).getBuffer().isEmpty()) {
            ROOT root =
                    data.getGroup()
                            .get(idocID)
                            .getBuffer()
                            .get(data.getGroup().get(idocID).getNextExpectedSeq());
            if (root == null) break;
            try {
                Logger.info("deliver flush");
                processXmlMessage(root);
            } catch (Exception e) {
                Logger.error(e);
            } finally {
                GroupBuffer buffer = data.getGroup().get(idocID);
                buffer.getBuffer().remove(data.getGroup().get(idocID).getNextExpectedSeq());
                buffer.setNextExpectedSeq(data.getGroup().get(idocID).getNextExpectedSeq() + 1);
                data.getGroup().put(idocID, buffer);
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

    public void run() {
        try {
            if (data.lockAvailable("TD2")) {
                data.lockTable("TD2");
                Logger.info("Start process");
                data.processBatFile();
                data.cleanUpTemp();
                Thread.sleep(1000);
                process();
                Logger.info("End process");
                data.unlockTable("TD2");
            }
        } catch (Exception e) {
            Logger.error(e);
        }
    }
}
