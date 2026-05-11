package com.revelio.api.controller;

import static org.junit.jupiter.api.Assertions.*;

import com.revelio.api.dto.BlogResponseDto;
import com.revelio.api.model.Blog;
import com.revelio.api.service.BlogService;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BlogControllerTest {

  private BlogController blogController;
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

    blogService = new BlogService(testBlogs);
    blogController = new BlogController(blogService);
  }

  @Test
  void testGetBlogsReturnsPublishedPostsOnly() {
    List<BlogResponseDto> result = blogController.getBlogs(0, 10);

    assertEquals(2, result.size());
    assertEquals(3L, result.get(0).getId());
    assertEquals(1L, result.get(1).getId());
  }

  @Test
  void testGetBlogsReturnsSortedByPublishedAtDescending() {
    List<BlogResponseDto> result = blogController.getBlogs(0, 10);

    assertEquals(Instant.parse("2024-01-25T10:00:00Z"), result.get(0).getPublishedAt());
    assertEquals(Instant.parse("2024-01-15T10:00:00Z"), result.get(1).getPublishedAt());
  }

  @Test
  void testGetBlogsWithPagination() {
    List<BlogResponseDto> result = blogController.getBlogs(0, 1);

    assertEquals(1, result.size());
    assertEquals(3L, result.get(0).getId());
  }

  @Test
  void testGetBlogsReturnsEmptyListWhenNoPublishedPosts() {
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

    BlogService emptyService = new BlogService(unpublishedBlogs);
    BlogController emptyController = new BlogController(emptyService);

    List<BlogResponseDto> result = emptyController.getBlogs(0, 10);

    assertTrue(result.isEmpty());
  }

  @Test
  void testGetBlogsConvertsToDto() {
    List<BlogResponseDto> result = blogController.getBlogs(0, 10);

    BlogResponseDto firstDto = result.get(0);
    assertEquals("Third Post", firstDto.getTitle());
    assertEquals("Third excerpt", firstDto.getExcerpt());
    assertEquals("https://example.com/3.jpg", firstDto.getCoverImageUrl());
    assertNotNull(firstDto.getAuthor());
    assertEquals("John Doe", firstDto.getAuthor().getName());
    assertEquals("https://example.com/john.jpg", firstDto.getAuthor().getAvatarUrl());
    assertEquals(Arrays.asList("spring"), firstDto.getTags());
  }

  @Test
  void testGetBlogsWithNullCoverImageUrl() {
    List<Blog> blogsWithNullCover = new ArrayList<>();
    Blog.Author author = new Blog.Author("John Doe", "https://example.com/john.jpg");

    blogsWithNullCover.add(
        new Blog(
            1L,
            "Post Without Cover",
            "Excerpt",
            null,
            author,
            Arrays.asList("tech"),
            Instant.parse("2024-01-15T10:00:00Z"),
            true));

    BlogService serviceWithNullCover = new BlogService(blogsWithNullCover);
    BlogController controllerWithNullCover = new BlogController(serviceWithNullCover);

    List<BlogResponseDto> result = controllerWithNullCover.getBlogs(0, 10);

    assertEquals(1, result.size());
    assertNull(result.get(0).getCoverImageUrl());
  }

  @Test
  void testGetBlogsWithPageBeyondAvailableData() {
    List<BlogResponseDto> result = blogController.getBlogs(5, 10);

    assertTrue(result.isEmpty());
  }

  @Test
  void testGetBlogsThrowsExceptionForInvalidPage() {
    assertThrows(IllegalArgumentException.class, () -> blogController.getBlogs(-1, 10));
  }

  @Test
  void testGetBlogsThrowsExceptionForInvalidSize() {
    assertThrows(IllegalArgumentException.class, () -> blogController.getBlogs(0, 0));
    assertThrows(IllegalArgumentException.class, () -> blogController.getBlogs(0, -5));
  }

  @Test
  void testGetBlogsWithDefaultConstructorIncludesAIBlogPost() {
    BlogService defaultService = new BlogService();
    BlogController defaultController = new BlogController(defaultService);

    List<BlogResponseDto> result = defaultController.getBlogs(0, 10);

    BlogResponseDto aiBlog = result.stream()
        .filter(blog -> blog.getTitle().equals("Development in the era of AI"))
        .findFirst()
        .orElse(null);

    assertNotNull(aiBlog);
    assertEquals("Development in the era of AI", aiBlog.getTitle());
    assertEquals("How AI tools are reshaping the way developers write, review, and ship code.", aiBlog.getExcerpt());
    assertEquals(Arrays.asList("ai", "development", "productivity"), aiBlog.getTags());
  }
}
