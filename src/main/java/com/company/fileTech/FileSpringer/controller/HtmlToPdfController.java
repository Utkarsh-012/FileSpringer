
package com.company.fileTech.FileSpringer.controller;

import com.company.fileTech.FileSpringer.service.HtmlToPdfService;
import jakarta.servlet.http.HttpServletRequest;
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
public class HtmlToPdfController {

    private final Map<String, byte[]> fileStorage = new HashMap<>();

    @PostMapping("/file/htmlToPdf")
    public ResponseEntity<Map<String, Object>> convertHtmlToPdf(
            @RequestParam("file") MultipartFile file,
            @RequestParam("targetFormat") String targetFormat,
            HttpServletRequest request) {

        try {
            // File size validation (5 MB limit)
            long maxFileSizeInBytes = 5 * 1024 * 1024; // 5MB
            if (file.getSize() > maxFileSizeInBytes) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("message", "File size exceeds the allowed limit of 10 MB");
                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
            }

            // Target format validation
            if (!"PDF".equalsIgnoreCase(targetFormat)) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("message", "Unsupported target format");
                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
            }

            // Call the service to convert HTML to PDF
            ByteArrayOutputStream pdfOutputStream = HtmlToPdfService.generatePDFFromHTML(file);
            byte[] pdfBytes = pdfOutputStream.toByteArray();

            // Extract the original file name and use it for the PDF
            String originalFileName = file.getOriginalFilename();
            if (originalFileName == null || !originalFileName.toLowerCase().endsWith(".html")) {
                originalFileName = "converted";
            } else {
                originalFileName = originalFileName.substring(0, originalFileName.lastIndexOf('.'));
            }
            String pdfFileName = originalFileName + ".pdf";

            // Get base URL dynamically from HttpServletRequest
            String baseUrl = request.getScheme() + "://" + request.getServerName()
                    + ":" + request.getServerPort();

            // Generate complete download link
            String downloadLink = baseUrl + "/download/htmlToPdf/" + pdfFileName;

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
        }
    }

    @GetMapping("/download/htmlToPdf/{fileName}")
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
