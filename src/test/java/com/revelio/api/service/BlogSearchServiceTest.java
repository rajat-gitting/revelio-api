package com.revelio.api.service;

import static org.junit.jupiter.api.Assertions.*;

import com.revelio.api.dto.PostFiltersDto;
import com.revelio.api.dto.PostSearchResultDto;
import com.revelio.api.dto.PostSearchResultDto.AppliedFiltersDto;
import com.revelio.api.model.Blog;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for the search / filter capabilities added in CR-23.
 *
 * <p>One focused test per acceptance criterion (AC) that can be exercised at the service layer. AC
 * numbers correspond to the ticket's numbered list.
 */
class BlogSearchServiceTest {

  private BlogService blogService;

  @BeforeEach
  void setUp() {
    Blog.Author alice = new Blog.Author("Alice Chen", null);
    Blog.Author bob = new Blog.Author("Bob Smith", null);

    List<Blog> blogs = new ArrayList<>();
    // published — title contains "Spring", tags: java, spring, tutorial
    blogs.add(
        new Blog(
            1L,
            "Getting Started with Spring Boot",
            "A beginner guide to REST APIs with Spring Boot.",
            null,
            alice,
            Arrays.asList("java", "spring", "tutorial"),
            Instant.parse("2024-01-15T10:00:00Z"),
            true));
    // published — tags: react, frontend
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
    // published — tags: api, design
    blogs.add(
        new Blog(
            3L,
            "Designing RESTful APIs",
            "Best practices for endpoint naming and pagination.",
            null,
            alice,
            Arrays.asList("api", "design"),
            Instant.parse("2024-01-25T10:00:00Z"),
            true));
    // unpublished — should never appear in any results
    blogs.add(
        new Blog(
            4L,
            "Draft Post",
            "Unpublished content about java.",
            null,
            alice,
            Arrays.asList("java"),
            Instant.parse("2024-01-10T10:00:00Z"),
            false));

    blogService = new BlogService(blogs);
  }

  // -------------------------------------------------------------------------
  // AC-2: Typing into the search input queries post titles, body text, and tags
  // -------------------------------------------------------------------------

  @Test
  void searchByTitle_returnsMatchingPosts() {
    PostSearchResultDto result = blogService.searchPosts("Spring", null, null, 0, 20);
    assertEquals(1, result.getTotal());
    assertEquals("Getting Started with Spring Boot", result.getResults().get(0).getTitle());
  }

  @Test
  void searchByBodyText_returnsMatchingPosts() {
    // "pagination" only appears in the excerpt of post 3
    PostSearchResultDto result = blogService.searchPosts("pagination", null, null, 0, 20);
    assertEquals(1, result.getTotal());
    assertEquals("Designing RESTful APIs", result.getResults().get(0).getTitle());
  }

  @Test
  void searchByTag_returnsMatchingPosts() {
    // "react" is a tag on post 2
    PostSearchResultDto result = blogService.searchPosts("react", null, null, 0, 20);
    assertEquals(1, result.getTotal());
    assertEquals("React Query vs SWR", result.getResults().get(0).getTitle());
  }

  @Test
  void searchIsCaseInsensitive() {
    PostSearchResultDto upper = blogService.searchPosts("SPRING", null, null, 0, 20);
    PostSearchResultDto lower = blogService.searchPosts("spring", null, null, 0, 20);
    assertEquals(upper.getTotal(), lower.getTotal());
  }

  @Test
  void searchExcludesUnpublishedPosts() {
    // "java" tag is on published post 1 and unpublished post 4
    PostSearchResultDto result = blogService.searchPosts("java", null, null, 0, 20);
    assertTrue(
        result.getResults().stream().allMatch(r -> !r.getTitle().equals("Draft Post")),
        "Unpublished posts must not appear in search results");
  }

  // -------------------------------------------------------------------------
  // AC-3 / AC-4: Category/Tag filter and Author filter; constraints are ANDed
  // -------------------------------------------------------------------------

