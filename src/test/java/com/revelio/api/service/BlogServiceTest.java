package com.revelio.api.service;

import static org.junit.jupiter.api.Assertions.*;

import com.revelio.api.model.Blog;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BlogServiceTest {

  private BlogService blogService;
  private List<Blog> testBlogs;

  @BeforeEach
  void setUp() {
    testBlogs = new ArrayList<>();

    Blog.Author author1 = new Blog.Author("John Doe", "https://example.com/john.jpg");
    Blog.Author author2 = new Blog.Author("Jane Smith", "https://example.com/jane.jpg");

    testBlogs.add(
        new Blog(
            1L,
            "First Post",
            "First excerpt",
            "https://example.com/1.jpg",
            author1,
            Arrays.asList("tech", "java"),
            Instant.parse("2024-01-15T10:00:00Z"),
            true));

    testBlogs.add(
        new Blog(
            2L,
            "Second Post",
            "Second excerpt",
            "https://example.com/2.jpg",
            author2,
            Arrays.asList("design"),
            Instant.parse("2024-01-20T10:00:00Z"),
            false));

    testBlogs.add(
        new Blog(
            3L,
            "Third Post",
            "Third excerpt",
            "https://example.com/3.jpg",
            author1,
            Arrays.asList("spring"),
            Instant.parse("2024-01-25T10:00:00Z"),
            true));

    testBlogs.add(
        new Blog(
            4L,
            "Fourth Post",
            "Fourth excerpt",
            "https://example.com/4.jpg",
            author2,
            Arrays.asList("ui"),
            Instant.parse("2024-01-10T10:00:00Z"),
            true));

    testBlogs.add(
        new Blog(
            5L,
            "Fifth Post",
            "Fifth excerpt",
            "https://example.com/5.jpg",
            author1,
            Arrays.asList("backend"),
            Instant.parse("2024-01-30T10:00:00Z"),
            false));

    blogService = new BlogService(testBlogs);
  }

  @Test
  void testGetPublishedBlogsReturnsOnlyPublishedPosts() {
    List<Blog> result = blogService.getPublishedBlogs(0, 10);

    assertEquals(3, result.size());
    assertTrue(result.stream().allMatch(Blog::isPublished));
  }

  @Test
  void testGetPublishedBlogsSortsByPublishedAtDescending() {
    List<Blog> result = blogService.getPublishedBlogs(0, 10);

    assertEquals(3L, result.get(0).getId());
    assertEquals(1L, result.get(1).getId());
    assertEquals(4L, result.get(2).getId());

    assertEquals(Instant.parse("2024-01-25T10:00:00Z"), result.get(0).getPublishedAt());
    assertEquals(Instant.parse("2024-01-15T10:00:00Z"), result.get(1).getPublishedAt());
    assertEquals(Instant.parse("2024-01-10T10:00:00Z"), result.get(2).getPublishedAt());
  }

  @Test
  void testGetPublishedBlogsWithPaginationFirstPage() {
    List<Blog> result = blogService.getPublishedBlogs(0, 2);

    assertEquals(2, result.size());
    assertEquals(3L, result.get(0).getId());
    assertEquals(1L, result.get(1).getId());
  }

  @Test
  void testGetPublishedBlogsWithPaginationSecondPage() {
    List<Blog> result = blogService.getPublishedBlogs(1, 2);

    assertEquals(1, result.size());
    assertEquals(4L, result.get(0).getId());
  }

  @Test
  void testGetPublishedBlogsWithPaginationBeyondAvailableData() {
    List<Blog> result = blogService.getPublishedBlogs(5, 10);

    assertTrue(result.isEmpty());
  }

  @Test
  void testGetPublishedBlogsWithPageSizeLargerThanAvailableData() {
    List<Blog> result = blogService.getPublishedBlogs(0, 100);

    assertEquals(3, result.size());
  }

  @Test
  void testGetPublishedBlogsThrowsExceptionForNegativePage() {
    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> blogService.getPublishedBlogs(-1, 10));

    assertEquals("Page number must be non-negative", exception.getMessage());
  }

  @Test
  void testGetPublishedBlogsThrowsExceptionForZeroSize() {
    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> blogService.getPublishedBlogs(0, 0));

    assertEquals("Page size must be positive", exception.getMessage());
  }

  @Test
  void testGetPublishedBlogsThrowsExceptionForNegativeSize() {
    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> blogService.getPublishedBlogs(0, -5));

    assertEquals("Page size must be positive", exception.getMessage());
  }

  @Test
  void testGetPublishedBlogsWithEmptyRepository() {
    BlogService emptyService = new BlogService(new ArrayList<>());
    List<Blog> result = emptyService.getPublishedBlogs(0, 10);

    assertTrue(result.isEmpty());
  }

  @Test
  void testGetPublishedBlogsWithAllUnpublishedPosts() {
    List<Blog> unpublishedBlogs = new ArrayList<>();
    Blog.Author author = new Blog.Author("John Doe", "https://example.com/john.jpg");

    unpublishedBlogs.add(
        new Blog(
            1L,
            "Unpublished Post",
            "Excerpt",
            "https://example.com/1.jpg",
            author,
            Arrays.asList("tech"),
            Instant.parse("2024-01-15T10:00:00Z"),
            false));

    BlogService service = new BlogService(unpublishedBlogs);
    List<Blog> result = service.getPublishedBlogs(0, 10);

    assertTrue(result.isEmpty());
  }

  @Test
  void testBlogServiceConstructorWithNullRepository() {
    BlogService service = new BlogService(null);
    List<Blog> result = service.getPublishedBlogs(0, 10);

    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test
  void testBlogServiceDefaultConstructorReturnsSeedData() {
    BlogService service = new BlogService();
    List<Blog> result = service.getPublishedBlogs(0, 10);

    assertNotNull(result);
    assertFalse(result.isEmpty());
  }

  @Test
  void testGetPublishedBlogsWithPartialPage() {
    List<Blog> result = blogService.getPublishedBlogs(1, 5);

    assertTrue(result.isEmpty());
  }

  @Test
  void testGetPublishedBlogsPreservesOriginalList() {
    int originalSize = testBlogs.size();
    blogService.getPublishedBlogs(0, 10);

    assertEquals(originalSize, testBlogs.size());
  }

  @Test
  void testSeedDataContainsAIBlogPost() {
    BlogService service = new BlogService();
    List<Blog> result = service.getPublishedBlogs(0, 10);

    Blog aiBlog = result.stream()
        .filter(blog -> blog.getTitle().equals("Development in the era of AI"))
        .findFirst()
        .orElse(null);

    assertNotNull(aiBlog);
    assertEquals("Development in the era of AI", aiBlog.getTitle());
    assertEquals("How AI tools are reshaping the way developers write, review, and ship code.", aiBlog.getExcerpt());
    assertEquals(Arrays.asList("ai", "development", "productivity"), aiBlog.getTags());
    assertTrue(aiBlog.isPublished());
    assertEquals(6L, aiBlog.getId());
  }

  @Test
  void testSeedDataAIBlogPostHasCorrectAuthor() {
    BlogService service = new BlogService();
    List<Blog> result = service.getPublishedBlogs(0, 10);

    Blog aiBlog = result.stream()
        .filter(blog -> blog.getTitle().equals("Development in the era of AI"))
        .findFirst()
        .orElse(null);

    assertNotNull(aiBlog);
    assertNotNull(aiBlog.getAuthor());
    assertEquals("Alice Chen", aiBlog.getAuthor().getName());
  }

  @Test
  void testSeedDataAIBlogPostIsSortedCorrectly() {
    BlogService service = new BlogService();
    List<Blog> result = service.getPublishedBlogs(0, 10);

    assertTrue(result.size() >= 2);
    Blog firstPost = result.get(0);
    Blog secondPost = result.get(1);

    assertTrue(firstPost.getPublishedAt().isAfter(secondPost.getPublishedAt()) ||
               firstPost.getPublishedAt().equals(secondPost.getPublishedAt()));
  }
}
