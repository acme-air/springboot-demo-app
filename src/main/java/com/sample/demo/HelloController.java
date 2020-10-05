package com.sample.demo;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
public class HelloController {

    @RequestMapping("/")
    public String index() {

        // 
        // Business logic here...
        // 

        return "Hello there! Greetings from Spring Boot! v1.3";
    }

}

