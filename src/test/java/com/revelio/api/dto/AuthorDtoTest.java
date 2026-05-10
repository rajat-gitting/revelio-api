package com.revelio.api.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class AuthorDtoTest {

  @Test
  void testAuthorDtoConstructorAndGetters() {
    AuthorDto dto = new AuthorDto("John Doe", "https://example.com/avatar.jpg");

    assertEquals("John Doe", dto.getName());
    assertEquals("https://example.com/avatar.jpg", dto.getAvatarUrl());
  }

  @Test
  void testAuthorDtoSetters() {
    AuthorDto dto = new AuthorDto();
    dto.setName("Jane Smith");
    dto.setAvatarUrl("https://example.com/jane.jpg");

    assertEquals("Jane Smith", dto.getName());
    assertEquals("https://example.com/jane.jpg", dto.getAvatarUrl());
  }

  @Test
  void testAuthorDtoEqualsAndHashCode() {
    AuthorDto dto1 = new AuthorDto("John Doe", "https://example.com/avatar.jpg");
    AuthorDto dto2 = new AuthorDto("John Doe", "https://example.com/avatar.jpg");
    AuthorDto dto3 = new AuthorDto("Jane Smith", "https://example.com/jane.jpg");

    assertEquals(dto1, dto2);
    assertEquals(dto1.hashCode(), dto2.hashCode());
    assertNotEquals(dto1, dto3);
  }

  @Test
  void testAuthorDtoWithNullAvatarUrl() {
    AuthorDto dto = new AuthorDto("John Doe", null);

    assertEquals("John Doe", dto.getName());
    assertNull(dto.getAvatarUrl());
  }

  @Test
  void testAuthorDtoToString() {
    AuthorDto dto = new AuthorDto("John Doe", "https://example.com/avatar.jpg");

    String result = dto.toString();

    assertTrue(result.contains("name='John Doe'"));
    assertTrue(result.contains("avatarUrl='https://example.com/avatar.jpg'"));
  }
}
