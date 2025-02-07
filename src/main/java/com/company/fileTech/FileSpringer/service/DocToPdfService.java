package com.company.fileTech.FileSpringer.service;

import org.jodconverter.core.office.OfficeException;
import org.jodconverter.local.JodConverter;
import org.jodconverter.local.office.LocalOfficeManager;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

@Service
public class DocToPdfService {

    public ByteArrayOutputStream convertDocToPdf(MultipartFile file) throws IOException, OfficeException {
        // Create temporary files for the DOC and PDF
        File tempDocFile = File.createTempFile("tempDoc", ".doc");
        File tempPdfFile = File.createTempFile("tempPdf", ".pdf");

        // Write the uploaded DOC file to the temporary DOC file
        try (FileOutputStream fos = new FileOutputStream(tempDocFile)) {
            fos.write(file.getBytes());
        }

        // Initialize the office manager
        var officeManager = LocalOfficeManager.install();

        try {
            // Start the office manager
            officeManager.start();

            // Perform the DOC to PDF conversion
            JodConverter
                    .convert(tempDocFile)
                    .to(tempPdfFile)
                    .execute();

        } finally {
            // Stop the office manager
            if (officeManager.isRunning()) {
                officeManager.stop();
            }
        }

        // Read the converted PDF into a ByteArrayOutputStream
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (FileInputStream fis = new FileInputStream(tempPdfFile)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }

        // Clean up temporary files
        tempDocFile.delete();
        tempPdfFile.delete();

        return outputStream;
    }
}
