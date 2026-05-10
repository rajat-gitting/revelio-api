package com.revelio.api.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

class BlogTest {

  @Test
  void testBlogConstructorAndGetters() {
    Author author = new Author(1L, "John Doe", "https://example.com/avatar.jpg");
    List<String> tags = Arrays.asList("tech", "java", "spring");
    Instant publishedAt = Instant.parse("2024-01-15T10:00:00Z");

    Blog blog =
        new Blog(
            1L,
            "Test Blog Post",
            "This is a test excerpt",
            "https://example.com/cover.jpg",
            author,
            tags,
            publishedAt,
            true);

    assertEquals(1L, blog.getId());
    assertEquals("Test Blog Post", blog.getTitle());
    assertEquals("This is a test excerpt", blog.getExcerpt());
    assertEquals("https://example.com/cover.jpg", blog.getCoverImageUrl());
    assertEquals(author, blog.getAuthor());
    assertEquals(tags, blog.getTags());
    assertEquals(publishedAt, blog.getPublishedAt());
    assertTrue(blog.isPublished());
  }

  @Test
  void testBlogSetters() {
    Blog blog = new Blog();
    Author author = new Author(2L, "Jane Smith", "https://example.com/jane.jpg");
    List<String> tags = Arrays.asList("design", "ui");
    Instant publishedAt = Instant.parse("2024-02-20T15:30:00Z");

    blog.setId(2L);
    blog.setTitle("Another Post");
    blog.setExcerpt("Another excerpt");
    blog.setCoverImageUrl("https://example.com/another.jpg");
    blog.setAuthor(author);
    blog.setTags(tags);
    blog.setPublishedAt(publishedAt);
    blog.setPublished(false);

    assertEquals(2L, blog.getId());
    assertEquals("Another Post", blog.getTitle());
    assertEquals("Another excerpt", blog.getExcerpt());
    assertEquals("https://example.com/another.jpg", blog.getCoverImageUrl());
    assertEquals(author, blog.getAuthor());
    assertEquals(tags, blog.getTags());
    assertEquals(publishedAt, blog.getPublishedAt());
    assertFalse(blog.isPublished());
  }

  @Test
  void testBlogEqualsAndHashCode() {
    Author author1 = new Author(1L, "John Doe", "https://example.com/avatar.jpg");
    Author author2 = new Author(1L, "John Doe", "https://example.com/avatar.jpg");
    List<String> tags = Arrays.asList("tech", "java");
    Instant publishedAt = Instant.parse("2024-01-15T10:00:00Z");

    Blog blog1 =
        new Blog(
            1L,
            "Test Blog",
            "Excerpt",
            "https://example.com/cover.jpg",
            author1,
            tags,
            publishedAt,
            true);
    Blog blog2 =
        new Blog(
            1L,
            "Test Blog",
            "Excerpt",
            "https://example.com/cover.jpg",
            author2,
            tags,
            publishedAt,
            true);
    Blog blog3 =
        new Blog(
            2L,
            "Different Blog",
            "Different excerpt",
            "https://example.com/other.jpg",
            author1,
            tags,
            publishedAt,
            false);

    assertEquals(blog1, blog2);
    assertEquals(blog1.hashCode(), blog2.hashCode());
    assertNotEquals(blog1, blog3);
    assertNotEquals(blog1.hashCode(), blog3.hashCode());
  }

  @Test
  void testBlogWithNullCoverImageUrl() {
    Author author = new Author(1L, "John Doe", "https://example.com/avatar.jpg");
    List<String> tags = Arrays.asList("tech");
    Instant publishedAt = Instant.parse("2024-01-15T10:00:00Z");

    Blog blog = new Blog(1L, "Test Blog", "Excerpt", null, author, tags, publishedAt, true);

    assertNull(blog.getCoverImageUrl());
  }

  @Test
  void testBlogWithEmptyTags() {
    Author author = new Author(1L, "John Doe", "https://example.com/avatar.jpg");
    List<String> tags = Arrays.asList();
    Instant publishedAt = Instant.parse("2024-01-15T10:00:00Z");

    Blog blog =
        new Blog(
            1L,
            "Test Blog",
            "Excerpt",
            "https://example.com/cover.jpg",
            author,
            tags,
            publishedAt,
            true);

    assertTrue(blog.getTags().isEmpty());
  }

  @Test
  void testBlogToString() {
    Author author = new Author(1L, "John Doe", "https://example.com/avatar.jpg");
    List<String> tags = Arrays.asList("tech", "java");
    Instant publishedAt = Instant.parse("2024-01-15T10:00:00Z");

    Blog blog =
        new Blog(
            1L,
            "Test Blog",
            "Excerpt",
            "https://example.com/cover.jpg",
            author,
            tags,
            publishedAt,
            true);

    String result = blog.toString();

    assertTrue(result.contains("id=1"));
    assertTrue(result.contains("title='Test Blog'"));
    assertTrue(result.contains("published=true"));
  }

  @Test
  void testBlogWithAuthorHavingNullAvatarUrl() {
    Author author = new Author(1L, "John Doe", null);
    List<String> tags = Arrays.asList("tech");
    Instant publishedAt = Instant.parse("2024-01-15T10:00:00Z");

    Blog blog =
        new Blog(
            1L,
            "Test Blog",
            "Excerpt",
            "https://example.com/cover.jpg",
            author,
            tags,
            publishedAt,
            true);

    assertNull(blog.getAuthor().getAvatarUrl());
    assertEquals("John Doe", blog.getAuthor().getName());
  }
}
