package com.revelio.api.controller;

import com.revelio.api.dto.ApiResponse;
import com.revelio.api.dto.BlogResponseDto;
import com.revelio.api.service.BlogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Blogs")
@RestController
@RequestMapping("/api/blogs")
@RequiredArgsConstructor
@Slf4j
public class BlogController {

  private final BlogService blogService;

  @GetMapping
  @Operation(summary = "List published blog posts", description = "Returns paginated published posts sorted by publishedAt descending.")
  public ResponseEntity<ApiResponse<List<BlogResponseDto>>> getBlogs(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    log.debug("GET /api/blogs page={} size={}", page, size);
    List<BlogResponseDto> posts = blogService.getPublishedBlogs(page, size)
        .stream()
        .map(BlogResponseDto::fromBlog)
        .collect(Collectors.toList());
    return ResponseEntity.ok(ApiResponse.ok(posts));
  }
}
