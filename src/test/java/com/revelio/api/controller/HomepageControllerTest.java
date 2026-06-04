package com.revelio.api.controller;

import static org.junit.jupiter.api.Assertions.*;

import com.revelio.api.dto.ApiResponse;
import com.revelio.api.dto.HeroSectionDto;
import com.revelio.api.dto.HomepageDto;
import com.revelio.api.model.Blog;
import com.revelio.api.service.BlogService;
import com.revelio.api.service.HomepageService;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Unit tests for {@link HomepageController}. Each test maps to at least one acceptance criterion
 * from ticket CR-22.
 */
class HomepageControllerTest {

  private HomepageController homepageController;
  private HomepageService homepageService;

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
    BlogService blogService = new BlogService(blogs);
    homepageService = new HomepageService(blogService);
    homepageController = new HomepageController(homepageService);
  }

  // -----------------------------------------------------------------------
  // Helper: unwrap the response body
  // -----------------------------------------------------------------------

  private HomepageDto getHomepage(int page, int size, String backgroundImage) {
    ResponseEntity<ApiResponse<HomepageDto>> response =
        homepageController.getHomepage(page, size, backgroundImage);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().isSuccess());
    return response.getBody().getData();
  }

  // -----------------------------------------------------------------------
  // AC-1: hero is the first element (non-null, comes before blog cards)
  // -----------------------------------------------------------------------

  @Test
  void testGetHomepageHeroIsNotNull() {
    // AC-1: the hero section is the first visible element on the homepage
    HomepageDto homepage = getHomepage(0, 10, null);
    assertNotNull(homepage.getHero(), "Hero section must not be null (AC-1)");
  }

  @Test
  void testGetHomepageBlogCardsAreNotNull() {
    // AC-1: blog cards are present below the hero
    HomepageDto homepage = getHomepage(0, 10, null);
    assertNotNull(homepage.getBlogCards(), "Blog cards list must not be null (AC-1)");
  }

  @Test
  void testGetHomepageResponseIsSuccessful() {
    // AC-1: endpoint returns HTTP 200 with success flag
    ResponseEntity<ApiResponse<HomepageDto>> response = homepageController.getHomepage(0, 10, null);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().isSuccess());
  }

  // -----------------------------------------------------------------------
  // AC-2: desktop height 300–400 px
  // -----------------------------------------------------------------------

  @Test
  void testHeroDesktopHeightIsWithin300To400Px() {
    // AC-2: hero is 300–400 px tall on desktop viewports (>= 1024 px)
    HomepageDto homepage = getHomepage(0, 10, null);
    int h = homepage.getHero().getDesktopHeightPx();
    assertTrue(
        h >= 300 && h <= 400,
        "Hero desktopHeightPx must be 300–400 on desktop viewports; got " + h);
  }

  // -----------------------------------------------------------------------
  // AC-3: headline and subheading with approved placeholder copy
  // -----------------------------------------------------------------------

  @Test
  void testHeroHeadlinePlaceholderCopy() {
    // AC-3: hero headline placeholder is "Welcome to Our Blog"
    HomepageDto homepage = getHomepage(0, 10, null);
    assertEquals("Welcome to Our Blog", homepage.getHero().getHeadline());
  }

  @Test
  void testHeroSubheadingPlaceholderCopy() {
    // AC-3: hero subheading placeholder
    HomepageDto homepage = getHomepage(0, 10, null);
    assertEquals("Discover articles, insights, and stories", homepage.getHero().getSubheading());
  }

  @Test
  void testHeroHeadlineAndSubheadingAreDistinct() {
    // AC-3: headline and subheading are distinct typographic elements
    HomepageDto homepage = getHomepage(0, 10, null);
    HeroSectionDto hero = homepage.getHero();
    assertNotEquals(
        hero.getHeadline(),
        hero.getSubheading(),
        "Headline and subheading must be distinct typographic elements (AC-3)");
  }

  // -----------------------------------------------------------------------
  // AC-4: CTA label, href, and scroll-target
  // -----------------------------------------------------------------------

  @Test
  void testHeroCtaLabelIsExploreArticles() {
    // AC-4: CTA button labelled "Explore Articles" (or "Read Latest")
    HomepageDto homepage = getHomepage(0, 10, null);
    String label = homepage.getHero().getCtaLabel();
    assertTrue(
        "Explore Articles".equals(label) || "Read Latest".equals(label),
        "CTA label must be 'Explore Articles' or 'Read Latest'; got: " + label);
  }

  @Test
  void testHeroCtaHrefStartsWithHash() {
    // AC-4: CTA uses a smooth-scroll anchor href starting with '#'
    HomepageDto homepage = getHomepage(0, 10, null);
    assertTrue(
        homepage.getHero().getCtaHref().startsWith("#"),
        "CTA href must start with '#' for smooth-scroll anchor (AC-4)");
  }

  @Test
  void testHeroCtaHrefTargetsBlogSection() {
    // AC-4: CTA scrolls to #blog-section
    HomepageDto homepage = getHomepage(0, 10, null);
    assertEquals(
        "#blog-section",
        homepage.getHero().getCtaHref(),
        "CTA href must target '#blog-section' (AC-4)");
  }

  @Test
  void testBlogSectionIdMatchesHeroCtaTarget() {
    // AC-4: homepage blogSectionId must match the fragment identifier in ctaHref
    HomepageDto homepage = getHomepage(0, 10, null);
    String ctaFragment = homepage.getHero().getCtaHref().substring(1);
    assertEquals(
        ctaFragment,
        homepage.getBlogSectionId(),
        "blogSectionId must equal the fragment in hero ctaHref (AC-4)");
  }

  // -----------------------------------------------------------------------
  // AC-6: background gradient default / image + overlay
  // -----------------------------------------------------------------------

  @Test
  void testHeroDefaultBackgroundIsGradient() {
    // AC-6: when no backgroundImage is supplied, hero uses CSS gradient
    HomepageDto homepage = getHomepage(0, 10, null);
    HeroSectionDto hero = homepage.getHero();
    assertFalse(
        hero.hasBackgroundImage(), "Hero without backgroundImage prop must use gradient (AC-6)");
    assertNotNull(hero.getBackgroundGradient(), "backgroundGradient must be set (AC-6)");
    assertTrue(
        hero.getBackgroundGradient().contains("gradient"),
        "backgroundGradient must be a CSS gradient value (AC-6)");
  }

  @Test
  void testHeroWithBackgroundImageAppliesImageUrl() {
    // AC-6: when backgroundImage is supplied, it is included in the hero
    String imgUrl = "https://cdn.example.com/hero-bg.jpg";
    HomepageDto homepage = getHomepage(0, 10, imgUrl);
    HeroSectionDto hero = homepage.getHero();
    assertTrue(
        hero.hasBackgroundImage(), "Hero must have backgroundImage when URL is supplied (AC-6)");
    assertEquals(imgUrl, hero.getBackgroundImage(), "backgroundImage must equal the supplied URL");
  }

  @Test
  void testHeroWithBackgroundImageHasOverlayOpacity() {
    // AC-6, AC-7: semi-transparent overlay opacity must be set when image is present
    HomepageDto homepage = getHomepage(0, 10, "https://cdn.example.com/hero-bg.jpg");
    double opacity = homepage.getHero().getOverlayOpacity();
    assertTrue(
        opacity > 0.0 && opacity <= 1.0,
        "Overlay opacity must be in (0, 1] when background image is set; got " + opacity);
  }

  // -----------------------------------------------------------------------
  // AC-7: WCAG AA contrast – overlay opacity >= 0.40
  // -----------------------------------------------------------------------

  @Test
  void testHeroOverlayOpacityMeetsWcagAaContrast() {
    // AC-7: overlay opacity >= 0.40 ensures white text has >= 4.5:1 contrast
    HomepageDto homepage = getHomepage(0, 10, null);
    assertTrue(
        homepage.getHero().getOverlayOpacity() >= 0.40,
        "overlayOpacity must be >= 0.40 to meet WCAG AA contrast requirements (AC-7)");
  }

  // -----------------------------------------------------------------------
  // AC-8: mobile hero adapts to content (min-height, not fixed)
  // -----------------------------------------------------------------------

  @Test
  void testHeroMinHeightPxIsAtLeast300() {
    // AC-8: on viewports < 768 px the hero uses min-height (>= 300 px)
    HomepageDto homepage = getHomepage(0, 10, null);
    assertTrue(
        homepage.getHero().getMinHeightPx() >= 300,
        "Hero minHeightPx must be >= 300 for mobile responsiveness (AC-8); got "
            + homepage.getHero().getMinHeightPx());
  }

  // -----------------------------------------------------------------------
  // AC-9: >= 32 px gap between hero and blog-card grid
  // -----------------------------------------------------------------------

  @Test
  void testBlogSectionMarginTopIsAtLeast32Px() {
    // AC-9: minimum 32 px (2 rem) vertical spacing gap below the hero
    HomepageDto homepage = getHomepage(0, 10, null);
    assertTrue(
        homepage.getBlogSectionMarginTopPx() >= 32,
        "blogSectionMarginTopPx must be >= 32 px (2 rem) on all viewports (AC-9); got "
            + homepage.getBlogSectionMarginTopPx());
  }

  // -----------------------------------------------------------------------
  // AC-10: keyboard accessibility – CTA is a plain anchor link
  // -----------------------------------------------------------------------

  @Test
  void testHeroCtaHrefIsNotJavaScriptUri() {
    // AC-10: CTA must be a plain anchor link, not a javascript: URI, so it is
    // keyboard-accessible without JavaScript hydration.
    HomepageDto homepage = getHomepage(0, 10, null);
    String href = homepage.getHero().getCtaHref();
    assertFalse(
        href.toLowerCase().startsWith("javascript:"),
        "CTA href must not be a javascript: URI (AC-10)");
  }

  // -----------------------------------------------------------------------
  // Full validation contract
  // -----------------------------------------------------------------------

  @Test
  void testDefaultHomepagePassesFullValidation() {
    // All acceptance criteria with a structural contract must pass
    HomepageDto homepage = getHomepage(0, 10, null);
    assertTrue(homepage.isValid(), "Default homepage must pass full validation; got: " + homepage);
  }

  @Test
  void testHomepageWithBackgroundImagePassesFullValidation() {
    HomepageDto homepage = getHomepage(0, 10, "https://cdn.example.com/bg.jpg");
    assertTrue(
        homepage.isValid(),
        "Homepage with backgroundImage must pass full validation; got: " + homepage);
  }

  // -----------------------------------------------------------------------
  // Pagination
  // -----------------------------------------------------------------------

  @Test
  void testGetHomepageWithPagination() {
    HomepageDto homepage = getHomepage(0, 1, null);
    assertEquals(1, homepage.getBlogCards().size(), "Pagination must limit blog cards");
  }
}
