package com.revelio.api.service;

import com.revelio.api.model.Blog;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class BlogService {

  public List<Blog> getPublishedBlogs(List<Blog> allBlogs, int page, int size) {
    if (page < 0) {
      throw new IllegalArgumentException("Page number must be non-negative");
    }
    if (size <= 0) {
      throw new IllegalArgumentException("Page size must be positive");
    }

    return allBlogs.stream()
        .filter(Blog::isPublished)
        .sorted(Comparator.comparing(Blog::getPublishedAt).reversed())
        .skip((long) page * size)
        .limit(size)
        .collect(Collectors.toList());
  }
}
