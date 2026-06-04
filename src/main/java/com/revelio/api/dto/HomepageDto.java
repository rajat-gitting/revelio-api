package com.revelio.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import java.util.Objects;

/**
 * Data transfer object for the homepage response. Combines the hero section (always the first
 * visible element, AC-1) with a paginated list of blog cards (AC-9 – minimum 32 px gap enforced by
 * the {@code blogSectionMarginTopPx} field) and the id of the blog-section element so the CTA
 * anchor link can target it (AC-4).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HomepageDto {

  /**
   * Minimum gap in pixels between the bottom of the hero section and the top of the blog-card grid
   * on all viewport sizes (AC-9: minimum 32 px / 2 rem).
   */
  public static final int MINIMUM_BLOG_SECTION_MARGIN_TOP_PX = 32;

  private HeroSectionDto hero;
  private List<BlogResponseDto> blogCards;

  /**
   * DOM element id of the blog-card section. The hero CTA href must equal '#' + this value (AC-4).
   */
  private String blogSectionId;

  /**
   * Minimum top-margin in pixels between the hero bottom and the blog-card grid (AC-9). Must be >=
   * {@value #MINIMUM_BLOG_SECTION_MARGIN_TOP_PX}.
   */
  private int blogSectionMarginTopPx;

  public HomepageDto() {}

  public HomepageDto(
      HeroSectionDto hero,
      List<BlogResponseDto> blogCards,
      String blogSectionId,
      int blogSectionMarginTopPx) {
    this.hero = hero;
    this.blogCards = blogCards;
    this.blogSectionId = blogSectionId;
    this.blogSectionMarginTopPx = blogSectionMarginTopPx;
  }

  // -------------------------------------------------------------------------
  // Getters / setters
  // -------------------------------------------------------------------------

  public HeroSectionDto getHero() {
    return hero;
  }

  public void setHero(HeroSectionDto hero) {
    this.hero = hero;
  }

  public List<BlogResponseDto> getBlogCards() {
    return blogCards;
  }

  public void setBlogCards(List<BlogResponseDto> blogCards) {
    this.blogCards = blogCards;
  }

  public String getBlogSectionId() {
    return blogSectionId;
  }

  public void setBlogSectionId(String blogSectionId) {
    this.blogSectionId = blogSectionId;
  }

  public int getBlogSectionMarginTopPx() {
    return blogSectionMarginTopPx;
  }

  public void setBlogSectionMarginTopPx(int blogSectionMarginTopPx) {
    this.blogSectionMarginTopPx = blogSectionMarginTopPx;
  }

  // -------------------------------------------------------------------------
  // Validation helpers
  // -------------------------------------------------------------------------

  /**
   * Returns {@code true} when the homepage layout contract is satisfied:
   *
   * <ul>
   *   <li>hero is non-null and passes its own validation (AC-1, AC-2, AC-3, AC-4, AC-6)
   *   <li>blogSectionId matches the hero CTA href target (AC-4)
   *   <li>blogSectionMarginTopPx >= 32 px (AC-9)
   * </ul>
   */
  public boolean isValid() {
    if (hero == null || !hero.isValid()) return false;
    if (blogSectionId == null || blogSectionId.isBlank()) return false;
    if (!("#" + blogSectionId).equals(hero.getCtaHref())) return false;
    return blogSectionMarginTopPx >= MINIMUM_BLOG_SECTION_MARGIN_TOP_PX;
  }

  // -------------------------------------------------------------------------
  // equals / hashCode / toString
  // -------------------------------------------------------------------------

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    HomepageDto that = (HomepageDto) o;
    return blogSectionMarginTopPx == that.blogSectionMarginTopPx
        && Objects.equals(hero, that.hero)
        && Objects.equals(blogCards, that.blogCards)
        && Objects.equals(blogSectionId, that.blogSectionId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(hero, blogCards, blogSectionId, blogSectionMarginTopPx);
  }

  @Override
  public String toString() {
    return "HomepageDto{"
        + "hero="
        + hero
        + ", blogCards="
        + (blogCards != null ? "[" + blogCards.size() + " items]" : "null")
        + ", blogSectionId='"
        + blogSectionId
        + '\''
        + ", blogSectionMarginTopPx="
        + blogSectionMarginTopPx
        + '}';
  }
}
