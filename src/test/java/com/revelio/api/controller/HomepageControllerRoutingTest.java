package com.revelio.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.revelio.api.dto.HomepageDto;
import com.revelio.api.service.HomepageService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Locks in the public URL of the homepage endpoint. With {@code server.servlet.context-path=/api}
 * the controller mapping must not repeat {@code /api}; the endpoint is served at {@code
 * /api/homepage}. If the mapping regressed to {@code /api/homepage} it would move to {@code
 * /api/api/homepage} and this test would fail.
 */
@WebMvcTest(controllers = HomepageController.class)
@AutoConfigureMockMvc(addFilters = false)
class HomepageControllerRoutingTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private HomepageService homepageService;

  @Test
  void homepageIsServedAtApiHomepage() throws Exception {
    when(homepageService.getHomepage(anyInt(), anyInt(), any()))
        .thenReturn(new HomepageDto(null, List.of(), "blog-section", 32));

    mockMvc
        .perform(get("/api/homepage").contextPath("/api").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }
}
