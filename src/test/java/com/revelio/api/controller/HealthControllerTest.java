package com.revelio.api.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.revelio.api.exception.GlobalExceptionHandler;
import com.revelio.api.model.HealthStatus;
import com.revelio.api.service.HealthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = HealthController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class HealthControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private HealthService healthService;

  @Test
  void healthReturnsOkWithUpStatus() throws Exception {
    when(healthService.getStatus())
        .thenReturn(HealthStatus.builder().status("UP").service("revelio-api").build());

    mockMvc
        .perform(get("/api/health").contextPath("/api").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.status").value("UP"))
        .andExpect(jsonPath("$.data.service").value("revelio-api"));
  }
}
