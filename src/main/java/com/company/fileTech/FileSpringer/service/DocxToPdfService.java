//package com.company.fileTech.FileSpringer.service;
//
//import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
//import org.docx4j.Docx4J;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//import java.io.ByteArrayOutputStream;
//
//
//@Service
//public class DocxToPdfService {
//
//    public byte[] convertDocxToPdf(MultipartFile docxFile) throws Exception {
//        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(docxFile.getInputStream());
//
//        // FOP configuration for PDF conversion
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        Docx4J.toPDF(wordMLPackage, outputStream);
//
//        return outputStream.toByteArray();
//    }
//}
package com.company.fileTech.FileSpringer.service;

import org.docx4j.Docx4J;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

@Service
public class DocxToPdfService {

    public byte[] convertDocxToPdf(MultipartFile docxFile) throws Exception {
        try (InputStream inputStream = docxFile.getInputStream();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            // Load DOCX file into memory-efficient package
            WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(inputStream);

            // Convert to PDF using Docx4J, streaming output
            Docx4J.toPDF(wordMLPackage, outputStream);

            return outputStream.toByteArray();
        }
    }
}
