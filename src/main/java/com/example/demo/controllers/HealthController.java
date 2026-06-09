package com.example.demo.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    // PARA SLIPLANE
    @GetMapping("/healthcheck")
    public String healthcheck() {
        return "OK";
    }
}