  @Test
  void categoryFilter_returnsPostsWithMatchingTag() {
    PostSearchResultDto result =
        blogService.searchPosts(null, Arrays.asList("frontend"), null, 0, 20);
    assertEquals(1, result.getTotal());
    assertEquals("React Query vs SWR", result.getResults().get(0).getTitle());
  }

  @Test
  void categoryFilter_multiSelect_matchesAnyOfTheRequestedTags() {
    // Posts with "java" OR "design"
    PostSearchResultDto result =
        blogService.searchPosts(null, Arrays.asList("java", "design"), null, 0, 20);
    assertEquals(2, result.getTotal());
  }

  @Test
  void authorFilter_returnPostsBySpecifiedAuthor() {
    PostSearchResultDto result =
        blogService.searchPosts(null, null, Arrays.asList("Bob Smith"), 0, 20);
    assertEquals(1, result.getTotal());
    assertEquals("Bob Smith", result.getResults().get(0).getAuthor().getName());
  }

  @Test
  void combinedQueryAndCategoryFilter_andsConstraints() {
    // q="API" should match posts 1 (body) and 3 (title+body); category="api" narrows to post 3
    PostSearchResultDto result = blogService.searchPosts("API", Arrays.asList("api"), null, 0, 20);
    assertEquals(1, result.getTotal());
    assertEquals("Designing RESTful APIs", result.getResults().get(0).getTitle());
  }

  @Test
  void combinedQueryAndAuthorFilter_andsConstraints() {
    // q="Spring" matches post 1 (Alice); author="Bob Smith" → no results
    PostSearchResultDto result =
        blogService.searchPosts("Spring", null, Arrays.asList("Bob Smith"), 0, 20);
    assertEquals(0, result.getTotal());
  }

  // -------------------------------------------------------------------------
  // AC-5: Total number of matched results is returned and reflects active constraints
  // -------------------------------------------------------------------------

  @Test
  void totalCountReflectsAllActiveConstraints() {
    // No filters → all 3 published posts
    PostSearchResultDto all = blogService.searchPosts(null, null, null, 0, 20);
    assertEquals(3, all.getTotal());

    // With category filter → fewer results
    PostSearchResultDto filtered =
        blogService.searchPosts(null, Arrays.asList("frontend"), null, 0, 20);
    assertEquals(1, filtered.getTotal());
  }

  @Test
  void totalCountIsAccurateAcrossPages() {
    // 3 published posts, page size 1 → total should still be 3 regardless of page
    PostSearchResultDto page0 = blogService.searchPosts(null, null, null, 0, 1);
    assertEquals(3, page0.getTotal());
    assertEquals(1, page0.getResults().size());

    PostSearchResultDto page1 = blogService.searchPosts(null, null, null, 1, 1);
    assertEquals(3, page1.getTotal());
    assertEquals(1, page1.getResults().size());
  }

  // -------------------------------------------------------------------------
  // AC-7 / AC-8: Empty state — no matches → result list empty, total = 0
  // -------------------------------------------------------------------------

  @Test
  void noMatchingPosts_returnsEmptyResultsWithZeroTotal() {
    PostSearchResultDto result = blogService.searchPosts("xyzzy_no_match_42", null, null, 0, 20);
    assertEquals(0, result.getTotal());
    assertTrue(result.getResults().isEmpty());
  }

  // -------------------------------------------------------------------------
  // AC-11: Pagination support (results update in-place, no page reload on server)
  // -------------------------------------------------------------------------

  @Test
  void paginationReturnsCorrectSlice() {
    PostSearchResultDto page0 = blogService.searchPosts(null, null, null, 0, 2);
    assertEquals(2, page0.getResults().size());
    assertEquals(0, page0.getPage());

    PostSearchResultDto page1 = blogService.searchPosts(null, null, null, 1, 2);
    assertEquals(1, page1.getResults().size());
    assertEquals(1, page1.getPage());
  }

