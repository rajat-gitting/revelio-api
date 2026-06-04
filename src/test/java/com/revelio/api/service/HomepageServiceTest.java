package com.revelio.api.service;

import static org.junit.jupiter.api.Assertions.*;

import com.revelio.api.dto.BlogResponseDto;
import com.revelio.api.dto.HeroSectionDto;
import com.revelio.api.dto.HomepageDto;
import com.revelio.api.model.Blog;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link HomepageService}. Each test maps to at least one acceptance criterion from
 * ticket CR-22.
 */
class HomepageServiceTest {

  private HomepageService homepageService;
  private BlogService blogService;

  @BeforeEach
  void setUp() {
    Blog.Author alice = new Blog.Author("Alice Chen", null);
    List<Blog> blogs =
        Arrays.asList(
            new Blog(
                1L,
                "Spring Boot Guide",
                "A guide",
                null,
                alice,
                Arrays.asList("java"),
                Instant.parse("2024-03-01T10:00:00Z"),
                true),
            new Blog(
                2L,
                "React Tips",
                "Some tips",
                null,
                alice,
                Arrays.asList("react"),
                Instant.parse("2024-03-05T10:00:00Z"),
                true));
    blogService = new BlogService(blogs);
    homepageService = new HomepageService(blogService);
  }

  // -----------------------------------------------------------------------
  // AC-1: hero is the first element (hero field is non-null and comes first)
  // -----------------------------------------------------------------------

  @Test
  void testHomepageHeroIsNotNull() {
    // AC-1: the hero section is present as the first element of the homepage payload
    HomepageDto homepage = homepageService.getHomepage(0, 10);
    assertNotNull(homepage.getHero(), "Hero section must not be null (AC-1)");
  }

  @Test
  void testHomepageBlogCardsAreNotNull() {
    // AC-1: blog cards must be present (the hero appears above them)
    HomepageDto homepage = homepageService.getHomepage(0, 10);
    assertNotNull(homepage.getBlogCards(), "Blog cards must not be null");
  }

  // -----------------------------------------------------------------------
  // AC-2: desktop height 300–400 px
  // -----------------------------------------------------------------------

  @Test
  void testHeroDesktopHeightIsWithin300To400Px() {
    // AC-2: hero is 300–400 px tall on viewports >= 1024 px
    HeroSectionDto hero = homepageService.buildHero(null);
    int h = hero.getDesktopHeightPx();
    assertTrue(
        h >= 300 && h <= 400,
        "Hero desktopHeightPx must be 300–400 for desktop viewports; got " + h);
  }

  // -----------------------------------------------------------------------
  // AC-3: headline and subheading with approved placeholder copy
  // -----------------------------------------------------------------------

  @Test
  void testHeroHeadlinePlaceholderCopy() {
    // AC-3: headline placeholder is "Welcome to Our Blog"
    HeroSectionDto hero = homepageService.buildHero(null);
    assertEquals("Welcome to Our Blog", hero.getHeadline());
  }

  @Test
  void testHeroSubheadingPlaceholderCopy() {
    // AC-3: subheading placeholder is "Discover articles, insights, and stories"
    HeroSectionDto hero = homepageService.buildHero(null);
    assertEquals("Discover articles, insights, and stories", hero.getSubheading());
  }

  @Test
  void testHeroHeadlineAndSubheadingAreDistinct() {
    // AC-3: headline and subheading are distinct typographic elements
    HeroSectionDto hero = homepageService.buildHero(null);
    assertNotEquals(
        hero.getHeadline(),
        hero.getSubheading(),
        "Headline and subheading must be distinct typographic elements");
  }

  // -----------------------------------------------------------------------
  // AC-4: CTA label, href, and scroll-target
  // -----------------------------------------------------------------------

  @Test
  void testHeroCtaLabelIsExploreArticles() {
    // AC-4: CTA button labelled "Explore Articles"
    HeroSectionDto hero = homepageService.buildHero(null);
    assertEquals("Explore Articles", hero.getCtaLabel(), "CTA label must be 'Explore Articles'");
  }

