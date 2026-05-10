package com.revelio.api.config;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "app.cors")
public class CorsProperties {

  private boolean allowAll = false;
  private List<String> allowedOrigins = new ArrayList<>(List.of("http://localhost:5173"));
}
