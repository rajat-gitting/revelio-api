package com.revelio.api.controller;

import com.revelio.api.dto.ApiResponse;
import com.revelio.api.dto.HealthQuery;
import com.revelio.api.model.HealthStatus;
import com.revelio.api.service.HealthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Health")
@RestController
@RequestMapping
@RequiredArgsConstructor
@Slf4j
public class HealthController {

  private final HealthService healthService;

  @GetMapping("/health")
  @Operation(
      summary = "Service health",
      description =
          "Returns readiness information for load balancers and orchestration probes. "
              + "Optional notifyEmail query parameter is validated when present.")
  public ResponseEntity<ApiResponse<HealthStatus>> health(
      @Valid @ModelAttribute HealthQuery query) {
    if (query.getNotifyEmail() != null) {
      log.debug("Health check invoked with notifyEmail parameter present");
    }
    return ResponseEntity.ok(ApiResponse.ok(healthService.getStatus()));
  }
}
