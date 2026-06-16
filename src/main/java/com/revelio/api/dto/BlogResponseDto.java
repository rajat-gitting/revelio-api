package com.revelio.api.dto;

import com.revelio.api.model.Blog;
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
  private String body;
  private Integer readingTimeMinutes;

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

  public BlogResponseDto(
      Long id,
      String title,
      String excerpt,
      String coverImageUrl,
      AuthorDto author,
      List<String> tags,
      Instant publishedAt,
      String body) {
    this(id, title, excerpt, coverImageUrl, author, tags, publishedAt);
    this.body = body;
  }

  public static BlogResponseDto fromBlog(Blog blog) {
    if (blog == null) {
      return null;
    }
    AuthorDto authorDto = null;
    if (blog.getAuthor() != null) {
      authorDto = new AuthorDto(blog.getAuthor().getName(), blog.getAuthor().getAvatarUrl());
    }
    String body = blog.getBody();
    int wordCount = (body == null || body.isBlank()) ? 0 : body.trim().split("\\s+").length;
    int readingTimeMinutes = Math.max(1, (int) Math.ceil((double) wordCount / 200));
    BlogResponseDto dto =
        new BlogResponseDto(
            blog.getId(),
            blog.getTitle(),
            blog.getExcerpt(),
            blog.getCoverImageUrl(),
            authorDto,
            blog.getTags(),
            blog.getPublishedAt(),
            body);
    dto.setReadingTimeMinutes(readingTimeMinutes);
    return dto;
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

  public String getBody() {
    return body;
  }

  public void setBody(String body) {
    this.body = body;
  }

  public Integer getReadingTimeMinutes() {
    return readingTimeMinutes;
  }

  public void setReadingTimeMinutes(Integer readingTimeMinutes) {
    this.readingTimeMinutes = readingTimeMinutes;
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
        && Objects.equals(publishedAt, that.publishedAt)
        && Objects.equals(body, that.body)
        && Objects.equals(readingTimeMinutes, that.readingTimeMinutes);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        id, title, excerpt, coverImageUrl, author, tags, publishedAt, body, readingTimeMinutes);
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
  }
}
