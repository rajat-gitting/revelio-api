package com.revelio.api.service;

import static org.junit.jupiter.api.Assertions.*;

import com.revelio.api.dto.BlogSummaryDto;
import com.revelio.api.dto.FiltersDto;
import com.revelio.api.dto.PostSearchResponse;
import com.revelio.api.model.Blog;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link BlogSearchService}.
 *
 * <p>Covers all acceptance criteria defined in CR-23:
 *
 * <ul>
 *   <li>AC-2: query searches title, excerpt (body), and tags
 *   <li>AC-3: category/tag multi-select and author filter
 *   <li>AC-4: combined filter + query constraints
 *   <li>AC-5: total result count returned
 *   <li>AC-7: clear all / individual filter removal (service-layer contract)
 *   <li>AC-8: empty-state result set when no match
 *   <li>AC-11: in-place update (no reload needed — service returns data, not redirects)
 *   <li>AC-12: service does not throw on errors that are not logic errors
 * </ul>
 */
class BlogSearchServiceTest {

  private BlogSearchService blogSearchService;
  private List<Blog> testBlogs;

  @BeforeEach
  void setUp() {
    testBlogs = new ArrayList<>();

    Blog.Author alice = new Blog.Author("Alice Chen", null);
    Blog.Author bob = new Blog.Author("Bob Smith", null);

    // id=1: title matches "spring boot", tags: java, spring
    testBlogs.add(
        new Blog(
            1L,
            "Getting Started with Spring Boot",
            "A beginner-friendly guide to building REST APIs.",
            null,
            alice,
            Arrays.asList("java", "spring"),
            Instant.parse("2024-01-10T10:00:00Z"),
            true));

    // id=2: title/excerpt matches "react", tags: react, frontend
    testBlogs.add(
        new Blog(
            2L,
            "React Query vs SWR",
            "Comparing data-fetching libraries for React.",
            null,
            bob,
            Arrays.asList("react", "frontend"),
            Instant.parse("2024-01-20T10:00:00Z"),
            true));

    // id=3: tags: api, design
    testBlogs.add(
        new Blog(
            3L,
            "Designing RESTful APIs",
            "Best practices for endpoint naming.",
            null,
            alice,
            Arrays.asList("api", "design"),
            Instant.parse("2024-01-15T10:00:00Z"),
            true));

    // id=4: UNPUBLISHED — must never appear in results
    testBlogs.add(
        new Blog(
            4L,
            "Draft Post about java",
            "This post is not published yet.",
            null,
            alice,
            Arrays.asList("java"),
            Instant.parse("2024-01-25T10:00:00Z"),
            false));

    // id=5: tags: css, frontend
    testBlogs.add(
        new Blog(
            5L,
            "CSS Grid Layouts",
            "Mastering two-dimensional layouts with CSS Grid.",
            null,
            bob,
            Arrays.asList("css", "frontend"),
            Instant.parse("2024-01-05T10:00:00Z"),
            true));

    BlogService blogService = new BlogService(testBlogs);
    blogSearchService = new BlogSearchService(blogService);
  }

  // -------------------------------------------------------------------------
  // AC-2: search queries title, body (excerpt), and tags
  // -------------------------------------------------------------------------

  /** AC-2: Searching by a word that appears in the title returns matching posts. */
  @Test
  void testSearchByTitleKeyword() {
    PostSearchResponse response = blogSearchService.search("React", null, null, 0, 20);

    assertEquals(1, response.getTotal());
    assertEquals("React Query vs SWR", response.getResults().get(0).getTitle());
  }

  /** AC-2: Searching by a word that appears in the excerpt/body returns matching posts. */
  @Test
  void testSearchByExcerptKeyword() {
    PostSearchResponse response = blogSearchService.search("endpoint", null, null, 0, 20);

    assertEquals(1, response.getTotal());
    assertEquals("Designing RESTful APIs", response.getResults().get(0).getTitle());
  }

