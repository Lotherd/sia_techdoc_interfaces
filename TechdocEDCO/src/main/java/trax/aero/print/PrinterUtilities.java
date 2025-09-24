/*
* This source code file is the intellectual property of TRAX USA Corp.
* Copyright (c) 2025 TRAX USA Corp. All rights reserved.
*/
package trax.aero.print;

import java.io.File;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.io.FilenameUtils;
import org.tinylog.Logger;
import trax.aero.pojo.Dw_Wo_Pack_Print;
import trax.types.PrintQueueJob;

public class PrinterUtilities {

    static ArrayList<String> heavyPrinters =
            new ArrayList<>(
                    Arrays.asList("EC61", "EC62", "EC63", "SINW", "SIO1", "EC1O", "SIOP", "NQP1"));
    static ArrayList<String> heavyPrintersRicoh =
            new ArrayList<>(Arrays.asList("EC1O", "SIOP", "NQP1"));
    static ArrayList<String> heavyPrintersOcepdf =
            new ArrayList<>(Arrays.asList("EC61", "EC62", "EC63", "SINW", "SIO1"));
    private static final String SIO1 = "SIO1", SINW = "SINW";

    public static int sendWorkPackPrintJob(String printWindow, Dw_Wo_Pack_Print dwSel)
            throws Exception {
        int job = 0;
        String s_wo_print;

        StringWriter sw = new StringWriter();
        JAXBContext jc = JAXBContext.newInstance(Dw_Wo_Pack_Print.class);
        Marshaller marshaller = jc.createMarshaller();
        marshaller.marshal(dwSel, sw);
        String xml = sw.toString();

        xml = xml.replaceAll("(<\\?xml.*?\\?>)", "");

        if ("Y".equalsIgnoreCase(dwSel.getRow().getMulti_wo_selected()))
            s_wo_print = "Work Order Pack Print Multi Wo : " + dwSel.getRow().getL_wo_multi();
        else s_wo_print = "Work Order Pack Print Wo: " + dwSel.getRow().getWo();

        PrintQueueJob propJob = new PrintQueueJob();
        propJob.setPrintWindow(printWindow);
        propJob.setPrintTitle(s_wo_print);
        propJob.setPrintParameters(xml.getBytes());
        propJob.setUser("ADM");

        addJobToJMSQueue(propJob);

        Logger.info(
                "Print Job " + dwSel.getRow().getWo() + " Has been successfully sent to the print queue");

        return job;
    }

    private static void addJobToJMSQueue(PrintQueueJob propJob) {
        propJob.setIsWPP("Y");

        String url = System.getProperty("Trax_Print_WPP_URL") + "rest/print/printwpp";

        Client client = null;

        try {

            client = ClientBuilder.newClient();
            Logger.info("Calling Print web service : " + "'" + url + "'");

            client.target(url).request().post(Entity.entity(propJob, MediaType.APPLICATION_JSON));

            Logger.info("After Print web service ");

        } finally {
            if (client != null) client.close();
        }
    }

    public static void sendToPrinterHeavyLP(String printService, File file, String side, String tray)
            throws Exception {

        if (file != null && printService != null && file.exists() && file.isFile()) {
            Logger.info("Job received for printer: " + printService);
            try {
                String ricohCommands = "";
                String oceCommands = "";
                if (heavyPrintersOcepdf.contains(printService)) {
                    String tempTray = tray;
                    if (printService.equalsIgnoreCase(SIO1)) {
                        switch (tempTray) {
                            case "2":
                                tempTray = "3";
                                break;
                            case "3":
                                tempTray = "2";
                                break;
                        }
                        switch (tray) {
                            case "1":
                                tray = "";
                                break;
                            case "2":
                                tray = " -o MediaType=ROSE";
                                break;
                            case "3":
                                tray = " -o MediaType=BLUE";
                                break;
                            default:
                                tray = " -o MediaType=ORANGE";
                                break;
                        }
                        side = "DUPLEX";
                    } else if (printService.equalsIgnoreCase(SINW)) {
                        switch (tempTray) {
                            case "1":
                                tempTray = "2";
                                break;
                            case "2":
                                tempTray = "3";
                                break;
                            case "3":
                                tempTray = "1";
                                break;
                        }
                        switch (tray) {
                            case "1":
                                tray = "";
                                break;
                            case "2":
                                tray = " -o MediaType=ROSE";
                                break;
                            case "3":
                                tray = " -o MediaType=BLUE";
                                break;
                            default:
                                tray = " -o MediaType=ORANGE";
                                break;
                        }
                        side = "DUPLEX";
                    } else {
                        switch (tempTray) {
                            case "2":
                                tempTray = "4";
                                break;
                            case "3":
                                tempTray = "2";
                                break;
                        }
                        // InputSlot=1Tray  2Tray 3Tray OCE
                        switch (tray) {
                            case "1":
                                tray = "";
                                break;
                            case "2":
                                tray = " -o MediaType=ROSE";
                                break;
                            case "3":
                                tray = " -o MediaType=BLUE";
                                break;
                            default:
                                tray = " -o MediaType=ORANGE";
                                break;
                        }
                    }
                    // Duplex=DuplexNoTumble OCE
                    if (side.equalsIgnoreCase("DUPLEX")) {
                        side = " -o Duplex=True -o Tumble=False";
                    } else {
                        side = " -o Duplex=False";
                    }

                    String trayValue =
                            (tray != null && !tray.isEmpty() && tray.contains("="))
                                    ? tray.substring(tray.indexOf("=") + 1)
                                    : "WHITE";

                    String sideValue = (side != null) ? side.replaceAll("-o ", "") : "";

                    Logger.info("OCE tray: " + trayValue + " side: " + sideValue);
                    oceCommands =
                            " -o StapleWhen=EndOfSet -o OCStaple=TopLeftPortrait -o PaperDimension=A4"
                                    + tray
                                    + side;
                } else if (heavyPrintersRicoh.contains(printService)) {
                    // InputSlot=1Tray  2Tray 3Tray RICHO
                    switch (tray) {
                        case "1":
                            tray = "1Tray";
                            break;
                        case "2":
                            tray = "2Tray";
                            break;
                        case "3":
                            tray = "3Tray";
                            break;
                        default:
                            tray = "MultiTray";
                            break;
                    }
                    // Duplex=DuplexNoTumble RICHO
                    if (side.equalsIgnoreCase("DUPLEX")) {
                        side = "DuplexNoTumble";
                    } else {
                        side = "None";
                    }
                    Logger.info("Ricoh tray: " + tray + " side: " + side);
                    ricohCommands =
                            " -o StapleLocation=UpperLeft -o InputSlot="
                                    + tray
                                    + " -o PageRegion=A4 -o Duplex="
                                    + side;
                }
                // Command to print the document with duplex and tray options using lp on Linux
                String command =
                        "lp -d " + printService + oceCommands + ricohCommands + " " + file.getAbsolutePath();
                Logger.info("Command: " + command);
                // Create the process builder
                ProcessBuilder processBuilder = new ProcessBuilder(command.split(" "));

                // Start the process (print)
                Process process = processBuilder.start();

                // Wait for the process to finish
                int exitCode = process.waitFor();
                if (exitCode == 0) {
                    Logger.info("Print job submitted successfully.");
                } else {
                    Logger.error("Error occurred during printing: " + exitCode);
                }
            } catch (Exception e) {
                Logger.error("Exception printing JOB ", e);
                throw e;
            }
        }
    }