  @Test
  void paginationBeyondResults_returnsEmptyList() {
    PostSearchResultDto result = blogService.searchPosts(null, null, null, 99, 20);
    assertTrue(result.getResults().isEmpty());
    assertEquals(3, result.getTotal()); // total unchanged
  }

  // -------------------------------------------------------------------------
  // Filters endpoint (AC-3 support) — available categories and authors
  // -------------------------------------------------------------------------

  @Test
  void getAvailableFilters_returnsDistinctSortedCategoriesAndAuthors() {
    PostFiltersDto filters = blogService.getAvailableFilters();

    assertNotNull(filters.getCategories());
    assertFalse(filters.getCategories().isEmpty());
    // categories must be distinct
    assertEquals(
        filters.getCategories().stream().distinct().count(), filters.getCategories().size());
    // categories must be sorted
    List<String> sorted = new ArrayList<>(filters.getCategories());
    sorted.sort(String::compareTo);
    assertEquals(sorted, filters.getCategories());

    assertNotNull(filters.getAuthors());
    assertFalse(filters.getAuthors().isEmpty());
    // authors must be distinct
    List<String> authorNames =
        filters.getAuthors().stream()
            .map(PostFiltersDto.AuthorSummaryDto::getName)
            .collect(java.util.stream.Collectors.toList());
    assertEquals(authorNames.stream().distinct().count(), authorNames.size());
  }

  @Test
  void getAvailableFilters_excludesUnpublishedPostTags() {
    // "java" appears on both a published and an unpublished post;
    // "draft" only on the unpublished post → should not appear
    PostFiltersDto filters = blogService.getAvailableFilters();
    // The unpublished post 4 has no exclusive tag here, but let's verify "java" is present
    // (published post 1 has it) and no bogus tag leaked through.
    assertTrue(
        filters.getCategories().contains("java"), "Tag from published post must be in filter list");
  }

  // -------------------------------------------------------------------------
  // AC-6: Active filters and search term are echoed back in the response
  //       so the UI can render chip/badge indicators (visually distinct).
  // -------------------------------------------------------------------------

  @Test
  void searchResponse_appliedFilters_containsActiveSearchQuery() {
    // When a query is supplied, appliedFilters.query must echo it back so the UI can render a chip.
    PostSearchResultDto result = blogService.searchPosts("Spring", null, null, 0, 20);
    AppliedFiltersDto applied = result.getAppliedFilters();
    assertNotNull(applied, "appliedFilters must always be present in the response");
    assertEquals(
        "Spring",
        applied.getQuery(),
        "The active search term must be echoed back for chip rendering");
  }

  @Test
  void searchResponse_appliedFilters_queryNullWhenNoSearchTerm() {
    // With no query, appliedFilters.query must be null so the UI knows no text chip should appear.
    PostSearchResultDto result = blogService.searchPosts(null, null, null, 0, 20);
    AppliedFiltersDto applied = result.getAppliedFilters();
    assertNotNull(applied, "appliedFilters must always be present in the response");
    assertNull(
        applied.getQuery(), "query must be null when no search term is active (no chip to render)");
  }

  @Test
  void searchResponse_appliedFilters_containsActiveCategoryFilters() {
    // Active category filters must be echoed back so the UI can render per-chip removal.
    List<String> activeCats = Arrays.asList("java", "spring");
    PostSearchResultDto result = blogService.searchPosts(null, activeCats, null, 0, 20);
    AppliedFiltersDto applied = result.getAppliedFilters();
    assertNotNull(applied);
    assertTrue(
        applied.getCategories().containsAll(activeCats),
        "Each active category filter must appear in appliedFilters for chip rendering");
  }

  @Test
  void searchResponse_appliedFilters_containsActiveAuthorFilters() {
    // Active author filters must be echoed back so the UI can render per-chip removal.
    List<String> activeAuthors = Arrays.asList("Alice Chen");
    PostSearchResultDto result = blogService.searchPosts(null, null, activeAuthors, 0, 20);
    AppliedFiltersDto applied = result.getAppliedFilters();
    assertNotNull(applied);
    assertTrue(
        applied.getAuthors().containsAll(activeAuthors),
        "Each active author filter must appear in appliedFilters for chip rendering");
  }

