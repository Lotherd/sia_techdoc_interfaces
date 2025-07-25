/*
* This source code file is the intellectual property of TRAX USA Corp.
* Copyright (c) 2025 TRAX USA Corp. All rights reserved.
*/
package trax.aero.storage.s3;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.tinylog.Logger;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import trax.aero.pojo.json.OUTPUT;
import trax.aero.storage.sftp.SftpUtilities;

public class S3Utilities {

    private static final String bucketNameTrax = System.getProperty("Techdoc_Traxbucket");

    private static final String bucketNamePrint = System.getProperty("Techdoc_Printbucket");

    private static final String bucketNameEDCO = System.getProperty("Techdoc_EDCObucket");

    private static final String pathS3Trax =
            System.getProperty("Techdoc_pathS3Trax") + File.separator;

    private static final String pathS3Print =
            System.getProperty("Techdoc_pathS3Print") + File.separator;

    private static final String pathS3EDCO =
            System.getProperty("Techdoc_pathS3EDCO") + File.separator;

    private static final String fileLocOut = System.getProperty("TECH_fileLocOut");

    // ---------------------------------------------------------------------------- Send File

    protected static void putS3Object(File file, String key, String bucketName) throws Exception {

        try (S3Client s3 = S3Client.builder().build()) {
            // Path targetPath  = Paths.get(key+File.separator+file.getName());
            // if (Files.notExists(targetPath )) {
            //    Files.createDirectories(targetPath.getParent());
            // }
            // Files.move(file.toPath(), new File(key+File.separator+file.getName()).toPath(),
            // StandardCopyOption.REPLACE_EXISTING);
            if (file.getName().contains("index") || file.getName().contains("footer")) {
                HeadObjectRequest headObjectRequest =
                        HeadObjectRequest.builder().bucket(bucketName).key(key + file.getName()).build();

                try {
                    @SuppressWarnings("unused")
                    HeadObjectResponse headObjectResponse = s3.headObject(headObjectRequest);
                    Logger.info("File exists in the bucket: " + key + file.getName());
                    return;
                } catch (Exception e) {
                    Logger.info("File does not exists in the bucket: " + key + file.getName());
                }
            }

            PutObjectRequest request =
                    PutObjectRequest.builder().bucket(bucketName).key(key + file.getName()).build();
            s3.putObject(request, RequestBody.fromFile(file));

            Logger.info("Successfully placed " + key + file.getName() + " into bucket " + bucketName);
        } catch (Exception e) {
            Logger.error(e);
            throw e;
        }
    }

    public static OUTPUT sendEDCO(
            OUTPUT json,
            String pdfName,
            ArrayList<String> txt,
            String txtName,
            String path,
            String printer,
            String header,
            String footer,
            ArrayList<String> headerTxt,
            ArrayList<String> footerTxt,
            String folder)
            throws Exception {

        // ArrayList<File> pdfs = new ArrayList<File>();

        try {
            File print = new File(path);

            File theDir =
                    new File(fileLocOut + File.separator + FilenameUtils.removeExtension(print.getName()));
            if (!theDir.exists()) {
                theDir.mkdirs();
            }

            Files.move(
                    print.toPath(),
                    new File(
                                    fileLocOut
                                            + File.separator
                                            + FilenameUtils.removeExtension(print.getName())
                                            + File.separator
                                            + pdfName)
                            .toPath(),
                    StandardCopyOption.REPLACE_EXISTING);

            File folderPrint =
                    new File(fileLocOut + File.separator + FilenameUtils.removeExtension(print.getName()));

            File[] prints = folderPrint.listFiles();

            // Print server folder logic
            if (prints != null) {

                // FOUND PDFS
                for (File p : prints) {
                    putS3Object(p, pathS3EDCO, bucketNameEDCO);
                }
            }

            json.setATTACHMENT(new ArrayList<>());
            json.setFILENAME(pdfName);

        } catch (Exception e) {
            Logger.error(e);
            throw e;
        }

        return json;
    }

