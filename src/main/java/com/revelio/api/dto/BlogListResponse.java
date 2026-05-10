package com.revelio.api.dto;

import java.util.List;
import java.util.Objects;

public class BlogListResponse {
  private List<BlogDto> blogs;
  private int page;
  private int size;
  private long totalElements;
  private int totalPages;
  private boolean hasMore;

  public BlogListResponse() {}

  public BlogListResponse(
      List<BlogDto> blogs,
      int page,
      int size,
      long totalElements,
      int totalPages,
      boolean hasMore) {
    this.blogs = blogs;
    this.page = page;
    this.size = size;
    this.totalElements = totalElements;
    this.totalPages = totalPages;
    this.hasMore = hasMore;
  }

  public List<BlogDto> getBlogs() {
    return blogs;
  }

  public void setBlogs(List<BlogDto> blogs) {
    this.blogs = blogs;
  }

  public int getPage() {
    return page;
  }

  public void setPage(int page) {
    this.page = page;
  }

  public int getSize() {
    return size;
  }

  public void setSize(int size) {
    this.size = size;
  }

  public long getTotalElements() {
    return totalElements;
  }

  public void setTotalElements(long totalElements) {
    this.totalElements = totalElements;
  }

  public int getTotalPages() {
    return totalPages;
  }

  public void setTotalPages(int totalPages) {
    this.totalPages = totalPages;
  }

  public boolean isHasMore() {
    return hasMore;
  }

  public void setHasMore(boolean hasMore) {
    this.hasMore = hasMore;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    BlogListResponse that = (BlogListResponse) o;
    return page == that.page
        && size == that.size
        && totalElements == that.totalElements
        && totalPages == that.totalPages
        && hasMore == that.hasMore
        && Objects.equals(blogs, that.blogs);
  }

  @Override
  public int hashCode() {
    return Objects.hash(blogs, page, size, totalElements, totalPages, hasMore);
  }

  @Override
  public String toString() {
    return "BlogListResponse{"
        + "blogs="
        + blogs
        + ", page="
        + page
        + ", size="
        + size
        + ", totalElements="
        + totalElements
        + ", totalPages="
        + totalPages
        + ", hasMore="
        + hasMore
        + '}';
  }
}
