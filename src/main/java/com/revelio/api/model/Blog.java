package com.revelio.api.model;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

public class Blog {
  private Long id;
  private String title;
  private String excerpt;
  private String coverImageUrl;
  private Author author;
  private List<String> tags;
  private Instant publishedAt;
  private boolean published;

  public Blog() {}

  public Blog(
      Long id,
      String title,
      String excerpt,
      String coverImageUrl,
      Author author,
      List<String> tags,
      Instant publishedAt,
      boolean published) {
    this.id = id;
    this.title = title;
    this.excerpt = excerpt;
    this.coverImageUrl = coverImageUrl;
    this.author = author;
    this.tags = tags;
    this.publishedAt = publishedAt;
    this.published = published;
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

  public String getExcerpt() {
    return excerpt;
  }

  public void setExcerpt(String excerpt) {
    this.excerpt = excerpt;
  }

  public String getCoverImageUrl() {
    return coverImageUrl;
  }

  public void setCoverImageUrl(String coverImageUrl) {
    this.coverImageUrl = coverImageUrl;
  }

  public Author getAuthor() {
    return author;
  }

  public void setAuthor(Author author) {
    this.author = author;
  }

  public List<String> getTags() {
    return tags;
  }

  public void setTags(List<String> tags) {
    this.tags = tags;
  }

  public Instant getPublishedAt() {
    return publishedAt;
  }

  public void setPublishedAt(Instant publishedAt) {
    this.publishedAt = publishedAt;
  }

  public boolean isPublished() {
    return published;
  }

  public void setPublished(boolean published) {
    this.published = published;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Blog blog = (Blog) o;
    return published == blog.published
        && Objects.equals(id, blog.id)
        && Objects.equals(title, blog.title)
        && Objects.equals(excerpt, blog.excerpt)
        && Objects.equals(coverImageUrl, blog.coverImageUrl)
        && Objects.equals(author, blog.author)
        && Objects.equals(tags, blog.tags)
        && Objects.equals(publishedAt, blog.publishedAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, title, excerpt, coverImageUrl, author, tags, publishedAt, published);
  }

  @Override
  public String toString() {
    return "Blog{"
        + "id="
        + id
        + ", title='"
        + title
        + '\"'
        + ", excerpt='"
        + excerpt
        + '\"'
        + ", coverImageUrl='"
        + coverImageUrl
        + '\"'
        + ", author="
        + author
        + ", tags="
        + tags
        + ", publishedAt="
        + publishedAt
        + ", published="
        + published
        + '}';
  }

  public static class Author {
    private String name;
    private String avatarUrl;

    public Author() {}

    public Author(String name, String avatarUrl) {
      this.name = name;
      this.avatarUrl = avatarUrl;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getAvatarUrl() {
      return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
      this.avatarUrl = avatarUrl;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      Author author = (Author) o;
      return Objects.equals(name, author.name) && Objects.equals(avatarUrl, author.avatarUrl);
    }

    @Override
    public int hashCode() {
      return Objects.hash(name, avatarUrl);
    }

    @Override
    public String toString() {
      return "Author{" + "name='" + name + '\"' + ", avatarUrl='" + avatarUrl + '\"' + '}';
    }
  }
}