    public static trax.aero.pojo.xml.OUTPUT sendTrax(
            trax.aero.pojo.xml.OUTPUT xml,
            String pdfName,
            ArrayList<String> txt,
            String txtName,
            String path,
            String folder,
            String header,
            String footer,
            ArrayList<String> headerTxt,
            ArrayList<String> footerTxt,
            boolean sendFinal)
            throws Exception {

        // ArrayList<File> pdfs = new ArrayList<File>();

        try {
            // PDF
            File print = new File(path);

            File theDir =
                    new File(fileLocOut + File.separator + FilenameUtils.removeExtension(print.getName()));
            if (!theDir.exists()) {
                theDir.mkdirs();
            }
            Logger.info(
                    "MOVE "
                            + print.toPath()
                            + " TO "
                            + fileLocOut
                            + File.separator
                            + FilenameUtils.removeExtension(print.getName())
                            + File.separator
                            + pdfName);
            Files.move(
                    print.toPath(),
                    new File(
                                    fileLocOut
                                            + File.separator
                                            + FilenameUtils.removeExtension(print.getName())
                                            + File.separator
                                            + pdfName)
                            .toPath(),
                    StandardCopyOption.REPLACE_EXISTING);

            // xml
            File output =
                    new File(
                            fileLocOut
                                    + File.separator
                                    + FilenameUtils.removeExtension(print.getName())
                                    + File.separator
                                    + "wo_"
                                    + RandomStringUtils.random(19, false, true)
                                    + ".xml");

            // txt
            if (sendFinal) {
                Path text =
                        Paths.get(
                                fileLocOut
                                        + File.separator
                                        + FilenameUtils.removeExtension(print.getName())
                                        + File.separator
                                        + txtName
                                        + ".txt");
                List<String> lines = txt;
                Files.write(text, lines, StandardCharsets.UTF_8);

                // header
                PDDocument documentHeader = new PDDocument();
                PDPage pageHeader = new PDPage();
                documentHeader.addPage(pageHeader);
                PDPageContentStream contentStreamHeader =
                        new PDPageContentStream(documentHeader, pageHeader);

                contentStreamHeader.beginText();
                contentStreamHeader.setLeading(14.5f);

                contentStreamHeader.newLineAtOffset(25, 700);
                contentStreamHeader.setFont(PDType1Font.HELVETICA_BOLD, 9);

                for (String linesheader : headerTxt) {
                    Logger.info(linesheader);
                    contentStreamHeader.showText(linesheader);
                    contentStreamHeader.newLine();
                }
                contentStreamHeader.endText();
                contentStreamHeader.close();
                documentHeader.save(
                        fileLocOut
                                + File.separator
                                + FilenameUtils.removeExtension(print.getName())
                                + File.separator
                                + header);
                documentHeader.close();

                // footer
                PDDocument documentFooter = new PDDocument();
                PDPage pageFooter = new PDPage();
                documentFooter.addPage(pageFooter);

                PDPageContentStream contentStreamFooter =
                        new PDPageContentStream(documentFooter, pageFooter);

                contentStreamFooter.beginText();
                contentStreamFooter.setLeading(14.5f);

                contentStreamFooter.newLineAtOffset(25, 700);
                contentStreamFooter.setFont(PDType1Font.HELVETICA_BOLD, 9);

                for (String linesFooter : footerTxt) {
                    Logger.info(linesFooter);

                    contentStreamFooter.showText(linesFooter);
                    contentStreamFooter.newLine();
                }
                contentStreamFooter.endText();

                contentStreamFooter.close();
                documentFooter.save(
                        fileLocOut
                                + File.separator
                                + FilenameUtils.removeExtension(print.getName())
                                + File.separator
                                + footer);
                documentFooter.close();
            }

            JAXBContext jc = JAXBContext.newInstance(trax.aero.pojo.xml.OUTPUT.class);
            Marshaller marshall = jc.createMarshaller();
            marshall.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshall.marshal(xml, output);

            File folderPrint =
                    new File(fileLocOut + File.separator + FilenameUtils.removeExtension(print.getName()));

            File[] prints = folderPrint.listFiles();

            Arrays.sort(
                    prints,
                    (f1, f2) -> {
                        if (f1.getName().endsWith(".txt")) return 1;
                        if (f2.getName().endsWith(".txt")) return -1;
                        return 0;
                    });

            // Print server folder logic
            if (prints != null) {
                // putS3Object(pathS3Trax+folder+ File.separator, bucketNameTrax);
                // FOUND PDFS
                for (File p : prints) {
                    if (p.getName().endsWith(".txt")) {
                        try {
                            Thread.sleep(1000); // Wait for 1 second
                        } catch (InterruptedException e) {
                            Logger.error(e);
                        }
                    }
                    putS3Object(p, pathS3Trax + folder + File.separator, bucketNameTrax);
                }
            }
        } catch (Exception e) {
            Logger.error(e);
            throw e;
        }
        return xml;
    }

