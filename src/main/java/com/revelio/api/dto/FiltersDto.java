package com.revelio.api.dto;

import java.util.List;
import java.util.Objects;

/**
 * Response envelope for GET /api/v1/posts/filters.
 *
 * <p>Returns distinct author names and category/tag values that can be used to populate the filter
 * dropdowns on the client.
 */
public class FiltersDto {

  private List<String> authors;
  private List<String> categories;

  public FiltersDto() {}

  public FiltersDto(List<String> authors, List<String> categories) {
    this.authors = authors;
    this.categories = categories;
  }

  public List<String> getAuthors() {
    return authors;
  }

  public void setAuthors(List<String> authors) {
    this.authors = authors;
  }

  public List<String> getCategories() {
    return categories;
  }

  public void setCategories(List<String> categories) {
    this.categories = categories;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    FiltersDto that = (FiltersDto) o;
    return Objects.equals(authors, that.authors) && Objects.equals(categories, that.categories);
  }

  @Override
  public int hashCode() {
    return Objects.hash(authors, categories);
  }
}