  /** AC-2: Searching by a tag value returns matching posts (tag search). */
  @Test
  void testSearchByTagKeyword() {
    PostSearchResponse response = blogSearchService.search("spring", null, null, 0, 20);

    // "spring" appears in title AND is a tag for post 1
    assertEquals(1, response.getTotal());
    assertEquals(1L, response.getResults().get(0).getId());
  }

  /** AC-2: Search is case-insensitive. */
  @Test
  void testSearchIsCaseInsensitive() {
    PostSearchResponse upperCase = blogSearchService.search("REACT", null, null, 0, 20);
    PostSearchResponse lowerCase = blogSearchService.search("react", null, null, 0, 20);

    assertEquals(upperCase.getTotal(), lowerCase.getTotal());
    assertEquals(upperCase.getResults().size(), lowerCase.getResults().size());
  }

  /** AC-2: Blank query returns all published posts (no text filter applied). */
  @Test
  void testBlankQueryReturnsAllPublishedPosts() {
    PostSearchResponse response = blogSearchService.search("", null, null, 0, 20);

    // 4 published posts (id=4 is unpublished)
    assertEquals(4, response.getTotal());
  }

  /** AC-2: Null query behaves identically to blank query. */
  @Test
  void testNullQueryReturnsAllPublishedPosts() {
    PostSearchResponse response = blogSearchService.search(null, null, null, 0, 20);

    assertEquals(4, response.getTotal());
  }

  // -------------------------------------------------------------------------
  // AC-3: Category/Tag multi-select filter and Author filter
  // -------------------------------------------------------------------------

  /** AC-3: Single category filter narrows results to posts with that tag. */
  @Test
  void testFilterBySingleCategory() {
    PostSearchResponse response =
        blogSearchService.search("", Arrays.asList("frontend"), null, 0, 20);

    // posts 2 (react, frontend) and 5 (css, frontend)
    assertEquals(2, response.getTotal());
    assertTrue(
        response.getResults().stream()
            .allMatch(r -> r.getCategories() != null && r.getCategories().contains("frontend")));
  }

  /** AC-3: Multi-value category filter returns posts matching ANY of the categories (OR). */
  @Test
  void testFilterByMultipleCategories() {
    PostSearchResponse response =
        blogSearchService.search("", Arrays.asList("java", "api"), null, 0, 20);

    // post 1 (java, spring), post 3 (api, design)
    assertEquals(2, response.getTotal());
  }

  /** AC-3: Author filter (single author) returns only posts by that author. */
  @Test
  void testFilterBySingleAuthor() {
    PostSearchResponse response =
        blogSearchService.search("", null, Arrays.asList("Alice Chen"), 0, 20);

    // posts 1 and 3 (Alice Chen)
    assertEquals(2, response.getTotal());
    assertTrue(
        response.getResults().stream()
            .allMatch(r -> r.getAuthor() != null && "Alice Chen".equals(r.getAuthor().getName())));
  }

  /** AC-3: Author filter with multiple authors returns posts from any of the listed authors. */
  @Test
  void testFilterByMultipleAuthors() {
    PostSearchResponse response =
        blogSearchService.search("", null, Arrays.asList("Alice Chen", "Bob Smith"), 0, 20);

    assertEquals(4, response.getTotal());
  }

  /** AC-3: Author filter is case-insensitive. */
  @Test
  void testAuthorFilterCaseInsensitive() {
    PostSearchResponse lower =
        blogSearchService.search("", null, Arrays.asList("alice chen"), 0, 20);
    PostSearchResponse upper =
        blogSearchService.search("", null, Arrays.asList("ALICE CHEN"), 0, 20);

    assertEquals(lower.getTotal(), upper.getTotal());
  }

  // -------------------------------------------------------------------------
  // AC-4: Combined filters and query applied simultaneously
  // -------------------------------------------------------------------------

