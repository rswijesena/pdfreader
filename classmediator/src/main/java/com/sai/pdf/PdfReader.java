package com.sai.pdf;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;
import org.apache.synapse.MessageContext;
import org.apache.synapse.mediators.AbstractMediator;

import java.io.*;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
This class get the file type, file path, file name, if it is a PDF file it reads the content and send the output back
If it is a zip file first it uzip and extract the file and read it then send the response back in the body.
 */
public class PdfReader extends AbstractMediator {

    private void readPdfFile(String fileName, String fileType, String filePath, MessageContext messageContext)
            throws IOException {

        if ("zip".equals(fileType)) {

            unzipAndReadPDF(fileName, fileType, filePath, messageContext);

        } else if ("pdf".equals(fileType)) {

            PDDocument document = PDDocument.load(new File(filePath + "/" + fileName + "." + fileType));

            document.getClass();

            if (!document.isEncrypted()) {
                PDFTextStripperByArea stripper = new PDFTextStripperByArea();
                stripper.setSortByPosition(true);

                PDFTextStripper tStripper = new PDFTextStripper();

                String pdfFileInText = tStripper.getText(document);
                //System.out.println("Text:" + st);

                // split by whitespace
                String lines[] = pdfFileInText.split("\\r?\\n");
                String pdfText = "";
                for (String line : lines) {

                    pdfText = pdfText + line;

                }
                messageContext.setProperty("pdfContent", pdfText);
            }
        } else {
            messageContext.setProperty("pdfContent", "Invalid file type.");
        }
    }


    public boolean mediate(MessageContext messageContext) {

        System.out.println("+++++++++++++++++++++++++++");
        try {

            String fileName = (String) messageContext.getProperty("fileName");
            String fileType = (String) messageContext.getProperty("fileType");
            String filePath = (String) messageContext.getProperty("fileLocation");

            readPdfFile(fileName, fileType, filePath, messageContext);

        } catch (IOException e) {
            messageContext.setProperty("pdfContent", e.getMessage());
        } catch (Exception e) {
            messageContext.setProperty("pdfContent", e.getMessage());
        }
        return true;
    }

    private void unzipAndReadPDF(String fileName, String fileType, String filePath, MessageContext messageContext)
            throws IOException {

        byte[] buffer = new byte[1024];
        File folder = new File(filePath);
        if (!folder.exists()) {
            folder.mkdir();
        }

        //get the zip file content
        ZipInputStream zis =
                new ZipInputStream(new FileInputStream(filePath + "/" + fileName + "." + fileType));
        //get the zipped file list entry
        ZipEntry ze = zis.getNextEntry();

        while (ze != null) {
            //unzip only pdf file according to the naming convention
            if (ze.getName().equals(fileName + "." + "pdf")) {

                fileName = ze.getName();

                File newFile = new File(filePath + File.separator + fileName);

                System.out.println("file unzip : " + newFile.getAbsoluteFile());

                //create all non exists folders
                //else you will hit FileNotFoundException for compressed folder
                new File(newFile.getParent()).mkdirs();

                FileOutputStream fos = new FileOutputStream(newFile);

                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }

                fos.close();

                break;
            }
        }
        zis.closeEntry();
        zis.close();

        File pdfFile = new File(fileName);
        String fileNameOfPDFFile = pdfFile.getName();

        String[] strArray = fileNameOfPDFFile.split(Pattern.quote("."));

        readPdfFile(strArray[0], "pdf", filePath, messageContext);


        System.out.println("Done");
    }
}
