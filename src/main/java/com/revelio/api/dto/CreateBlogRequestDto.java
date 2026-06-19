package com.revelio.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateBlogRequestDto {

  @NotBlank(message = "title is required")
  private String title;

  @NotBlank(message = "excerpt is required")
  private String excerpt;

  @NotBlank(message = "body is required")
  private String body;

  private List<String> tags;

  @Valid private AuthorDto author;

  private String coverImageUrl;

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class AuthorDto {

    @NotBlank(message = "author.name is required")
    private String name;

    private String avatarUrl;
  }
}
