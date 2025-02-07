package com.company.fileTech.FileSpringer.controller;

import com.company.fileTech.FileSpringer.service.DocToPdfService;
import jakarta.servlet.http.HttpServletRequest;
import org.jodconverter.core.office.OfficeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping
public class DocToPdfController {

    private final Map<String, byte[]> fileStorage = new HashMap<>();

    @Autowired
    private DocToPdfService docToPdfService;

    @PostMapping("/file/docToPdf")
    public ResponseEntity<Map<String, Object>> convertDocToPdf(
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request) {

        try {
            // File size validation (10 MB limit)
            long maxFileSizeInBytes = 10 * 1024 * 1024; // 10MB
            if (file.getSize() > maxFileSizeInBytes) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("message", "File size exceeds the allowed limit of 10 MB");
                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
            }

            // Call the service to convert DOC to PDF
            ByteArrayOutputStream pdfOutputStream = docToPdfService.convertDocToPdf(file);
            byte[] pdfBytes = pdfOutputStream.toByteArray();

            // Extract the original file name and use it for the PDF
            String originalFileName = file.getOriginalFilename();
            if (originalFileName == null || !originalFileName.toLowerCase().endsWith(".doc")) {
                originalFileName = "converted";
            } else {
                originalFileName = originalFileName.substring(0, originalFileName.lastIndexOf('.'));
            }
            String pdfFileName = originalFileName + ".pdf";

            // Get base URL dynamically from HttpServletRequest
            String baseUrl = request.getScheme() + "://" + request.getServerName()
                    + ":" + request.getServerPort();

            // Generate complete download link
            String downloadLink = baseUrl + "/download/docToPdf/" + pdfFileName;

            // Get current creation time
            String creationTime = LocalDateTime.now().toString();

            // Store PDF bytes in memory
            fileStorage.put(pdfFileName, pdfBytes);

            // Build the JSON response
            Map<String, Object> jsonResponse = new HashMap<>();
            jsonResponse.put("pdfName", pdfFileName);
            jsonResponse.put("createdAt", creationTime);
            jsonResponse.put("pdf", downloadLink);

            return new ResponseEntity<>(jsonResponse, HttpStatus.OK);
        } catch (IOException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error during conversion: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (OfficeException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/download/docToPdf/{fileName}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) {
        if (!fileStorage.containsKey(fileName)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        byte[] fileData = fileStorage.get(fileName);
        ByteArrayResource resource = new ByteArrayResource(fileData);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", fileName);

        return new ResponseEntity<>(resource, headers, HttpStatus.OK);
    }
}
