package com.revelio.api.dto;

import static org.junit.jupiter.api.Assertions.*;

import com.revelio.api.model.Blog;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

class BlogResponseDtoTest {

  @Test
  void testFromBlogConvertsAllFields() {
    Blog.Author author = new Blog.Author("John Doe", "https://example.com/avatar.jpg");
    List<String> tags = Arrays.asList("tech", "java");
    Instant publishedAt = Instant.parse("2024-01-15T10:00:00Z");

    Blog blog =
        new Blog(
            1L,
            "Test Title",
            "Test excerpt",
            "https://example.com/cover.jpg",
            author,
            tags,
            publishedAt,
            true);

    BlogResponseDto dto = BlogResponseDto.fromBlog(blog);

    assertNotNull(dto);
    assertEquals(1L, dto.getId());
    assertEquals("Test Title", dto.getTitle());
    assertEquals("Test excerpt", dto.getExcerpt());
    assertEquals("https://example.com/cover.jpg", dto.getCoverImageUrl());
    assertNotNull(dto.getAuthor());
    assertEquals("John Doe", dto.getAuthor().getName());
    assertEquals("https://example.com/avatar.jpg", dto.getAuthor().getAvatarUrl());
    assertEquals(tags, dto.getTags());
    assertEquals(publishedAt, dto.getPublishedAt());
  }

  @Test
  void testFromBlogWithNullBlog() {
    BlogResponseDto dto = BlogResponseDto.fromBlog(null);

    assertNull(dto);
  }

  @Test
  void testFromBlogWithNullCoverImageUrl() {
    Blog.Author author = new Blog.Author("John Doe", "https://example.com/avatar.jpg");
    List<String> tags = Arrays.asList("tech");
    Instant publishedAt = Instant.parse("2024-01-15T10:00:00Z");

    Blog blog =
        new Blog(1L, "Test Title", "Test excerpt", null, author, tags, publishedAt, true);

    BlogResponseDto dto = BlogResponseDto.fromBlog(blog);

    assertNotNull(dto);
    assertNull(dto.getCoverImageUrl());
  }

  @Test
  void testFromBlogWithNullAuthor() {
    List<String> tags = Arrays.asList("tech");
    Instant publishedAt = Instant.parse("2024-01-15T10:00:00Z");

    Blog blog =
        new Blog(
            1L,
            "Test Title",
            "Test excerpt",
            "https://example.com/cover.jpg",
            null,
            tags,
            publishedAt,
            true);

    BlogResponseDto dto = BlogResponseDto.fromBlog(blog);

    assertNotNull(dto);
    assertNull(dto.getAuthor());
  }

  @Test
  void testBlogResponseDtoConstructorAndGetters() {
    BlogResponseDto.AuthorDto author =
        new BlogResponseDto.AuthorDto("John Doe", "https://example.com/avatar.jpg");
    List<String> tags = Arrays.asList("tech", "java");
    Instant publishedAt = Instant.parse("2024-01-15T10:00:00Z");

    BlogResponseDto dto =
        new BlogResponseDto(
            1L,
            "Test Title",
            "Test excerpt",
            "https://example.com/cover.jpg",
            author,
            tags,
            publishedAt);

    assertEquals(1L, dto.getId());
    assertEquals("Test Title", dto.getTitle());
    assertEquals("Test excerpt", dto.getExcerpt());
    assertEquals("https://example.com/cover.jpg", dto.getCoverImageUrl());
    assertEquals(author, dto.getAuthor());
    assertEquals(tags, dto.getTags());
    assertEquals(publishedAt, dto.getPublishedAt());
  }

  @Test
  void testBlogResponseDtoSetters() {
    BlogResponseDto dto = new BlogResponseDto();
    BlogResponseDto.AuthorDto author =
        new BlogResponseDto.AuthorDto("Jane Smith", "https://example.com/jane.jpg");
    List<String> tags = Arrays.asList("design");
    Instant publishedAt = Instant.parse("2024-02-20T15:30:00Z");

    dto.setId(2L);
    dto.setTitle("Updated Title");
    dto.setExcerpt("Updated excerpt");
    dto.setCoverImageUrl("https://example.com/updated.jpg");
    dto.setAuthor(author);
    dto.setTags(tags);
    dto.setPublishedAt(publishedAt);

    assertEquals(2L, dto.getId());
    assertEquals("Updated Title", dto.getTitle());
    assertEquals("Updated excerpt", dto.getExcerpt());
    assertEquals("https://example.com/updated.jpg", dto.getCoverImageUrl());
    assertEquals(author, dto.getAuthor());
    assertEquals(tags, dto.getTags());
    assertEquals(publishedAt, dto.getPublishedAt());
  }

  @Test
  void testBlogResponseDtoEqualsAndHashCode() {
    BlogResponseDto.AuthorDto author1 =
        new BlogResponseDto.AuthorDto("John Doe", "https://example.com/avatar.jpg");
    BlogResponseDto.AuthorDto author2 =
        new BlogResponseDto.AuthorDto("John Doe", "https://example.com/avatar.jpg");
    List<String> tags = Arrays.asList("tech");
    Instant publishedAt = Instant.parse("2024-01-15T10:00:00Z");

    BlogResponseDto dto1 =
        new BlogResponseDto(
            1L,
            "Test Title",
            "Test excerpt",
            "https://example.com/cover.jpg",
            author1,
            tags,
            publishedAt);
    BlogResponseDto dto2 =
        new BlogResponseDto(
            1L,
            "Test Title",
            "Test excerpt",
            "https://example.com/cover.jpg",
            author2,
            tags,
            publishedAt);
    BlogResponseDto dto3 =
        new BlogResponseDto(
            2L,
            "Different Title",
            "Different excerpt",
            "https://example.com/other.jpg",
            author1,
            tags,
            publishedAt);

    assertEquals(dto1, dto2);
    assertNotEquals(dto1, dto3);
    assertEquals(dto1.hashCode(), dto2.hashCode());
    assertNotEquals(dto1.hashCode(), dto3.hashCode());
  }

  @Test
  void testAuthorDtoConstructorAndGetters() {
    BlogResponseDto.AuthorDto author =
        new BlogResponseDto.AuthorDto("John Doe", "https://example.com/avatar.jpg");

    assertEquals("John Doe", author.getName());
    assertEquals("https://example.com/avatar.jpg", author.getAvatarUrl());
  }

  @Test
  void testAuthorDtoSetters() {
    BlogResponseDto.AuthorDto author = new BlogResponseDto.AuthorDto();

    author.setName("Jane Smith");
    author.setAvatarUrl("https://example.com/jane.jpg");

    assertEquals("Jane Smith", author.getName());
    assertEquals("https://example.com/jane.jpg", author.getAvatarUrl());
  }

  @Test
  void testAuthorDtoEqualsAndHashCode() {
    BlogResponseDto.AuthorDto author1 =
        new BlogResponseDto.AuthorDto("John Doe", "https://example.com/avatar.jpg");
    BlogResponseDto.AuthorDto author2 =
        new BlogResponseDto.AuthorDto("John Doe", "https://example.com/avatar.jpg");
    BlogResponseDto.AuthorDto author3 =
        new BlogResponseDto.AuthorDto("Jane Smith", "https://example.com/jane.jpg");

    assertEquals(author1, author2);
    assertNotEquals(author1, author3);
    assertEquals(author1.hashCode(), author2.hashCode());
    assertNotEquals(author1.hashCode(), author3.hashCode());
  }
}
