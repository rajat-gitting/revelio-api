package com.revelio.api.controller;

import com.revelio.api.dto.ApiResponse;
import com.revelio.api.service.PingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Ping")
@RestController
@RequestMapping("/ping")
@RequiredArgsConstructor
public class PingController {

  private final PingService pingService;

  @GetMapping
  @Operation(summary = "Connectivity ping", description = "Returns a static pong payload.")
  public ResponseEntity<ApiResponse<String>> ping() {
    return ResponseEntity.ok(ApiResponse.ok(pingService.pong()));
  }
}
