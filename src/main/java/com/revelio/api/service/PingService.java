package com.revelio.api.service;

import org.springframework.stereotype.Service;

@Service
public class PingService {

  public String pong() {
    return "pong";
  }

  public String echoMessage(String message) {
    return message;
  }
}
