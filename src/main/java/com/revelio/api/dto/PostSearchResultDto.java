package com.revelio.api.dto;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Response envelope for the search/filter endpoint (CR-23).
 *
 * <p>In addition to the paginated result list and total count, the envelope includes an {@link
 * AppliedFiltersDto} that mirrors the active constraints back to the caller. The UI uses this to
 * render chip/badge indicators for each active filter and the current search term (AC-6) so that
 * users can see — at a glance — what is applied, and can remove individual constraints or use
 * 'Clear all'.
 */
public class PostSearchResultDto {
  private long total;
  private int page;
  private int size;
  private List<BlogResponseDto> results;

  /**
   * Reflects the active search/filter constraints back to the client so that the UI can render
   * chips or badges for each applied constraint (AC-6). A null or empty value for any field means
   * that constraint was not applied.
   */
  private AppliedFiltersDto appliedFilters;

  public PostSearchResultDto() {}

  public PostSearchResultDto(long total, int page, int size, List<BlogResponseDto> results) {
    this.total = total;
    this.page = page;
    this.size = size;
    this.results = results;
  }

  public PostSearchResultDto(
      long total,
      int page,
      int size,
      List<BlogResponseDto> results,
      AppliedFiltersDto appliedFilters) {
    this.total = total;
    this.page = page;
    this.size = size;
    this.results = results;
    this.appliedFilters = appliedFilters;
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

  public List<BlogResponseDto> getResults() {
    return results;
  }

  public void setResults(List<BlogResponseDto> results) {
    this.results = results;
  }

  public AppliedFiltersDto getAppliedFilters() {
    return appliedFilters;
  }

  public void setAppliedFilters(AppliedFiltersDto appliedFilters) {
    this.appliedFilters = appliedFilters;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    PostSearchResultDto that = (PostSearchResultDto) o;
    return total == that.total
        && page == that.page
        && size == that.size
        && Objects.equals(results, that.results)
        && Objects.equals(appliedFilters, that.appliedFilters);
  }

  @Override
  public int hashCode() {
    return Objects.hash(total, page, size, results, appliedFilters);
  }

  /**
   * Mirrors the active constraints back to the client (AC-6). Each non-null / non-empty field
   * represents an active filter that the UI should render as a removable chip or badge.
   */
  public static class AppliedFiltersDto {
    /** The current search query string, or {@code null} if no text search is active. */
    private String query;

    /** The list of active category/tag filters, or an empty list if none are applied. */
    private List<String> categories;

    /** The list of active author-name filters, or an empty list if none are applied. */
    private List<String> authors;

    public AppliedFiltersDto() {}

    public AppliedFiltersDto(String query, List<String> categories, List<String> authors) {
      this.query = query;
      this.categories = categories != null ? categories : Collections.emptyList();
      this.authors = authors != null ? authors : Collections.emptyList();
    }

    public String getQuery() {
      return query;
    }

    public void setQuery(String query) {
      this.query = query;
    }

    public List<String> getCategories() {
      return categories;
    }

    public void setCategories(List<String> categories) {
      this.categories = categories;
    }

    public List<String> getAuthors() {
      return authors;
    }

    public void setAuthors(List<String> authors) {
      this.authors = authors;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      AppliedFiltersDto that = (AppliedFiltersDto) o;
      return Objects.equals(query, that.query)
          && Objects.equals(categories, that.categories)
          && Objects.equals(authors, that.authors);
    }

    @Override
    public int hashCode() {
      return Objects.hash(query, categories, authors);
    }
  }
}
