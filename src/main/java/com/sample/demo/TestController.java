package com.sample.demo;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import io.micrometer.core.instrument.Counter;
import javax.annotation.PostConstruct;

@RestController
public class TestController {
    @Autowired
    private MeterRegistry meterRegistry = null;
  
    @RequestMapping("/trackme")
    public String index() {

        // 
        // Business logic here...
        // 

        Counter featureCounter = this.meterRegistry.counter("trackme", "endpoint", "count");
        featureCounter.increment();   

        // 
        // Business logic here...
        // 

        String count = String.valueOf(featureCounter.count());
        return "Testing... " + count;
    }
    
}

