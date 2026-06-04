package com.revelio.api.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link HeroSectionDto}. Each test maps to at least one acceptance criterion from
 * ticket CR-22.
 */
class HeroSectionDtoTest {

  // -----------------------------------------------------------------------
  // Helper: build a valid default HeroSectionDto
  // -----------------------------------------------------------------------

  private HeroSectionDto defaultHero() {
    return new HeroSectionDto(
        "Welcome to Our Blog",
        "Discover articles, insights, and stories",
        HeroSectionDto.DEFAULT_CTA_LABEL,
        HeroSectionDto.DEFAULT_CTA_HREF,
        null,
        HeroSectionDto.DEFAULT_BACKGROUND_GRADIENT,
        HeroSectionDto.DEFAULT_OVERLAY_OPACITY,
        HeroSectionDto.DEFAULT_MIN_HEIGHT_PX,
        HeroSectionDto.DESKTOP_HEIGHT_PX,
        HeroSectionDto.DEFAULT_BLOG_SECTION_ID);
  }

  // -----------------------------------------------------------------------
  // AC-3: headline and subheading with approved placeholder copy
  // -----------------------------------------------------------------------

  @Test
  void testDefaultHeroHeadlinePlaceholderCopy() {
    // AC-3: headline placeholder is "Welcome to Our Blog"
    HeroSectionDto hero = defaultHero();
    assertEquals("Welcome to Our Blog", hero.getHeadline());
  }

  @Test
  void testDefaultHeroSubheadingPlaceholderCopy() {
    // AC-3: subheading placeholder is "Discover articles, insights, and stories"
    HeroSectionDto hero = defaultHero();
    assertEquals("Discover articles, insights, and stories", hero.getSubheading());
  }

  @Test
  void testHeadlineAndSubheadingAreDistinct() {
    // AC-3: headline and subheading are distinct typographic elements (different values)
    HeroSectionDto hero = defaultHero();
    assertNotEquals(hero.getHeadline(), hero.getSubheading());
    assertNotNull(hero.getHeadline());
    assertNotNull(hero.getSubheading());
  }

  // -----------------------------------------------------------------------
  // AC-4: CTA label and href
  // -----------------------------------------------------------------------

  @Test
  void testDefaultCtaLabelIsExploreArticles() {
    // AC-4: CTA button labelled "Explore Articles"
    HeroSectionDto hero = defaultHero();
    assertEquals("Explore Articles", hero.getCtaLabel());
  }

  @Test
  void testCtaLabelCanBeReadLatest() {
    // AC-4: alternative accepted CTA label is "Read Latest"
    HeroSectionDto hero = defaultHero();
    hero.setCtaLabel("Read Latest");
    assertNotNull(hero.getCtaLabel());
    assertTrue(
        hero.getCtaLabel().equals("Explore Articles") || hero.getCtaLabel().equals("Read Latest"),
        "CTA label must be 'Explore Articles' or 'Read Latest'");
  }

  @Test
  void testDefaultCtaHrefIsAnchorLink() {
    // AC-4: CTA href must start with '#' for smooth-scroll anchor
    HeroSectionDto hero = defaultHero();
    assertTrue(
        hero.getCtaHref().startsWith("#"),
        "ctaHref must start with '#' to implement smooth-scroll anchor");
  }

  @Test
  void testDefaultCtaHrefTargetsBlogSection() {
    // AC-4: CTA scrolls to #blog-section
    HeroSectionDto hero = defaultHero();
    assertEquals("#blog-section", hero.getCtaHref());
  }

  @Test
  void testBlogSectionIdMatchesCtaHrefTarget() {
    // AC-4: blogSectionId must match the fragment in ctaHref
    HeroSectionDto hero = defaultHero();
    String expectedTarget = hero.getCtaHref().substring(1); // strip leading '#'
    assertEquals(
        expectedTarget,
        hero.getBlogSectionId(),
        "blogSectionId must equal the fragment identifier in ctaHref");
  }

  // -----------------------------------------------------------------------
  // AC-2: desktop height 300–400 px
  // -----------------------------------------------------------------------

  @Test
  void testDesktopHeightIsWithin300To400Px() {
    // AC-2: hero is 300–400 px tall on viewports >= 1024 px
    HeroSectionDto hero = defaultHero();
    int h = hero.getDesktopHeightPx();
    assertTrue(h >= 300 && h <= 400, "desktopHeightPx must be 300–400; got " + h);
  }

  // -----------------------------------------------------------------------
  // AC-8: mobile min-height >= 300 px (adapts to content, not fixed)
  // -----------------------------------------------------------------------

  @Test
  void testMinHeightPxIsAtLeast300() {
    // AC-8: on mobile the hero uses min-height (>= 300 px) rather than a fixed height
    HeroSectionDto hero = defaultHero();
    assertTrue(
        hero.getMinHeightPx() >= 300,
        "minHeightPx must be >= 300 so mobile hero adapts to content; got "
            + hero.getMinHeightPx());
  }

  // -----------------------------------------------------------------------
  // AC-6: background gradient default / image + overlay
  // -----------------------------------------------------------------------

  @Test
  void testDefaultBackgroundGradientIsSet() {
    // AC-6: hero background defaults to a CSS gradient
    HeroSectionDto hero = defaultHero();
    assertNotNull(
        hero.getBackgroundGradient(), "backgroundGradient must be set for the default hero");
    assertTrue(
        hero.getBackgroundGradient().startsWith("linear-gradient"),
        "backgroundGradient should be a CSS linear-gradient; got: " + hero.getBackgroundGradient());
  }

