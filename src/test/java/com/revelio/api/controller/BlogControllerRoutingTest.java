package com.revelio.api.controller;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.revelio.api.dto.BlogResponseDto;
import com.revelio.api.dto.PagedResponse;
import com.revelio.api.service.BlogService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Locks in the public URL of the blogs endpoint. The global {@code server.servlet.context-path} is
 * {@code /api}, so the controller must NOT repeat the {@code /api} prefix in its mapping. If the
 * mapping ever regresses to {@code /api/blogs} the endpoint would move to {@code /api/api/blogs}
 * and this test (which requests {@code /api/blogs}) would fail.
 */
@WebMvcTest(controllers = BlogController.class)
@AutoConfigureMockMvc(addFilters = false)
class BlogControllerRoutingTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private BlogService blogService;

  @Test
  void blogsAreServedAtApiBlogs() throws Exception {
    when(blogService.getPublishedBlogsPaged(anyInt(), anyInt()))
        .thenReturn(new PagedResponse<BlogResponseDto>(List.of(), 0, 0, 0, 12));

    mockMvc
        .perform(get("/api/blogs").contextPath("/api").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }
}
