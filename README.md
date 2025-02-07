                                                                                          File to PDF Converter

                                                                                              Overview

The File to PDF Converter is a Spring Boot-based web application that allows users to convert various file formats into PDF. The application supports multiple input formats such as DOCX, DOC, TXT, HTML, XLS, PPT, and images, converting them seamlessly into high-quality PDFs.

Features

DOCX to PDF (Using Docx4j)

DOC to PDF (Using JodConverter)

TXT to PDF (Using Apache PDFBox)

HTML to PDF (Using iText)

XLS to PDF (Using JodConverter)

PPT to PDF (Using Apache POI)

Images to PDF (Supports multiple images in a single PDF using iText)

File Size Handling (Supports up to 10MB per file)

Downloadable PDFs (Provides a direct download link after conversion)

Technologies Used

Java 21

Spring Boot

Maven

Docx4j (11.5.0)

JodConverter

Apache POI

Apache PDFBox

iText

Thymeleaf (for front-end integration)

Installation and Setup

Build the Project:

mvn clean install

Run the Application:

mvn spring-boot:run

Access the Web Interface:

Open http://localhost:8080 in a browser.

API Endpoints

Convert File to PDF

Request:

URL: POST /convert

Headers: Content-Type: multipart/form-data

Body: Upload a file (DOCX, DOC, TXT, HTML, XLS, PPT, or image)

Response:

{
  "pdfName": "converted_file.pdf",
  "createdAt": "2024-09-15T12:30:00Z",
  "downloadLink": "http://localhost:8080/download/converted_file.pdf"
}

Download Converted PDF

Request:

URL: GET /download/{fileName}

Response: Returns the PDF file as a downloadable attachment.

Future Enhancements

Support for additional file formats

File compression and optimization

Cloud storage integration (AWS S3, Google Drive, etc.)

Batch file conversion

Contributing

Feel free to fork the repository, raise issues, and contribute via pull requests.

License

This project is licensed under the MIT License.