    public static void sendPrint(String printer, String path, String side, String tray)
            throws Exception {

        String fileLocOut = System.getProperty("TECH_fileLocOut");
        try {

            // PDF
            File print = new File(path);

            File theDir =
                    new File(fileLocOut + File.separator + FilenameUtils.removeExtension(print.getName()));
            if (!theDir.exists()) {
                Logger.info(theDir.mkdirs());
            }
            Logger.info(
                    String.format(
                            "MOVE %s TO %s%s%s%s%s",
                            print.toPath(),
                            fileLocOut,
                            File.separator,
                            FilenameUtils.removeExtension(print.getName()),
                            File.separator,
                            print.getName()));
            Files.move(
                    print.toPath(),
                    new File(
                                    fileLocOut
                                            + File.separator
                                            + FilenameUtils.removeExtension(print.getName())
                                            + File.separator
                                            + print.getName())
                            .toPath(),
                    StandardCopyOption.REPLACE_EXISTING);

            // Print server folder logic
            File localPrint =
                    new File(
                            fileLocOut
                                    + File.separator
                                    + FilenameUtils.removeExtension(print.getName())
                                    + File.separator
                                    + print.getName());
            // sendToPrinter(printer, print);
            if (heavyPrinters.contains(printer)) {
                sendToPrinterHeavyLP(printer, localPrint, side, tray);
            } else {
                sendToPrinterLP(printer, localPrint);
            }
        } catch (Exception e) {
            Logger.error(e);
            throw e;
        }
    }

    private static void sendToPrinterLP(String printer, File print) throws Exception {
        try {
            // Create a ProcessBuilder to run the 'lp' command
            String command =
                    "lp -o fit-to-page -o scaling=100 -o Duplexer=True -o PageSize=A4 -o Duplex=DuplexNoTumble -d "
                            + printer
                            + " "
                            + print.getAbsolutePath();
            Logger.info(command);
            CommandLine cmdLine = new CommandLine("lp");
            cmdLine.addArguments(
                    new String[] {
                        "-o",
                        "fit-to-page",
                        "-o",
                        "scaling=100",
                        "-o",
                        "Duplexer=True",
                        "-o",
                        "PageSize=A4",
                        "-o",
                        "Duplex=DuplexNoTumble",
                        "-d",
                        printer,
                        print.getAbsolutePath()
                    });
            DefaultExecutor executor = DefaultExecutor.builder().get();
            int exitCode = executor.execute(cmdLine);
            /*
            ProcessBuilder processBuilder = new ProcessBuilder(command.split(" "));
            // Start the process
            Process process = processBuilder.start();

            // Wait for the process to complete
            int exitCode = process.waitFor();
             */
            if (exitCode == 0) {
                Logger.info("Printing completed successfully!");
            } else {
                Logger.info("Error occurred while printing. Exit code: " + exitCode);
                throw new Exception("Error occurred while printing. Exit code: " + exitCode);
            }
        } catch (Exception exc) {
            Logger.info("Exception printing JOB***************************");
            Logger.error(exc);
            throw exc;
        }
    }
}
