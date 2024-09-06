package com.company.fileTech.FileSpringer.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WebController {


    @RequestMapping("/")
    public String home() {
        return "upload" ;   // This maps to src/main/resources/static/upload.html
    }
}
