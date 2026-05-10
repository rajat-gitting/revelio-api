package com.revelio.api.dto;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

public class BlogDto {
  private Long id;
  private String title;
  private String excerpt;
  private String coverImageUrl;
  private AuthorDto author;
  private List<String> tags;
  private Instant publishedAt;

  public BlogDto() {}

  public BlogDto(
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
    BlogDto blogDto = (BlogDto) o;
    return Objects.equals(id, blogDto.id)
        && Objects.equals(title, blogDto.title)
        && Objects.equals(excerpt, blogDto.excerpt)
        && Objects.equals(coverImageUrl, blogDto.coverImageUrl)
        && Objects.equals(author, blogDto.author)
        && Objects.equals(tags, blogDto.tags)
        && Objects.equals(publishedAt, blogDto.publishedAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, title, excerpt, coverImageUrl, author, tags, publishedAt);
  }

  @Override
  public String toString() {
    return "BlogDto{"
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

  public static class AuthorDto {
    private String name;
    private String avatarUrl;

    public AuthorDto() {}

    public AuthorDto(String name, String avatarUrl) {
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
      AuthorDto authorDto = (AuthorDto) o;
      return Objects.equals(name, authorDto.name) && Objects.equals(avatarUrl, authorDto.avatarUrl);
    }

    @Override
    public int hashCode() {
      return Objects.hash(name, avatarUrl);
    }

    @Override
    public String toString() {
      return "AuthorDto{" + "name='" + name + '\'' + ", avatarUrl='" + avatarUrl + '\'' + '}';
    }
  }
}
