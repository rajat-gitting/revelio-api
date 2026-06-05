package com.revelio.api.dto;

import java.util.List;
import java.util.Objects;

/**
 * Paginated envelope returned by GET /api/v1/posts/search.
 *
 * <p>Shape: { total, page, size, results: BlogSummaryDto[] }
 */
public class PostSearchResponse {

  private long total;
  private int page;
  private int size;
  private List<BlogSummaryDto> results;

  public PostSearchResponse() {}

  public PostSearchResponse(long total, int page, int size, List<BlogSummaryDto> results) {
    this.total = total;
    this.page = page;
    this.size = size;
    this.results = results;
  }

  public long getTotal() {
    return total;
  }

  public void setTotal(long total) {
    this.total = total;
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

  public List<BlogSummaryDto> getResults() {
    return results;
  }

  public void setResults(List<BlogSummaryDto> results) {
    this.results = results;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    PostSearchResponse that = (PostSearchResponse) o;
    return total == that.total
        && page == that.page
        && size == that.size
        && Objects.equals(results, that.results);
  }

  @Override
  public int hashCode() {
    return Objects.hash(total, page, size, results);
  }
}
