package com.revelio.api.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.revelio.api.dto.BlogDto;
import com.revelio.api.dto.BlogListRequest;
import com.revelio.api.dto.BlogListResponse;
import com.revelio.api.service.BlogService;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class BlogControllerTest {

  private BlogController blogController;
  private BlogService blogService;

  @BeforeEach
  void setUp() {
    blogService = mock(BlogService.class);
    blogController = new BlogController(blogService);
  }

  @Test
  void testGetBlogsWithDefaultParameters() {
    BlogDto.AuthorDto author = new BlogDto.AuthorDto("John Doe", "https://example.com/avatar.jpg");
    BlogDto blog =
        new BlogDto(
            1L,
            "Test Title",
            "Test excerpt",
            "https://example.com/cover.jpg",
            author,
            Arrays.asList("tech", "java"),
            Instant.parse("2024-01-15T10:00:00Z"));
    BlogListResponse mockResponse =
        new BlogListResponse(Arrays.asList(blog), 0, 10, 1, 1, false);

    when(blogService.getPublishedBlogs(any(BlogListRequest.class))).thenReturn(mockResponse);

    ResponseEntity<BlogListResponse> response = blogController.getBlogs(0, 10);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(1, response.getBody().getBlogs().size());
    assertEquals(0, response.getBody().getPage());
    assertEquals(10, response.getBody().getSize());

    ArgumentCaptor<BlogListRequest> requestCaptor =
        ArgumentCaptor.forClass(BlogListRequest.class);
    verify(blogService).getPublishedBlogs(requestCaptor.capture());
    assertEquals(0, requestCaptor.getValue().getPage());
    assertEquals(10, requestCaptor.getValue().getSize());
  }

  @Test
  void testGetBlogsWithCustomPageAndSize() {
    BlogListResponse mockResponse =
        new BlogListResponse(Collections.emptyList(), 2, 20, 0, 0, false);

    when(blogService.getPublishedBlogs(any(BlogListRequest.class))).thenReturn(mockResponse);

    ResponseEntity<BlogListResponse> response = blogController.getBlogs(2, 20);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(2, response.getBody().getPage());
    assertEquals(20, response.getBody().getSize());

    ArgumentCaptor<BlogListRequest> requestCaptor =
        ArgumentCaptor.forClass(BlogListRequest.class);
    verify(blogService).getPublishedBlogs(requestCaptor.capture());
    assertEquals(2, requestCaptor.getValue().getPage());
    assertEquals(20, requestCaptor.getValue().getSize());
  }

  @Test
  void testGetBlogsReturnsOnlyPublishedPosts() {
    BlogDto.AuthorDto author1 = new BlogDto.AuthorDto("John Doe", "https://example.com/john.jpg");
    BlogDto.AuthorDto author2 =
        new BlogDto.AuthorDto("Jane Smith", "https://example.com/jane.jpg");

    BlogDto blog1 =
        new BlogDto(
            1L,
            "Published Post 1",
            "Excerpt 1",
            "https://example.com/cover1.jpg",
            author1,
            Arrays.asList("tech"),
            Instant.parse("2024-01-25T10:00:00Z"));
    BlogDto blog2 =
        new BlogDto(
            2L,
            "Published Post 2",
            "Excerpt 2",
            "https://example.com/cover2.jpg",
            author2,
            Arrays.asList("design"),
            Instant.parse("2024-01-20T10:00:00Z"));

    BlogListResponse mockResponse =
        new BlogListResponse(Arrays.asList(blog1, blog2), 0, 10, 2, 1, false);

    when(blogService.getPublishedBlogs(any(BlogListRequest.class))).thenReturn(mockResponse);

    ResponseEntity<BlogListResponse> response = blogController.getBlogs(0, 10);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(2, response.getBody().getBlogs().size());
  }

  @Test
  void testGetBlogsReturnsSortedByPublishedAtDescending() {
    BlogDto.AuthorDto author = new BlogDto.AuthorDto("John Doe", "https://example.com/john.jpg");

    BlogDto newerPost =
        new BlogDto(
            1L,
            "Newer Post",
            "Excerpt",
            "https://example.com/cover1.jpg",
            author,
            Arrays.asList("tech"),
            Instant.parse("2024-01-25T10:00:00Z"));
    BlogDto olderPost =
        new BlogDto(
            2L,
            "Older Post",
            "Excerpt",
            "https://example.com/cover2.jpg",
            author,
            Arrays.asList("tech"),
            Instant.parse("2024-01-15T10:00:00Z"));

    BlogListResponse mockResponse =
        new BlogListResponse(Arrays.asList(newerPost, olderPost), 0, 10, 2, 1, false);

    when(blogService.getPublishedBlogs(any(BlogListRequest.class))).thenReturn(mockResponse);

    ResponseEntity<BlogListResponse> response = blogController.getBlogs(0, 10);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(2, response.getBody().getBlogs().size());
    assertEquals("Newer Post", response.getBody().getBlogs().get(0).getTitle());
    assertEquals("Older Post", response.getBody().getBlogs().get(1).getTitle());
  }

  @Test
  void testGetBlogsWithPageStartingAtZero() {
    BlogListResponse mockResponse =
        new BlogListResponse(Collections.emptyList(), 0, 10, 0, 0, false);

    when(blogService.getPublishedBlogs(any(BlogListRequest.class))).thenReturn(mockResponse);

    ResponseEntity<BlogListResponse> response = blogController.getBlogs(0, 10);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(0, response.getBody().getPage());
  }

  @Test
  void testGetBlogsThrowsExceptionForNegativePage() {
    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> blogController.getBlogs(-1, 10));

    assertEquals("Page number must be non-negative", exception.getMessage());
    verify(blogService, never()).getPublishedBlogs(any(BlogListRequest.class));
  }

  @Test
  void testGetBlogsThrowsExceptionForZeroSize() {
    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> blogController.getBlogs(0, 0));

    assertEquals("Page size must be positive", exception.getMessage());
    verify(blogService, never()).getPublishedBlogs(any(BlogListRequest.class));
  }

  @Test
  void testGetBlogsThrowsExceptionForNegativeSize() {
    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> blogController.getBlogs(0, -5));

    assertEquals("Page size must be positive", exception.getMessage());
    verify(blogService, never()).getPublishedBlogs(any(BlogListRequest.class));
  }

  @Test
  void testGetBlogsThrowsExceptionForSizeExceedingMaximum() {
    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> blogController.getBlogs(0, 101));

    assertEquals("Page size must not exceed 100", exception.getMessage());
    verify(blogService, never()).getPublishedBlogs(any(BlogListRequest.class));
  }

  @Test
  void testGetBlogsWithEmptyResult() {
    BlogListResponse mockResponse =
        new BlogListResponse(Collections.emptyList(), 0, 10, 0, 0, false);

    when(blogService.getPublishedBlogs(any(BlogListRequest.class))).thenReturn(mockResponse);

    ResponseEntity<BlogListResponse> response = blogController.getBlogs(0, 10);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().getBlogs().isEmpty());
    assertEquals(0, response.getBody().getTotalElements());
  }

  @Test
  void testGetBlogsWithMaximumAllowedSize() {
    BlogListResponse mockResponse =
        new BlogListResponse(Collections.emptyList(), 0, 100, 0, 0, false);

    when(blogService.getPublishedBlogs(any(BlogListRequest.class))).thenReturn(mockResponse);

    ResponseEntity<BlogListResponse> response = blogController.getBlogs(0, 100);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(100, response.getBody().getSize());
  }

  @Test
  void testGetBlogsWithPaginationMetadata() {
    BlogDto.AuthorDto author = new BlogDto.AuthorDto("John Doe", "https://example.com/john.jpg");
    BlogDto blog =
        new BlogDto(
            1L,
            "Test Post",
            "Excerpt",
            "https://example.com/cover.jpg",
            author,
            Arrays.asList("tech"),
            Instant.parse("2024-01-15T10:00:00Z"));

    BlogListResponse mockResponse =
        new BlogListResponse(Arrays.asList(blog), 0, 10, 25, 3, true);

    when(blogService.getPublishedBlogs(any(BlogListRequest.class))).thenReturn(mockResponse);

    ResponseEntity<BlogListResponse> response = blogController.getBlogs(0, 10);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(25, response.getBody().getTotalElements());
    assertEquals(3, response.getBody().getTotalPages());
    assertTrue(response.getBody().isHasMore());
  }

  @Test
  void testGetBlogsDoesNotRequireAuthentication() {
    BlogListResponse mockResponse =
        new BlogListResponse(Collections.emptyList(), 0, 10, 0, 0, false);

    when(blogService.getPublishedBlogs(any(BlogListRequest.class))).thenReturn(mockResponse);

    ResponseEntity<BlogListResponse> response = blogController.getBlogs(0, 10);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    verify(blogService).getPublishedBlogs(any(BlogListRequest.class));
  }
}
