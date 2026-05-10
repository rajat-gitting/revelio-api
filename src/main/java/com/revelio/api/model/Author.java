package com.revelio.api.model;

import java.util.Objects;

public class Author {
  private Long id;
  private String name;
  private String avatarUrl;

  public Author() {}

  public Author(Long id, String name, String avatarUrl) {
    this.id = id;
    this.name = name;
    this.avatarUrl = avatarUrl;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
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
    return Objects.equals(id, author.id)
        && Objects.equals(name, author.name)
        && Objects.equals(avatarUrl, author.avatarUrl);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, avatarUrl);
  }

  @Override
  public String toString() {
    return "Author{"
        + "id="
        + id
        + ", name='"
        + name
        + '\'
        + ", avatarUrl='"
        + avatarUrl
        + '\'
        + '}';
  }
}
