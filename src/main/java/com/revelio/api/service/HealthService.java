package com.revelio.api.service;

import com.revelio.api.model.HealthStatus;
import org.springframework.stereotype.Service;

@Service
public class HealthService {

  public HealthStatus getStatus() {
    return HealthStatus.builder().status("UP").service("revelio-api").build();
  }
}
