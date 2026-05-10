package com.revelio.api.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

class BlogListResponseTest {

  @Test
  void testDefaultConstructor() {
    BlogListResponse response = new BlogListResponse();

    assertNull(response.getBlogs());
    assertEquals(0, response.getPage());
    assertEquals(0, response.getSize());
    assertEquals(0, response.getTotalElements());
    assertEquals(0, response.getTotalPages());
    assertFalse(response.isHasMore());
  }

  @Test
  void testParameterizedConstructor() {
    BlogDto.AuthorDto author = new BlogDto.AuthorDto("John Doe", "https://example.com/avatar.jpg");
    BlogDto blog1 =
        new BlogDto(
            1L,
            "Test Title",
            "Test excerpt",
            "https://example.com/cover.jpg",
            author,
            Arrays.asList("tech", "java"),
            Instant.parse("2024-01-15T10:00:00Z"));
    List<BlogDto> blogs = Arrays.asList(blog1);

    BlogListResponse response = new BlogListResponse(blogs, 0, 10, 25, 3, true);

    assertEquals(blogs, response.getBlogs());
    assertEquals(0, response.getPage());
    assertEquals(10, response.getSize());
    assertEquals(25, response.getTotalElements());
    assertEquals(3, response.getTotalPages());
    assertTrue(response.isHasMore());
  }

  @Test
  void testSetters() {
    BlogListResponse response = new BlogListResponse();
    BlogDto.AuthorDto author = new BlogDto.AuthorDto("Jane Smith", "https://example.com/jane.jpg");
    BlogDto blog =
        new BlogDto(
            2L,
            "Another Title",
            "Another excerpt",
            null,
            author,
            Arrays.asList("design"),
            Instant.parse("2024-02-20T15:30:00Z"));
    List<BlogDto> blogs = Arrays.asList(blog);

    response.setBlogs(blogs);
    response.setPage(1);
    response.setSize(20);
    response.setTotalElements(50);
    response.setTotalPages(3);
    response.setHasMore(false);

    assertEquals(blogs, response.getBlogs());
    assertEquals(1, response.getPage());
    assertEquals(20, response.getSize());
    assertEquals(50, response.getTotalElements());
    assertEquals(3, response.getTotalPages());
    assertFalse(response.isHasMore());
  }

  @Test
  void testEqualsAndHashCode() {
    BlogDto.AuthorDto author = new BlogDto.AuthorDto("John Doe", "https://example.com/avatar.jpg");
    BlogDto blog =
        new BlogDto(
            1L,
            "Test Title",
            "Test excerpt",
            "https://example.com/cover.jpg",
            author,
            Arrays.asList("tech"),
            Instant.parse("2024-01-15T10:00:00Z"));
    List<BlogDto> blogs = Arrays.asList(blog);

    BlogListResponse response1 = new BlogListResponse(blogs, 0, 10, 25, 3, true);
    BlogListResponse response2 = new BlogListResponse(blogs, 0, 10, 25, 3, true);
    BlogListResponse response3 = new BlogListResponse(blogs, 1, 10, 25, 3, false);

    assertEquals(response1, response2);
    assertNotEquals(response1, response3);
    assertEquals(response1.hashCode(), response2.hashCode());
    assertNotEquals(response1.hashCode(), response3.hashCode());
  }

  @Test
  void testToString() {
    BlogDto.AuthorDto author = new BlogDto.AuthorDto("John Doe", "https://example.com/avatar.jpg");
    BlogDto blog =
        new BlogDto(
            1L,
            "Test Title",
            "Test excerpt",
            "https://example.com/cover.jpg",
            author,
            Arrays.asList("tech"),
            Instant.parse("2024-01-15T10:00:00Z"));
    List<BlogDto> blogs = Arrays.asList(blog);

    BlogListResponse response = new BlogListResponse(blogs, 0, 10, 25, 3, true);

    String toString = response.toString();
    assertTrue(toString.contains("page=0"));
    assertTrue(toString.contains("size=10"));
    assertTrue(toString.contains("totalElements=25"));
    assertTrue(toString.contains("totalPages=3"));
    assertTrue(toString.contains("hasMore=true"));
  }

  @Test
  void testHasMoreIsFalseWhenOnLastPage() {
    BlogListResponse response = new BlogListResponse(Arrays.asList(), 2, 10, 25, 3, false);

    assertFalse(response.isHasMore());
  }

  @Test
  void testHasMoreIsTrueWhenMorePagesExist() {
    BlogListResponse response = new BlogListResponse(Arrays.asList(), 0, 10, 25, 3, true);

    assertTrue(response.isHasMore());
  }
}
