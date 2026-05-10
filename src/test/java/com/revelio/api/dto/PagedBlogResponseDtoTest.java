package com.revelio.api.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

class PagedBlogResponseDtoTest {

  @Test
  void testPagedBlogResponseDtoConstructorAndGetters() {
    AuthorDto author = new AuthorDto("John Doe", "https://example.com/avatar.jpg");
    BlogResponseDto blog =
        new BlogResponseDto(
            1L,
            "Test",
            "Excerpt",
            "https://example.com/cover.jpg",
            author,
            Arrays.asList("tech"),
            Instant.parse("2024-01-15T10:00:00Z"));
    List<BlogResponseDto> content = Arrays.asList(blog);

    PagedBlogResponseDto dto = new PagedBlogResponseDto(content, 0, 10, 1, 1, false);

    assertEquals(content, dto.getContent());
    assertEquals(0, dto.getPage());
    assertEquals(10, dto.getSize());
    assertEquals(1, dto.getTotalElements());
    assertEquals(1, dto.getTotalPages());
    assertFalse(dto.isHasMore());
  }

  @Test
  void testPagedBlogResponseDtoSetters() {
    PagedBlogResponseDto dto = new PagedBlogResponseDto();
    AuthorDto author = new AuthorDto("John Doe", "https://example.com/avatar.jpg");
    BlogResponseDto blog =
        new BlogResponseDto(
            1L,
            "Test",
            "Excerpt",
            "https://example.com/cover.jpg",
            author,
            Arrays.asList("tech"),
            Instant.parse("2024-01-15T10:00:00Z"));
    List<BlogResponseDto> content = Arrays.asList(blog);

    dto.setContent(content);
    dto.setPage(1);
    dto.setSize(5);
    dto.setTotalElements(20);
    dto.setTotalPages(4);
    dto.setHasMore(true);

    assertEquals(content, dto.getContent());
    assertEquals(1, dto.getPage());
    assertEquals(5, dto.getSize());
    assertEquals(20, dto.getTotalElements());
    assertEquals(4, dto.getTotalPages());
    assertTrue(dto.isHasMore());
  }

  @Test
  void testPagedBlogResponseDtoEqualsAndHashCode() {
    AuthorDto author = new AuthorDto("John Doe", "https://example.com/avatar.jpg");
    BlogResponseDto blog =
        new BlogResponseDto(
            1L,
            "Test",
            "Excerpt",
            "https://example.com/cover.jpg",
            author,
            Arrays.asList("tech"),
            Instant.parse("2024-01-15T10:00:00Z"));
    List<BlogResponseDto> content = Arrays.asList(blog);

    PagedBlogResponseDto dto1 = new PagedBlogResponseDto(content, 0, 10, 1, 1, false);
    PagedBlogResponseDto dto2 = new PagedBlogResponseDto(content, 0, 10, 1, 1, false);
    PagedBlogResponseDto dto3 = new PagedBlogResponseDto(content, 1, 10, 1, 1, true);

    assertEquals(dto1, dto2);
    assertEquals(dto1.hashCode(), dto2.hashCode());
    assertNotEquals(dto1, dto3);
  }

  @Test
  void testPagedBlogResponseDtoToString() {
    AuthorDto author = new AuthorDto("John Doe", "https://example.com/avatar.jpg");
    BlogResponseDto blog =
        new BlogResponseDto(
            1L,
            "Test",
            "Excerpt",
            "https://example.com/cover.jpg",
            author,
            Arrays.asList("tech"),
            Instant.parse("2024-01-15T10:00:00Z"));
    List<BlogResponseDto> content = Arrays.asList(blog);

    PagedBlogResponseDto dto = new PagedBlogResponseDto(content, 0, 10, 1, 1, false);

    String result = dto.toString();

    assertTrue(result.contains("page=0"));
    assertTrue(result.contains("size=10"));
    assertTrue(result.contains("totalElements=1"));
    assertTrue(result.contains("hasMore=false"));
  }
}
