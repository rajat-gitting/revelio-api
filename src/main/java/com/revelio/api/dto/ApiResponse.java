package com.revelio.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

  private boolean success;
  private String message;
  private T data;

  @Builder.Default private Instant timestamp = Instant.now();

  public static <T> ApiResponse<T> ok(T data) {
    return ApiResponse.<T>builder()
        .success(true)
        .message("OK")
        .data(data)
        .timestamp(Instant.now())
        .build();
  }

  public static <T> ApiResponse<T> ok(String message, T data) {
    return ApiResponse.<T>builder()
        .success(true)
        .message(message)
        .data(data)
        .timestamp(Instant.now())
        .build();
  }
}
