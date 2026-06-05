package com.revelio.api.dto;

import java.util.List;
import java.util.Objects;

public class PostFiltersDto {
  private List<String> categories;
  private List<AuthorSummaryDto> authors;

  public PostFiltersDto() {}

  public PostFiltersDto(List<String> categories, List<AuthorSummaryDto> authors) {
    this.categories = categories;
    this.authors = authors;
  }

  public List<String> getCategories() {
    return categories;
  }

  public void setCategories(List<String> categories) {
    this.categories = categories;
  }

  public List<AuthorSummaryDto> getAuthors() {
    return authors;
  }

  public void setAuthors(List<AuthorSummaryDto> authors) {
    this.authors = authors;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    PostFiltersDto that = (PostFiltersDto) o;
    return Objects.equals(categories, that.categories) && Objects.equals(authors, that.authors);
  }

  @Override
  public int hashCode() {
    return Objects.hash(categories, authors);
  }

  public static class AuthorSummaryDto {
    private String name;

    public AuthorSummaryDto() {}

    public AuthorSummaryDto(String name) {
      this.name = name;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      AuthorSummaryDto that = (AuthorSummaryDto) o;
      return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
      return Objects.hash(name);
    }
  }
}
