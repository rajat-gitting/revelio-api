package com.revelio.api.controller;

import com.revelio.api.dto.ApiResponse;
import com.revelio.api.dto.BlogResponseDto;
import com.revelio.api.dto.CreateBlogRequestDto;
import com.revelio.api.dto.PagedResponse;
import com.revelio.api.dto.PostFiltersDto;
import com.revelio.api.dto.PostSearchResultDto;
import com.revelio.api.dto.UpdateBlogRequestDto;
import com.revelio.api.service.BlogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Blogs")
@RestController
@RequestMapping("/blogs")
@RequiredArgsConstructor
@Slf4j
public class BlogController {

  private final BlogService blogService;

  @GetMapping
  @Operation(
      summary = "List published blog posts",
      description =
          "Returns paginated published posts sorted by publishedAt descending. "
              + "The response mirrors the Spring Data Page structure: content, totalElements, "
              + "totalPages, number (0-based page index), and size.")
  public ResponseEntity<ApiResponse<PagedResponse<BlogResponseDto>>> getBlogs(
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "12") int size) {
    log.debug("GET /api/blogs page={} size={}", page, size);
    PagedResponse<BlogResponseDto> paged = blogService.getPublishedBlogsPaged(page, size);
    return ResponseEntity.ok(ApiResponse.ok(paged));
  }

  @GetMapping("/search")
  @Operation(
      summary = "Search and filter blog posts",
      description =
          "Returns paginated published posts matching the given query string, categories,"
              + " and/or authors. All constraints are ANDed. The response envelope includes the"
              + " total matched count, current page, page size, and the result list.")
  public ResponseEntity<ApiResponse<PostSearchResultDto>> searchPosts(
      @RequestParam(required = false) String q,
      @RequestParam(required = false) List<String> category,
      @RequestParam(required = false) List<String> author,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size) {
    log.debug(
        "GET /api/blogs/search q={} category={} author={} page={} size={}",
        q,
        category,
        author,
        page,
        size);
    PostSearchResultDto result = blogService.searchPosts(q, category, author, page, size);
    return ResponseEntity.ok(ApiResponse.ok(result));
  }

  @GetMapping("/filters")
  @Operation(
      summary = "Get available filter values",
      description =
          "Returns the distinct categories (tags) and author names available across all published"
              + " posts, for use in populating the search filter dropdowns.")
  public ResponseEntity<ApiResponse<PostFiltersDto>> getFilters() {
    log.debug("GET /api/blogs/filters");
    PostFiltersDto filters = blogService.getAvailableFilters();
    return ResponseEntity.ok(ApiResponse.ok(filters));
  }

  @PostMapping
  @Operation(
      summary = "Create a new blog post",
      description =
          "Creates a new published blog post. The backend assigns a new id, sets publishedAt to"
              + " now, and sets published = true. Returns HTTP 201 with the created post.")
  public ResponseEntity<ApiResponse<BlogResponseDto>> createBlog(
      @Valid @RequestBody CreateBlogRequestDto request) {
    log.debug("POST /api/blogs title={}", request.getTitle());
    BlogResponseDto created = blogService.createBlog(request);
    return ResponseEntity.created(URI.create("/api/blogs/" + created.getId()))
        .body(ApiResponse.ok(created));
  }

  @GetMapping("/{id}")
  @Operation(
      summary = "Get a single published blog post",
      description =
          "Returns the full details of the published post with the given id, including the article"
              + " body. Returns HTTP 404 if the post does not exist or is not published.")
  public ResponseEntity<ApiResponse<BlogResponseDto>> getBlogById(@PathVariable Long id) {
    log.debug("GET /api/blogs/{}", id);
    return blogService
        .getBlogById(id)
        .map(dto -> ResponseEntity.ok(ApiResponse.ok(dto)))
        .orElse(ResponseEntity.notFound().build());
  }

  @PutMapping("/{id}")
  @Operation(
      summary = "Update an existing blog post",
      description =
          "Partially updates a published blog post with the given id. Only supplied fields are"
              + " applied; omitted fields retain their current values. Returns HTTP 200 with the"
              + " updated post. Returns HTTP 404 if no post exists with the given id.")
  public ResponseEntity<ApiResponse<BlogResponseDto>> updateBlog(
      @PathVariable Long id, @Valid @RequestBody UpdateBlogRequestDto request) {
    log.debug("PUT /api/blogs/{}", id);
    BlogResponseDto updated = blogService.updateBlog(id, request);
    return ResponseEntity.ok(ApiResponse.ok(updated));
  }
}
