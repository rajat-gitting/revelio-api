package com.revelio.api.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

class BlogResponseDtoTest {

  @Test
  void testBlogResponseDtoConstructorAndGetters() {
    AuthorDto author = new AuthorDto("John Doe", "https://example.com/avatar.jpg");
    List<String> tags = Arrays.asList("tech", "java");
    Instant publishedAt = Instant.parse("2024-01-15T10:00:00Z");

    BlogResponseDto dto =
        new BlogResponseDto(
            1L,
            "Test Blog",
            "Test excerpt",
            "https://example.com/cover.jpg",
            author,
            tags,
            publishedAt);

    assertEquals(1L, dto.getId());
    assertEquals("Test Blog", dto.getTitle());
    assertEquals("Test excerpt", dto.getExcerpt());
    assertEquals("https://example.com/cover.jpg", dto.getCoverImageUrl());
    assertEquals(author, dto.getAuthor());
    assertEquals(tags, dto.getTags());
    assertEquals(publishedAt, dto.getPublishedAt());
  }

  @Test
  void testBlogResponseDtoSetters() {
    BlogResponseDto dto = new BlogResponseDto();
    AuthorDto author = new AuthorDto("Jane Smith", "https://example.com/jane.jpg");
    List<String> tags = Arrays.asList("design");
    Instant publishedAt = Instant.parse("2024-02-20T15:30:00Z");

    dto.setId(2L);
    dto.setTitle("Another Blog");
    dto.setExcerpt("Another excerpt");
    dto.setCoverImageUrl("https://example.com/another.jpg");
    dto.setAuthor(author);
    dto.setTags(tags);
    dto.setPublishedAt(publishedAt);

    assertEquals(2L, dto.getId());
    assertEquals("Another Blog", dto.getTitle());
    assertEquals("Another excerpt", dto.getExcerpt());
    assertEquals("https://example.com/another.jpg", dto.getCoverImageUrl());
    assertEquals(author, dto.getAuthor());
    assertEquals(tags, dto.getTags());
    assertEquals(publishedAt, dto.getPublishedAt());
  }

  @Test
  void testBlogResponseDtoEqualsAndHashCode() {
    AuthorDto author1 = new AuthorDto("John Doe", "https://example.com/avatar.jpg");
    AuthorDto author2 = new AuthorDto("John Doe", "https://example.com/avatar.jpg");
    List<String> tags = Arrays.asList("tech");
    Instant publishedAt = Instant.parse("2024-01-15T10:00:00Z");

    BlogResponseDto dto1 =
        new BlogResponseDto(
            1L, "Test", "Excerpt", "https://example.com/cover.jpg", author1, tags, publishedAt);
    BlogResponseDto dto2 =
        new BlogResponseDto(
            1L, "Test", "Excerpt", "https://example.com/cover.jpg", author2, tags, publishedAt);
    BlogResponseDto dto3 =
        new BlogResponseDto(
            2L, "Different", "Excerpt", "https://example.com/other.jpg", author1, tags, publishedAt);

    assertEquals(dto1, dto2);
    assertEquals(dto1.hashCode(), dto2.hashCode());
    assertNotEquals(dto1, dto3);
  }

  @Test
  void testBlogResponseDtoWithNullCoverImageUrl() {
    AuthorDto author = new AuthorDto("John Doe", "https://example.com/avatar.jpg");
    List<String> tags = Arrays.asList("tech");
    Instant publishedAt = Instant.parse("2024-01-15T10:00:00Z");

    BlogResponseDto dto =
        new BlogResponseDto(1L, "Test", "Excerpt", null, author, tags, publishedAt);

    assertNull(dto.getCoverImageUrl());
  }

  @Test
  void testBlogResponseDtoToString() {
    AuthorDto author = new AuthorDto("John Doe", "https://example.com/avatar.jpg");
    List<String> tags = Arrays.asList("tech");
    Instant publishedAt = Instant.parse("2024-01-15T10:00:00Z");

    BlogResponseDto dto =
        new BlogResponseDto(
            1L, "Test", "Excerpt", "https://example.com/cover.jpg", author, tags, publishedAt);

    String result = dto.toString();

    assertTrue(result.contains("id=1"));
    assertTrue(result.contains("title='Test'"));
  }
}
