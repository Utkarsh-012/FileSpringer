package com.company.fileTech.FileSpringer.service;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
public class XlsxToPdfService {

    public static ByteArrayOutputStream generatePDFFromXlsx(MultipartFile file) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document();

        try {
            // Create a PDF writer to write to the ByteArrayOutputStream
            PdfWriter.getInstance(document, outputStream);
            document.open();

            // Read the XLSX file using Apache POI
            InputStream inputStream = new ByteArrayInputStream(file.getBytes());
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0); // Convert the first sheet

            // Create a PDF table
            PdfPTable table = new PdfPTable(sheet.getRow(0).getPhysicalNumberOfCells()); // Use the number of columns in the first row

            // Iterate over the rows and cells in the Excel sheet
            for (Row row : sheet) {
                for (Cell cell : row) {
                    PdfPCell pdfCell = new PdfPCell(new Phrase(cell.toString()));
                    pdfCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(pdfCell);
                }
            }

            // Add the table to the PDF document
            document.add(table);
        } catch (DocumentException e) {
            throw new IOException("Error occurred while converting XLSX to PDF: " + e.getMessage());
        } finally {
            document.close();
        }

        return outputStream;
    }
}
