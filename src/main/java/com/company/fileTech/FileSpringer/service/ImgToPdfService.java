package com.company.fileTech.FileSpringer.service;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class ImgToPdfService {
    public static ByteArrayOutputStream generatePDFFromImages(List<MultipartFile> files) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document();

        try {
            PdfWriter.getInstance(document, outputStream);
            document.open();

            for (MultipartFile file : files) {
                // Convert the image from the MultipartFile to an Image object
                Image img = Image.getInstance(file.getBytes());

                // Optionally scale the image to fit within the PDF dimensions
                img.scaleToFit(document.getPageSize().getWidth() - 50, document.getPageSize().getHeight() - 50);
                img.setAlignment(Image.ALIGN_CENTER);

                // Add a new page before each image (except the first one)
                if (document.getPageNumber() > 0) {
                    document.newPage();
                }

                // Add the image to the PDF document
                document.add(img);
            }

        } catch (DocumentException e) {
            throw new IOException("Error occurred while converting images to PDF: " + e.getMessage());
        } finally {
            document.close();
        }

        return outputStream;
    }
}