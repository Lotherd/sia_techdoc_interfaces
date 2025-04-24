package trax.aero.controller;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.SimpleEmail;
import org.tinylog.Logger;
import trax.aero.model.InterfaceData;

import javax.mail.util.ByteArrayDataSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ModelController {

    static String errors = "";
    private static final String[] headersPDF = {"Name of PDF Attachment", "SAP Service Order", "Jobcard Number", "JC Title", "SAP Group Number", "Task Type", "TRAX WO Number", "Attachment", "Attachment ID"};
    private static final String[] headersCOVER = {"SAP Service Order", "Jobcard Number", "JC Title", "SAP Group Number", "Task Type", "TRAX WO Number", "Attachment", "Attachment ID"};
    private static final String[] acType = {"380", "350", "737", "747", "777", "777F", "737NG", "737Max", "330", "789", "290", "744", "738",};

    public ModelController() {

    }


    //MOD 19
    public static void sendEmailTrax(String wo,
                                     String revision,
                                     String date,
                                     String time,
                                     String pAcks, String printer, String stringHeader) {
        try {
            String[] headers = null;
            if (stringHeader.equalsIgnoreCase("Attachment Missing")) {
                headers = headersPDF;
            } else {
                headers = headersCOVER;
            }

            String fromEmail = System.getProperty("fromEmail");
            String host = System.getProperty("fromHost");
            String port = System.getProperty("fromPort");
            String toEmail = System.getProperty("Techdoc_TRAXtoEmail");

            ArrayList<String> emailsList = new ArrayList<String>(Arrays.asList(toEmail.split(",")));


            HtmlEmail email = new HtmlEmail();
            email.setHostName(host);
            email.setSmtpPort(Integer.valueOf(port));
            email.setFrom(fromEmail);
            email.setSubject("TECHDOC 2 - TRAX - PRINT FAILURE [" + printer + "] - PDF attachment not printed[" + wo + "] - ALERT");
            for (String emails : emailsList) {
                email.addTo(emails);
            }

            // Create the HTML content
            StringBuilder htmlContent = new StringBuilder();
            htmlContent.append("<p>Hi All,</p>")
                    .append("<p>Please find below PDF attachment details for not printed:-</p>")
                    .append("<p>1. SAP Revision: " + revision + "</p>")
                    .append("<p>2. iDOC Date: " + date + "</p>")
                    .append("<p>3. iDOC Time: " + time + "</p>")
                    .append("<br><br>");
            htmlContent.append("<h1 style='text-align: center;'>" + stringHeader + "</h1>")
                    .append("<table style='border-collapse: collapse; width: 100%;'>")
                    .append("<tr>");

            // Add table headers
            for (String header : headers) {
                htmlContent.append("<th style='border: 1px solid black; padding: 8px; text-align: left;'>").append(header).append("</th>");
            }
            htmlContent.append("</tr>");

            // Add table rows
            htmlContent.append(pAcks);

            htmlContent.append("</table>");
            htmlContent.append("<br><br>");
            htmlContent.append("<p>Note: This is a system generated mail. Please do NOT reply to this mail.</p>");

            String print = htmlContent.toString();

            email.setHtmlMsg(
                    print);

            email.send();
            Logger.info("SUCCESS EMAIL SENT");
        } catch (Exception e) {

            Logger.error(e, "Email not found");

        } finally {
            errors = "";
        }
    }


    //MOD 19
    public static void sendEmailEDCO(String wo,
                                     String revision,
                                     String date,
                                     String time,
                                     String pAcks, String printer, String stringHeader) {
        try {
            String[] headers = null;
            if (stringHeader.equalsIgnoreCase("Attachment Missing")) {
                headers = headersPDF;
            } else {
                headers = headersCOVER;
            }

            String fromEmail = System.getProperty("fromEmail");
            String host = System.getProperty("fromHost");
            String port = System.getProperty("fromPort");
            String toEmail = System.getProperty("Techdoc_EDCOtoEmail");

            ArrayList<String> emailsList = new ArrayList<String>(Arrays.asList(toEmail.split(",")));


            HtmlEmail email = new HtmlEmail();
            email.setHostName(host);
            email.setSmtpPort(Integer.valueOf(port));
            email.setFrom(fromEmail);
            email.setSubject("TECHDOC 2 - EDCO - PRINT FAILURE [" + printer + "] - EDCO attachment not printed[" + wo + "] - ALERT");
            for (String emails : emailsList) {
                email.addTo(emails);
            }

            // Create the HTML content
            StringBuilder htmlContent = new StringBuilder();
            htmlContent.append("<p>Hi All,</p>")
                    .append("<p>Please find below PDF attachment details for not printed:-</p>")
                    .append("<p>1. SAP Revision: " + revision + "</p>")
                    .append("<p>2. iDOC Date: " + date + "</p>")
                    .append("<p>3. iDOC Time: " + time + "</p>")
                    .append("<br><br>");
            htmlContent.append("<h1 style='text-align: center;'>" + stringHeader + "</h1>")
                    .append("<table style='border-collapse: collapse; width: 100%;'>")
                    .append("<tr>");

            // Add table headers
            for (String header : headers) {
                htmlContent.append("<th style='border: 1px solid black; padding: 8px; text-align: left;'>").append(header).append("</th>");
            }
            htmlContent.append("</tr>");

            // Add table rows
            htmlContent.append(pAcks);

            htmlContent.append("</table>");
            htmlContent.append("<br><br>");
            htmlContent.append("<p>Note: This is a system generated mail. Please do NOT reply to this mail.</p>");

            String print = htmlContent.toString();

            email.setHtmlMsg(
                    print);

            email.send();
            Logger.info("SUCCESS EMAIL SENT");
        } catch (Exception e) {

            Logger.error(e, "Email not found");

        } finally {
            errors = "";
        }
    }

    public static void sendEmailMQ(String error) {
        try {

            String fromEmail = System.getProperty("fromEmail");
            String host = System.getProperty("fromHost");
            String port = System.getProperty("fromPort");
            String toEmail = System.getProperty("Techdoc_MQtoEmail");

            ArrayList<String> emailsList = new ArrayList<String>(Arrays.asList(toEmail.split(",")));


            Email email = new SimpleEmail();
            email.setHostName(host);
            email.setSmtpPort(Integer.valueOf(port));
            email.setFrom(fromEmail);
            email.setSubject("TECHDOC 2 - MQ - Connection Error");
            for (String emails : emailsList) {
                email.addTo(emails);
            }

            email.setMsg("Hi All,\r\n" +
                    "Please Find MQ Error:-\r\n" +
                    "\r\n" +
                    error);

            email.send();
            Logger.info("SUCCESS EMAIL SENT");
        } catch (Exception e) {

            Logger.error(e, "Email not found");

        } finally {
            errors = "";
        }
    }

    public static void sendEmailPrint(String wo,
                                      String revision,
                                      String date,
                                      String time,
                                      String pAcks, String printer, String stringHeader) {
        try {
            String[] headers = null;
            if (stringHeader.equalsIgnoreCase("Attachment Missing")) {
                headers = headersPDF;
            } else {
                headers = headersCOVER;
            }

            String fromEmail = System.getProperty("fromEmail");
            String host = System.getProperty("fromHost");
            String port = System.getProperty("fromPort");
            String toEmail = System.getProperty("Techdoc_PrinttoEmail");

            ArrayList<String> emailsList = new ArrayList<String>(Arrays.asList(toEmail.split(",")));


            HtmlEmail email = new HtmlEmail();
            email.setHostName(host);
            email.setSmtpPort(Integer.valueOf(port));
            email.setFrom(fromEmail);
            email.setSubject("TECHDOC 2 - PRINT - PRINT FAILURE [" + printer + "] - attachment not printed[" + wo + "] - ALERT");
            for (String emails : emailsList) {
                email.addTo(emails);
            }

            // Create the HTML content
            StringBuilder htmlContent = new StringBuilder();

            htmlContent.append("<p>Hi All,</p>")
                    .append("<p>Please find below PDF attachment details for not printed:-</p>")
                    .append("<p>1. SAP Revision: " + revision + "</p>")
                    .append("<p>2. iDOC Date: " + date + "</p>")
                    .append("<p>3. iDOC Time: " + time + "</p>")
                    .append("<br><br>");
            htmlContent.append("<h1 style='text-align: center;'>" + stringHeader + "</h1>")
                    .append("<table style='border-collapse: collapse; width: 100%;'>")
                    .append("<tr>");

            // Add table headers
            for (String header : headers) {
                htmlContent.append("<th style='border: 1px solid black; padding: 8px; text-align: left;'>").append(header).append("</th>");
            }
            htmlContent.append("</tr>");

            // Add table rows
            htmlContent.append(pAcks);

            htmlContent.append("</table>");
            htmlContent.append("<br><br>");
            htmlContent.append("<p>Note: This is a system generated mail. Please do NOT reply to this mail.</p>");

            String print = htmlContent.toString();

            email.setHtmlMsg(
                    print);

            email.send();
            Logger.info("SUCCESS EMAIL SENT");
        } catch (Exception e) {

            Logger.error(e, "Email not found");

        } finally {
            errors = "";
        }
    }


    public static void sendEmailDat(List<InterfaceData> ids) {
        try {
            String fleet = "";
            try {
                ArrayList<String> fileName = new ArrayList<String>(Arrays.asList(ids.get(0).getFileName().split("-")));
                fleet = fileName.get(1);
            } catch (Exception e) {
                Logger.error(e);
                Logger.info("File Name: " + ids.get(0).getFileName());
                for (String type : acType) {
                    if (ids.get(0).getFileName().contains(type)) {
                        fleet = type;
                        break;
                    }
                }


            }
            String fromEmail = System.getProperty("fromEmail");
            String host = System.getProperty("fromHost");
            String port = System.getProperty("fromPort");
            String toEmail = System.getProperty(fleet);

            ArrayList<String> emailsList = new ArrayList<String>(Arrays.asList(toEmail.split(",")));


            HtmlEmail email = new HtmlEmail();
            email.setHostName(host);
            email.setSmtpPort(Integer.valueOf(port));
            email.setFrom(fromEmail);
            email.setSubject("New " + fleet + " Attachments Loaded in CDM. Requires Follow-up action. Techdoc 2");
            for (String emails : emailsList) {
                email.addTo(emails);
            }

            for (InterfaceData id : ids) {

                email.attach(new ByteArrayDataSource(id.getClobDocument().getBytes(), "text/plain"),
                        id.getFileName() + ".dat", id.getFileName() + ".dat", EmailAttachment.ATTACHMENT);
            }
            String emails = "";
            for (String e : emailsList) {
                emails += e + ", ";
            }
            emails = emails.trim(); // Remove trailing spaces
            if (emails.endsWith(",")) {
                emails = emails.substring(0, emails.length() - 1);
            }

            String message = "**********************************************************\r\n" +
                    "* This is an system generated email.                     *\r\n" +
                    "*                                                        *\r\n" +
                    "* Please send your email to opn_csb@singaporeair.com.sg. *\r\n" +
                    "**********************************************************\r\n" + System.lineSeparator() + System.lineSeparator() +
                    "TO: " + emails + System.lineSeparator() + System.lineSeparator() +
                    "The latest copy of attachment has been loaded into CDM." + System.lineSeparator() +
                    "Attached are the new tasks, deleted tasks and current tasks generated as attachment for your review and necessary action." + System.lineSeparator() +
                    "*****\r\n" +
                    "The list of attachment for new tasks will be transferred to SAP for your further action.";


            email.setMsg(message);

            email.send();
            Logger.info("SUCCESS EMAIL SENT");
        } catch (Exception e) {

            Logger.error(e, "Email not found");

        } finally {
            errors = "";
        }
    }

}
