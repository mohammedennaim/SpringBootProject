package com.example.digitallogistics.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Test {

    @GetMapping("/api/test")
    public String testApi() {
        return "✅ API is working !";
    }
}