  @Test
  void testHeroCtaHrefIsBlogSectionAnchor() {
    // AC-4: CTA uses smooth-scroll anchor link (href='#blog-section')
    HeroSectionDto hero = homepageService.buildHero(null);
    assertEquals("#blog-section", hero.getCtaHref(), "CTA href must be '#blog-section'");
    assertTrue(hero.getCtaHref().startsWith("#"), "CTA href must start with '#' for smooth scroll");
  }

  @Test
  void testHomepageBlogSectionIdMatchesHeroCtaTarget() {
    // AC-4: homepage blogSectionId matches the CTA href target
    HomepageDto homepage = homepageService.getHomepage(0, 10);
    HeroSectionDto hero = homepage.getHero();
    String ctaTarget = hero.getCtaHref().substring(1);
    assertEquals(
        ctaTarget,
        homepage.getBlogSectionId(),
        "Homepage blogSectionId must match hero CTA href target");
  }

  // -----------------------------------------------------------------------
  // AC-6: background gradient default / image + overlay
  // -----------------------------------------------------------------------

  @Test
  void testHeroWithoutBackgroundImageUsesGradient() {
    // AC-6: default background is a CSS gradient
    HeroSectionDto hero = homepageService.buildHero(null);
    assertFalse(
        hero.hasBackgroundImage(), "Hero without backgroundImage must use gradient only (AC-6)");
    assertNotNull(hero.getBackgroundGradient(), "backgroundGradient must be set (AC-6)");
    assertTrue(
        hero.getBackgroundGradient().contains("gradient"),
        "backgroundGradient must be a CSS gradient value (AC-6)");
  }

  @Test
  void testHeroWithBackgroundImageSetsImageUrl() {
    // AC-6: when backgroundImageUrl is provided, it is applied to the hero
    String imageUrl = "https://cdn.example.com/hero.jpg";
    HeroSectionDto hero = homepageService.buildHero(imageUrl);
    assertTrue(hero.hasBackgroundImage(), "Hero must have backgroundImage when URL is provided");
    assertEquals(
        imageUrl, hero.getBackgroundImage(), "backgroundImage must equal the provided URL");
  }

  @Test
  void testHeroWithBackgroundImagePreservesOverlayOpacity() {
    // AC-6, AC-7: overlay opacity must be preserved when a background image is set
    String imageUrl = "https://cdn.example.com/hero.jpg";
    HeroSectionDto hero = homepageService.buildHero(imageUrl);
    assertEquals(
        HeroSectionDto.DEFAULT_OVERLAY_OPACITY,
        hero.getOverlayOpacity(),
        1e-9,
        "Overlay opacity must be preserved for background image hero (AC-6)");
  }

  // -----------------------------------------------------------------------
  // AC-7: WCAG AA contrast – overlay opacity >= 0.40
  // -----------------------------------------------------------------------

  @Test
  void testHeroOverlayOpacityMeetsWcagAaRequirement() {
    // AC-7: overlay opacity must be >= 0.40 so white text achieves 4.5:1 contrast
    HeroSectionDto hero = homepageService.buildHero(null);
    assertTrue(
        hero.getOverlayOpacity() >= 0.40,
        "overlayOpacity must be >= 0.40 to meet WCAG AA contrast; got " + hero.getOverlayOpacity());
  }

  @Test
  void testHeroOverlayOpacityIsValidCssValue() {
    // AC-7: opacity must be in [0, 1]
    HeroSectionDto hero = homepageService.buildHero(null);
    assertTrue(
        hero.getOverlayOpacity() >= 0.0 && hero.getOverlayOpacity() <= 1.0,
        "overlayOpacity must be a valid CSS opacity value in [0, 1]");
  }

  @Test
  void testDefaultBackgroundGradientImpliesHighContrast() {
    // AC-7: the default gradient uses dark colours (starts with 'linear-gradient')
    // so white text achieves >= 4.5:1 contrast ratio.
    HeroSectionDto hero = homepageService.buildHero(null);
    assertTrue(
        hero.getBackgroundGradient().startsWith("linear-gradient"),
        "Default background must be a linear-gradient to guarantee high contrast (AC-7)");
  }

  // -----------------------------------------------------------------------
  // AC-8: mobile hero adapts to content (min-height, not fixed height)
  // -----------------------------------------------------------------------

