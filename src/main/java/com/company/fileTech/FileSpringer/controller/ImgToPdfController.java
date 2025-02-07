package com.company.fileTech.FileSpringer.controller;

import com.company.fileTech.FileSpringer.service.ImgToPdfService;
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
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping
public class ImgToPdfController {

    private final Map<String, byte[]> fileStorage = new HashMap<>();
    @PostMapping("/file/imgToPdf")
    public ResponseEntity<Map<String, Object>> convertImgToPdf(
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam("targetFormat") String targetFormat,
            HttpServletRequest request) {

        try {
            // File size validation (5 MB limit per file)
            long maxFileSizeInBytes = 5 * 1024 * 1024; // 5 MB
            for (MultipartFile file : files) {
                if (file.getSize() > maxFileSizeInBytes) {
                    Map<String, Object> errorResponse = new HashMap<>();
                    errorResponse.put("message", "One or more files exceed the allowed limit of 5 MB");
                    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
                }
            }

            // Target format validation
            if (!"PDF".equalsIgnoreCase(targetFormat)) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("message", "Unsupported target format");
                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
            }

            // Call the service to convert images to PDF
            ByteArrayOutputStream pdfOutputStream = ImgToPdfService.generatePDFFromImages(files);
            byte[] pdfBytes = pdfOutputStream.toByteArray();

            // Use a common name for the output PDF file
            String pdfFileName = "converted_images.pdf";

            // Generate complete download link
            String baseUrl = request.getScheme() + "://" + request.getServerName()
                    + ":" + request.getServerPort();
            String downloadLink = baseUrl + "/download/imgToPdf/" + pdfFileName;

            // Store PDF bytes in memory
            fileStorage.put(pdfFileName, pdfBytes);

            // Build the JSON response
            Map<String, Object> jsonResponse = new HashMap<>();
            jsonResponse.put("pdfName", pdfFileName);
            jsonResponse.put("createdAt", LocalDateTime.now().toString());
            jsonResponse.put("pdf", downloadLink);

            return new ResponseEntity<>(jsonResponse, HttpStatus.OK);
        } catch (IOException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error during conversion: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/download/imgToPdf/{fileName}")
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

