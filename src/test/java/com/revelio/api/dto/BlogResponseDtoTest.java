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

    Blog blog = new Blog(1L, "Test Title", "Test excerpt", null, author, tags, publishedAt, true);

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

  // --- readingTimeMinutes tests (AC 1-4) ---

  @Test
  void testReadingTimeMinutesNullBodyIsOne() {
    // AC 3: null body → readingTimeMinutes == 1
    Blog blog = new Blog(1L, "Title", "Excerpt", null, null, List.of(), Instant.now(), true, null);
    BlogResponseDto dto = BlogResponseDto.fromBlog(blog);
    assertEquals(1, dto.getReadingTimeMinutes());
  }

  @Test
  void testReadingTimeMinutesBlankBodyIsOne() {
    // AC 3: blank body → readingTimeMinutes == 1
    Blog blog = new Blog(1L, "Title", "Excerpt", null, null, List.of(), Instant.now(), true, "   ");
    BlogResponseDto dto = BlogResponseDto.fromBlog(blog);
    assertEquals(1, dto.getReadingTimeMinutes());
  }

  @Test
  void testReadingTimeMinutesEmptyBodyIsOne() {
    // AC 3: empty body → readingTimeMinutes == 1
    Blog blog = new Blog(1L, "Title", "Excerpt", null, null, List.of(), Instant.now(), true, "");
    BlogResponseDto dto = BlogResponseDto.fromBlog(blog);
    assertEquals(1, dto.getReadingTimeMinutes());
  }

  @Test
  void testReadingTimeMinutesCalculationCeilDiv200() {
    // AC 2: Math.ceil(wordCount / 200), clamped to min 1
    // 200 words → 1 min, 201 words → 2 min, 400 words → 2 min, 401 words → 3 min
    String body200 = "word ".repeat(200).trim();
    Blog blog200 =
        new Blog(1L, "Title", "Excerpt", null, null, List.of(), Instant.now(), true, body200);
    assertEquals(1, BlogResponseDto.fromBlog(blog200).getReadingTimeMinutes());

    String body201 = "word ".repeat(201).trim();
    Blog blog201 =
        new Blog(2L, "Title", "Excerpt", null, null, List.of(), Instant.now(), true, body201);
    assertEquals(2, BlogResponseDto.fromBlog(blog201).getReadingTimeMinutes());

    String body400 = "word ".repeat(400).trim();
    Blog blog400 =
        new Blog(3L, "Title", "Excerpt", null, null, List.of(), Instant.now(), true, body400);
    assertEquals(2, BlogResponseDto.fromBlog(blog400).getReadingTimeMinutes());

    String body401 = "word ".repeat(401).trim();
    Blog blog401 =
        new Blog(4L, "Title", "Excerpt", null, null, List.of(), Instant.now(), true, body401);
    assertEquals(3, BlogResponseDto.fromBlog(blog401).getReadingTimeMinutes());
  }

  @Test
  void testReadingTimeMinutesFieldPresentInDto() {
    // AC 1: readingTimeMinutes is a top-level field on BlogResponseDto
    Blog blog =
        new Blog(
            1L, "Title", "Excerpt", null, null, List.of(), Instant.now(), true, "one two three");
    BlogResponseDto dto = BlogResponseDto.fromBlog(blog);
    // 3 words / 200 = 0.015 → ceil = 1, min 1 → 1
    assertEquals(1, dto.getReadingTimeMinutes());
  }

  @Test
  void testReadingTimeMinutesIncludedInEqualsAndHashCode() {
    // AC: equals() and hashCode() include readingTimeMinutes
    Blog blog =
        new Blog(
            1L, "Title", "Excerpt", null, null, List.of(), Instant.now(), true, "one two three");
    BlogResponseDto dto1 = BlogResponseDto.fromBlog(blog);
    BlogResponseDto dto2 = BlogResponseDto.fromBlog(blog);
    assertEquals(dto1, dto2);
    assertEquals(dto1.hashCode(), dto2.hashCode());

    dto2.setReadingTimeMinutes(99);
    assertNotEquals(dto1, dto2);
  }
}
