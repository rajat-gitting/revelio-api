package com.revelio.api.service;

import static org.junit.jupiter.api.Assertions.*;

import com.revelio.api.model.Author;
import com.revelio.api.model.Blog;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BlogServiceTest {

  private BlogService blogService;
  private List<Blog> testBlogs;

  @BeforeEach
  void setUp() {
    blogService = new BlogService();

    Author author1 = new Author(1L, "John Doe", "https://example.com/john.jpg");
    Author author2 = new Author(2L, "Jane Smith", "https://example.com/jane.jpg");

    testBlogs =
        Arrays.asList(
            new Blog(
                1L,
                "Published Post 1",
                "Excerpt 1",
                "https://example.com/cover1.jpg",
                author1,
                Arrays.asList("tech", "java"),
                Instant.parse("2024-01-15T10:00:00Z"),
                true),
            new Blog(
                2L,
                "Unpublished Post",
                "Excerpt 2",
                "https://example.com/cover2.jpg",
                author2,
                Arrays.asList("design"),
                Instant.parse("2024-01-16T10:00:00Z"),
                false),
            new Blog(
                3L,
                "Published Post 2",
                "Excerpt 3",
                "https://example.com/cover3.jpg",
                author1,
                Arrays.asList("spring"),
                Instant.parse("2024-01-17T10:00:00Z"),
                true),
            new Blog(
                4L,
                "Published Post 3",
                "Excerpt 4",
                "https://example.com/cover4.jpg",
                author2,
                Arrays.asList("kotlin"),
                Instant.parse("2024-01-18T10:00:00Z"),
                true),
            new Blog(
                5L,
                "Unpublished Post 2",
                "Excerpt 5",
                "https://example.com/cover5.jpg",
                author1,
                Arrays.asList("python"),
                Instant.parse("2024-01-19T10:00:00Z"),
                false));
  }

  @Test
  void testGetPublishedBlogsReturnsOnlyPublishedPosts() {
    List<Blog> result = blogService.getPublishedBlogs(testBlogs, 0, 10);

    assertEquals(3, result.size());
    assertTrue(result.stream().allMatch(Blog::isPublished));
  }

  @Test
  void testGetPublishedBlogsSortsByPublishedAtDescending() {
    List<Blog> result = blogService.getPublishedBlogs(testBlogs, 0, 10);

    assertEquals("Published Post 3", result.get(0).getTitle());
    assertEquals("Published Post 2", result.get(1).getTitle());
    assertEquals("Published Post 1", result.get(2).getTitle());
  }

  @Test
  void testGetPublishedBlogsWithPaginationFirstPage() {
    List<Blog> result = blogService.getPublishedBlogs(testBlogs, 0, 2);

    assertEquals(2, result.size());
    assertEquals("Published Post 3", result.get(0).getTitle());
    assertEquals("Published Post 2", result.get(1).getTitle());
  }

  @Test
  void testGetPublishedBlogsWithPaginationSecondPage() {
    List<Blog> result = blogService.getPublishedBlogs(testBlogs, 1, 2);

    assertEquals(1, result.size());
    assertEquals("Published Post 1", result.get(0).getTitle());
  }

  @Test
  void testGetPublishedBlogsWithPaginationBeyondAvailable() {
    List<Blog> result = blogService.getPublishedBlogs(testBlogs, 5, 10);

    assertTrue(result.isEmpty());
  }

  @Test
  void testGetPublishedBlogsWithEmptyList() {
    List<Blog> result = blogService.getPublishedBlogs(Arrays.asList(), 0, 10);

    assertTrue(result.isEmpty());
  }

  @Test
  void testGetPublishedBlogsWithNoPublishedPosts() {
    List<Blog> unpublishedOnly =
        Arrays.asList(
            new Blog(
                1L,
                "Unpublished",
                "Excerpt",
                "url",
                new Author(1L, "Author", "avatar"),
                Arrays.asList("tag"),
                Instant.now(),
                false));

    List<Blog> result = blogService.getPublishedBlogs(unpublishedOnly, 0, 10);

    assertTrue(result.isEmpty());
  }

  @Test
  void testGetPublishedBlogsThrowsExceptionForNegativePage() {
    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class, () -> blogService.getPublishedBlogs(testBlogs, -1, 10));

    assertEquals("Page number must be non-negative", exception.getMessage());
  }

  @Test
  void testGetPublishedBlogsThrowsExceptionForZeroSize() {
    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class, () -> blogService.getPublishedBlogs(testBlogs, 0, 0));

    assertEquals("Page size must be positive", exception.getMessage());
  }

  @Test
  void testGetPublishedBlogsThrowsExceptionForNegativeSize() {
    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class, () -> blogService.getPublishedBlogs(testBlogs, 0, -5));

    assertEquals("Page size must be positive", exception.getMessage());
  }

  @Test
  void testFilterPublishedPostsReturnsOnlyPublished() {
    List<Blog> result = blogService.filterPublishedPosts(testBlogs);

    assertEquals(3, result.size());
    assertTrue(result.stream().allMatch(Blog::isPublished));
  }

  @Test
  void testFilterPublishedPostsWithEmptyList() {
    List<Blog> result = blogService.filterPublishedPosts(Arrays.asList());

    assertTrue(result.isEmpty());
  }

  @Test
  void testFilterPublishedPostsWithNoPublishedPosts() {
    List<Blog> unpublishedOnly =
        Arrays.asList(
            new Blog(
                1L,
                "Unpublished",
                "Excerpt",
                "url",
                new Author(1L, "Author", "avatar"),
                Arrays.asList("tag"),
                Instant.now(),
                false));

    List<Blog> result = blogService.filterPublishedPosts(unpublishedOnly);

    assertTrue(result.isEmpty());
  }

  @Test
  void testFilterPublishedPostsWithAllPublishedPosts() {
    List<Blog> publishedOnly =
        Arrays.asList(
            new Blog(
                1L,
                "Published 1",
                "Excerpt",
                "url",
                new Author(1L, "Author", "avatar"),
                Arrays.asList("tag"),
                Instant.now(),
                true),
            new Blog(
                2L,
                "Published 2",
                "Excerpt",
                "url",
                new Author(1L, "Author", "avatar"),
                Arrays.asList("tag"),
                Instant.now(),
                true));

    List<Blog> result = blogService.filterPublishedPosts(publishedOnly);

    assertEquals(2, result.size());
  }

  @Test
  void testGetPublishedBlogsWithPageSizeLargerThanAvailable() {
    List<Blog> result = blogService.getPublishedBlogs(testBlogs, 0, 100);

    assertEquals(3, result.size());
  }

  @Test
  void testGetPublishedBlogsPreservesOrderWithinSameTimestamp() {
    Author author = new Author(1L, "Author", "avatar");
    Instant sameTime = Instant.parse("2024-01-20T10:00:00Z");

    List<Blog> blogsWithSameTimestamp =
        Arrays.asList(
            new Blog(
                1L,
                "Post A",
                "Excerpt",
                "url",
                author,
                Arrays.asList("tag"),
                sameTime,
                true),
            new Blog(
                2L,
                "Post B",
                "Excerpt",
                "url",
                author,
                Arrays.asList("tag"),
                sameTime,
                true));

    List<Blog> result = blogService.getPublishedBlogs(blogsWithSameTimestamp, 0, 10);

    assertEquals(2, result.size());
  }
}