    public static void sendVirtualPrint(
            String path,
            String folder,
            String pdfName,
            String header,
            String footer,
            ArrayList<String> headerTxt,
            ArrayList<String> footerTxt,
            boolean sendFinal,
            ArrayList<String> txt,
            String txtName)
            throws Exception {

        try {
            File print = new File(path);
            File theDir =
                    new File(fileLocOut + File.separator + FilenameUtils.removeExtension(print.getName()));
            if (!theDir.exists()) {
                theDir.mkdirs();
            }
            Logger.info(
                    "MOVE "
                            + print.toPath()
                            + " TO "
                            + fileLocOut
                            + File.separator
                            + FilenameUtils.removeExtension(print.getName())
                            + File.separator
                            + pdfName);
            Files.move(
                    print.toPath(),
                    new File(
                                    fileLocOut
                                            + File.separator
                                            + FilenameUtils.removeExtension(print.getName())
                                            + File.separator
                                            + pdfName)
                            .toPath(),
                    StandardCopyOption.REPLACE_EXISTING);

            if (sendFinal) {

                Path text =
                        Paths.get(
                                fileLocOut
                                        + File.separator
                                        + FilenameUtils.removeExtension(print.getName())
                                        + File.separator
                                        + txtName
                                        + ".txt");
                List<String> lines = txt;
                Files.write(text, lines, StandardCharsets.UTF_8);

                // header
                PDDocument documentHeader = new PDDocument();
                PDPage pageHeader = new PDPage();
                documentHeader.addPage(pageHeader);
                PDPageContentStream contentStreamHeader =
                        new PDPageContentStream(documentHeader, pageHeader);

                contentStreamHeader.beginText();
                contentStreamHeader.setLeading(14.5f);

                contentStreamHeader.newLineAtOffset(25, 700);
                contentStreamHeader.setFont(PDType1Font.HELVETICA_BOLD, 9);

                for (String linesheader : headerTxt) {
                    Logger.info(linesheader);
                    contentStreamHeader.showText(linesheader);
                    contentStreamHeader.newLine();
                }
                contentStreamHeader.endText();
                contentStreamHeader.close();
                documentHeader.save(
                        fileLocOut
                                + File.separator
                                + FilenameUtils.removeExtension(print.getName())
                                + File.separator
                                + header);
                documentHeader.close();

                // footer
                PDDocument documentFooter = new PDDocument();
                PDPage pageFooter = new PDPage();
                documentFooter.addPage(pageFooter);

                PDPageContentStream contentStreamFooter =
                        new PDPageContentStream(documentFooter, pageFooter);

                contentStreamFooter.beginText();
                contentStreamFooter.setLeading(14.5f);

                contentStreamFooter.newLineAtOffset(25, 700);
                contentStreamFooter.setFont(PDType1Font.HELVETICA_BOLD, 9);

                for (String linesFooter : footerTxt) {
                    Logger.info(linesFooter);

                    contentStreamFooter.showText(linesFooter);
                    contentStreamFooter.newLine();
                }
                contentStreamFooter.endText();

                contentStreamFooter.close();
                documentFooter.save(
                        fileLocOut
                                + File.separator
                                + FilenameUtils.removeExtension(print.getName())
                                + File.separator
                                + footer);
                documentFooter.close();
            }

            // putS3Object(pathS3Print +folder +File.separator,bucketNamePrint);
            // putS3Object(print,pathS3Print +folder +File.separator,bucketNamePrint);
            File folderPrint =
                    new File(fileLocOut + File.separator + FilenameUtils.removeExtension(print.getName()));

            File[] prints = folderPrint.listFiles();

            Arrays.sort(
                    prints,
                    (f1, f2) -> {
                        if (f1.getName().endsWith(footer)) return 1;
                        if (f2.getName().endsWith(footer)) return -1;
                        return 0;
                    });

            // Print server folder logic
            if (prints != null) {
                // putS3Object(pathS3Trax+folder+ File.separator, bucketNameTrax);
                // FOUND PDFS
                for (File p : prints) {
                    if (p.getName().endsWith(footer)) {
                        try {
                            Thread.sleep(1000); // Wait for 1 second
                        } catch (InterruptedException e) {
                            Logger.error(e);
                        }
                    }
                    putS3Object(p, pathS3Print + folder + File.separator, bucketNamePrint);
                }
            }

        } catch (Exception e) {
            Logger.error(e);
            throw e;
        }
    }

    public static void setDatFile(String document, String fileName, String transaction)
            throws Exception {
        File theDir = new File(fileLocOut + File.separator + (transaction));
        if (!theDir.exists()) {
            theDir.mkdirs();
        }

        Path dat =
                Paths.get(fileLocOut + File.separator + (transaction) + File.separator + fileName + ".dat");
        Files.write(dat, document.getBytes());

        File datFile =
                new File(fileLocOut + File.separator + (transaction) + File.separator + fileName + ".dat");
        // TODO
        SftpUtilities sftp = new SftpUtilities();

        sftp.sendSftpFile(Files.newInputStream(datFile.toPath()), fileName + ".dat");

        // putS3Object(batFile, pathS3Print,bucketNamePrint );
    }
}