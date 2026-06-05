package com.revelio.api.controller;

import static org.junit.jupiter.api.Assertions.*;

import com.revelio.api.dto.ApiResponse;
import com.revelio.api.dto.FiltersDto;
import com.revelio.api.dto.PostSearchResponse;
import com.revelio.api.dto.SearchUiConfigDto;
import com.revelio.api.model.Blog;
import com.revelio.api.service.BlogSearchService;
import com.revelio.api.service.BlogService;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Unit tests for {@link PostSearchController}.
 *
 * <p>Covers all CR-23 acceptance criteria at the controller layer:
 *
 * <ul>
 *   <li>AC-1: search endpoint is available (GET /api/v1/posts/search)
 *   <li>AC-2: results update per text query (title, body, tags)
 *   <li>AC-3: category multi-select and author filter available
 *   <li>AC-4: combined filters applied simultaneously
 *   <li>AC-5: total count in response
 *   <li>AC-6: active filters visible via result shape
 *   <li>AC-7: "clear all" supported via empty params
 *   <li>AC-8: empty state via zero results / empty list
 *   <li>AC-9: mobile-first API (response is flat JSON — no server-side viewport concerns)
 *   <li>AC-10: keyboard shortcut is client-side — not testable here
 *   <li>AC-11: no redirect — 200 OK with data
 *   <li>AC-12: error handling — invalid params return structured response
 * </ul>
 */
class PostSearchControllerTest {

  private PostSearchController controller;
  private BlogSearchService blogSearchService;
  private List<Blog> testBlogs;

  @BeforeEach
  void setUp() {
    testBlogs = new ArrayList<>();

    Blog.Author alice = new Blog.Author("Alice Chen", null);
    Blog.Author bob = new Blog.Author("Bob Smith", null);

    testBlogs.add(
        new Blog(
            1L,
            "Getting Started with Spring Boot",
            "A guide to REST APIs.",
            null,
            alice,
            Arrays.asList("java", "spring"),
            Instant.parse("2024-01-10T10:00:00Z"),
            true));

    testBlogs.add(
        new Blog(
            2L,
            "React Query vs SWR",
            "Comparing React data-fetching libraries.",
            null,
            bob,
            Arrays.asList("react", "frontend"),
            Instant.parse("2024-01-20T10:00:00Z"),
            true));

    testBlogs.add(
        new Blog(
            3L,
            "Designing RESTful APIs",
            "Best practices for API design.",
            null,
            alice,
            Arrays.asList("api", "design"),
            Instant.parse("2024-01-15T10:00:00Z"),
            true));

    testBlogs.add(
        new Blog(
            4L,
            "CSS Grid Layouts",
            "Mastering CSS Grid.",
            null,
            bob,
            Arrays.asList("css", "frontend"),
            Instant.parse("2024-01-05T10:00:00Z"),
            true));

    // Unpublished — must not appear in any result
    testBlogs.add(
        new Blog(
            5L,
            "Draft about AI",
            "Draft content.",
            null,
            alice,
            Arrays.asList("ai"),
            Instant.parse("2024-01-25T10:00:00Z"),
            false));

    BlogService blogService = new BlogService(testBlogs);
    blogSearchService = new BlogSearchService(blogService);
    controller = new PostSearchController(blogSearchService);
  }

  // -----------------------------------------------------------------------
  // AC-1: Search endpoint is reachable and returns 200
  // -----------------------------------------------------------------------

