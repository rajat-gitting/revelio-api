package com.revelio.api.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

class BlogDtoTest {

  @Test
  void testBlogDtoConstructorAndGetters() {
    BlogDto.AuthorDto author = new BlogDto.AuthorDto("John Doe", "https://example.com/avatar.jpg");
    List<String> tags = Arrays.asList("tech", "java", "spring");
    Instant publishedAt = Instant.parse("2024-01-15T10:00:00Z");

    BlogDto blogDto =
        new BlogDto(
            1L,
            "Test Title",
            "Test excerpt content",
            "https://example.com/cover.jpg",
            author,
            tags,
            publishedAt);

    assertEquals(1L, blogDto.getId());
    assertEquals("Test Title", blogDto.getTitle());
    assertEquals("Test excerpt content", blogDto.getExcerpt());
    assertEquals("https://example.com/cover.jpg", blogDto.getCoverImageUrl());
    assertEquals(author, blogDto.getAuthor());
    assertEquals(tags, blogDto.getTags());
    assertEquals(publishedAt, blogDto.getPublishedAt());
  }

  @Test
  void testBlogDtoSetters() {
    BlogDto blogDto = new BlogDto();
    BlogDto.AuthorDto author = new BlogDto.AuthorDto("Jane Smith", "https://example.com/jane.jpg");
    List<String> tags = Arrays.asList("design", "ui");
    Instant publishedAt = Instant.parse("2024-02-20T15:30:00Z");

    blogDto.setId(2L);
    blogDto.setTitle("Updated Title");
    blogDto.setExcerpt("Updated excerpt");
    blogDto.setCoverImageUrl("https://example.com/new-cover.jpg");
    blogDto.setAuthor(author);
    blogDto.setTags(tags);
    blogDto.setPublishedAt(publishedAt);

    assertEquals(2L, blogDto.getId());
    assertEquals("Updated Title", blogDto.getTitle());
    assertEquals("Updated excerpt", blogDto.getExcerpt());
    assertEquals("https://example.com/new-cover.jpg", blogDto.getCoverImageUrl());
    assertEquals(author, blogDto.getAuthor());
    assertEquals(tags, blogDto.getTags());
    assertEquals(publishedAt, blogDto.getPublishedAt());
  }

  @Test
  void testBlogDtoEqualsAndHashCode() {
    BlogDto.AuthorDto author1 = new BlogDto.AuthorDto("John Doe", "https://example.com/avatar.jpg");
    BlogDto.AuthorDto author2 = new BlogDto.AuthorDto("John Doe", "https://example.com/avatar.jpg");
    List<String> tags = Arrays.asList("tech", "java");
    Instant publishedAt = Instant.parse("2024-01-15T10:00:00Z");

    BlogDto blogDto1 =
        new BlogDto(
            1L,
            "Test Title",
            "Test excerpt",
            "https://example.com/cover.jpg",
            author1,
            tags,
            publishedAt);
    BlogDto blogDto2 =
        new BlogDto(
            1L,
            "Test Title",
            "Test excerpt",
            "https://example.com/cover.jpg",
            author2,
            tags,
            publishedAt);
    BlogDto blogDto3 =
        new BlogDto(
            2L,
            "Different Title",
            "Different excerpt",
            "https://example.com/other.jpg",
            author1,
            tags,
            publishedAt);

    assertEquals(blogDto1, blogDto2);
    assertNotEquals(blogDto1, blogDto3);
    assertEquals(blogDto1.hashCode(), blogDto2.hashCode());
    assertNotEquals(blogDto1.hashCode(), blogDto3.hashCode());
  }

  @Test
  void testBlogDtoWithNullCoverImageUrl() {
    BlogDto.AuthorDto author = new BlogDto.AuthorDto("John Doe", "https://example.com/avatar.jpg");
    List<String> tags = Arrays.asList("tech");
    Instant publishedAt = Instant.parse("2024-01-15T10:00:00Z");

    BlogDto blogDto =
        new BlogDto(1L, "Test Title", "Test excerpt", null, author, tags, publishedAt);

    assertNull(blogDto.getCoverImageUrl());
  }

  @Test
  void testBlogDtoToString() {
    BlogDto.AuthorDto author = new BlogDto.AuthorDto("John Doe", "https://example.com/avatar.jpg");
    List<String> tags = Arrays.asList("tech", "java");
    Instant publishedAt = Instant.parse("2024-01-15T10:00:00Z");

    BlogDto blogDto =
        new BlogDto(
            1L,
            "Test Title",
            "Test excerpt",
            "https://example.com/cover.jpg",
            author,
            tags,
            publishedAt);

    String toString = blogDto.toString();
    assertTrue(toString.contains("id=1"));
    assertTrue(toString.contains("title='Test Title'"));
    assertTrue(toString.contains("excerpt='Test excerpt'"));
  }

  @Test
  void testAuthorDtoConstructorAndGetters() {
    BlogDto.AuthorDto author = new BlogDto.AuthorDto("Alice Johnson", "https://example.com/alice.jpg");

    assertEquals("Alice Johnson", author.getName());
    assertEquals("https://example.com/alice.jpg", author.getAvatarUrl());
  }

  @Test
  void testAuthorDtoSetters() {
    BlogDto.AuthorDto author = new BlogDto.AuthorDto();

    author.setName("Bob Williams");
    author.setAvatarUrl("https://example.com/bob.jpg");

    assertEquals("Bob Williams", author.getName());
    assertEquals("https://example.com/bob.jpg", author.getAvatarUrl());
  }

  @Test
  void testAuthorDtoEqualsAndHashCode() {
    BlogDto.AuthorDto author1 = new BlogDto.AuthorDto("John Doe", "https://example.com/avatar.jpg");
    BlogDto.AuthorDto author2 = new BlogDto.AuthorDto("John Doe", "https://example.com/avatar.jpg");
    BlogDto.AuthorDto author3 = new BlogDto.AuthorDto("Jane Smith", "https://example.com/jane.jpg");

    assertEquals(author1, author2);
    assertNotEquals(author1, author3);
    assertEquals(author1.hashCode(), author2.hashCode());
    assertNotEquals(author1.hashCode(), author3.hashCode());
  }

  @Test
  void testAuthorDtoWithNullAvatarUrl() {
    BlogDto.AuthorDto author = new BlogDto.AuthorDto("John Doe", null);

    assertEquals("John Doe", author.getName());
    assertNull(author.getAvatarUrl());
  }

  @Test
  void testAuthorDtoToString() {
    BlogDto.AuthorDto author = new BlogDto.AuthorDto("John Doe", "https://example.com/avatar.jpg");

    String toString = author.toString();
    assertTrue(toString.contains("name='John Doe'"));
    assertTrue(toString.contains("avatarUrl='https://example.com/avatar.jpg'"));
  }
}
