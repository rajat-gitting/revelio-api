package com.revelio.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HealthQuery {

  @Email(message = "notifyEmail must be a valid email address")
  private String notifyEmail;

  @Size(max = 128, message = "traceId must be at most 128 characters")
  private String traceId;
}
