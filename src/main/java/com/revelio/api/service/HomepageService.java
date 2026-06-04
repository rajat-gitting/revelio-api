package com.revelio.api.service;

import com.revelio.api.dto.BlogResponseDto;
import com.revelio.api.dto.HeroSectionDto;
import com.revelio.api.dto.HomepageDto;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

/**
 * Service responsible for assembling the homepage payload. It combines the hero section with the
 * first page of published blog cards.
 *
 * <p>Hero-section design contract (from ticket CR-22):
 *
 * <ul>
 *   <li>Hero is the first visible element on the homepage (AC-1).
 *   <li>Desktop hero height 300–400 px on viewports >= 1024 px (AC-2).
 *   <li>Headline and subheading use the approved placeholder copy (AC-3).
 *   <li>CTA label is "Explore Articles" and href is "#blog-section" (AC-4).
 *   <li>Background defaults to a CSS gradient; optional image + 0.45 overlay (AC-6, AC-7).
 *   <li>Min-height 300 px on mobile so hero adapts to content (AC-8).
 *   <li>Blog-section margin-top >= 32 px (2 rem) on all viewports (AC-9).
 * </ul>
 */
@Service
public class HomepageService {

  private final BlogService blogService;

  public HomepageService(BlogService blogService) {
    this.blogService = blogService;
  }

  /**
   * Builds the default homepage payload using the approved placeholder copy and no background
   * image.
   *
   * @param page zero-based page index for the blog-card grid
   * @param size number of blog cards to include
   * @return fully assembled {@link HomepageDto}
   */
  public HomepageDto getHomepage(int page, int size) {
    return getHomepage(page, size, null);
  }

  /**
   * Builds the homepage payload. When {@code backgroundImageUrl} is non-null the hero section
   * includes the image URL and the default overlay opacity so that WCAG AA contrast is maintained
   * regardless of the image content (AC-6, AC-7).
   *
   * @param page zero-based page index for the blog-card grid
   * @param size number of blog cards to include
   * @param backgroundImageUrl optional URL of a hero background image
   * @return fully assembled {@link HomepageDto}
   */
  public HomepageDto getHomepage(int page, int size, String backgroundImageUrl) {
    HeroSectionDto hero = buildHero(backgroundImageUrl);
    List<BlogResponseDto> blogCards =
        blogService.getPublishedBlogs(page, size).stream()
            .map(BlogResponseDto::fromBlog)
            .collect(Collectors.toList());

    return new HomepageDto(
        hero,
        blogCards,
        HeroSectionDto.DEFAULT_BLOG_SECTION_ID,
        HomepageDto.MINIMUM_BLOG_SECTION_MARGIN_TOP_PX);
  }

  /**
   * Constructs a {@link HeroSectionDto} with the approved defaults. When a background image URL is
   * provided the {@code backgroundImage} field is populated and the default overlay opacity (0.45)
   * is preserved to guarantee WCAG AA contrast ratios (AC-6, AC-7).
   */
  HeroSectionDto buildHero(String backgroundImageUrl) {
    return new HeroSectionDto(
        "Welcome to Our Blog",
        "Discover articles, insights, and stories",
        HeroSectionDto.DEFAULT_CTA_LABEL,
        HeroSectionDto.DEFAULT_CTA_HREF,
        backgroundImageUrl,
        HeroSectionDto.DEFAULT_BACKGROUND_GRADIENT,
        HeroSectionDto.DEFAULT_OVERLAY_OPACITY,
        HeroSectionDto.DEFAULT_MIN_HEIGHT_PX,
        HeroSectionDto.DESKTOP_HEIGHT_PX,
        HeroSectionDto.DEFAULT_BLOG_SECTION_ID);
  }
}