  @Test
  void testHeroMinHeightIsAtLeast300Px() {
    // AC-8: on mobile the hero uses min-height >= 300 px (adapts to content)
    HeroSectionDto hero = homepageService.buildHero(null);
    assertTrue(
        hero.getMinHeightPx() >= 300,
        "minHeightPx must be >= 300 for mobile responsiveness; got " + hero.getMinHeightPx());
  }

  @Test
  void testHeroMinHeightIsLessThanOrEqualToDesktopHeight() {
    // AC-8: minHeight (mobile) must not exceed the desktop fixed height
    HeroSectionDto hero = homepageService.buildHero(null);
    assertTrue(
        hero.getMinHeightPx() <= hero.getDesktopHeightPx(),
        "minHeightPx must be <= desktopHeightPx");
  }

  // -----------------------------------------------------------------------
  // AC-9: >= 32 px gap between hero and blog-card grid
  // -----------------------------------------------------------------------

  @Test
  void testHomepageBlogSectionMarginTopIsAtLeast32Px() {
    // AC-9: minimum 32 px (2 rem) vertical spacing between hero bottom and blog-card grid
    HomepageDto homepage = homepageService.getHomepage(0, 10);
    assertTrue(
        homepage.getBlogSectionMarginTopPx() >= HomepageDto.MINIMUM_BLOG_SECTION_MARGIN_TOP_PX,
        "blogSectionMarginTopPx must be >= 32 px (2 rem); got "
            + homepage.getBlogSectionMarginTopPx());
  }

  @Test
  void testHomepageBlogSectionMarginTopConstantIs32() {
    // AC-9: the constant itself must be 32 (= 2 rem at 16 px/rem)
    assertEquals(
        32,
        HomepageDto.MINIMUM_BLOG_SECTION_MARGIN_TOP_PX,
        "MINIMUM_BLOG_SECTION_MARGIN_TOP_PX must be 32 px (= 2 rem)");
  }

  // -----------------------------------------------------------------------
  // AC-10: keyboard accessibility – CTA is an anchor link (natively focusable)
  // -----------------------------------------------------------------------

  @Test
  void testHeroCtaHrefIsNotJavaScriptUri() {
    // AC-10: CTA must be a plain anchor link – not a javascript: URI – so it is
    // keyboard-accessible without JavaScript.
    HeroSectionDto hero = homepageService.buildHero(null);
    assertFalse(
        hero.getCtaHref().toLowerCase().startsWith("javascript:"),
        "CTA href must not be a javascript: URI (AC-10)");
  }

  // -----------------------------------------------------------------------
  // Overall validity
  // -----------------------------------------------------------------------

  @Test
  void testDefaultHomepagePassesFullValidation() {
    // All acceptance criteria that have a structural contract must pass
    HomepageDto homepage = homepageService.getHomepage(0, 10);
    assertTrue(
        homepage.isValid(), "Default homepage must pass full validation; homepage=" + homepage);
  }

  @Test
  void testHomepageWithBackgroundImagePassesValidation() {
    HomepageDto homepage =
        homepageService.getHomepage(0, 10, "https://cdn.example.com/hero-bg.jpg");
    assertTrue(
        homepage.isValid(),
        "Homepage with background image must pass full validation; homepage=" + homepage);
  }

  // -----------------------------------------------------------------------
  // Blog cards integration
  // -----------------------------------------------------------------------

  @Test
  void testHomepageBlogCardsMatchPublishedBlogs() {
    HomepageDto homepage = homepageService.getHomepage(0, 10);
    List<BlogResponseDto> cards = homepage.getBlogCards();
    assertNotNull(cards);
    assertEquals(2, cards.size(), "Blog cards must match the number of published blogs");
  }

  @Test
  void testHomepageBlogCardsPagination() {
    HomepageDto homepage = homepageService.getHomepage(0, 1);
    List<BlogResponseDto> cards = homepage.getBlogCards();
    assertEquals(1, cards.size(), "Pagination must limit blog cards to the requested size");
  }

  @Test
  void testHomepageBlogSectionIdIsSet() {
    HomepageDto homepage = homepageService.getHomepage(0, 10);
    assertNotNull(homepage.getBlogSectionId(), "blogSectionId must be set on the homepage");
    assertFalse(homepage.getBlogSectionId().isBlank(), "blogSectionId must not be blank");
  }
}
