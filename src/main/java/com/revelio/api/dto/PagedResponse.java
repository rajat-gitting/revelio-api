package com.revelio.api.dto;

import java.util.List;
import java.util.Objects;

/**
 * A Spring-Data-Page-compatible response envelope used for paginated endpoints.
 *
 * <p>Fields mirror the standard Spring Data {@code Page} serialisation so that frontend consumers
 * can treat this exactly like a Spring Data page:
 *
 * <ul>
 *   <li>{@code content} – the items on the current page
 *   <li>{@code totalElements} – total number of items across all pages
 *   <li>{@code totalPages} – total number of pages
 *   <li>{@code number} – current page index (0-based)
 *   <li>{@code size} – requested page size
 * </ul>
 */
public class PagedResponse<T> {

  private List<T> content;
  private long totalElements;
  private int totalPages;
  private int number;
  private int size;

  public PagedResponse() {}

  public PagedResponse(List<T> content, long totalElements, int totalPages, int number, int size) {
    this.content = content;
    this.totalElements = totalElements;
    this.totalPages = totalPages;
    this.number = number;
    this.size = size;
  }

  /**
   * Convenience factory that computes {@code totalPages} from {@code totalElements} and {@code
   * size}.
   */
  public static <T> PagedResponse<T> of(List<T> content, long totalElements, int page, int size) {
    int totalPages = (size == 0) ? 0 : (int) Math.ceil((double) totalElements / size);
    return new PagedResponse<>(content, totalElements, totalPages, page, size);
  }

  public List<T> getContent() {
    return content;
  }

  public void setContent(List<T> content) {
    this.content = content;
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

  public int getNumber() {
    return number;
  }

  public void setNumber(int number) {
    this.number = number;
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
    PagedResponse<?> that = (PagedResponse<?>) o;
    return totalElements == that.totalElements
        && totalPages == that.totalPages
        && number == that.number
        && size == that.size
        && Objects.equals(content, that.content);
  }

  @Override
  public int hashCode() {
    return Objects.hash(content, totalElements, totalPages, number, size);
  }
}
