package com.revelio.api.controller;

import com.revelio.api.dto.AuthorDto;
import com.revelio.api.dto.BlogResponseDto;
import com.revelio.api.dto.PagedBlogResponseDto;
import com.revelio.api.model.Blog;
import com.revelio.api.service.BlogService;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/blogs")
public class BlogController {

  private final BlogService blogService;
  private final List<Blog> allBlogs;

  public BlogController(BlogService blogService, List<Blog> allBlogs) {
    this.blogService = blogService;
    this.allBlogs = allBlogs;
  }

  @GetMapping
  public ResponseEntity<PagedBlogResponseDto> getBlogs(
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {

    try {
      List<Blog> publishedBlogs = blogService.getPublishedBlogs(allBlogs, page, size);

      List<BlogResponseDto> content =
          publishedBlogs.stream().map(this::convertToDto).collect(Collectors.toList());

      long totalPublished = allBlogs.stream().filter(Blog::isPublished).count();

      int totalPages = (int) Math.ceil((double) totalPublished / size);
      boolean hasMore = page < totalPages - 1;

      PagedBlogResponseDto response =
          new PagedBlogResponseDto(content, page, size, totalPublished, totalPages, hasMore);

      return ResponseEntity.ok(response);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().build();
    }
  }

  private BlogResponseDto convertToDto(Blog blog) {
    AuthorDto authorDto =
        new AuthorDto(blog.getAuthor().getName(), blog.getAuthor().getAvatarUrl());

    return new BlogResponseDto(
        blog.getId(),
        blog.getTitle(),
        blog.getExcerpt(),
        blog.getCoverImageUrl(),
        authorDto,
        blog.getTags(),
        blog.getPublishedAt());
  }
}
