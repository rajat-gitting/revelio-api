package com.revelio.api.dto;

import java.util.Objects;

/**
 * Server-declared UI configuration for the blog search feature.
 *
 * <p>Clients (e.g. a React SPA) consume this object to wire up keyboard shortcuts and element
 * targeting without hard-coding values. The search shortcut key and the DOM element ID of the
 * search input are declared here so the backend is the single source of truth.
 *
 * <p>Expected usage in the client:
 *
 * <pre>
 *   // On keydown (when focus is not in a text input):
 *   if (event.key === config.keyboardShortcutKey && !isTextInputFocused()) {
 *     event.preventDefault();
 *     document.getElementById(config.searchInputId)?.focus();
 *   }
 * </pre>
 */
public class SearchUiConfigDto {

  /**
   * The keyboard key that should focus the search input when pressed outside a text element. Value:
   * {@code "/"} per CR-23 specification.
   */
  private String keyboardShortcutKey;

  /**
   * The HTML element {@code id} of the search input that the keyboard shortcut should focus. The
   * client must render an {@code <input id="blog-search-input" ...>} to satisfy this contract.
   */
  private String searchInputId;

  /**
   * Human-readable description of the keyboard shortcut displayed in the UI (e.g. a tooltip or help
   * panel).
   */
  private String shortcutDescription;

  public SearchUiConfigDto() {}

  public SearchUiConfigDto(
      String keyboardShortcutKey, String searchInputId, String shortcutDescription) {
    this.keyboardShortcutKey = keyboardShortcutKey;
    this.searchInputId = searchInputId;
    this.shortcutDescription = shortcutDescription;
  }

  public String getKeyboardShortcutKey() {
    return keyboardShortcutKey;
  }

  public void setKeyboardShortcutKey(String keyboardShortcutKey) {
    this.keyboardShortcutKey = keyboardShortcutKey;
  }

  public String getSearchInputId() {
    return searchInputId;
  }

  public void setSearchInputId(String searchInputId) {
    this.searchInputId = searchInputId;
  }

  public String getShortcutDescription() {
    return shortcutDescription;
  }

  public void setShortcutDescription(String shortcutDescription) {
    this.shortcutDescription = shortcutDescription;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    SearchUiConfigDto that = (SearchUiConfigDto) o;
    return Objects.equals(keyboardShortcutKey, that.keyboardShortcutKey)
        && Objects.equals(searchInputId, that.searchInputId)
        && Objects.equals(shortcutDescription, that.shortcutDescription);
  }

  @Override
  public int hashCode() {
    return Objects.hash(keyboardShortcutKey, searchInputId, shortcutDescription);
  }

  @Override
  public String toString() {
    return "SearchUiConfigDto{"
        + "keyboardShortcutKey='"
        + keyboardShortcutKey
        + '\''
        + ", searchInputId='"
        + searchInputId
        + '\''
        + ", shortcutDescription='"
        + shortcutDescription
        + '\''
        + '}';
  }
}
