package com.revelio.api.dto;

import java.util.List;

public class CreateBlogRequestDto {

  private String title;
  private String excerpt;
  private String body;
  private List<String> tags;
  private AuthorDto author;
  private String coverImageUrl;

  public CreateBlogRequestDto() {}

  public CreateBlogRequestDto(
      String title,
      String excerpt,
      String body,
      List<String> tags,
      AuthorDto author,
      String coverImageUrl) {
    this.title = title;
    this.excerpt = excerpt;
    this.body = body;
    this.tags = tags;
    this.author = author;
    this.coverImageUrl = coverImageUrl;
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

  public String getBody() {
    return body;
  }

  public void setBody(String body) {
    this.body = body;
  }

  public List<String> getTags() {
    return tags;
  }

  public void setTags(List<String> tags) {
    this.tags = tags;
  }

  public AuthorDto getAuthor() {
    return author;
  }

  public void setAuthor(AuthorDto author) {
    this.author = author;
  }

  public String getCoverImageUrl() {
    return coverImageUrl;
  }

  public void setCoverImageUrl(String coverImageUrl) {
    this.coverImageUrl = coverImageUrl;
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
  }
}
