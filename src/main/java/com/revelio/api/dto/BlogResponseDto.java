package com.revelio.api.dto;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

public class BlogResponseDto {
  private Long id;
  private String title;
  private String excerpt;
  private String coverImageUrl;
  private AuthorDto author;
  private List<String> tags;
  private Instant publishedAt;

  public BlogResponseDto() {}

  public BlogResponseDto(
      Long id,
      String title,
      String excerpt,
      String coverImageUrl,
      AuthorDto author,
      List<String> tags,
      Instant publishedAt) {
    this.id = id;
    this.title = title;
    this.excerpt = excerpt;
    this.coverImageUrl = coverImageUrl;
    this.author = author;
    this.tags = tags;
    this.publishedAt = publishedAt;
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

  public AuthorDto getAuthor() {
    return author;
  }

  public void setAuthor(AuthorDto author) {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    BlogResponseDto that = (BlogResponseDto) o;
    return Objects.equals(id, that.id)
        && Objects.equals(title, that.title)
        && Objects.equals(excerpt, that.excerpt)
        && Objects.equals(coverImageUrl, that.coverImageUrl)
        && Objects.equals(author, that.author)
        && Objects.equals(tags, that.tags)
        && Objects.equals(publishedAt, that.publishedAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, title, excerpt, coverImageUrl, author, tags, publishedAt);
  }

  @Override
  public String toString() {
    return "BlogResponseDto{"
        + "id="
        + id
        + ", title='"
        + title
        + '\'
        + ", excerpt='"
        + excerpt
        + '\'
        + ", coverImageUrl='"
        + coverImageUrl
        + '\'
        + ", author="
        + author
        + ", tags="
        + tags
        + ", publishedAt="
        + publishedAt
        + '}';
  }
}
