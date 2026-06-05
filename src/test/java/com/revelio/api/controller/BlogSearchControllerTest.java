package com.revelio.api.controller;

import static org.junit.jupiter.api.Assertions.*;

import com.revelio.api.dto.ApiResponse;
import com.revelio.api.dto.PostFiltersDto;
import com.revelio.api.dto.PostSearchResultDto;
import com.revelio.api.model.Blog;
import com.revelio.api.service.BlogService;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

/**
 * Controller-layer tests for the search and filter endpoints added in CR-23.
 *
 * <p>Uses the real BlogService with a controlled data set so tests remain pure unit tests (no
 * Spring context spin-up required) while still exercising the full controller → service → DTO
 * chain.
 */
class BlogSearchControllerTest {

  private BlogController blogController;
  private BlogService blogService;

  @BeforeEach
  void setUp() {
    Blog.Author alice = new Blog.Author("Alice Chen", null);
    Blog.Author bob = new Blog.Author("Bob Smith", null);

    List<Blog> blogs = new ArrayList<>();
    blogs.add(
        new Blog(
            1L,
            "Getting Started with Spring Boot",
            "A beginner guide to REST APIs.",
            null,
            alice,
            Arrays.asList("java", "spring", "tutorial"),
            Instant.parse("2024-01-15T10:00:00Z"),
            true));
    blogs.add(
        new Blog(
            2L,
            "React Query vs SWR",
            "Comparing data-fetching libraries for React.",
            null,
            bob,
            Arrays.asList("react", "frontend"),
            Instant.parse("2024-01-20T10:00:00Z"),
            true));
    blogs.add(
        new Blog(
            3L,
            "Designing RESTful APIs",
            "Best practices for endpoint naming.",
            null,
            alice,
            Arrays.asList("api", "design"),
            Instant.parse("2024-01-25T10:00:00Z"),
            true));

    blogService = new BlogService(blogs);
    blogController = new BlogController(blogService);
  }

  // -------------------------------------------------------------------------
  // Helper
  // -------------------------------------------------------------------------

  private PostSearchResultDto search(
      String q, List<String> category, List<String> author, int page, int size) {
    ResponseEntity<ApiResponse<PostSearchResultDto>> response =
        blogController.searchPosts(q, category, author, page, size);
    assertNotNull(response.getBody());
    assertTrue(response.getBody().isSuccess());
    return response.getBody().getData();
  }

  private PostFiltersDto getFilters() {
    ResponseEntity<ApiResponse<PostFiltersDto>> response = blogController.getFilters();
    assertNotNull(response.getBody());
    assertTrue(response.getBody().isSuccess());
    return response.getBody().getData();
  }

  // -------------------------------------------------------------------------
  // AC-1: Search endpoint is accessible (visible / one-interaction)
  // The endpoint exists and returns 200 OK without any query params.
  // -------------------------------------------------------------------------

  @Test
  void searchEndpoint_isAccessibleWithoutParams_returnsAllPublishedPosts() {
    PostSearchResultDto result = search(null, null, null, 0, 20);
    assertNotNull(result);
    assertEquals(3, result.getTotal());
  }

  // -------------------------------------------------------------------------
  // AC-2: Queries titles, body, and tags
  // -------------------------------------------------------------------------

  @Test
  void searchByTitle_matchesCorrectPost() {
    PostSearchResultDto result = search("Spring", null, null, 0, 20);
    assertEquals(1, result.getTotal());
    assertEquals("Getting Started with Spring Boot", result.getResults().get(0).getTitle());
  }

  @Test
  void searchByBodyText_matchesCorrectPost() {
    PostSearchResultDto result = search("naming", null, null, 0, 20);
    assertEquals(1, result.getTotal());
    assertEquals("Designing RESTful APIs", result.getResults().get(0).getTitle());
  }

  @Test
  void searchByTag_matchesCorrectPost() {
    PostSearchResultDto result = search("frontend", null, null, 0, 20);
    assertEquals(1, result.getTotal());
    assertEquals("React Query vs SWR", result.getResults().get(0).getTitle());
  }

  // -------------------------------------------------------------------------
  // AC-3: Category/Tag multi-select filter and Author filter available
  // -------------------------------------------------------------------------

