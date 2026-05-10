package com.revelio.api.controller;

import com.revelio.api.dto.BlogListRequest;
import com.revelio.api.dto.BlogListResponse;
import com.revelio.api.service.BlogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/blogs")
public class BlogController {
  private final BlogService blogService;

  public BlogController(BlogService blogService) {
    this.blogService = blogService;
  }

  @GetMapping
  public ResponseEntity<BlogListResponse> getBlogs(
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {

    if (page < 0) {
      throw new IllegalArgumentException("Page number must be non-negative");
    }

    if (size <= 0) {
      throw new IllegalArgumentException("Page size must be positive");
    }

    if (size > 100) {
      throw new IllegalArgumentException("Page size must not exceed 100");
    }

    BlogListRequest request = new BlogListRequest(page, size);
    BlogListResponse response = blogService.getPublishedBlogs(request);

    return ResponseEntity.ok(response);
  }
}
