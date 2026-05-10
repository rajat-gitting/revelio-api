package com.revelio.api.controller;

import static org.junit.jupiter.api.Assertions.*;

import com.revelio.api.dto.PagedBlogResponseDto;
import com.revelio.api.model.Author;
import com.revelio.api.model.Blog;
import com.revelio.api.service.BlogService;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class BlogControllerTest {

  private BlogController blogController;
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
                true));

    blogController = new BlogController(blogService, testBlogs);
  }

  @Test
  void testGetBlogsReturnsOnlyPublishedPostsSortedByPublishedAtDescending() {
    ResponseEntity<PagedBlogResponseDto> response = blogController.getBlogs(0, 10);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(3, response.getBody().getContent().size());
    assertEquals("Published Post 3", response.getBody().getContent().get(0).getTitle());
    assertEquals("Published Post 2", response.getBody().getContent().get(1).getTitle());
    assertEquals("Published Post 1", response.getBody().getContent().get(2).getTitle());
  }

  @Test
  void testGetBlogsWithPaginationFirstPage() {
    ResponseEntity<PagedBlogResponseDto> response = blogController.getBlogs(0, 2);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(2, response.getBody().getContent().size());
    assertEquals(0, response.getBody().getPage());
    assertEquals(2, response.getBody().getSize());
    assertEquals(3, response.getBody().getTotalElements());
    assertEquals(2, response.getBody().getTotalPages());
    assertTrue(response.getBody().isHasMore());
  }

  @Test
  void testGetBlogsWithPaginationSecondPage() {
    ResponseEntity<PagedBlogResponseDto> response = blogController.getBlogs(1, 2);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(1, response.getBody().getContent().size());
    assertEquals(1, response.getBody().getPage());
    assertEquals(2, response.getBody().getSize());
    assertFalse(response.getBody().isHasMore());
  }

  @Test
  void testGetBlogsWithPaginationBeyondAvailable() {
    ResponseEntity<PagedBlogResponseDto> response = blogController.getBlogs(5, 10);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().getContent().isEmpty());
    assertFalse(response.getBody().isHasMore());
  }

  @Test
  void testGetBlogsWithEmptyResult() {
    List<Blog> emptyBlogs = Arrays.asList();
    BlogController emptyController = new BlogController(blogService, emptyBlogs);

    ResponseEntity<PagedBlogResponseDto> response = emptyController.getBlogs(0, 10);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().getContent().isEmpty());
    assertEquals(0, response.getBody().getTotalElements());
    assertEquals(0, response.getBody().getTotalPages());
    assertFalse(response.getBody().isHasMore());
  }

  @Test
  void testGetBlogsWithNegativePageReturnsBadRequest() {
    ResponseEntity<PagedBlogResponseDto> response = blogController.getBlogs(-1, 10);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  void testGetBlogsWithZeroSizeReturnsBadRequest() {
    ResponseEntity<PagedBlogResponseDto> response = blogController.getBlogs(0, 0);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  void testGetBlogsWithNegativeSizeReturnsBadRequest() {
    ResponseEntity<PagedBlogResponseDto> response = blogController.getBlogs(0, -5);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  void testGetBlogsDefaultParameters() {
    ResponseEntity<PagedBlogResponseDto> response = blogController.getBlogs(0, 10);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(0, response.getBody().getPage());
    assertEquals(10, response.getBody().getSize());
  }

  @Test
  void testGetBlogsIncludesAuthorInformation() {
    ResponseEntity<PagedBlogResponseDto> response = blogController.getBlogs(0, 10);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertNotNull(response.getBody().getContent().get(0).getAuthor());
    assertEquals("Jane Smith", response.getBody().getContent().get(0).getAuthor().getName());
    assertEquals(
        "https://example.com/jane.jpg",
        response.getBody().getContent().get(0).getAuthor().getAvatarUrl());
  }

  @Test
  void testGetBlogsIncludesAllRequiredFields() {
    ResponseEntity<PagedBlogResponseDto> response = blogController.getBlogs(0, 10);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertFalse(response.getBody().getContent().isEmpty());

    var firstBlog = response.getBody().getContent().get(0);
    assertNotNull(firstBlog.getId());
    assertNotNull(firstBlog.getTitle());
    assertNotNull(firstBlog.getExcerpt());
    assertNotNull(firstBlog.getCoverImageUrl());
    assertNotNull(firstBlog.getAuthor());
    assertNotNull(firstBlog.getTags());
    assertNotNull(firstBlog.getPublishedAt());
  }

  @Test
  void testGetBlogsWithNullCoverImageUrl() {
    Author author = new Author(1L, "John Doe", "https://example.com/john.jpg");
    List<Blog> blogsWithNullCover =
        Arrays.asList(
            new Blog(
                1L,
                "Post Without Cover",
                "Excerpt",
                null,
                author,
                Arrays.asList("tech"),
                Instant.parse("2024-01-15T10:00:00Z"),
                true));

    BlogController controllerWithNullCover = new BlogController(blogService, blogsWithNullCover);
    ResponseEntity<PagedBlogResponseDto> response = controllerWithNullCover.getBlogs(0, 10);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertNull(response.getBody().getContent().get(0).getCoverImageUrl());
  }

  @Test
  void testGetBlogsWithAuthorHavingNullAvatarUrl() {
    Author authorWithoutAvatar = new Author(1L, "John Doe", null);
    List<Blog> blogsWithNullAvatar =
        Arrays.asList(
            new Blog(
                1L,
                "Post",
                "Excerpt",
                "https://example.com/cover.jpg",
                authorWithoutAvatar,
                Arrays.asList("tech"),
                Instant.parse("2024-01-15T10:00:00Z"),
                true));

    BlogController controllerWithNullAvatar = new BlogController(blogService, blogsWithNullAvatar);
    ResponseEntity<PagedBlogResponseDto> response = controllerWithNullAvatar.getBlogs(0, 10);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertNull(response.getBody().getContent().get(0).getAuthor().getAvatarUrl());
    assertEquals("John Doe", response.getBody().getContent().get(0).getAuthor().getName());
  }

  @Test
  void testGetBlogsWithEmptyTags() {
    Author author = new Author(1L, "John Doe", "https://example.com/john.jpg");
    List<Blog> blogsWithEmptyTags =
        Arrays.asList(
            new Blog(
                1L,
                "Post",
                "Excerpt",
                "https://example.com/cover.jpg",
                author,
                Arrays.asList(),
                Instant.parse("2024-01-15T10:00:00Z"),
                true));

    BlogController controllerWithEmptyTags = new BlogController(blogService, blogsWithEmptyTags);
    ResponseEntity<PagedBlogResponseDto> response = controllerWithEmptyTags.getBlogs(0, 10);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().getContent().get(0).getTags().isEmpty());
  }

  @Test
  void testGetBlogsCalculatesHasMoreCorrectly() {
    ResponseEntity<PagedBlogResponseDto> firstPage = blogController.getBlogs(0, 2);
    assertTrue(firstPage.getBody().isHasMore());

    ResponseEntity<PagedBlogResponseDto> lastPage = blogController.getBlogs(1, 2);
    assertFalse(lastPage.getBody().isHasMore());
  }

  @Test
  void testGetBlogsCalculatesTotalPagesCorrectly() {
    ResponseEntity<PagedBlogResponseDto> response = blogController.getBlogs(0, 2);

    assertEquals(2, response.getBody().getTotalPages());
  }

  @Test
  void testGetBlogsWithOnlyUnpublishedPosts() {
    Author author = new Author(1L, "John Doe", "https://example.com/john.jpg");
    List<Blog> unpublishedOnly =
        Arrays.asList(
            new Blog(
                1L,
                "Unpublished",
                "Excerpt",
                "url",
                author,
                Arrays.asList("tag"),
                Instant.now(),
                false));

    BlogController controllerWithUnpublished = new BlogController(blogService, unpublishedOnly);
    ResponseEntity<PagedBlogResponseDto> response = controllerWithUnpublished.getBlogs(0, 10);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().getContent().isEmpty());
    assertEquals(0, response.getBody().getTotalElements());
  }
}
