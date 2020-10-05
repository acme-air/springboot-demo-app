package com.sample.demo;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import io.micrometer.core.instrument.Counter;
import javax.annotation.PostConstruct;

@Component
class MyComponent {
  @Autowired
  private MeterRegistry meterRegistry = null;

  @PostConstruct
  public void init() {
    Counter featureCounter = this.meterRegistry.counter("feature", "region", "test");
    featureCounter.increment();
  }
  
}
