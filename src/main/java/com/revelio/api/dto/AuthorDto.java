package com.revelio.api.dto;

import java.util.Objects;

public class AuthorDto {
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
