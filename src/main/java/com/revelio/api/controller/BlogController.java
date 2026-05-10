package com.revelio.api.controller;

import com.revelio.api.dto.BlogResponseDto;
import com.revelio.api.model.Blog;
import com.revelio.api.service.BlogService;
import java.util.List;
import java.util.stream.Collectors;

public class BlogController {

  private final BlogService blogService;

  public BlogController(BlogService blogService) {
    this.blogService = blogService;
  }

  public List<BlogResponseDto> getBlogs(int page, int size) {
    List<Blog> blogs = blogService.getPublishedBlogs(page, size);
    return blogs.stream().map(BlogResponseDto::fromBlog).collect(Collectors.toList());
  }
}