  @Test
  void categoryFilter_returnsPostsWithMatchingTag() {
    PostSearchResultDto result = search(null, Arrays.asList("java"), null, 0, 20);
    assertEquals(1, result.getTotal());
    assertEquals("Getting Started with Spring Boot", result.getResults().get(0).getTitle());
  }

  @Test
  void authorFilter_returnsPostsBySpecifiedAuthor() {
    PostSearchResultDto result = search(null, null, Arrays.asList("Bob Smith"), 0, 20);
    assertEquals(1, result.getTotal());
    assertEquals("Bob Smith", result.getResults().get(0).getAuthor().getName());
  }

  @Test
  void filtersEndpoint_returnsCategoriesAndAuthors() {
    PostFiltersDto filters = getFilters();
    assertFalse(filters.getCategories().isEmpty());
    assertFalse(filters.getAuthors().isEmpty());
  }

  // -------------------------------------------------------------------------
  // AC-4: Filters and search query can be combined
  // -------------------------------------------------------------------------

  @Test
  void combinedQueryAndCategoryFilter_returnsIntersection() {
    // "api" in q hits posts 1 (body) and 3 (title/body); category="api" → only post 3
    PostSearchResultDto result = search("api", Arrays.asList("api"), null, 0, 20);
    assertEquals(1, result.getTotal());
    assertEquals("Designing RESTful APIs", result.getResults().get(0).getTitle());
  }

  // -------------------------------------------------------------------------
  // AC-5: Total count is returned and reflects constraints
  // -------------------------------------------------------------------------

  @Test
  void totalCountIsReturnedInResponseEnvelope() {
    PostSearchResultDto result = search(null, null, null, 0, 20);
    assertEquals(3, result.getTotal());
  }

  @Test
  void totalCountUpdatesWithActiveFilter() {
    PostSearchResultDto result = search(null, Arrays.asList("react"), null, 0, 20);
    assertEquals(1, result.getTotal());
  }

  // -------------------------------------------------------------------------
  // AC-7: 'Clear all' — nulls / empty params return full result set
  // -------------------------------------------------------------------------

  @Test
  void clearAll_nullParams_returnsAllPublishedPosts() {
    PostSearchResultDto result = search(null, null, null, 0, 20);
    assertEquals(3, result.getTotal());
  }

  // -------------------------------------------------------------------------
  // AC-8: Empty state — no matches → empty result list, total = 0
  // -------------------------------------------------------------------------

  @Test
  void noMatch_returnsEmptyResultsAndZeroTotal() {
    PostSearchResultDto result = search("xyzzy_no_match_99", null, null, 0, 20);
    assertEquals(0, result.getTotal());
    assertTrue(result.getResults().isEmpty());
  }

  // -------------------------------------------------------------------------
  // AC-9: Pagination (size/page params work correctly)
  // -------------------------------------------------------------------------

  @Test
  void paginationParams_limitResultsPerPage() {
    PostSearchResultDto page0 = search(null, null, null, 0, 2);
    assertEquals(2, page0.getResults().size());
    assertEquals(3, page0.getTotal()); // total still 3

    PostSearchResultDto page1 = search(null, null, null, 1, 2);
    assertEquals(1, page1.getResults().size());
    assertEquals(3, page1.getTotal());
  }

  // -------------------------------------------------------------------------
  // AC-11: No full page reload — validated by the in-place update of the response
  //        (the same endpoint is called; pagination data is embedded in the response)
  // -------------------------------------------------------------------------

  @Test
  void searchResponse_containsPageAndSizeMetadata() {
    PostSearchResultDto result = search(null, null, null, 0, 5);
    assertEquals(0, result.getPage());
    assertEquals(5, result.getSize());
  }

  // -------------------------------------------------------------------------
  // Validation — bad params propagate as exceptions
  // -------------------------------------------------------------------------

  @Test
  void searchEndpoint_throwsForNegativePage() {
    assertThrows(
        IllegalArgumentException.class, () -> blogController.searchPosts(null, null, null, -1, 10));
  }

  @Test
  void searchEndpoint_throwsForZeroSize() {
    assertThrows(
        IllegalArgumentException.class, () -> blogController.searchPosts(null, null, null, 0, 0));
  }
}
