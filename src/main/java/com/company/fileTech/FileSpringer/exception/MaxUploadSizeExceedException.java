package com.company.fileTech.FileSpringer.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class MaxUploadSizeExceedException {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Map<String, Object>> handleMaxSizeException(MaxUploadSizeExceededException exc) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("message", "File size exceeds the allowed limit of 5 MB");
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
