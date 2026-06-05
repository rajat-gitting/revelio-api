package com.revelio.api.service;

import com.revelio.api.dto.BlogResponseDto;
import com.revelio.api.dto.PagedResponse;
import com.revelio.api.dto.PostFiltersDto;
import com.revelio.api.dto.PostFiltersDto.AuthorSummaryDto;
import com.revelio.api.dto.PostSearchResultDto;
import com.revelio.api.dto.PostSearchResultDto.AppliedFiltersDto;
import com.revelio.api.model.Blog;
import com.revelio.api.model.Blog.Author;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class BlogService {

  private final List<Blog> blogRepository;

  public BlogService() {
    this.blogRepository = seedData();
  }

  public BlogService(List<Blog> blogRepository) {
    this.blogRepository = blogRepository != null ? blogRepository : new ArrayList<>();
  }

  public List<Blog> getPublishedBlogs(int page, int size) {
    if (page < 0) throw new IllegalArgumentException("Page number must be non-negative");
    if (size <= 0) throw new IllegalArgumentException("Page size must be positive");

    List<Blog> sorted =
        blogRepository.stream()
            .filter(Blog::isPublished)
            .sorted(Comparator.comparing(Blog::getPublishedAt).reversed())
            .collect(Collectors.toList());

    int start = page * size;
    if (start >= sorted.size()) return new ArrayList<>();
    return new ArrayList<>(sorted.subList(start, Math.min(start + size, sorted.size())));
  }

  /**
   * Returns a {@link PagedResponse} of {@link BlogResponseDto} for the given page and size,
   * including pagination metadata (totalElements, totalPages, number, size).
   *
   * <p>Validation rules (also enforced here so the controller stays thin):
   *
   * <ul>
   *   <li>{@code page} must be &gt;= 0
   *   <li>{@code size} must be between 1 and 100 (inclusive)
   * </ul>
   *
   * <p>If {@code page} &gt;= {@code totalPages}, an empty {@code content} is returned with valid
   * metadata (AC-3 default behaviour: empty content, not HTTP 400).
   */
  public PagedResponse<BlogResponseDto> getPublishedBlogsPaged(int page, int size) {
    if (page < 0) throw new IllegalArgumentException("Page number must be non-negative");
    if (size < 1 || size > 100)
      throw new IllegalArgumentException("Page size must be between 1 and 100");

    List<Blog> sorted =
        blogRepository.stream()
            .filter(Blog::isPublished)
            .sorted(Comparator.comparing(Blog::getPublishedAt).reversed())
            .collect(Collectors.toList());

    long totalElements = sorted.size();
    int start = page * size;
    List<BlogResponseDto> content;
    if (start >= sorted.size()) {
      content = new ArrayList<>();
    } else {
      content =
          sorted.subList(start, Math.min(start + size, sorted.size())).stream()
              .map(BlogResponseDto::fromBlog)
              .collect(Collectors.toList());
    }

    return PagedResponse.of(content, totalElements, page, size);
  }

  public List<Blog> filterPublishedPosts(List<Blog> blogs) {
    if (blogs == null) return new ArrayList<>();
    return blogs.stream().filter(Blog::isPublished).collect(Collectors.toList());
  }

  /**
   * Search and filter published blog posts.
   *
   * <p>The {@code q} parameter is matched case-insensitively against the post title, excerpt
   * (body), and tags. {@code categories} is a list of tag values; a post matches if it has at least
   * one of the requested tags. {@code authors} is a list of author names; a post matches if its
   * author name is among those requested. All active constraints are ANDed together.
   */
  public PostSearchResultDto searchPosts(
      String q, List<String> categories, List<String> authors, int page, int size) {
    if (page < 0) throw new IllegalArgumentException("Page number must be non-negative");
    if (size <= 0) throw new IllegalArgumentException("Page size must be positive");

    String normalizedQ = (q == null) ? "" : q.trim().toLowerCase(Locale.ROOT);
    List<String> normalizedCategories =
        (categories == null)
            ? new ArrayList<>()
            : categories.stream()
                .filter(c -> c != null && !c.isBlank())
                .map(c -> c.toLowerCase(Locale.ROOT))
                .collect(Collectors.toList());
    List<String> normalizedAuthors =
        (authors == null)
            ? new ArrayList<>()
            : authors.stream()
                .filter(a -> a != null && !a.isBlank())
                .map(a -> a.toLowerCase(Locale.ROOT))
                .collect(Collectors.toList());

    List<Blog> filtered =
        blogRepository.stream()
            .filter(Blog::isPublished)
            .filter(blog -> matchesQuery(blog, normalizedQ))
            .filter(blog -> matchesCategories(blog, normalizedCategories))
            .filter(blog -> matchesAuthors(blog, normalizedAuthors))
            .sorted(Comparator.comparing(Blog::getPublishedAt).reversed())
            .collect(Collectors.toList());

    long total = filtered.size();
    int start = page * size;
    List<Blog> pageSlice;
    if (start >= filtered.size()) {
      pageSlice = new ArrayList<>();
    } else {
      pageSlice = new ArrayList<>(filtered.subList(start, Math.min(start + size, filtered.size())));
    }

    List<com.revelio.api.dto.BlogResponseDto> results =
        pageSlice.stream()
            .map(com.revelio.api.dto.BlogResponseDto::fromBlog)
            .collect(Collectors.toList());

    // AC-6: Echo the active constraints back so the UI can render chip/badge indicators for each
    // applied filter or search term, allowing users to see — at a glance — what is active and
    // remove individual constraints or use 'Clear all'.
    AppliedFiltersDto appliedFilters =
        new AppliedFiltersDto(
            (q == null || q.isBlank()) ? null : q.trim(),
            categories == null ? new ArrayList<>() : new ArrayList<>(categories),
            authors == null ? new ArrayList<>() : new ArrayList<>(authors));

    return new PostSearchResultDto(total, page, size, results, appliedFilters);
  }

  /** Returns the distinct set of tags (categories) and author names across all published posts. */
  public PostFiltersDto getAvailableFilters() {
    List<String> categories =
        blogRepository.stream()
            .filter(Blog::isPublished)
            .flatMap(
                blog ->
                    blog.getTags() == null
                        ? java.util.stream.Stream.empty()
                        : blog.getTags().stream())
            .filter(tag -> tag != null && !tag.isBlank())
            .distinct()
            .sorted()
            .collect(Collectors.toList());

    List<AuthorSummaryDto> authors =
        blogRepository.stream()
            .filter(Blog::isPublished)
            .map(Blog::getAuthor)
            .filter(
                author -> author != null && author.getName() != null && !author.getName().isBlank())
            .map(author -> author.getName())
            .distinct()
            .sorted()
            .map(AuthorSummaryDto::new)
            .collect(Collectors.toList());

    return new PostFiltersDto(categories, authors);
  }

  // -------------------------------------------------------------------------
  // private helpers
  // -------------------------------------------------------------------------

  private boolean matchesQuery(Blog blog, String normalizedQ) {
    if (normalizedQ.isEmpty()) return true;
    if (blog.getTitle() != null && blog.getTitle().toLowerCase(Locale.ROOT).contains(normalizedQ)) {
      return true;
    }
    if (blog.getExcerpt() != null
        && blog.getExcerpt().toLowerCase(Locale.ROOT).contains(normalizedQ)) {
      return true;
    }
    if (blog.getTags() != null) {
      for (String tag : blog.getTags()) {
        if (tag != null && tag.toLowerCase(Locale.ROOT).contains(normalizedQ)) {
          return true;
        }
      }
    }
    return false;
  }

  private boolean matchesCategories(Blog blog, List<String> normalizedCategories) {
    if (normalizedCategories.isEmpty()) return true;
    if (blog.getTags() == null) return false;
    for (String tag : blog.getTags()) {
      if (tag != null && normalizedCategories.contains(tag.toLowerCase(Locale.ROOT))) {
        return true;
      }
    }
    return false;
  }

  private boolean matchesAuthors(Blog blog, List<String> normalizedAuthors) {
    if (normalizedAuthors.isEmpty()) return true;
    if (blog.getAuthor() == null || blog.getAuthor().getName() == null) return false;
    return normalizedAuthors.contains(blog.getAuthor().getName().toLowerCase(Locale.ROOT));
  }

  private static List<Blog> seedData() {
    Author alice = new Author("Alice Chen", null);
    Author bob = new Author("Bob Smith", null);
    Instant now = Instant.now();
    return Arrays.asList(
        new Blog(
            1L,
            "Getting Started with Spring Boot",
            "A beginner-friendly guide to building REST APIs with Spring Boot and Gradle.",
            null,
            alice,
            Arrays.asList("java", "spring", "tutorial"),
            now.minus(1, ChronoUnit.DAYS),
            true),
        new Blog(
            2L,
            "React Query vs SWR",
            "Comparing the two most popular data-fetching libraries for React in 2024.",
            null,
            bob,
            Arrays.asList("react", "frontend"),
            now.minus(3, ChronoUnit.DAYS),
            true),
        new Blog(
            3L,
            "Designing RESTful APIs",
            "Best practices for endpoint naming, pagination, and error responses.",
            null,
            alice,
            Arrays.asList("api", "design"),
            now.minus(7, ChronoUnit.DAYS),
            true),
        new Blog(
            4L,
            "CSS Grid Layouts",
            "Mastering two-dimensional layouts with CSS Grid including responsive breakpoints.",
            null,
            bob,
            Arrays.asList("css", "frontend", "responsive"),
            now.minus(10, ChronoUnit.DAYS),
            true),
        new Blog(
            5L,
            "JUnit 5 Tips and Tricks",
            "Lesser-known JUnit 5 features that will make your test suite cleaner.",
            null,
            alice,
            Arrays.asList("java", "testing"),
            now.minus(14, ChronoUnit.DAYS),
            true),
        new Blog(
            6L,
            "Development in the era of AI",
            "How AI tools are reshaping the way developers write, review, and ship code.",
            null,
            alice,
            Arrays.asList("ai", "development", "productivity"),
            now.minus(2, ChronoUnit.DAYS),
            true));
  }
}
