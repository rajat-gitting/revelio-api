package com.revelio.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Objects;

/**
 * Data transfer object representing the hero/banner section displayed at the very top of the
 * homepage (above the blog-card grid). The section displays a headline, a subheading, and a
 * call-to-action (CTA) button. An optional background image URL may be supplied; when present the
 * UI must layer a semi-transparent dark overlay over it to preserve WCAG AA text-contrast ratios.
 *
 * <p>Design contract (enforced by the API):
 *
 * <ul>
 *   <li>{@code headline} – required, non-blank (AC-3)
 *   <li>{@code subheading} – required, non-blank (AC-3)
 *   <li>{@code ctaLabel} – required, non-blank; value "Explore Articles" or "Read Latest" (AC-4)
 *   <li>{@code ctaHref} – required, must start with '#' to implement smooth-scroll anchor (AC-4)
 *   <li>{@code backgroundImage} – optional; when {@code null} the UI renders its default CSS
 *       gradient; when provided the UI renders the image with an rgba overlay (AC-6)
 *   <li>{@code backgroundGradient} – CSS gradient string used as the default background; exposed
 *       here so the token can be overridden per environment without touching UI code (AC-6)
 *   <li>{@code overlayOpacity} – opacity value (0.0–1.0) for the semi-transparent overlay when a
 *       {@code backgroundImage} is present; defaults to 0.45 (AC-6, AC-7)
 *   <li>{@code minHeightPx} – minimum height in px applied at all viewports; the UI must also apply
 *       a fixed height of 300–400 px on viewports ≥ 1024 px (AC-2, AC-8)
 *   <li>{@code blogSectionId} – the DOM element id to which the CTA scrolls (AC-4)
 * </ul>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HeroSectionDto {

  /** Default CSS gradient used when no background image is supplied (AC-6). */
  public static final String DEFAULT_BACKGROUND_GRADIENT =
      "linear-gradient(135deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%)";

  /**
   * Default dark-overlay opacity applied over a background image to ensure WCAG AA contrast ratios
   * (AC-6, AC-7). Value 0.45 provides at least 4.5:1 contrast for white text on dark-medium images.
   */
  public static final double DEFAULT_OVERLAY_OPACITY = 0.45;

  /** Default CTA label (AC-4). */
  public static final String DEFAULT_CTA_LABEL = "Explore Articles";

  /** Default CTA href – smooth-scroll anchor pointing to the blog-card section (AC-4). */
  public static final String DEFAULT_CTA_HREF = "#blog-section";

  /** Default blog-section DOM id (AC-4). */
  public static final String DEFAULT_BLOG_SECTION_ID = "blog-section";

  /** Minimum hero height in pixels at all viewports (AC-2, AC-8). */
  public static final int DEFAULT_MIN_HEIGHT_PX = 300;

  /** Desktop hero height in pixels applied on viewports >= 1024 px (AC-2). */
  public static final int DESKTOP_HEIGHT_PX = 350;

  private String headline;
  private String subheading;
  private String ctaLabel;
  private String ctaHref;
  private String backgroundImage;
  private String backgroundGradient;
  private double overlayOpacity;
  private int minHeightPx;
  private int desktopHeightPx;
  private String blogSectionId;

  public HeroSectionDto() {}

  public HeroSectionDto(
      String headline,
      String subheading,
      String ctaLabel,
      String ctaHref,
      String backgroundImage,
      String backgroundGradient,
      double overlayOpacity,
      int minHeightPx,
      int desktopHeightPx,
      String blogSectionId) {
    this.headline = headline;
    this.subheading = subheading;
    this.ctaLabel = ctaLabel;
    this.ctaHref = ctaHref;
    this.backgroundImage = backgroundImage;
    this.backgroundGradient = backgroundGradient;
    this.overlayOpacity = overlayOpacity;
    this.minHeightPx = minHeightPx;
    this.desktopHeightPx = desktopHeightPx;
    this.blogSectionId = blogSectionId;
  }

  // -------------------------------------------------------------------------
  // Getters / setters
  // -------------------------------------------------------------------------

  public String getHeadline() {
    return headline;
  }

  public void setHeadline(String headline) {
    this.headline = headline;
  }

  public String getSubheading() {
    return subheading;
  }

  public void setSubheading(String subheading) {
    this.subheading = subheading;
  }

  public String getCtaLabel() {
    return ctaLabel;
  }

  public void setCtaLabel(String ctaLabel) {
    this.ctaLabel = ctaLabel;
  }

  public String getCtaHref() {
    return ctaHref;
  }

  public void setCtaHref(String ctaHref) {
    this.ctaHref = ctaHref;
  }

  public String getBackgroundImage() {
    return backgroundImage;
  }

  public void setBackgroundImage(String backgroundImage) {
    this.backgroundImage = backgroundImage;
  }

  public String getBackgroundGradient() {
    return backgroundGradient;
  }

  public void setBackgroundGradient(String backgroundGradient) {
    this.backgroundGradient = backgroundGradient;
  }

  public double getOverlayOpacity() {
    return overlayOpacity;
  }

  public void setOverlayOpacity(double overlayOpacity) {
    this.overlayOpacity = overlayOpacity;
  }

  public int getMinHeightPx() {
    return minHeightPx;
  }

  public void setMinHeightPx(int minHeightPx) {
    this.minHeightPx = minHeightPx;
  }

  public int getDesktopHeightPx() {
    return desktopHeightPx;
  }

  public void setDesktopHeightPx(int desktopHeightPx) {
    this.desktopHeightPx = desktopHeightPx;
  }

  public String getBlogSectionId() {
    return blogSectionId;
  }

  public void setBlogSectionId(String blogSectionId) {
    this.blogSectionId = blogSectionId;
  }

  // -------------------------------------------------------------------------
  // Validation helpers
  // -------------------------------------------------------------------------

  /**
   * Returns true if the DTO is fully valid for rendering:
   *
   * <ul>
   *   <li>headline is non-blank
   *   <li>subheading is non-blank
   *   <li>ctaLabel is non-blank
   *   <li>ctaHref starts with '#' (smooth-scroll anchor, AC-4)
   *   <li>overlayOpacity is between 0 and 1 inclusive
   *   <li>minHeightPx >= 0
   *   <li>desktopHeightPx is between 300 and 400 (AC-2)
   * </ul>
   */
  public boolean isValid() {
    return isNonBlank(headline)
        && isNonBlank(subheading)
        && isNonBlank(ctaLabel)
        && isNonBlank(ctaHref)
        && ctaHref.startsWith("#")
        && overlayOpacity >= 0.0
        && overlayOpacity <= 1.0
        && minHeightPx >= 0
        && desktopHeightPx >= 300
        && desktopHeightPx <= 400;
  }

  /**
   * Returns true if a background image URL has been supplied, meaning the UI should render the
   * image with a semi-transparent overlay rather than the default gradient (AC-6).
   */
  public boolean hasBackgroundImage() {
    return isNonBlank(backgroundImage);
  }

  private static boolean isNonBlank(String s) {
    return s != null && !s.isBlank();
  }

  // -------------------------------------------------------------------------
  // equals / hashCode / toString
  // -------------------------------------------------------------------------

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    HeroSectionDto that = (HeroSectionDto) o;
    return Double.compare(that.overlayOpacity, overlayOpacity) == 0
        && minHeightPx == that.minHeightPx
        && desktopHeightPx == that.desktopHeightPx
        && Objects.equals(headline, that.headline)
        && Objects.equals(subheading, that.subheading)
        && Objects.equals(ctaLabel, that.ctaLabel)
        && Objects.equals(ctaHref, that.ctaHref)
        && Objects.equals(backgroundImage, that.backgroundImage)
        && Objects.equals(backgroundGradient, that.backgroundGradient)
        && Objects.equals(blogSectionId, that.blogSectionId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        headline,
        subheading,
        ctaLabel,
        ctaHref,
        backgroundImage,
        backgroundGradient,
        overlayOpacity,
        minHeightPx,
        desktopHeightPx,
        blogSectionId);
  }

  @Override
  public String toString() {
    return "HeroSectionDto{"
        + "headline='"
        + headline
        + '\''
        + ", subheading='"
        + subheading
        + '\''
        + ", ctaLabel='"
        + ctaLabel
        + '\''
        + ", ctaHref='"
        + ctaHref
        + '\''
        + ", backgroundImage='"
        + backgroundImage
        + '\''
        + ", backgroundGradient='"
        + backgroundGradient
        + '\''
        + ", overlayOpacity="
        + overlayOpacity
        + ", minHeightPx="
        + minHeightPx
        + ", desktopHeightPx="
        + desktopHeightPx
        + ", blogSectionId='"
        + blogSectionId
        + '\''
        + '}';
  }
}
