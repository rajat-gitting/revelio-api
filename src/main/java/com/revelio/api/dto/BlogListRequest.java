package com.revelio.api.dto;

import java.util.Objects;

public class BlogListRequest {
  private int page;
  private int size;

  public BlogListRequest() {
    this.page = 0;
    this.size = 10;
  }

  public BlogListRequest(int page, int size) {
    this.page = page;
    this.size = size;
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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    BlogListRequest that = (BlogListRequest) o;
    return page == that.page && size == that.size;
  }

  @Override
  public int hashCode() {
    return Objects.hash(page, size);
  }

  @Override
  public String toString() {
    return "BlogListRequest{" + "page=" + page + ", size=" + size + '}';
  }
}