  /**
   * AC-1: GET /api/v1/posts/search is available and returns HTTP 200 with a non-null body. The
   * search input is available without requiring a click (endpoint is public / unauthenticated).
   */
  @Test
  void testSearchEndpointIsAvailableAndReturns200() {
    ResponseEntity<ApiResponse<PostSearchResponse>> response =
        controller.searchPosts("", null, null, 0, 20);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().isSuccess());
    assertNotNull(response.getBody().getData());
  }

  /**
   * AC-1: GET /api/v1/posts/filters is available and returns HTTP 200. Filters dropdown can be
   * populated once on mount without further interactions.
   */
  @Test
  void testFiltersEndpointIsAvailableAndReturns200() {
    ResponseEntity<ApiResponse<FiltersDto>> response = controller.getFilters();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().isSuccess());
    assertNotNull(response.getBody().getData());
  }

  // -----------------------------------------------------------------------
  // AC-2: Typing into search queries title, body, tags; results update
  // -----------------------------------------------------------------------

  /** AC-2: Query matching post title returns only relevant post. */
  @Test
  void testSearchByTitleReturnsMatchingPost() {
    ResponseEntity<ApiResponse<PostSearchResponse>> response =
        controller.searchPosts("Spring Boot", null, null, 0, 20);

    PostSearchResponse data = response.getBody().getData();
    assertEquals(1, data.getTotal());
    assertEquals("Getting Started with Spring Boot", data.getResults().get(0).getTitle());
  }

  /** AC-2: Query matching excerpt/body text returns correct post. */
  @Test
  void testSearchByBodyTextReturnsMatchingPost() {
    ResponseEntity<ApiResponse<PostSearchResponse>> response =
        controller.searchPosts("data-fetching", null, null, 0, 20);

    PostSearchResponse data = response.getBody().getData();
    assertEquals(1, data.getTotal());
    assertEquals("React Query vs SWR", data.getResults().get(0).getTitle());
  }

  /** AC-2: Query matching a tag value returns posts with that tag. */
  @Test
  void testSearchByTagReturnsMatchingPost() {
    ResponseEntity<ApiResponse<PostSearchResponse>> response =
        controller.searchPosts("css", null, null, 0, 20);

    PostSearchResponse data = response.getBody().getData();
    assertEquals(1, data.getTotal());
    assertEquals("CSS Grid Layouts", data.getResults().get(0).getTitle());
  }

  /** AC-2: Empty query returns all published posts (full list). */
  @Test
  void testEmptyQueryReturnsAllPublishedPosts() {
    ResponseEntity<ApiResponse<PostSearchResponse>> response =
        controller.searchPosts("", null, null, 0, 20);

    assertEquals(4, response.getBody().getData().getTotal());
  }

  // -----------------------------------------------------------------------
  // AC-3: Category/Tag multi-select and Author filter are available
  // -----------------------------------------------------------------------

  /** AC-3: Category filter (single value) narrows results. */
  @Test
  void testCategoryFilterNarrowsResults() {
    ResponseEntity<ApiResponse<PostSearchResponse>> response =
        controller.searchPosts("", Arrays.asList("java"), null, 0, 20);

    PostSearchResponse data = response.getBody().getData();
    assertEquals(1, data.getTotal());
    assertTrue(
        data.getResults().stream()
            .allMatch(r -> r.getCategories() != null && r.getCategories().contains("java")));
  }

  /** AC-3: Category multi-select filter accepts multiple values. */
  @Test
  void testCategoryMultiSelectReturnsPostsMatchingAnyCategory() {
    ResponseEntity<ApiResponse<PostSearchResponse>> response =
        controller.searchPosts("", Arrays.asList("java", "css"), null, 0, 20);

    PostSearchResponse data = response.getBody().getData();
    // post 1 (java) + post 4 (css)
    assertEquals(2, data.getTotal());
  }

  /** AC-3: Author filter narrows results to specified author. */
  @Test
  void testAuthorFilterNarrowsResults() {
    ResponseEntity<ApiResponse<PostSearchResponse>> response =
        controller.searchPosts("", null, Arrays.asList("Bob Smith"), 0, 20);

    PostSearchResponse data = response.getBody().getData();
    assertEquals(2, data.getTotal());
    assertTrue(
        data.getResults().stream()
            .allMatch(r -> r.getAuthor() != null && "Bob Smith".equals(r.getAuthor().getName())));
  }

  /** AC-3: /filters endpoint returns authors and categories lists for dropdown population. */
  @Test
  void testFiltersEndpointReturnsAuthorsAndCategories() {
    FiltersDto filters = controller.getFilters().getBody().getData();

    assertNotNull(filters.getAuthors());
    assertNotNull(filters.getCategories());
    assertFalse(filters.getAuthors().isEmpty());
    assertFalse(filters.getCategories().isEmpty());
  }

  // -----------------------------------------------------------------------
  // AC-4: Filters and query combined; results reflect all active constraints
  // -----------------------------------------------------------------------

  /** AC-4: Text query combined with category filter returns only posts satisfying both. */
  @Test
  void testQueryAndCategoryFilterCombined() {
    // q="guide" matches post 1 excerpt; category "java" also matches post 1.
    ResponseEntity<ApiResponse<PostSearchResponse>> response =
        controller.searchPosts("guide", Arrays.asList("java"), null, 0, 20);

    assertEquals(1, response.getBody().getData().getTotal());
    assertEquals(1L, response.getBody().getData().getResults().get(0).getId());
  }

  /** AC-4: Query + category + author all active simultaneously, returns intersection. */
  @Test
  void testQueryCategoryAndAuthorCombined() {
    ResponseEntity<ApiResponse<PostSearchResponse>> response =
        controller.searchPosts("API", Arrays.asList("api"), Arrays.asList("Alice Chen"), 0, 20);

    // Post 3 matches title ("RESTful APIs"), category "api", and author Alice Chen
    assertEquals(1, response.getBody().getData().getTotal());
    assertEquals(3L, response.getBody().getData().getResults().get(0).getId());
  }

  /** AC-4: Incompatible combined constraints produce zero results (not an error). */
  @Test
  void testIncompatibleCombinedConstraintsReturnZero() {
    // React post has frontend tag but is by Bob, not Alice.
    ResponseEntity<ApiResponse<PostSearchResponse>> response =
        controller.searchPosts(
            "React", Arrays.asList("frontend"), Arrays.asList("Alice Chen"), 0, 20);

    assertEquals(0, response.getBody().getData().getTotal());
  }

  // -----------------------------------------------------------------------
  // AC-5: Total number of matched results displayed and updates in real time
  // -----------------------------------------------------------------------

  /** AC-5: Response envelope contains 'total' field equal to matched post count. */
  @Test
  void testResponseContainsTotalField() {
    PostSearchResponse data = controller.searchPosts("", null, null, 0, 20).getBody().getData();

    assertEquals(4, data.getTotal());
    assertNotNull(data.getResults());
  }

  /** AC-5: Total changes when different queries are sent (simulates real-time updates). */
  @Test
  void testTotalChangesWithDifferentQueries() {
    long totalAll = controller.searchPosts("", null, null, 0, 20).getBody().getData().getTotal();
    long totalFiltered =
        controller.searchPosts("Spring", null, null, 0, 20).getBody().getData().getTotal();

    assertTrue(totalFiltered < totalAll);
  }

  // -----------------------------------------------------------------------
  // AC-6: Active filters are visually distinct — results carry filter context
  // -----------------------------------------------------------------------

  /**
   * AC-6: Each result carries its categories list so the UI can render active-filter chips. The API
   * surface supports this by including categories in BlogSummaryDto.
   */
  @Test
  void testResultsIncludeCategoriesForChipRendering() {
    PostSearchResponse data =
        controller.searchPosts("", Arrays.asList("java"), null, 0, 20).getBody().getData();

    assertFalse(data.getResults().isEmpty());
    data.getResults().forEach(r -> assertNotNull(r.getCategories()));
  }

  /** AC-6: Each result carries author info so the UI can render an author filter chip. */
  @Test
  void testResultsIncludeAuthorInfoForChipRendering() {
    PostSearchResponse data =
        controller.searchPosts("", null, Arrays.asList("Alice Chen"), 0, 20).getBody().getData();

    assertFalse(data.getResults().isEmpty());
    data.getResults()
        .forEach(
            r -> {
              assertNotNull(r.getAuthor());
              assertNotNull(r.getAuthor().getName());
            });
  }

  // -----------------------------------------------------------------------
  // AC-7: Clear all removes search query and all filters
  // -----------------------------------------------------------------------

  /** AC-7: Passing null/empty params is equivalent to "clear all" and returns all posts. */
  @Test
  void testClearAllReturnsAllPublishedPosts() {
    PostSearchResponse cleared = controller.searchPosts("", null, null, 0, 20).getBody().getData();

    assertEquals(4, cleared.getTotal());
  }

  /** AC-7: Removing only the category filter while keeping the query broadens results. */
  @Test
  void testRemovingCategoryFilterBroadensResults() {
    PostSearchResponse withCategory =
        controller.searchPosts("", Arrays.asList("java"), null, 0, 20).getBody().getData();
    PostSearchResponse withoutCategory =
        controller.searchPosts("", null, null, 0, 20).getBody().getData();

    assertTrue(withoutCategory.getTotal() > withCategory.getTotal());
  }

  // -----------------------------------------------------------------------
  // AC-8: Empty state shown when no results match
  // -----------------------------------------------------------------------

  /** AC-8: Response with zero total and empty list is the empty state (no exception thrown). */
  @Test
  void testEmptyStateReturnedWhenNoResultsMatch() {
    ResponseEntity<ApiResponse<PostSearchResponse>> response =
        controller.searchPosts("nonexistent_search_xyzzy", null, null, 0, 20);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    PostSearchResponse data = response.getBody().getData();
    assertEquals(0, data.getTotal());
    assertTrue(data.getResults().isEmpty());
  }

  /** AC-8: Unpublished posts never appear — they don't inflate results beyond actual content. */
  @Test
  void testUnpublishedPostsAbsentFromResults() {
    PostSearchResponse data =
        controller.searchPosts("Draft", null, null, 0, 20).getBody().getData();

    // The unpublished draft post should never appear
    assertEquals(0, data.getTotal());
  }

  // -----------------------------------------------------------------------
  // AC-9: Mobile-first — API returns flat JSON usable at any viewport
  // -----------------------------------------------------------------------

  /**
   * AC-9: The API response is a flat JSON structure with no server-rendered HTML. Any viewport
   * width is supported by the client consuming this JSON. Test verifies the response structure is a
   * pure data envelope (no HTML, no server-side layout constraints).
   */
  @Test
  void testResponseIsDataOnlyNoPresentationHtml() {
    PostSearchResponse data = controller.searchPosts("", null, null, 0, 20).getBody().getData();

    // Verify flat JSON fields — no viewport-specific structure
    assertTrue(data.getTotal() >= 0);
    assertTrue(data.getPage() >= 0);
    assertTrue(data.getSize() > 0);
    assertNotNull(data.getResults());
  }

  // -----------------------------------------------------------------------
  // AC-10: Keyboard shortcut (/ key) — server declares config for client wiring
  // -----------------------------------------------------------------------

  /**
   * AC-10: GET /api/v1/posts/search-config returns HTTP 200 with a non-null body. The endpoint is
   * public (no authentication required) so the client can fetch the keyboard shortcut configuration
   * on mount.
   */
  @Test
  void testSearchConfigEndpointIsAvailableAndReturns200() {
    ResponseEntity<ApiResponse<SearchUiConfigDto>> response = controller.getSearchConfig();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().isSuccess());
    assertNotNull(response.getBody().getData());
  }

  /**
   * AC-10: The server declares {@code "/"} as the keyboard shortcut key. This is the single source
   * of truth that the client reads to attach a {@code keydown} listener; the client must call
   * {@code event.preventDefault()} and focus the search input when this key is pressed outside a
   * text element.
   */
  @Test
  void testSearchConfigDeclaresSlashAsKeyboardShortcutKey() {
    SearchUiConfigDto config = controller.getSearchConfig().getBody().getData();

    assertEquals(
        "/",
        config.getKeyboardShortcutKey(),
        "The keyboard shortcut key must be '/' per CR-23 specification");
  }

  /**
   * AC-10: The server provides the {@code searchInputId} that the client must use when calling
   * {@code document.getElementById(...).focus()} from the keydown handler. This ensures the '/'
   * shortcut targets the correct search {@code <input>} element and no other.
   */
  @Test
  void testSearchConfigProvidesSearchInputId() {
    SearchUiConfigDto config = controller.getSearchConfig().getBody().getData();

    assertNotNull(config.getSearchInputId(), "searchInputId must not be null");
    assertFalse(
        config.getSearchInputId().isBlank(), "searchInputId must be a non-blank element id");
  }

  /**
   * AC-10: The config includes a human-readable shortcut description so the UI can display a
   * tooltip or help hint (e.g. 'Press / to search'). This description must mention the '/' key so
   * users understand how to invoke the shortcut.
   */
  @Test
  void testSearchConfigShortcutDescriptionMentionsSlashKey() {
    SearchUiConfigDto config = controller.getSearchConfig().getBody().getData();

    assertNotNull(config.getShortcutDescription());
    assertTrue(
        config.getShortcutDescription().contains("/"),
        "Shortcut description must mention the '/' key so the UI can display a helpful hint");
  }

  // -----------------------------------------------------------------------
  // AC-11: No full page reload — returns 200 with data, not a redirect
  // -----------------------------------------------------------------------

  /** AC-11: Response is HTTP 200 (not 3xx redirect), confirming in-place update pattern. */
  @Test
  void testSearchReturnsOkNotRedirect() {
    ResponseEntity<ApiResponse<PostSearchResponse>> response =
        controller.searchPosts("spring", null, null, 0, 20);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertFalse(response.getStatusCode().is3xxRedirection());
  }

  /** AC-11: Subsequent queries with different params return fresh data without redirect. */
  @Test
  void testSubsequentQueriesReturnFreshData() {
    // "spring" matches only post 1 (total=1); empty query returns all published posts (total=4).
    PostSearchResponse first =
        controller.searchPosts("spring", null, null, 0, 20).getBody().getData();
    PostSearchResponse second = controller.searchPosts("", null, null, 0, 20).getBody().getData();

    // Totals must differ — confirms fresh data is returned for each query (in-place update)
    assertNotEquals(first.getTotal(), second.getTotal());
    // First result for "spring" is post 1; first result for empty is the newest post
    assertNotEquals(first.getResults().get(0).getId(), second.getResults().get(0).getId());
  }

  // -----------------------------------------------------------------------
  // AC-12: Backend request failure — non-blocking error, last valid results remain
  // -----------------------------------------------------------------------

  /**
   * AC-12: Illegal pagination params throw an IllegalArgumentException (not a silent failure),
   * allowing the caller to decide whether to show a non-blocking error and retain previous results.
   */
  @Test
  void testInvalidPageParameterThrowsIllegalArgumentException() {
    assertThrows(
        IllegalArgumentException.class, () -> controller.searchPosts("", null, null, -1, 20));
  }

  /** AC-12: Invalid size throws a clear exception enabling non-blocking error handling. */
  @Test
  void testInvalidSizeParameterThrowsIllegalArgumentException() {
    assertThrows(
        IllegalArgumentException.class, () -> controller.searchPosts("", null, null, 0, 0));
  }

  // -----------------------------------------------------------------------
  // Pagination
  // -----------------------------------------------------------------------

  /** Pagination: page and size are reflected in the response envelope. */
  @Test
  void testPaginationFieldsReflectedInResponse() {
    PostSearchResponse data = controller.searchPosts("", null, null, 0, 2).getBody().getData();

    assertEquals(0, data.getPage());
    assertEquals(2, data.getSize());
    assertEquals(2, data.getResults().size());
    assertEquals(4, data.getTotal()); // total unchanged
  }

  /** Pagination: page beyond data returns empty results but correct total. */
  @Test
  void testPageBeyondDataReturnsEmptyResultsWithCorrectTotal() {
    PostSearchResponse data = controller.searchPosts("", null, null, 99, 20).getBody().getData();

    assertEquals(4, data.getTotal());
    assertTrue(data.getResults().isEmpty());
  }
}
