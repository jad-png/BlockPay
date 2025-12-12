package com.serv.user_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hello")
public class TestController {

    @GetMapping("/test-route")
    public String testRoute() {
        System.out.println("Received request via Gateway!");
        return "Response from USER-SERVICE (Port 8080). Gateway routing successful!";
    }
}
