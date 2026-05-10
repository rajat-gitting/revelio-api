package com.revelio.api.service;

import com.revelio.api.model.Blog;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class BlogService {

  private final List<Blog> blogRepository;

  public BlogService() {
    this.blogRepository = new ArrayList<>();
  }

  public BlogService(List<Blog> blogRepository) {
    this.blogRepository = blogRepository != null ? blogRepository : new ArrayList<>();
  }

  public List<Blog> getPublishedBlogs(int page, int size) {
    if (page < 0) {
      throw new IllegalArgumentException("Page number must be non-negative");
    }
    if (size <= 0) {
      throw new IllegalArgumentException("Page size must be positive");
    }

    List<Blog> publishedBlogs = filterPublishedPosts(blogRepository);
    List<Blog> sortedBlogs = sortByPublishedAtDescending(publishedBlogs);

    int startIndex = page * size;
    if (startIndex >= sortedBlogs.size()) {
      return new ArrayList<>();
    }

    int endIndex = Math.min(startIndex + size, sortedBlogs.size());
    return new ArrayList<>(sortedBlogs.subList(startIndex, endIndex));
  }

  public List<Blog> filterPublishedPosts(List<Blog> posts) {
    if (posts == null) {
      return new ArrayList<>();
    }
    return posts.stream().filter(Blog::isPublished).collect(Collectors.toList());
  }

  private List<Blog> sortByPublishedAtDescending(List<Blog> posts) {
    return posts.stream()
        .sorted(Comparator.comparing(Blog::getPublishedAt).reversed())
        .collect(Collectors.toList());
  }
}