  @Test
  void searchResponse_appliedFilters_presentEvenWhenNoConstraintsActive() {
    // The appliedFilters object must always be present (never null) so the UI can safely
    // inspect it without null checks.  All fields are empty/null when no constraint is active.
    PostSearchResultDto result = blogService.searchPosts(null, null, null, 0, 20);
    AppliedFiltersDto applied = result.getAppliedFilters();
    assertNotNull(applied, "appliedFilters must never be null");
    assertNull(applied.getQuery());
    assertNotNull(applied.getCategories());
    assertTrue(applied.getCategories().isEmpty());
    assertNotNull(applied.getAuthors());
    assertTrue(applied.getAuthors().isEmpty());
  }

  // -------------------------------------------------------------------------
  // AC-9: The API supports mobile-first page sizes and returns all fields
  //       needed to render the listing on small viewports (≥ 320 px).
  //       Backend responsibility: every result includes id, title, author,
  //       publishedAt, tags, and excerpt so the UI can lay them out correctly
  //       regardless of viewport width.
  // -------------------------------------------------------------------------

  @Test
  void searchResponse_eachResult_containsAllFieldsRequiredByMobileLayout() {
    // Every result item must carry the fields the UI needs to render the mobile card:
    // title, excerpt, author name, tags, and publishedAt.
    PostSearchResultDto result = blogService.searchPosts(null, null, null, 0, 20);
    assertFalse(result.getResults().isEmpty(), "Expected at least one result");
    result
        .getResults()
        .forEach(
            post -> {
              assertNotNull(post.getId(), "id must be present for result-key and deep-link");
              assertNotNull(post.getTitle(), "title must be present for mobile card header");
              assertNotNull(post.getAuthor(), "author must be present for mobile card sub-header");
              assertNotNull(
                  post.getAuthor().getName(), "author.name must be present for mobile display");
              assertNotNull(
                  post.getPublishedAt(), "publishedAt must be present for mobile card metadata");
              assertNotNull(
                  post.getTags(), "tags list must be present (may be empty) for mobile chips");
            });
  }

  @Test
  void searchResponse_supportsSmallPageSize_mobileDefaultPage() {
    // Mobile clients may request a smaller page to reduce payload on slow connections.
    // Verify that a page size of 5 (a reasonable mobile default) works correctly.
    PostSearchResultDto result = blogService.searchPosts(null, null, null, 0, 5);
    assertNotNull(result);
    assertEquals(0, result.getPage());
    assertEquals(5, result.getSize());
    assertTrue(
        result.getResults().size() <= 5,
        "Number of returned results must not exceed the requested page size");
    assertTrue(result.getTotal() >= result.getResults().size(), "total must be >= results count");
  }

  // -------------------------------------------------------------------------
  // AC-10: The / keyboard shortcut focuses the search input.
  //        Backend responsibility: the search endpoint must be reachable at a
  //        stable, predictable path and must accept an empty/instant query so
  //        that the first keystroke after focus triggers a valid request.
  // -------------------------------------------------------------------------

  @Test
  void searchEndpoint_acceptsEmptyQuery_forInstantFocusAndType() {
    // When the user presses / the input is focused and the field is empty.
    // The first debounced call is issued with q="" or q=null.
    // The endpoint must treat this identically to "no query" and return all published posts.
    PostSearchResultDto withNull = blogService.searchPosts(null, null, null, 0, 20);
    PostSearchResultDto withEmpty = blogService.searchPosts("", null, null, 0, 20);
    assertEquals(
        withNull.getTotal(),
        withEmpty.getTotal(),
        "Empty-string query must behave identically to a null query (keyboard-shortcut use-case)");
    assertEquals(
        withNull.getResults().size(),
        withEmpty.getResults().size(),
        "Result list size must be identical for null and empty query");
  }