  /** AC-4: Combining a text query with a category filter returns only posts satisfying both. */
  @Test
  void testCombinedQueryAndCategoryFilter() {
    // q="guide" only matches post 1 (excerpt); category "java" also matches post 1.
    PostSearchResponse response =
        blogSearchService.search("guide", Arrays.asList("java"), null, 0, 20);

    assertEquals(1, response.getTotal());
    assertEquals(1L, response.getResults().get(0).getId());
  }

  /** AC-4: Combining query with author filter applies both constraints. */
  @Test
  void testCombinedQueryAndAuthorFilter() {
    // q="Grid" matches post 5 (title/excerpt); author Bob Smith wrote post 5.
    PostSearchResponse response =
        blogSearchService.search("Grid", null, Arrays.asList("Bob Smith"), 0, 20);

    assertEquals(1, response.getTotal());
    assertEquals(5L, response.getResults().get(0).getId());
  }

  /** AC-4: Conflicting query+category combination yields zero results — not an error. */
  @Test
  void testCombinedQueryAndCategoryFilterNoMatch() {
    // q="React" matches post 2, but category "java" does not match post 2.
    PostSearchResponse response =
        blogSearchService.search("React", Arrays.asList("java"), null, 0, 20);

    assertEquals(0, response.getTotal());
    assertTrue(response.getResults().isEmpty());
  }

  // -------------------------------------------------------------------------
  // AC-5: Total count returned and matches actual results
  // -------------------------------------------------------------------------

  /** AC-5: The total field reflects the full matched count regardless of page size. */
  @Test
  void testTotalCountReflectsAllMatches() {
    // 4 published posts; request page 0 size 2 → total should still be 4.
    PostSearchResponse response = blogSearchService.search("", null, null, 0, 2);

    assertEquals(4, response.getTotal());
    assertEquals(2, response.getResults().size());
  }

  /** AC-5: Total updates when filters are active. */
  @Test
  void testTotalCountUpdatesWithFilters() {
    PostSearchResponse all = blogSearchService.search("", null, null, 0, 20);
    PostSearchResponse filtered = blogSearchService.search("", Arrays.asList("java"), null, 0, 20);

    assertTrue(filtered.getTotal() < all.getTotal());
  }

  /** AC-5: Total is zero when no results match. */
  @Test
  void testTotalIsZeroWhenNoMatchFound() {
    PostSearchResponse response =
        blogSearchService.search("xyzzy_no_match_ever", null, null, 0, 20);

    assertEquals(0, response.getTotal());
  }

  // -------------------------------------------------------------------------
  // AC-7: Clear all / individual removal (service-level contract)
  // -------------------------------------------------------------------------

  /**
   * AC-7: Passing empty/null query and empty/null filters is equivalent to "clear all" — returns
   * all published posts.
   */
  @Test
  void testClearAllReturnsAllPublishedPosts() {
    // First apply filters
    PostSearchResponse withFilters =
        blogSearchService.search(
            "React", Arrays.asList("frontend"), Arrays.asList("Bob Smith"), 0, 20);

    // Then "clear all" by passing empty params
    PostSearchResponse cleared = blogSearchService.search(null, null, null, 0, 20);

    assertEquals(4, cleared.getTotal());
    assertTrue(cleared.getTotal() > withFilters.getTotal());
  }

  /**
   * AC-7: Removing an individual filter (e.g. category) while keeping query intact returns broader
   * results.
   */
  @Test
  void testRemovingIndividualCategoryFilterBroadensResults() {
    PostSearchResponse withCategory =
        blogSearchService.search("", Arrays.asList("java"), null, 0, 20);
    PostSearchResponse withoutCategory = blogSearchService.search("", null, null, 0, 20);

    assertTrue(withoutCategory.getTotal() > withCategory.getTotal());
  }

  // -------------------------------------------------------------------------
  // AC-8: Empty state — no results, user-friendly data for UI
  // -------------------------------------------------------------------------

