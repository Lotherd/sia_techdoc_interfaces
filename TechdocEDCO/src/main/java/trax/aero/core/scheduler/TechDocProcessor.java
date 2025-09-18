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
import trax.aero.utilities.GroupBufferManager;
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
                            new BigDecimal(
                                    data.filterADDATTR(
                                            root.getMODELS()
                                                    .get(0)
                                                    .getEFFECTIVITY()
                                                    .getJOBCARD()
                                                    .getJOBI()
                                                    .getPLI()
                                                    .getADDATTR(),
                                            "BUSR12"));
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
                    GroupBuffer buffer = GroupBufferManager.get(idocID);
                    if (buffer == null) {
                        GroupBufferManager.put(idocID, buffer = new GroupBuffer());
                    }
                    buffer.setTotalCount(totalCount);
                    Logger.info("Total count is " + totalCount);
                    Logger.info("idocID " + idocID);
                    Logger.info("Counter start" + buffer.getCounter());

                    Logger.info("save - sequence " + seqNbr.longValue());
                    buffer.getBuffer().put(seqNbr.longValue(), root);
                    buffer.incrementCounter();
                    GroupBufferManager.put(idocID, buffer);

                    Logger.info("Buffer size: " + buffer.getBuffer().size());
                    Logger.info("Counter end: " + buffer.getCounter());

                    if (buffer.isComplete()) {
                        Logger.info("All documents received! Starting ordered processing...");
                        flushContiguous(idocID);
                    } else {
                        Logger.info(
                                "Waiting for more documents ("
                                        + buffer.getCounter()
                                        + "/"
                                        + buffer.getTotalCount()
                                        + ")");
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
        GroupBuffer buffer = GroupBufferManager.get(idocID);

        if (!buffer.isComplete()) {
            Logger.info("Group not complete yet, skipping flush");
            return;
        }

        Logger.info("Processing all documents in order");

        long counter = 1;
        while (!buffer.getBuffer().isEmpty()) {

            Long firstKey = buffer.getBuffer().firstKey();
            ROOT root = buffer.getBuffer().get(firstKey);

            if (root == null) break;

            try {
                Logger.info("deliver flush with key " + firstKey);
                processXmlMessage(root, counter);
            } catch (Exception e) {
                Logger.error(e);
            } finally {

                buffer.getBuffer().remove(firstKey);
                counter++;
                GroupBufferManager.put(idocID, buffer);
            }
        }
        GroupBufferManager.remove(idocID);
    }

    private void processXmlMessage(ROOT root, Long counter) throws Exception {
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
            data.linkWoToParent(w, parent, new BigDecimal(counter));
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
