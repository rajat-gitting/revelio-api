package com.revelio.api.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link HomepageDto}. Covers the layout contract and acceptance criteria from
 * ticket CR-22.
 */
class HomepageDtoTest {

  // -----------------------------------------------------------------------
  // Helper: build a valid default HeroSectionDto
  // -----------------------------------------------------------------------

  private HeroSectionDto validHero() {
    return new HeroSectionDto(
        "Welcome to Our Blog",
        "Discover articles, insights, and stories",
        "Explore Articles",
        "#blog-section",
        null,
        HeroSectionDto.DEFAULT_BACKGROUND_GRADIENT,
        HeroSectionDto.DEFAULT_OVERLAY_OPACITY,
        HeroSectionDto.DEFAULT_MIN_HEIGHT_PX,
        HeroSectionDto.DESKTOP_HEIGHT_PX,
        "blog-section");
  }

  private HomepageDto validHomepage() {
    return new HomepageDto(validHero(), Collections.emptyList(), "blog-section", 32);
  }

  // -----------------------------------------------------------------------
  // AC-9: minimum 32 px gap constant
  // -----------------------------------------------------------------------

  @Test
  void testMinimumBlogSectionMarginTopConstantIs32() {
    // AC-9: the constant must be exactly 32 px (= 2 rem at default 16 px/rem root)
    assertEquals(32, HomepageDto.MINIMUM_BLOG_SECTION_MARGIN_TOP_PX);
  }

  @Test
  void testDefaultHomepageBlogSectionMarginTopIs32() {
    // AC-9: default homepage has a 32 px gap between hero and blog cards
    HomepageDto homepage = validHomepage();
    assertTrue(
        homepage.getBlogSectionMarginTopPx() >= HomepageDto.MINIMUM_BLOG_SECTION_MARGIN_TOP_PX,
        "blogSectionMarginTopPx must be >= 32 px on all viewport sizes (AC-9)");
  }

  // -----------------------------------------------------------------------
  // isValid() contract
  // -----------------------------------------------------------------------

  @Test
  void testValidHomepagePassesValidation() {
    HomepageDto homepage = validHomepage();
    assertTrue(homepage.isValid(), "Valid homepage must pass isValid()");
  }

  @Test
  void testHomepageWithNullHeroFailsValidation() {
    // AC-1: hero must be present
    HomepageDto homepage = new HomepageDto(null, Collections.emptyList(), "blog-section", 32);
    assertFalse(homepage.isValid(), "Homepage with null hero must fail isValid()");
  }

  @Test
  void testHomepageWithInvalidHeroFailsValidation() {
    // AC-1: an invalid hero makes the homepage invalid
    HeroSectionDto badHero = validHero();
    badHero.setHeadline(""); // blank headline
    HomepageDto homepage = new HomepageDto(badHero, Collections.emptyList(), "blog-section", 32);
    assertFalse(homepage.isValid(), "Homepage with invalid hero must fail isValid()");
  }

  @Test
  void testHomepageWithMismatchedBlogSectionIdFailsValidation() {
    // AC-4: blogSectionId must match the hero CTA href target
    HomepageDto homepage =
        new HomepageDto(validHero(), Collections.emptyList(), "different-section", 32);
    assertFalse(
        homepage.isValid(),
        "Homepage with blogSectionId not matching hero ctaHref must fail isValid()");
  }

  @Test
  void testHomepageWithMarginBelow32FailsValidation() {
    // AC-9: a gap of less than 32 px must fail
    HomepageDto homepage =
        new HomepageDto(validHero(), Collections.emptyList(), "blog-section", 31);
    assertFalse(
        homepage.isValid(), "Homepage with blogSectionMarginTopPx < 32 must fail isValid()");
  }

  @Test
  void testHomepageWithExactly32PxMarginPassesValidation() {
    // AC-9: exactly 32 px is the minimum acceptable margin
    HomepageDto homepage =
        new HomepageDto(validHero(), Collections.emptyList(), "blog-section", 32);
    assertTrue(homepage.isValid(), "Homepage with exactly 32 px margin must pass isValid()");
  }

  @Test
  void testHomepageWithLargerMarginPassesValidation() {
    // AC-9: a larger gap (e.g., 40 px) also satisfies the constraint
    HomepageDto homepage =
        new HomepageDto(validHero(), Collections.emptyList(), "blog-section", 40);
    assertTrue(homepage.isValid(), "Homepage with margin > 32 px must pass isValid()");
  }

  @Test
  void testHomepageWithNullBlogSectionIdFailsValidation() {
    // AC-4: blogSectionId must not be null
    HomepageDto homepage = new HomepageDto(validHero(), Collections.emptyList(), null, 32);
    assertFalse(homepage.isValid(), "Homepage with null blogSectionId must fail isValid()");
  }

  // -----------------------------------------------------------------------
  // AC-1: hero comes first – field ordering in equals/toString
  // -----------------------------------------------------------------------

  @Test
  void testHomepageHeroFieldIsAccessible() {
    // AC-1: hero field is the first field and must always be accessible
    HomepageDto homepage = validHomepage();
    assertNotNull(homepage.getHero());
  }

  @Test
  void testToStringContainsHeroAndBlogSectionId() {
    HomepageDto homepage = validHomepage();
    String s = homepage.toString();
    assertTrue(s.contains("blog-section"), "toString must contain blogSectionId");
  }

  // -----------------------------------------------------------------------
  // equals / hashCode
  // -----------------------------------------------------------------------

  @Test
  void testEqualsAndHashCode() {
    HomepageDto a = validHomepage();
    HomepageDto b = validHomepage();
    assertEquals(a, b);
    assertEquals(a.hashCode(), b.hashCode());
  }

  @Test
  void testNotEqualWhenMarginTopDiffers() {
    HomepageDto a = new HomepageDto(validHero(), Collections.emptyList(), "blog-section", 32);
    HomepageDto b = new HomepageDto(validHero(), Collections.emptyList(), "blog-section", 48);
    assertNotEquals(a, b);
  }

  // -----------------------------------------------------------------------
  // Blog cards
  // -----------------------------------------------------------------------

  @Test
  void testBlogCardsFieldIsAccessible() {
    HomepageDto homepage = validHomepage();
    assertNotNull(homepage.getBlogCards());
  }

  @Test
  void testHomepageWithNonEmptyBlogCardsList() {
    BlogResponseDto card = new BlogResponseDto();
    card.setTitle("Test Post");
    HomepageDto homepage = new HomepageDto(validHero(), Arrays.asList(card), "blog-section", 32);
    assertEquals(1, homepage.getBlogCards().size());
    assertTrue(homepage.isValid());
  }
}
