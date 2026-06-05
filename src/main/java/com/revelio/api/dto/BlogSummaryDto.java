package com.revelio.api.dto;

import com.revelio.api.model.Blog;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

/** Lightweight blog summary returned by the search/filter endpoint. */
public class BlogSummaryDto {

  private Long id;
  private String title;
  private String slug;
  private AuthorInfo author;
  private List<String> categories;
  private Instant publishedAt;
  private String excerpt;

  public BlogSummaryDto() {}

  public BlogSummaryDto(
      Long id,
      String title,
      String slug,
      AuthorInfo author,
      List<String> categories,
      Instant publishedAt,
      String excerpt) {
    this.id = id;
    this.title = title;
    this.slug = slug;
    this.author = author;
    this.categories = categories;
    this.publishedAt = publishedAt;
    this.excerpt = excerpt;
  }

  /** Build a BlogSummaryDto from a Blog domain object. */
  public static BlogSummaryDto fromBlog(Blog blog) {
    if (blog == null) return null;
    AuthorInfo authorInfo = null;
    if (blog.getAuthor() != null) {
      authorInfo = new AuthorInfo(String.valueOf(blog.getId()), blog.getAuthor().getName());
    }
    String slug = blog.getTitle() == null ? "" : blog.getTitle().toLowerCase().replace(' ', '-');
    return new BlogSummaryDto(
        blog.getId(),
        blog.getTitle(),
        slug,
        authorInfo,
        blog.getTags(),
        blog.getPublishedAt(),
        blog.getExcerpt());
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getSlug() {
    return slug;
  }

  public void setSlug(String slug) {
    this.slug = slug;
  }

  public AuthorInfo getAuthor() {
    return author;
  }

  public void setAuthor(AuthorInfo author) {
    this.author = author;
  }

  public List<String> getCategories() {
    return categories;
  }

  public void setCategories(List<String> categories) {
    this.categories = categories;
  }

  public Instant getPublishedAt() {
    return publishedAt;
  }

  public void setPublishedAt(Instant publishedAt) {
    this.publishedAt = publishedAt;
  }

  public String getExcerpt() {
    return excerpt;
  }

  public void setExcerpt(String excerpt) {
    this.excerpt = excerpt;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    BlogSummaryDto that = (BlogSummaryDto) o;
    return Objects.equals(id, that.id)
        && Objects.equals(title, that.title)
        && Objects.equals(slug, that.slug)
        && Objects.equals(author, that.author)
        && Objects.equals(categories, that.categories)
        && Objects.equals(publishedAt, that.publishedAt)
        && Objects.equals(excerpt, that.excerpt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, title, slug, author, categories, publishedAt, excerpt);
  }

  /** Minimal author info embedded in search results. */
  public static class AuthorInfo {
    private String id;
    private String name;

    public AuthorInfo() {}

    public AuthorInfo(String id, String name) {
      this.id = id;
      this.name = name;
    }

    public String getId() {
      return id;
    }

    public void setId(String id) {
      this.id = id;
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
      AuthorInfo that = (AuthorInfo) o;
      return Objects.equals(id, that.id) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
      return Objects.hash(id, name);
    }
  }
}
