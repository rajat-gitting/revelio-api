package com.revelio.api.controller;

import com.revelio.api.dto.ApiResponse;
import com.revelio.api.dto.FiltersDto;
import com.revelio.api.dto.PostSearchResponse;
import com.revelio.api.dto.SearchUiConfigDto;
import com.revelio.api.service.BlogSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Exposes blog post search and filter endpoints.
 *
 * <ul>
 *   <li>GET /api/v1/posts/search — full-text + filter search with pagination
 *   <li>GET /api/v1/posts/filters — distinct authors and categories for dropdown population
 * </ul>
 */
@Tag(name = "Post Search")
@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
@Slf4j
public class PostSearchController {

  private final BlogSearchService blogSearchService;

  /**
   * Search and filter published blog posts.
   *
   * <p>Query parameters:
   *
   * <ul>
   *   <li>{@code q} — free-text query matched against title, body (excerpt), and tags
   *   <li>{@code category} — zero or more category/tag values to filter on (multi-value)
   *   <li>{@code author} — zero or more author names to filter on (multi-value)
   *   <li>{@code page} — zero-based page index (default 0)
   *   <li>{@code size} — page size (default 20)
   * </ul>
   *
   * <p>All active constraints are combined simultaneously (AND semantics across dimensions).
   * Results include the {@code total} count so callers can display "N results" and render
   * pagination controls.
   */
  @GetMapping("/search")
  @Operation(
      summary = "Search blog posts",
      description =
          "Full-text search across title, body, and tags with optional category/tag and author"
              + " filters. Supports pagination. Returns total matched count.")
  public ResponseEntity<ApiResponse<PostSearchResponse>> searchPosts(
      @RequestParam(required = false, defaultValue = "") String q,
      @RequestParam(required = false) List<String> category,
      @RequestParam(required = false) List<String> author,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size) {

    log.debug(
        "GET /api/v1/posts/search q='{}' category={} author={} page={} size={}",
        q,
        category,
        author,
        page,
        size);

    PostSearchResponse response = blogSearchService.search(q, category, author, page, size);
    return ResponseEntity.ok(ApiResponse.ok(response));
  }

  /**
   * Returns the distinct author names and category/tag values available for filter dropdowns.
   * Intended to be fetched once on client mount and cached locally.
   */
  @GetMapping("/filters")
  @Operation(
      summary = "Get available filter values",
      description =
          "Returns distinct author names and category/tag values derived from all published posts."
              + " Used to populate the client-side filter dropdowns.")
  public ResponseEntity<ApiResponse<FiltersDto>> getFilters() {
    log.debug("GET /api/v1/posts/filters");
    FiltersDto filters = blogSearchService.getFilters();
    return ResponseEntity.ok(ApiResponse.ok(filters));
  }

  /**
   * Returns the server-declared UI configuration for the blog search feature.
   *
   * <p>Clients use this to wire up the keyboard shortcut ({@code /} key) that moves focus to the
   * search input, and to resolve the target element ID without hard-coding it. The configuration is
   * intentionally served from the API so that the shortcut key and element binding can be changed
   * server-side without a client deployment.
   *
   * <p>Keyboard-shortcut implementation contract (client side):
   *
   * <pre>
   *   document.addEventListener('keydown', (event) =&gt; {
   *     const tag = event.target.tagName;
   *     const editable = event.target.isContentEditable;
   *     if (tag === 'INPUT' || tag === 'TEXTAREA' || tag === 'SELECT' || editable) return;
   *     if (event.key === config.keyboardShortcutKey) {
   *       event.preventDefault();
   *       document.getElementById(config.searchInputId)?.focus();
   *     }
   *   });
   * </pre>
   */
  @GetMapping("/search-config")
  @Operation(
      summary = "Get search UI configuration",
      description =
          "Returns keyboard-shortcut key and search-input element ID so clients can implement"
              + " the '/' shortcut that moves focus to the search input without hard-coding"
              + " client-side values.")
  public ResponseEntity<ApiResponse<SearchUiConfigDto>> getSearchConfig() {
    log.debug("GET /api/v1/posts/search-config");
    SearchUiConfigDto config =
        new SearchUiConfigDto(
            "/",
            "blog-search-input",
            "Press / to focus the search bar (when not typing in another field)");
    return ResponseEntity.ok(ApiResponse.ok(config));
  }
}
