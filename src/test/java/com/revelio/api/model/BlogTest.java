package com.revelio.api.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

class BlogTest {

  @Test
  void testBlogConstructorAndGetters() {
    Blog.Author author = new Blog.Author("John Doe", "https://example.com/avatar.jpg");
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
    Blog.Author author = new Blog.Author("Jane Smith", "https://example.com/jane.jpg");
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
    Blog.Author author1 = new Blog.Author("John Doe", "https://example.com/avatar.jpg");
    Blog.Author author2 = new Blog.Author("John Doe", "https://example.com/avatar.jpg");
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
    Blog.Author author = new Blog.Author("John Doe", "https://example.com/avatar.jpg");
    List<String> tags = Arrays.asList("tech");
    Instant publishedAt = Instant.parse("2024-01-15T10:00:00Z");

    Blog blog =
        new Blog(1L, "Test Blog", "Excerpt", null, author, tags, publishedAt, true);

    assertNull(blog.getCoverImageUrl());
  }

  @Test
  void testBlogWithEmptyTags() {
    Blog.Author author = new Blog.Author("John Doe", "https://example.com/avatar.jpg");
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
  void testAuthorConstructorAndGetters() {
    Blog.Author author = new Blog.Author("John Doe", "https://example.com/avatar.jpg");

    assertEquals("John Doe", author.getName());
    assertEquals("https://example.com/avatar.jpg", author.getAvatarUrl());
  }

  @Test
  void testAuthorSetters() {
    Blog.Author author = new Blog.Author();
    author.setName("Jane Smith");
    author.setAvatarUrl("https://example.com/jane.jpg");

    assertEquals("Jane Smith", author.getName());
    assertEquals("https://example.com/jane.jpg", author.getAvatarUrl());
  }

  @Test
  void testAuthorEqualsAndHashCode() {
    Blog.Author author1 = new Blog.Author("John Doe", "https://example.com/avatar.jpg");
    Blog.Author author2 = new Blog.Author("John Doe", "https://example.com/avatar.jpg");
    Blog.Author author3 = new Blog.Author("Jane Smith", "https://example.com/jane.jpg");

    assertEquals(author1, author2);
    assertEquals(author1.hashCode(), author2.hashCode());
    assertNotEquals(author1, author3);
    assertNotEquals(author1.hashCode(), author3.hashCode());
  }

  @Test
  void testAuthorWithNullAvatarUrl() {
    Blog.Author author = new Blog.Author("John Doe", null);

    assertEquals("John Doe", author.getName());
    assertNull(author.getAvatarUrl());
  }

  @Test
  void testBlogToString() {
    Blog.Author author = new Blog.Author("John Doe", "https://example.com/avatar.jpg");
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
  void testAuthorToString() {
    Blog.Author author = new Blog.Author("John Doe", "https://example.com/avatar.jpg");

    String result = author.toString();

    assertTrue(result.contains("name='John Doe'"));
    assertTrue(result.contains("avatarUrl='https://example.com/avatar.jpg'"));
  }
}
