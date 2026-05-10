package com.revelio.api.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class BlogListRequestTest {

  @Test
  void testDefaultConstructorSetsDefaultValues() {
    BlogListRequest request = new BlogListRequest();

    assertEquals(0, request.getPage());
    assertEquals(10, request.getSize());
  }

  @Test
  void testParameterizedConstructor() {
    BlogListRequest request = new BlogListRequest(2, 20);

    assertEquals(2, request.getPage());
    assertEquals(20, request.getSize());
  }

  @Test
  void testSetters() {
    BlogListRequest request = new BlogListRequest();

    request.setPage(5);
    request.setSize(15);

    assertEquals(5, request.getPage());
    assertEquals(15, request.getSize());
  }

  @Test
  void testEqualsAndHashCode() {
    BlogListRequest request1 = new BlogListRequest(1, 10);
    BlogListRequest request2 = new BlogListRequest(1, 10);
    BlogListRequest request3 = new BlogListRequest(2, 20);

    assertEquals(request1, request2);
    assertNotEquals(request1, request3);
    assertEquals(request1.hashCode(), request2.hashCode());
    assertNotEquals(request1.hashCode(), request3.hashCode());
  }

  @Test
  void testToString() {
    BlogListRequest request = new BlogListRequest(3, 25);

    String toString = request.toString();
    assertTrue(toString.contains("page=3"));
    assertTrue(toString.contains("size=25"));
  }

  @Test
  void testPageStartsAtZero() {
    BlogListRequest request = new BlogListRequest(0, 10);

    assertEquals(0, request.getPage());
  }
}
