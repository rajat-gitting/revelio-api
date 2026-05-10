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
            "Test Title",
            "Test excerpt content",
            "https://example.com/cover.jpg",
            author,
            tags,
            publishedAt,
            true);

    assertEquals(1L, blog.getId());
    assertEquals("Test Title", blog.getTitle());
    assertEquals("Test excerpt content", blog.getExcerpt());
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
    blog.setTitle("Updated Title");
    blog.setExcerpt("Updated excerpt");
    blog.setCoverImageUrl("https://example.com/new-cover.jpg");
    blog.setAuthor(author);
    blog.setTags(tags);
    blog.setPublishedAt(publishedAt);
    blog.setPublished(false);

    assertEquals(2L, blog.getId());
    assertEquals("Updated Title", blog.getTitle());
    assertEquals("Updated excerpt", blog.getExcerpt());
    assertEquals("https://example.com/new-cover.jpg", blog.getCoverImageUrl());
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
            "Test Title",
            "Test excerpt",
            "https://example.com/cover.jpg",
            author1,
            tags,
            publishedAt,
            true);
    Blog blog2 =
        new Blog(
            1L,
            "Test Title",
            "Test excerpt",
            "https://example.com/cover.jpg",
            author2,
            tags,
            publishedAt,
            true);
    Blog blog3 =
        new Blog(
            2L,
            "Different Title",
            "Different excerpt",
            "https://example.com/other.jpg",
            author1,
            tags,
            publishedAt,
            false);

    assertEquals(blog1, blog2);
    assertNotEquals(blog1, blog3);
    assertEquals(blog1.hashCode(), blog2.hashCode());
    assertNotEquals(blog1.hashCode(), blog3.hashCode());
  }

  @Test
  void testBlogWithNullCoverImageUrl() {
    Blog.Author author = new Blog.Author("John Doe", "https://example.com/avatar.jpg");
    List<String> tags = Arrays.asList("tech");
    Instant publishedAt = Instant.parse("2024-01-15T10:00:00Z");

    Blog blog =
        new Blog(1L, "Test Title", "Test excerpt", null, author, tags, publishedAt, true);

    assertNull(blog.getCoverImageUrl());
  }

  @Test
  void testAuthorConstructorAndGetters() {
    Blog.Author author = new Blog.Author("Alice Johnson", "https://example.com/alice.jpg");

    assertEquals("Alice Johnson", author.getName());
    assertEquals("https://example.com/alice.jpg", author.getAvatarUrl());
  }

  @Test
  void testAuthorSetters() {
    Blog.Author author = new Blog.Author();

    author.setName("Bob Williams");
    author.setAvatarUrl("https://example.com/bob.jpg");

    assertEquals("Bob Williams", author.getName());
    assertEquals("https://example.com/bob.jpg", author.getAvatarUrl());
  }

  @Test
  void testAuthorEqualsAndHashCode() {
    Blog.Author author1 = new Blog.Author("John Doe", "https://example.com/avatar.jpg");
    Blog.Author author2 = new Blog.Author("John Doe", "https://example.com/avatar.jpg");
    Blog.Author author3 = new Blog.Author("Jane Smith", "https://example.com/jane.jpg");

    assertEquals(author1, author2);
    assertNotEquals(author1, author3);
    assertEquals(author1.hashCode(), author2.hashCode());
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
            "Test Title",
            "Test excerpt",
            "https://example.com/cover.jpg",
            author,
            tags,
            publishedAt,
            true);

    String toString = blog.toString();
    assertTrue(toString.contains("id=1"));
    assertTrue(toString.contains("title='Test Title'"));
    assertTrue(toString.contains("published=true"));
  }

  @Test
  void testAuthorToString() {
    Blog.Author author = new Blog.Author("John Doe", "https://example.com/avatar.jpg");

    String toString = author.toString();
    assertTrue(toString.contains("name='John Doe'"));
    assertTrue(toString.contains("avatarUrl='https://example.com/avatar.jpg'"));
  }
}
