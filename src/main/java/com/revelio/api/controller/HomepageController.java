package com.revelio.api.controller;

import com.revelio.api.dto.ApiResponse;
import com.revelio.api.dto.HomepageDto;
import com.revelio.api.service.HomepageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller that serves the homepage payload, including the hero section and a paginated list
 * of published blog cards (ticket CR-22).
 *
 * <p>The homepage response is structured so that:
 *
 * <ul>
 *   <li>The hero section is the first object in the payload (AC-1).
 *   <li>The hero contains all fields needed by the UI: headline, subheading, CTA label+href,
 *       background gradient, optional background image, overlay opacity, min-height, desktop-height
 *       and the blog-section element id (AC-2 through AC-9).
 *   <li>Blog cards follow the hero in the same response, separated by a minimum 32 px margin
 *       expressed as {@code blogSectionMarginTopPx} (AC-9).
 * </ul>
 */
@Tag(name = "Homepage")
@RestController
@RequestMapping("/homepage")
@RequiredArgsConstructor
@Slf4j
public class HomepageController {

  private final HomepageService homepageService;

  /**
   * Returns the full homepage payload: hero section + first page of published blog cards.
   *
   * @param page zero-based page index (default 0)
   * @param size number of blog cards per page (default 10)
   * @param backgroundImage optional background image URL for the hero; when omitted the hero uses
   *     its default CSS gradient (AC-6)
   * @return {@link HomepageDto} wrapped in {@link ApiResponse}
   */
  @GetMapping
  @Operation(
      summary = "Homepage payload",
      description =
          "Returns the hero section (positioned above all blog cards, AC-1) together with"
              + " a paginated list of published blog cards.")
  public ResponseEntity<ApiResponse<HomepageDto>> getHomepage(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(required = false) String backgroundImage) {
    log.debug("GET /api/homepage page={} size={} backgroundImage={}", page, size, backgroundImage);
    HomepageDto homepage = homepageService.getHomepage(page, size, backgroundImage);
    return ResponseEntity.ok(ApiResponse.ok(homepage));
  }
}
