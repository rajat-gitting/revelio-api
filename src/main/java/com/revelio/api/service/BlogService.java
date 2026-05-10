package com.revelio.api.service;

import com.revelio.api.dto.BlogDto;
import com.revelio.api.dto.BlogListRequest;
import com.revelio.api.dto.BlogListResponse;
import com.revelio.api.model.Blog;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class BlogService {
  private final List<Blog> blogRepository;

  public BlogService(List<Blog> blogRepository) {
    this.blogRepository = blogRepository;
  }

  public BlogListResponse getPublishedBlogs(BlogListRequest request) {
    List<Blog> publishedBlogs = filterPublishedPosts(blogRepository);
    List<Blog> sortedBlogs = sortByPublishedAtDescending(publishedBlogs);

    int totalElements = sortedBlogs.size();
    int totalPages = (int) Math.ceil((double) totalElements / request.getSize());

    int startIndex = request.getPage() * request.getSize();
    int endIndex = Math.min(startIndex + request.getSize(), totalElements);

    List<Blog> paginatedBlogs =
        startIndex < totalElements
            ? sortedBlogs.subList(startIndex, endIndex)
            : new ArrayList<>();

    List<BlogDto> blogDtos =
        paginatedBlogs.stream().map(this::convertToDto).collect(Collectors.toList());

    boolean hasMore = request.getPage() + 1 < totalPages;

    return new BlogListResponse(
        blogDtos, request.getPage(), request.getSize(), totalElements, totalPages, hasMore);
  }

  public List<Blog> filterPublishedPosts(List<Blog> posts) {
    return posts.stream().filter(Blog::isPublished).collect(Collectors.toList());
  }

  private List<Blog> sortByPublishedAtDescending(List<Blog> posts) {
    return posts.stream()
        .sorted(Comparator.comparing(Blog::getPublishedAt).reversed())
        .collect(Collectors.toList());
  }

  private BlogDto convertToDto(Blog blog) {
    BlogDto.AuthorDto authorDto = null;
    if (blog.getAuthor() != null) {
      authorDto =
          new BlogDto.AuthorDto(blog.getAuthor().getName(), blog.getAuthor().getAvatarUrl());
    }

    return new BlogDto(
        blog.getId(),
        blog.getTitle(),
        blog.getExcerpt(),
        blog.getCoverImageUrl(),
        authorDto,
        blog.getTags(),
        blog.getPublishedAt());
  }
}