  @Test
  void testDefaultHeroHasNoBackgroundImage() {
    // AC-6: when no backgroundImage prop is supplied, hasBackgroundImage() is false
    HeroSectionDto hero = defaultHero();
    assertFalse(
        hero.hasBackgroundImage(), "default hero must not have a backgroundImage (gradient only)");
    assertNull(hero.getBackgroundImage());
  }

  @Test
  void testHeroWithBackgroundImageHasBackgroundImageTrue() {
    // AC-6: when backgroundImage URL is supplied, hasBackgroundImage() returns true
    HeroSectionDto hero = defaultHero();
    hero.setBackgroundImage("https://example.com/hero-bg.jpg");
    assertTrue(hero.hasBackgroundImage(), "hasBackgroundImage() must return true when URL is set");
  }

  @Test
  void testDefaultOverlayOpacityIs045() {
    // AC-6, AC-7: overlay opacity defaults to 0.45 to guarantee WCAG AA contrast
    HeroSectionDto hero = defaultHero();
    assertEquals(
        0.45,
        hero.getOverlayOpacity(),
        1e-9,
        "Default overlay opacity must be 0.45 to preserve text contrast over background images");
  }

  @Test
  void testOverlayOpacityIsBetweenZeroAndOne() {
    // AC-7: overlay opacity must be in [0, 1] to be a valid CSS opacity value
    HeroSectionDto hero = defaultHero();
    assertTrue(
        hero.getOverlayOpacity() >= 0.0 && hero.getOverlayOpacity() <= 1.0,
        "overlayOpacity must be in [0, 1]");
  }

  // -----------------------------------------------------------------------
  // AC-7: WCAG AA contrast – overlay opacity >= 0.40 for white text on images
  // -----------------------------------------------------------------------

  @Test
  void testOverlayOpacityMeetsMinimumForWcagAaContrast() {
    // AC-7: a minimum overlay opacity of 0.40 is required so that white text
    // achieves at least 4.5:1 contrast ratio against arbitrary background images.
    HeroSectionDto hero = defaultHero();
    assertTrue(
        hero.getOverlayOpacity() >= 0.40,
        "overlayOpacity must be >= 0.40 to meet WCAG AA contrast requirements; got "
            + hero.getOverlayOpacity());
  }

  // -----------------------------------------------------------------------
  // isValid() contract
  // -----------------------------------------------------------------------

  @Test
  void testDefaultHeroPassesValidation() {
    // All default values must satisfy the validation contract
    HeroSectionDto hero = defaultHero();
    assertTrue(hero.isValid(), "Default hero must pass isValid(); hero=" + hero);
  }

  @Test
  void testHeroWithBlankHeadlineFailsValidation() {
    HeroSectionDto hero = defaultHero();
    hero.setHeadline("");
    assertFalse(hero.isValid(), "Hero with blank headline must fail isValid()");
  }

  @Test
  void testHeroWithNullSubheadingFailsValidation() {
    HeroSectionDto hero = defaultHero();
    hero.setSubheading(null);
    assertFalse(hero.isValid(), "Hero with null subheading must fail isValid()");
  }

  @Test
  void testHeroWithNonAnchorCtaHrefFailsValidation() {
    // AC-4: ctaHref must start with '#'
    HeroSectionDto hero = defaultHero();
    hero.setCtaHref("/blog");
    assertFalse(
        hero.isValid(), "Hero ctaHref must start with '#'; a path-based href must fail isValid()");
  }

  @Test
  void testHeroWithDesktopHeightBelow300FailsValidation() {
    // AC-2: desktop height must be >= 300 px
    HeroSectionDto hero = defaultHero();
    hero.setDesktopHeightPx(299);
    assertFalse(hero.isValid(), "Hero with desktopHeightPx < 300 must fail isValid()");
  }

  @Test
  void testHeroWithDesktopHeightAbove400FailsValidation() {
    // AC-2: desktop height must be <= 400 px
    HeroSectionDto hero = defaultHero();
    hero.setDesktopHeightPx(401);
    assertFalse(hero.isValid(), "Hero with desktopHeightPx > 400 must fail isValid()");
  }

  // -----------------------------------------------------------------------
  // AC-10: keyboard accessibility – ctaHref is an anchor so it is natively
  // focusable and tab-reachable; validated by ensuring it is not a JS handler
  // -----------------------------------------------------------------------

  @Test
  void testCtaHrefIsAnchorNotJavaScriptHandler() {
    // AC-10: CTA must use an anchor link, not a javascript: URI, so it is
    // accessible without JS and reachable via keyboard tabbing.
    HeroSectionDto hero = defaultHero();
    assertFalse(
        hero.getCtaHref().toLowerCase().startsWith("javascript:"),
        "ctaHref must not be a javascript: URI – use a plain '#...' anchor link");
  }

  // -----------------------------------------------------------------------
  // equals / hashCode / toString sanity
  // -----------------------------------------------------------------------

  @Test
  void testEqualsAndHashCode() {
    HeroSectionDto a = defaultHero();
    HeroSectionDto b = defaultHero();
    assertEquals(a, b);
    assertEquals(a.hashCode(), b.hashCode());
  }

  @Test
  void testToStringContainsKeyFields() {
    HeroSectionDto hero = defaultHero();
    String s = hero.toString();
    assertTrue(s.contains("Welcome to Our Blog"), "toString must contain headline");
    assertTrue(s.contains("#blog-section"), "toString must contain ctaHref");
  }
}
