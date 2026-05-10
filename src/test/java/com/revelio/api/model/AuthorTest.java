package com.revelio.api.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class AuthorTest {

  @Test
  void testAuthorConstructorAndGetters() {
    Author author = new Author(1L, "John Doe", "https://example.com/avatar.jpg");

    assertEquals(1L, author.getId());
    assertEquals("John Doe", author.getName());
    assertEquals("https://example.com/avatar.jpg", author.getAvatarUrl());
  }

  @Test
  void testAuthorSetters() {
    Author author = new Author();
    author.setId(2L);
    author.setName("Jane Smith");
    author.setAvatarUrl("https://example.com/jane.jpg");

    assertEquals(2L, author.getId());
    assertEquals("Jane Smith", author.getName());
    assertEquals("https://example.com/jane.jpg", author.getAvatarUrl());
  }

  @Test
  void testAuthorEqualsAndHashCode() {
    Author author1 = new Author(1L, "John Doe", "https://example.com/avatar.jpg");
    Author author2 = new Author(1L, "John Doe", "https://example.com/avatar.jpg");
    Author author3 = new Author(2L, "Jane Smith", "https://example.com/jane.jpg");

    assertEquals(author1, author2);
    assertEquals(author1.hashCode(), author2.hashCode());
    assertNotEquals(author1, author3);
    assertNotEquals(author1.hashCode(), author3.hashCode());
  }

  @Test
  void testAuthorWithNullAvatarUrl() {
    Author author = new Author(1L, "John Doe", null);

    assertEquals(1L, author.getId());
    assertEquals("John Doe", author.getName());
    assertNull(author.getAvatarUrl());
  }

  @Test
  void testAuthorWithNullId() {
    Author author = new Author(null, "John Doe", "https://example.com/avatar.jpg");

    assertNull(author.getId());
    assertEquals("John Doe", author.getName());
    assertEquals("https://example.com/avatar.jpg", author.getAvatarUrl());
  }

  @Test
  void testAuthorToString() {
    Author author = new Author(1L, "John Doe", "https://example.com/avatar.jpg");

    String result = author.toString();

    assertTrue(result.contains("id=1"));
    assertTrue(result.contains("name='John Doe'"));
    assertTrue(result.contains("avatarUrl='https://example.com/avatar.jpg'"));
  }

  @Test
  void testAuthorEqualsWithSameInstance() {
    Author author = new Author(1L, "John Doe", "https://example.com/avatar.jpg");

    assertEquals(author, author);
  }

  @Test
  void testAuthorEqualsWithNull() {
    Author author = new Author(1L, "John Doe", "https://example.com/avatar.jpg");

    assertNotEquals(author, null);
  }

  @Test
  void testAuthorEqualsWithDifferentClass() {
    Author author = new Author(1L, "John Doe", "https://example.com/avatar.jpg");
    String notAnAuthor = "Not an Author";

    assertNotEquals(author, notAnAuthor);
  }

  @Test
  void testAuthorEqualsWithDifferentId() {
    Author author1 = new Author(1L, "John Doe", "https://example.com/avatar.jpg");
    Author author2 = new Author(2L, "John Doe", "https://example.com/avatar.jpg");

    assertNotEquals(author1, author2);
  }

  @Test
  void testAuthorEqualsWithDifferentName() {
    Author author1 = new Author(1L, "John Doe", "https://example.com/avatar.jpg");
    Author author2 = new Author(1L, "Jane Smith", "https://example.com/avatar.jpg");

    assertNotEquals(author1, author2);
  }

  @Test
  void testAuthorEqualsWithDifferentAvatarUrl() {
    Author author1 = new Author(1L, "John Doe", "https://example.com/avatar.jpg");
    Author author2 = new Author(1L, "John Doe", "https://example.com/different.jpg");

    assertNotEquals(author1, author2);
  }
}
