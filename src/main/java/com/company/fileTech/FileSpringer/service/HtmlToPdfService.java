package com.company.fileTech.FileSpringer.service;

import com.itextpdf.html2pdf.HtmlConverter;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class HtmlToPdfService {

    public static ByteArrayOutputStream generatePDFFromHTML(MultipartFile file) throws IOException {
        ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();

        // Convert HTML to PDF using HtmlConverter (handles <head>, <link>, etc.)
        HtmlConverter.convertToPdf(new ByteArrayInputStream(file.getBytes()), pdfOutputStream);

        return pdfOutputStream;
    }
}
