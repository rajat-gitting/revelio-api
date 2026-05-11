package com.revelio.api.service;

import com.revelio.api.model.Blog;
import com.revelio.api.model.Blog.Author;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
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

    List<Blog> sorted = blogRepository.stream()
        .filter(Blog::isPublished)
        .sorted(Comparator.comparing(Blog::getPublishedAt).reversed())
        .collect(Collectors.toList());

    int start = page * size;
    if (start >= sorted.size()) return new ArrayList<>();
    return new ArrayList<>(sorted.subList(start, Math.min(start + size, sorted.size())));
  }

  private static List<Blog> seedData() {
    Author alice = new Author("Alice Chen", null);
    Author bob = new Author("Bob Smith", null);
    Instant now = Instant.now();
    return Arrays.asList(
        new Blog(1L, "Getting Started with Spring Boot", "A beginner-friendly guide to building REST APIs with Spring Boot and Gradle.", null, alice, Arrays.asList("java", "spring", "tutorial"), now.minus(1, ChronoUnit.DAYS), true),
        new Blog(2L, "React Query vs SWR", "Comparing the two most popular data-fetching libraries for React in 2024.", null, bob, Arrays.asList("react", "frontend"), now.minus(3, ChronoUnit.DAYS), true),
        new Blog(3L, "Designing RESTful APIs", "Best practices for endpoint naming, pagination, and error responses.", null, alice, Arrays.asList("api", "design"), now.minus(7, ChronoUnit.DAYS), true),
        new Blog(4L, "CSS Grid Layouts", "Mastering two-dimensional layouts with CSS Grid including responsive breakpoints.", null, bob, Arrays.asList("css", "frontend", "responsive"), now.minus(10, ChronoUnit.DAYS), true),
        new Blog(5L, "JUnit 5 Tips and Tricks", "Lesser-known JUnit 5 features that will make your test suite cleaner.", null, alice, Arrays.asList("java", "testing"), now.minus(14, ChronoUnit.DAYS), true),
        new Blog(6L, "Development in the era of AI", "How AI tools are reshaping the way developers write, review, and ship code.", null, alice, Arrays.asList("ai", "development", "productivity"), now.minus(2, ChronoUnit.DAYS), true)
    );
  }
}