  /** AC-8: No results returns total=0 and empty results list (service side of empty state). */
  @Test
  void testEmptyStateWhenNoPostsMatch() {
    PostSearchResponse response =
        blogSearchService.search("impossible_search_term_xyz", null, null, 0, 20);

    assertEquals(0, response.getTotal());
    assertNotNull(response.getResults());
    assertTrue(response.getResults().isEmpty());
  }

  /** AC-8: Unpublished posts are never returned, even when they would match the query. */
  @Test
  void testUnpublishedPostsAreNeverReturned() {
    // Post id=4 is unpublished and has title "Draft Post about java"
    PostSearchResponse response = blogSearchService.search("Draft", null, null, 0, 20);

    assertEquals(0, response.getTotal());
  }

  // -------------------------------------------------------------------------
  // AC-9: Mobile-first — not directly testable in a unit test (UI concern)
  // -------------------------------------------------------------------------

  // -------------------------------------------------------------------------
  // AC-11: No full-page reload — results update in-place (service returns data, not redirects)
  // -------------------------------------------------------------------------

  /** AC-11: Service returns data structures (not HTTP redirects), enabling in-place UI updates. */
  @Test
  void testSearchReturnsDataNotRedirect() {
    PostSearchResponse response = blogSearchService.search("spring", null, null, 0, 20);

    assertNotNull(response);
    assertNotNull(response.getResults());
    // The result carries page info for the client to render without reload.
    assertEquals(0, response.getPage());
    assertTrue(response.getSize() > 0);
  }

  // -------------------------------------------------------------------------
  // AC-12: Backend error handling — service-level robustness
  // -------------------------------------------------------------------------

  /** AC-12: Null category list is handled gracefully (no NPE). */
  @Test
  void testNullCategoryListHandledGracefully() {
    assertDoesNotThrow(() -> blogSearchService.search("spring", null, null, 0, 20));
  }

  /** AC-12: Empty category list is handled gracefully. */
  @Test
  void testEmptyCategoryListHandledGracefully() {
    assertDoesNotThrow(
        () -> blogSearchService.search("spring", new ArrayList<>(), new ArrayList<>(), 0, 20));
  }

  /** AC-12: Invalid pagination throws a clear IllegalArgumentException. */
  @Test
  void testNegativePageThrows() {
    assertThrows(
        IllegalArgumentException.class, () -> blogSearchService.search("", null, null, -1, 20));
  }

  /** AC-12: Zero size throws a clear IllegalArgumentException. */
  @Test
  void testZeroSizeThrows() {
    assertThrows(
        IllegalArgumentException.class, () -> blogSearchService.search("", null, null, 0, 0));
  }

  // -------------------------------------------------------------------------
  // Filters endpoint (AC-3 / supporting)
  // -------------------------------------------------------------------------

  /** getFilters returns non-null authors and categories lists. */
  @Test
  void testGetFiltersReturnsNonNullLists() {
    FiltersDto filters = blogSearchService.getFilters();

    assertNotNull(filters.getAuthors());
    assertNotNull(filters.getCategories());
  }

  /** getFilters returns distinct, sorted author names from published posts only. */
  @Test
  void testGetFiltersAuthorsAreDistinctAndSorted() {
    FiltersDto filters = blogSearchService.getFilters();

    List<String> authors = filters.getAuthors();
    assertEquals(2, authors.size());
    // Alphabetically: Alice Chen, Bob Smith
    assertEquals("Alice Chen", authors.get(0));
    assertEquals("Bob Smith", authors.get(1));
  }

  /** getFilters returns distinct, sorted category/tag values from published posts only. */
  @Test
  void testGetFiltersCategoriesAreDistinctAndSorted() {
    FiltersDto filters = blogSearchService.getFilters();

    List<String> categories = filters.getCategories();
    assertFalse(categories.isEmpty());
    // Verify distinct: no duplicates
    long distinctCount = categories.stream().distinct().count();
    assertEquals(distinctCount, categories.size());
    // Verify sorted
    for (int i = 0; i < categories.size() - 1; i++) {
      assertTrue(categories.get(i).compareTo(categories.get(i + 1)) <= 0);
    }
  }

