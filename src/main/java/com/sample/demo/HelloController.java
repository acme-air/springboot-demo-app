package com.sample.demo;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
public class HelloController {

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index() {
        return "Hello there! Greetings from Spring Boot!";
    }

}

