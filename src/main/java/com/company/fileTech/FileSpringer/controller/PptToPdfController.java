package com.company.fileTech.FileSpringer.controller;

import com.company.fileTech.FileSpringer.service.PptToPdfService;
import jakarta.servlet.http.HttpServletRequest;
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
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping
public class PptToPdfController {

    private final Map<String, byte[]> fileStorage = new HashMap<>();
    private final PptToPdfService pptToPdfService;

    @Autowired
    public PptToPdfController(PptToPdfService pptToPdfService) {
        this.pptToPdfService = pptToPdfService;
    }

    @PostMapping("/file/pptToPdf")
    public ResponseEntity<Map<String, Object>> convertPptToPdf(
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

            // Convert PPT to PDF
            ByteArrayOutputStream pdfOutputStream = pptToPdfService.convertPptToPdf(file.getInputStream());
            byte[] pdfBytes = pdfOutputStream.toByteArray();

            // Extract the original file name
            String originalFileName = file.getOriginalFilename();
            if (originalFileName == null || !originalFileName.toLowerCase().endsWith(".pptx")) {
                originalFileName = "converted";
            } else {
                originalFileName = originalFileName.substring(0, originalFileName.lastIndexOf('.'));
            }
            String pdfFileName = originalFileName + ".pdf";

            // Get base URL dynamically from HttpServletRequest
            String baseUrl = request.getScheme() + "://" + request.getServerName()
                    + ":" + request.getServerPort();

            // Generate download link
            String downloadLink = baseUrl + "/download/" + pdfFileName;

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
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error during conversion: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/download/{fileName}")
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
