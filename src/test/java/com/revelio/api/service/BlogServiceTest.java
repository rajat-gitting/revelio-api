package com.revelio.api.service;

import static org.junit.jupiter.api.Assertions.*;

import com.revelio.api.dto.BlogDto;
import com.revelio.api.dto.BlogListRequest;
import com.revelio.api.dto.BlogListResponse;
import com.revelio.api.model.Blog;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BlogServiceTest {

  private BlogService blogService;
  private List<Blog> testBlogs;

  @BeforeEach
  void setUp() {
    Blog.Author author1 = new Blog.Author("John Doe", "https://example.com/john.jpg");
    Blog.Author author2 = new Blog.Author("Jane Smith", "https://example.com/jane.jpg");
    Blog.Author author3 = new Blog.Author("Bob Wilson", null);

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
                Instant.parse("2024-01-20T10:00:00Z"),
                false),
            new Blog(
                3L,
                "Published Post 2",
                "Excerpt 3",
                null,
                author3,
                Arrays.asList("ui", "ux", "design"),
                Instant.parse("2024-01-25T10:00:00Z"),
                true),
            new Blog(
                4L,
                "Published Post 3",
                "Excerpt 4",
                "https://example.com/cover4.jpg",
                author1,
                Arrays.asList("backend"),
                Instant.parse("2024-01-10T10:00:00Z"),
                true));

    blogService = new BlogService(testBlogs);
  }

  @Test
  void testFilterPublishedPostsReturnsOnlyPublishedBlogs() {
    List<Blog> publishedBlogs = blogService.filterPublishedPosts(testBlogs);

    assertEquals(3, publishedBlogs.size());
    assertTrue(publishedBlogs.stream().allMatch(Blog::isPublished));
  }

  @Test
  void testFilterPublishedPostsWithNoPublishedBlogs() {
    Blog.Author author = new Blog.Author("Test Author", "https://example.com/test.jpg");
    List<Blog> unpublishedBlogs =
        Arrays.asList(
            new Blog(
                1L,
                "Unpublished 1",
                "Excerpt",
                "https://example.com/cover.jpg",
                author,
                Arrays.asList("tech"),
                Instant.parse("2024-01-15T10:00:00Z"),
                false),
            new Blog(
                2L,
                "Unpublished 2",
                "Excerpt",
                "https://example.com/cover.jpg",
                author,
                Arrays.asList("tech"),
                Instant.parse("2024-01-16T10:00:00Z"),
                false));

    List<Blog> publishedBlogs = blogService.filterPublishedPosts(unpublishedBlogs);

    assertTrue(publishedBlogs.isEmpty());
  }

  @Test
  void testFilterPublishedPostsWithEmptyList() {
    List<Blog> publishedBlogs = blogService.filterPublishedPosts(Collections.emptyList());

    assertTrue(publishedBlogs.isEmpty());
  }

  @Test
  void testGetPublishedBlogsReturnsSortedByPublishedAtDescending() {
    BlogListRequest request = new BlogListRequest(0, 10);

    BlogListResponse response = blogService.getPublishedBlogs(request);

    assertEquals(3, response.getBlogs().size());
    assertEquals("Published Post 2", response.getBlogs().get(0).getTitle());
    assertEquals("Published Post 1", response.getBlogs().get(1).getTitle());
    assertEquals("Published Post 3", response.getBlogs().get(2).getTitle());
  }

  @Test
  void testGetPublishedBlogsWithPagination() {
    Blog.Author author = new Blog.Author("Test Author", "https://example.com/test.jpg");
    List<Blog> manyBlogs = Arrays.asList(
        new Blog(1L, "Post 1", "Excerpt", "https://example.com/cover.jpg", author, Arrays.asList("tech"), Instant.parse("2024-01-25T10:00:00Z"), true),
        new Blog(2L, "Post 2", "Excerpt", "https://example.com/cover.jpg", author, Arrays.asList("tech"), Instant.parse("2024-01-24T10:00:00Z"), true),
        new Blog(3L, "Post 3", "Excerpt", "https://example.com/cover.jpg", author, Arrays.asList("tech"), Instant.parse("2024-01-23T10:00:00Z"), true),
        new Blog(4L, "Post 4", "Excerpt", "https://example.com/cover.jpg", author, Arrays.asList("tech"), Instant.parse("2024-01-22T10:00:00Z"), true),
        new Blog(5L, "Post 5", "Excerpt", "https://example.com/cover.jpg", author, Arrays.asList("tech"), Instant.parse("2024-01-21T10:00:00Z"), true),
        new Blog(6L, "Post 6", "Excerpt", "https://example.com/cover.jpg", author, Arrays.asList("tech"), Instant.parse("2024-01-20T10:00:00Z"), true),
        new Blog(7L, "Post 7", "Excerpt", "https://example.com/cover.jpg", author, Arrays.asList("tech"), Instant.parse("2024-01-19T10:00:00Z"), true),
        new Blog(8L, "Post 8", "Excerpt", "https://example.com/cover.jpg", author, Arrays.asList("tech"), Instant.parse("2024-01-18T10:00:00Z"), true),
        new Blog(9L, "Post 9", "Excerpt", "https://example.com/cover.jpg", author, Arrays.asList("tech"), Instant.parse("2024-01-17T10:00:00Z"), true),
        new Blog(10L, "Post 10", "Excerpt", "https://example.com/cover.jpg", author, Arrays.asList("tech"), Instant.parse("2024-01-16T10:00:00Z"), true),
        new Blog(11L, "Post 11", "Excerpt", "https://example.com/cover.jpg", author, Arrays.asList("tech"), Instant.parse("2024-01-15T10:00:00Z"), true),
        new Blog(12L, "Post 12", "Excerpt", "https://example.com/cover.jpg", author, Arrays.asList("tech"), Instant.parse("2024-01-14T10:00:00Z"), true)
    );

    BlogService service = new BlogService(manyBlogs);
    BlogListRequest request = new BlogListRequest(0, 10);

    BlogListResponse response = service.getPublishedBlogs(request);

    assertEquals(10, response.getBlogs().size());
    assertEquals(0, response.getPage());
    assertEquals(10, response.getSize());
    assertEquals(12, response.getTotalElements());
    assertEquals(2, response.getTotalPages());
    assertTrue(response.isHasMore());
  }

  @Test
  void testGetPublishedBlogsSecondPage() {
    Blog.Author author = new Blog.Author("Test Author", "https://example.com/test.jpg");
    List<Blog> manyBlogs = Arrays.asList(
        new Blog(1L, "Post 1", "Excerpt", "https://example.com/cover.jpg", author, Arrays.asList("tech"), Instant.parse("2024-01-25T10:00:00Z"), true),
        new Blog(2L, "Post 2", "Excerpt", "https://example.com/cover.jpg", author, Arrays.asList("tech"), Instant.parse("2024-01-24T10:00:00Z"), true),
        new Blog(3L, "Post 3", "Excerpt", "https://example.com/cover.jpg", author, Arrays.asList("tech"), Instant.parse("2024-01-23T10:00:00Z"), true),
        new Blog(4L, "Post 4", "Excerpt", "https://example.com/cover.jpg", author, Arrays.asList("tech"), Instant.parse("2024-01-22T10:00:00Z"), true),
        new Blog(5L, "Post 5", "Excerpt", "https://example.com/cover.jpg", author, Arrays.asList("tech"), Instant.parse("2024-01-21T10:00:00Z"), true),
        new Blog(6L, "Post 6", "Excerpt", "https://example.com/cover.jpg", author, Arrays.asList("tech"), Instant.parse("2024-01-20T10:00:00Z"), true),
        new Blog(7L, "Post 7", "Excerpt", "https://example.com/cover.jpg", author, Arrays.asList("tech"), Instant.parse("2024-01-19T10:00:00Z"), true),
        new Blog(8L, "Post 8", "Excerpt", "https://example.com/cover.jpg", author, Arrays.asList("tech"), Instant.parse("2024-01-18T10:00:00Z"), true),
        new Blog(9L, "Post 9", "Excerpt", "https://example.com/cover.jpg", author, Arrays.asList("tech"), Instant.parse("2024-01-17T10:00:00Z"), true),
        new Blog(10L, "Post 10", "Excerpt", "https://example.com/cover.jpg", author, Arrays.asList("tech"), Instant.parse("2024-01-16T10:00:00Z"), true),
        new Blog(11L, "Post 11", "Excerpt", "https://example.com/cover.jpg", author, Arrays.asList("tech"), Instant.parse("2024-01-15T10:00:00Z"), true),
        new Blog(12L, "Post 12", "Excerpt", "https://example.com/cover.jpg", author, Arrays.asList("tech"), Instant.parse("2024-01-14T10:00:00Z"), true)
    );

    BlogService service = new BlogService(manyBlogs);
    BlogListRequest request = new BlogListRequest(1, 10);

    BlogListResponse response = service.getPublishedBlogs(request);

    assertEquals(2, response.getBlogs().size());
    assertEquals(1, response.getPage());
    assertEquals(10, response.getSize());
    assertEquals(12, response.getTotalElements());
    assertEquals(2, response.getTotalPages());
    assertFalse(response.isHasMore());
  }

  @Test
  void testGetPublishedBlogsWhenNoMorePostsExist() {
    BlogListRequest request = new BlogListRequest(0, 10);

    BlogListResponse response = blogService.getPublishedBlogs(request);

    assertEquals(3, response.getBlogs().size());
    assertEquals(0, response.getPage());
    assertEquals(10, response.getSize());
    assertEquals(3, response.getTotalElements());
    assertEquals(1, response.getTotalPages());
    assertFalse(response.isHasMore());
  }

  @Test
  void testGetPublishedBlogsWithPageBeyondTotalPages() {
    BlogListRequest request = new BlogListRequest(5, 10);

    BlogListResponse response = blogService.getPublishedBlogs(request);

    assertTrue(response.getBlogs().isEmpty());
    assertEquals(5, response.getPage());
    assertEquals(10, response.getSize());
    assertEquals(3, response.getTotalElements());
    assertEquals(1, response.getTotalPages());
    assertFalse(response.isHasMore());
  }

  @Test
  void testGetPublishedBlogsWithEmptyRepository() {
    BlogService emptyService = new BlogService(Collections.emptyList());
    BlogListRequest request = new BlogListRequest(0, 10);

    BlogListResponse response = emptyService.getPublishedBlogs(request);

    assertTrue(response.getBlogs().isEmpty());
    assertEquals(0, response.getPage());
    assertEquals(10, response.getSize());
    assertEquals(0, response.getTotalElements());
    assertEquals(0, response.getTotalPages());
    assertFalse(response.isHasMore());
  }

  @Test
  void testGetPublishedBlogsConvertsToDto() {
    BlogListRequest request = new BlogListRequest(0, 10);

    BlogListResponse response = blogService.getPublishedBlogs(request);

    BlogDto firstDto = response.getBlogs().get(0);
    assertEquals(3L, firstDto.getId());
    assertEquals("Published Post 2", firstDto.getTitle());
    assertEquals("Excerpt 3", firstDto.getExcerpt());
    assertNull(firstDto.getCoverImageUrl());
    assertEquals("Bob Wilson", firstDto.getAuthor().getName());
    assertNull(firstDto.getAuthor().getAvatarUrl());
    assertEquals(Arrays.asList("ui", "ux", "design"), firstDto.getTags());
    assertEquals(Instant.parse("2024-01-25T10:00:00Z"), firstDto.getPublishedAt());
  }

  @Test
  void testGetPublishedBlogsWithNullAuthor() {
    Blog.Author author = new Blog.Author("Test Author", "https://example.com/test.jpg");
    List<Blog> blogsWithNullAuthor =
        Arrays.asList(
            new Blog(
                1L,
                "Post with null author",
                "Excerpt",
                "https://example.com/cover.jpg",
                null,
                Arrays.asList("tech"),
                Instant.parse("2024-01-15T10:00:00Z"),
                true));

    BlogService service = new BlogService(blogsWithNullAuthor);
    BlogListRequest request = new BlogListRequest(0, 10);

    BlogListResponse response = service.getPublishedBlogs(request);

    assertEquals(1, response.getBlogs().size());
    assertNull(response.getBlogs().get(0).getAuthor());
  }

  @Test
  void testGetPublishedBlogsPageStartsAtZero() {
    BlogListRequest request = new BlogListRequest(0, 10);

    BlogListResponse response = blogService.getPublishedBlogs(request);

    assertEquals(0, response.getPage());
  }

  @Test
  void testGetPublishedBlogsWithCustomPageSize() {
    Blog.Author author = new Blog.Author("Test Author", "https://example.com/test.jpg");
    List<Blog> manyBlogs = Arrays.asList(
        new Blog(1L, "Post 1", "Excerpt", "https://example.com/cover.jpg", author, Arrays.asList("tech"), Instant.parse("2024-01-25T10:00:00Z"), true),
        new Blog(2L, "Post 2", "Excerpt", "https://example.com/cover.jpg", author, Arrays.asList("tech"), Instant.parse("2024-01-24T10:00:00Z"), true),
        new Blog(3L, "Post 3", "Excerpt", "https://example.com/cover.jpg", author, Arrays.asList("tech"), Instant.parse("2024-01-23T10:00:00Z"), true),
        new Blog(4L, "Post 4", "Excerpt", "https://example.com/cover.jpg", author, Arrays.asList("tech"), Instant.parse("2024-01-22T10:00:00Z"), true),
        new Blog(5L, "Post 5", "Excerpt", "https://example.com/cover.jpg", author, Arrays.asList("tech"), Instant.parse("2024-01-21T10:00:00Z"), true)
    );

    BlogService service = new BlogService(manyBlogs);
    BlogListRequest request = new BlogListRequest(0, 3);

    BlogListResponse response = service.getPublishedBlogs(request);

    assertEquals(3, response.getBlogs().size());
    assertEquals(0, response.getPage());
    assertEquals(3, response.getSize());
    assertEquals(5, response.getTotalElements());
    assertEquals(2, response.getTotalPages());
    assertTrue(response.isHasMore());
  }
}