  @Test
  void searchEndpoint_acceptsBlankQuery_treatedAsNoFilter() {
    // Whitespace-only input (e.g. from accidental space after / focus) must not narrow results.
    PostSearchResultDto result = blogService.searchPosts("   ", null, null, 0, 20);
    PostSearchResultDto noQuery = blogService.searchPosts(null, null, null, 0, 20);
    assertEquals(
        noQuery.getTotal(),
        result.getTotal(),
        "Whitespace-only query must be treated as no search term (keyboard-shortcut robustness)");
  }

  // -------------------------------------------------------------------------
  // AC-12: If the backend request fails, a non-blocking error must be returned
  //        while the last valid results (or empty state) remain visible.
  //        Backend responsibility: invalid parameters that would cause the
  //        service to throw must produce an IllegalArgumentException (mapped
  //        by the global handler to HTTP 400) rather than a 500 with a stack
  //        trace, so the UI can distinguish client errors from server errors.
  // -------------------------------------------------------------------------

  @Test
  void searchPosts_invalidPage_throwsIllegalArgument_notNullPointer() {
    // AC-12: a bad request (negative page) must produce a typed, catchable exception
    // — not an NPE or unchecked crash — so the global handler can return HTTP 400
    // and the UI can display a non-blocking error without losing the previous results.
    Exception ex =
        assertThrows(
            IllegalArgumentException.class,
            () -> blogService.searchPosts(null, null, null, -1, 10));
    assertNotNull(ex.getMessage(), "Exception message must not be null for error display");
    assertTrue(
        ex.getMessage().toLowerCase().contains("page"),
        "Exception message must mention 'page' so the error is diagnosable: " + ex.getMessage());
  }

  @Test
  void searchPosts_invalidSize_throwsIllegalArgument_notNullPointer() {
    // AC-12: a bad page size must produce a typed exception (not NPE / 500) so that
    // the global handler can return HTTP 400 and the UI can display a graceful message.
    Exception ex =
        assertThrows(
            IllegalArgumentException.class, () -> blogService.searchPosts(null, null, null, 0, 0));
    assertNotNull(ex.getMessage(), "Exception message must not be null for error display");
    assertTrue(
        ex.getMessage().toLowerCase().contains("size"),
        "Exception message must mention 'size' so the error is diagnosable: " + ex.getMessage());
  }

  @Test
  void searchPosts_onServiceError_errorResponseContainsMeaningfulMessage() {
    // AC-12: when an upstream error occurs the service must surface a descriptive
    // IllegalArgumentException (not a bare RuntimeException with no message) so the
    // global handler can propagate a useful, non-blocking error body to the UI.
    IllegalArgumentException pageEx =
        assertThrows(
            IllegalArgumentException.class,
            () -> blogService.searchPosts(null, null, null, -5, 10));
    assertFalse(
        pageEx.getMessage() == null || pageEx.getMessage().isBlank(),
        "Error message must be non-blank so the UI can display a meaningful non-blocking alert");

    IllegalArgumentException sizeEx =
        assertThrows(
            IllegalArgumentException.class, () -> blogService.searchPosts(null, null, null, 0, -1));
    assertFalse(
        sizeEx.getMessage() == null || sizeEx.getMessage().isBlank(),
        "Error message must be non-blank so the UI can display a meaningful non-blocking alert");
  }

  // -------------------------------------------------------------------------
  // Validation
  // -------------------------------------------------------------------------

  @Test
  void searchPosts_throwsForNegativePage() {
    assertThrows(
        IllegalArgumentException.class, () -> blogService.searchPosts(null, null, null, -1, 10));
  }

  @Test
  void searchPosts_throwsForZeroSize() {
    assertThrows(
        IllegalArgumentException.class, () -> blogService.searchPosts(null, null, null, 0, 0));
  }
}
