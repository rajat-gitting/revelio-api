package com.revelio.api.service;

import com.revelio.api.dto.BlogSummaryDto;
import com.revelio.api.dto.FiltersDto;
import com.revelio.api.dto.PostSearchResponse;
import com.revelio.api.model.Blog;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

/**
 * Provides in-memory full-text search and multi-filter capability over the blog post collection.
 *
 * <p>Searches across: title, excerpt (body proxy), tags. Supports filtering by category/tag and
 * author name. Results are paginated.
 *
 * <p>NOTE: This implementation uses in-memory search because the project does not yet have a
 * MongoDB persistence layer. The method signatures and response shapes match the agreed API
 * contract so that a MongoDB / Atlas Search upgrade can be made transparently later.
 */
@Service
public class BlogSearchService {

  private final BlogService blogService;

  public BlogSearchService(BlogService blogService) {
    this.blogService = blogService;
  }

  /**
   * Search published posts by full-text query and/or filter by categories and author name.
   *
   * @param q free-text query (searched in title, excerpt, tags); blank means "all"
   * @param categories list of category/tag values to filter on (AND logic within the set — actually
   *     OR across provided tags; a post matches if it has ANY of the given tags)
   * @param authors list of author names to filter on (a post matches if its author is in the list)
   * @param page zero-based page index
   * @param size page size
   * @return paginated search response with total count
   */
  public PostSearchResponse search(
      String q, List<String> categories, List<String> authors, int page, int size) {
    if (page < 0) throw new IllegalArgumentException("Page number must be non-negative");
    if (size <= 0) throw new IllegalArgumentException("Page size must be positive");

    // Sanitise inputs — normalise to lowercase trimmed strings to prevent injection and ensure
    // consistent matching.
    String query = (q == null) ? "" : q.trim().toLowerCase(Locale.ROOT);
    List<String> normCategories =
        (categories == null)
            ? new ArrayList<>()
            : categories.stream()
                .filter(c -> c != null && !c.isBlank())
                .map(c -> c.trim().toLowerCase(Locale.ROOT))
                .collect(Collectors.toList());
    List<String> normAuthors =
        (authors == null)
            ? new ArrayList<>()
            : authors.stream()
                .filter(a -> a != null && !a.isBlank())
                .map(a -> a.trim().toLowerCase(Locale.ROOT))
                .collect(Collectors.toList());

    // Start from all published posts sorted newest-first.
    List<Blog> allPublished =
        blogService.getPublishedBlogs(0, Integer.MAX_VALUE).stream()
            .sorted(Comparator.comparing(Blog::getPublishedAt).reversed())
            .collect(Collectors.toList());

    // Apply filters.
    List<Blog> filtered =
        allPublished.stream()
            .filter(blog -> matchesQuery(blog, query))
            .filter(blog -> matchesCategories(blog, normCategories))
            .filter(blog -> matchesAuthors(blog, normAuthors))
            .collect(Collectors.toList());

    long total = filtered.size();

    // Paginate.
    int start = page * size;
    List<Blog> paginated;
    if (start >= filtered.size()) {
      paginated = new ArrayList<>();
    } else {
      paginated = filtered.subList(start, Math.min(start + size, filtered.size()));
    }

    List<BlogSummaryDto> results =
        paginated.stream().map(BlogSummaryDto::fromBlog).collect(Collectors.toList());

    return new PostSearchResponse(total, page, size, results);
  }

  /**
   * Returns the distinct author names and category/tag values for all published posts. Used to
   * populate the filter dropdowns on the client.
   */
  public FiltersDto getFilters() {
    List<Blog> allPublished = blogService.getPublishedBlogs(0, Integer.MAX_VALUE);

    List<String> distinctAuthors =
        allPublished.stream()
            .filter(b -> b.getAuthor() != null && b.getAuthor().getName() != null)
            .map(b -> b.getAuthor().getName())
            .distinct()
            .sorted()
            .collect(Collectors.toList());

    List<String> distinctCategories =
        allPublished.stream()
            .filter(b -> b.getTags() != null)
            .flatMap(b -> b.getTags().stream())
            .filter(t -> t != null && !t.isBlank())
            .distinct()
            .sorted()
            .collect(Collectors.toList());

    return new FiltersDto(distinctAuthors, distinctCategories);
  }

  // ---------------------------------------------------------------------------
  // Private helpers
  // ---------------------------------------------------------------------------

  private boolean matchesQuery(Blog blog, String query) {
    if (query.isBlank()) return true;
    // Search in title
    if (blog.getTitle() != null && blog.getTitle().toLowerCase(Locale.ROOT).contains(query)) {
      return true;
    }
    // Search in excerpt (body proxy)
    if (blog.getExcerpt() != null && blog.getExcerpt().toLowerCase(Locale.ROOT).contains(query)) {
      return true;
    }
    // Search in tags
    if (blog.getTags() != null) {
      for (String tag : blog.getTags()) {
        if (tag != null && tag.toLowerCase(Locale.ROOT).contains(query)) {
          return true;
        }
      }
    }
    return false;
  }

  private boolean matchesCategories(Blog blog, List<String> normCategories) {
    if (normCategories.isEmpty()) return true;
    if (blog.getTags() == null) return false;
    // Post must contain at least one of the requested categories/tags.
    return blog.getTags().stream()
        .filter(t -> t != null)
        .map(t -> t.toLowerCase(Locale.ROOT))
        .anyMatch(normCategories::contains);
  }

  private boolean matchesAuthors(Blog blog, List<String> normAuthors) {
    if (normAuthors.isEmpty()) return true;
    if (blog.getAuthor() == null || blog.getAuthor().getName() == null) return false;
    return normAuthors.contains(blog.getAuthor().getName().toLowerCase(Locale.ROOT));
  }
}