  /** getFilters does not include tags from unpublished posts. */
  @Test
  void testGetFiltersExcludesUnpublishedPostData() {
    // Post id=4 is unpublished; it has tag "java" which also appears in published post id=1.
    // But let's add a unique tag to the unpublished post and verify it doesn't appear.
    List<Blog> blogsWithUniqueUnpublishedTag = new ArrayList<>(testBlogs);
    Blog.Author alice = new Blog.Author("Alice Chen", null);
    blogsWithUniqueUnpublishedTag.add(
        new Blog(
            99L,
            "Secret Draft",
            "Secret content",
            null,
            alice,
            Arrays.asList("secret_tag_xyz"),
            Instant.parse("2024-02-01T10:00:00Z"),
            false));

    BlogService serviceWithExtra = new BlogService(blogsWithUniqueUnpublishedTag);
    BlogSearchService searchServiceWithExtra = new BlogSearchService(serviceWithExtra);

    FiltersDto filters = searchServiceWithExtra.getFilters();
    assertFalse(filters.getCategories().contains("secret_tag_xyz"));
  }

  // -------------------------------------------------------------------------
  // Pagination
  // -------------------------------------------------------------------------

  /** Pagination: second page returns the correct slice. */
  @Test
  void testPaginationSecondPage() {
    PostSearchResponse page0 = blogSearchService.search("", null, null, 0, 2);
    PostSearchResponse page1 = blogSearchService.search("", null, null, 1, 2);

    assertEquals(4, page0.getTotal());
    assertEquals(4, page1.getTotal());
    assertEquals(2, page0.getResults().size());
    assertEquals(2, page1.getResults().size());

    // No overlap
    List<Long> page0Ids =
        page0.getResults().stream()
            .map(BlogSummaryDto::getId)
            .collect(java.util.stream.Collectors.toList());
    List<Long> page1Ids =
        page1.getResults().stream()
            .map(BlogSummaryDto::getId)
            .collect(java.util.stream.Collectors.toList());
    for (Long id : page1Ids) {
      assertFalse(page0Ids.contains(id));
    }
  }

  /** Pagination: page beyond available data returns empty results but correct total. */
  @Test
  void testPaginationBeyondAvailableDataReturnsEmptyResults() {
    PostSearchResponse response = blogSearchService.search("", null, null, 99, 20);

    assertEquals(4, response.getTotal());
    assertTrue(response.getResults().isEmpty());
  }

  // -------------------------------------------------------------------------
  // BlogSummaryDto shape (AC-1 / API contract)
  // -------------------------------------------------------------------------

  /** Each result contains the required fields: id, title, slug, author, categories, publishedAt. */
  @Test
  void testSearchResultContainsRequiredFields() {
    PostSearchResponse response = blogSearchService.search("Spring Boot", null, null, 0, 20);

    assertFalse(response.getResults().isEmpty());
    BlogSummaryDto dto = response.getResults().get(0);

    assertNotNull(dto.getId());
    assertNotNull(dto.getTitle());
    assertNotNull(dto.getSlug());
    assertNotNull(dto.getAuthor());
    assertNotNull(dto.getAuthor().getName());
    assertNotNull(dto.getCategories());
    assertNotNull(dto.getPublishedAt());
  }

  /** Slug is derived from title (lowercase, spaces replaced with hyphens). */
  @Test
  void testSlugIsDerivedFromTitle() {
    PostSearchResponse response = blogSearchService.search("CSS Grid", null, null, 0, 20);

    assertFalse(response.getResults().isEmpty());
    BlogSummaryDto dto = response.getResults().get(0);
    assertEquals("css-grid-layouts", dto.getSlug());
  }
}
