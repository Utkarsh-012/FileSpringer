package com.company.fileTech.FileSpringer.service;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Service
public class TxtToPdfService {

    public static ByteArrayOutputStream generatePDFFromText(MultipartFile file) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document();

        try {
            // Create a PDF writer to write to the ByteArrayOutputStream
            PdfWriter.getInstance(document, outputStream);
            document.open();

            // Read the content of the text file
            BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
            String line;

            // Add each line from the text file as a paragraph in the PDF
            while ((line = reader.readLine()) != null) {
                document.add(new Paragraph(line));
            }

        } catch (DocumentException e) {
            throw new IOException("Error occurred while converting text to PDF: " + e.getMessage());
        } finally {
            document.close();
        }

        return outputStream;
    }
}
