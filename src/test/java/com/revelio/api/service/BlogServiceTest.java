package com.revelio.api.service;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.revelio.api.dto.BlogResponseDto;
import com.revelio.api.dto.CreateBlogRequestDto;
import com.revelio.api.dto.PagedResponse;
import com.revelio.api.exception.BadRequestException;
import com.revelio.api.model.Blog;
import java.io.File;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class BlogServiceTest {

  @TempDir java.nio.file.Path tempDir;

  private BlogService blogService;
  private List<Blog> testBlogs;

  @BeforeEach
  void setUp() {
    testBlogs = new ArrayList<>();

    Blog.Author author1 = new Blog.Author("John Doe", "https://example.com/john.jpg");
    Blog.Author author2 = new Blog.Author("Jane Smith", "https://example.com/jane.jpg");

    testBlogs.add(
        new Blog(
            1L,
            "First Post",
            "First excerpt",
            "https://example.com/1.jpg",
            author1,
            Arrays.asList("tech", "java"),
            Instant.parse("2024-01-15T10:00:00Z"),
            true));

    testBlogs.add(
        new Blog(
            2L,
            "Second Post",
            "Second excerpt",
            "https://example.com/2.jpg",
            author2,
            Arrays.asList("design"),
            Instant.parse("2024-01-20T10:00:00Z"),
            false));

    testBlogs.add(
        new Blog(
            3L,
            "Third Post",
            "Third excerpt",
            "https://example.com/3.jpg",
            author1,
            Arrays.asList("spring"),
            Instant.parse("2024-01-25T10:00:00Z"),
            true));

    testBlogs.add(
        new Blog(
            4L,
            "Fourth Post",
            "Fourth excerpt",
            "https://example.com/4.jpg",
            author2,
            Arrays.asList("ui"),
            Instant.parse("2024-01-10T10:00:00Z"),
            true));

    testBlogs.add(
        new Blog(
            5L,
            "Fifth Post",
            "Fifth excerpt",
            "https://example.com/5.jpg",
            author1,
            Arrays.asList("backend"),
            Instant.parse("2024-01-30T10:00:00Z"),
            false));

    blogService = new BlogService(testBlogs);
  }

  @Test
  void testGetPublishedBlogsReturnsOnlyPublishedPosts() {
    List<Blog> result = blogService.getPublishedBlogs(0, 10);

    assertEquals(3, result.size());
    assertTrue(result.stream().allMatch(Blog::isPublished));
  }

  @Test
  void testGetPublishedBlogsSortsByPublishedAtDescending() {
    List<Blog> result = blogService.getPublishedBlogs(0, 10);

    assertEquals(3L, result.get(0).getId());
    assertEquals(1L, result.get(1).getId());
    assertEquals(4L, result.get(2).getId());

    assertEquals(Instant.parse("2024-01-25T10:00:00Z"), result.get(0).getPublishedAt());
    assertEquals(Instant.parse("2024-01-15T10:00:00Z"), result.get(1).getPublishedAt());
    assertEquals(Instant.parse("2024-01-10T10:00:00Z"), result.get(2).getPublishedAt());
  }

  @Test
  void testGetPublishedBlogsWithPaginationFirstPage() {
    List<Blog> result = blogService.getPublishedBlogs(0, 2);

    assertEquals(2, result.size());
    assertEquals(3L, result.get(0).getId());
    assertEquals(1L, result.get(1).getId());
  }

  @Test
  void testGetPublishedBlogsWithPaginationSecondPage() {
    List<Blog> result = blogService.getPublishedBlogs(1, 2);

    assertEquals(1, result.size());
    assertEquals(4L, result.get(0).getId());
  }

  @Test
  void testGetPublishedBlogsWithPaginationBeyondAvailableData() {
    List<Blog> result = blogService.getPublishedBlogs(5, 10);

    assertTrue(result.isEmpty());
  }

  @Test
  void testGetPublishedBlogsWithPageSizeLargerThanAvailableData() {
    List<Blog> result = blogService.getPublishedBlogs(0, 100);

    assertEquals(3, result.size());
  }

  @Test
  void testGetPublishedBlogsThrowsExceptionForNegativePage() {
    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> blogService.getPublishedBlogs(-1, 10));

    assertEquals("Page number must be non-negative", exception.getMessage());
  }

  @Test
  void testGetPublishedBlogsThrowsExceptionForZeroSize() {
    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> blogService.getPublishedBlogs(0, 0));

    assertEquals("Page size must be positive", exception.getMessage());
  }

  @Test
  void testGetPublishedBlogsThrowsExceptionForNegativeSize() {
    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> blogService.getPublishedBlogs(0, -5));

    assertEquals("Page size must be positive", exception.getMessage());
  }

  @Test
  void testGetPublishedBlogsWithEmptyRepository() {
    BlogService emptyService = new BlogService(new ArrayList<>());
    List<Blog> result = emptyService.getPublishedBlogs(0, 10);

    assertTrue(result.isEmpty());
  }

  @Test
  void testGetPublishedBlogsWithAllUnpublishedPosts() {
    List<Blog> unpublishedBlogs = new ArrayList<>();
    Blog.Author author = new Blog.Author("John Doe", "https://example.com/john.jpg");

    unpublishedBlogs.add(
        new Blog(
            1L,
            "Unpublished Post",
            "Excerpt",
            "https://example.com/1.jpg",
            author,
            Arrays.asList("tech"),
            Instant.parse("2024-01-15T10:00:00Z"),
            false));

    BlogService service = new BlogService(unpublishedBlogs);
    List<Blog> result = service.getPublishedBlogs(0, 10);

    assertTrue(result.isEmpty());
  }

  @Test
  void testFilterPublishedPostsReturnsOnlyPublishedPosts() {
    List<Blog> result = blogService.filterPublishedPosts(testBlogs);

    assertEquals(3, result.size());
    assertTrue(result.stream().allMatch(Blog::isPublished));
  }

  @Test
  void testFilterPublishedPostsWithNullInput() {
    List<Blog> result = blogService.filterPublishedPosts(null);

    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test
  void testFilterPublishedPostsWithEmptyList() {
    List<Blog> result = blogService.filterPublishedPosts(new ArrayList<>());

    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test
  void testFilterPublishedPostsWithAllPublishedPosts() {
    List<Blog> allPublished = new ArrayList<>();
    Blog.Author author = new Blog.Author("John Doe", "https://example.com/john.jpg");

    allPublished.add(
        new Blog(
            1L,
            "Post 1",
            "Excerpt 1",
            "https://example.com/1.jpg",
            author,
            Arrays.asList("tech"),
            Instant.parse("2024-01-15T10:00:00Z"),
            true));

    allPublished.add(
        new Blog(
            2L,
            "Post 2",
            "Excerpt 2",
            "https://example.com/2.jpg",
            author,
            Arrays.asList("java"),
            Instant.parse("2024-01-20T10:00:00Z"),
            true));

    List<Blog> result = blogService.filterPublishedPosts(allPublished);

    assertEquals(2, result.size());
  }

  @Test
  void testBlogServiceConstructorWithNullRepository() {
    BlogService service = new BlogService(null);
    List<Blog> result = service.getPublishedBlogs(0, 10);

    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test
  void testBlogServiceDefaultConstructor() {
    BlogService service = new BlogService();
    List<Blog> result = service.getPublishedBlogs(0, 10);

    assertNotNull(result);
    assertFalse(result.isEmpty());
  }

  @Test
  void testGetPublishedBlogsWithPartialPage() {
    List<Blog> result = blogService.getPublishedBlogs(1, 5);

    assertTrue(result.isEmpty());
  }

  @Test
  void testGetPublishedBlogsPreservesOriginalList() {
    int originalSize = testBlogs.size();
    blogService.getPublishedBlogs(0, 10);

    assertEquals(originalSize, testBlogs.size());
  }

  @Test
  void testSeedDataContainsNewAIBlogPost() {
    BlogService service = new BlogService();
    List<Blog> result = service.getPublishedBlogs(0, 10);

    Blog aiBlog =
        result.stream()
            .filter(blog -> blog.getTitle().equals("Development in the era of AI"))
            .findFirst()
            .orElse(null);

    assertNotNull(aiBlog);
    assertEquals(6L, aiBlog.getId());
    assertEquals("Development in the era of AI", aiBlog.getTitle());
    assertEquals(
        "How AI tools are reshaping the way developers write, review, and ship code.",
        aiBlog.getExcerpt());
    assertEquals(Arrays.asList("ai", "development", "productivity"), aiBlog.getTags());
    assertTrue(aiBlog.isPublished());
    assertNotNull(aiBlog.getAuthor());
    assertEquals("Alice Chen", aiBlog.getAuthor().getName());
  }

  @Test
  void testSeedDataContainsTenPublishedPosts() {
    BlogService service = new BlogService();
    List<Blog> result = service.getPublishedBlogs(0, 20);

    assertEquals(10, result.size());
  }

  @Test
  void testNewAIBlogPostHasUniqueId() {
    BlogService service = new BlogService();
    List<Blog> result = service.getPublishedBlogs(0, 20);

    List<Long> ids = result.stream().map(Blog::getId).collect(java.util.stream.Collectors.toList());
    long distinctCount = ids.stream().distinct().count();

    assertEquals(ids.size(), distinctCount);
    assertTrue(ids.contains(6L));
  }

  @Test
  void testSeedDataContainsMasteringCodeReviews() {
    BlogService service = new BlogService();
    List<Blog> result = service.getPublishedBlogs(0, 20);

    Blog card =
        result.stream()
            .filter(blog -> blog.getTitle().equals("Mastering Code Reviews"))
            .findFirst()
            .orElse(null);

    assertNotNull(card);
    assertEquals(7L, card.getId());
    assertEquals(
        "Best practices for giving and receiving feedback that improves code quality and team culture.",
        card.getExcerpt());
    assertEquals(Arrays.asList("code-review", "collaboration", "best-practices"), card.getTags());
    assertTrue(card.isPublished());
  }

  @Test
  void testSeedDataContainsRiseOfEdgeComputing() {
    BlogService service = new BlogService();
    List<Blog> result = service.getPublishedBlogs(0, 20);

    Blog card =
        result.stream()
            .filter(blog -> blog.getTitle().equals("The Rise of Edge Computing"))
            .findFirst()
            .orElse(null);

    assertNotNull(card);
    assertEquals(8L, card.getId());
    assertEquals(
        "Why processing data closer to the user is changing how we build modern applications.",
        card.getExcerpt());
    assertEquals(Arrays.asList("edge-computing", "architecture", "performance"), card.getTags());
    assertTrue(card.isPublished());
  }

  @Test
  void testSeedDataContainsSecuringCiCdPipeline() {
    BlogService service = new BlogService();
    List<Blog> result = service.getPublishedBlogs(0, 20);

    Blog card =
        result.stream()
            .filter(blog -> blog.getTitle().equals("Securing Your CI/CD Pipeline"))
            .findFirst()
            .orElse(null);

    assertNotNull(card);
    assertEquals(9L, card.getId());
    assertEquals(
        "Practical steps to protect your build and deployment workflows from common vulnerabilities.",
        card.getExcerpt());
    assertEquals(Arrays.asList("security", "ci-cd", "devops"), card.getTags());
    assertTrue(card.isPublished());
  }

  @Test
  void testSeedDataContainsWritingDocumentation() {
    BlogService service = new BlogService();
    List<Blog> result = service.getPublishedBlogs(0, 20);

    Blog card =
        result.stream()
            .filter(
                blog -> blog.getTitle().equals("Writing Documentation Developers Actually Read"))
            .findFirst()
            .orElse(null);

    assertNotNull(card);
    assertEquals(10L, card.getId());
    assertEquals(
        "Tips for creating clear, concise docs that reduce support tickets and onboarding time.",
        card.getExcerpt());
    assertEquals(Arrays.asList("documentation", "writing", "developer-experience"), card.getTags());
    assertTrue(card.isPublished());
  }

  // ---- Tests for getPublishedBlogsPaged (AC-1, AC-2, AC-3) ----

  /** AC-1: page and size params accepted; returns correct content slice. */
  @Test
  void testGetPublishedBlogsPagedReturnsCorrectContentSlice() {
    // testBlogs has 3 published posts; page=0 size=2 → 2 items
    PagedResponse<BlogResponseDto> result = blogService.getPublishedBlogsPaged(0, 2);

    assertEquals(2, result.getContent().size());
    assertEquals(3L, result.getContent().get(0).getId()); // most recent published first
    assertEquals(1L, result.getContent().get(1).getId());
  }

  /** AC-2: response includes totalElements, totalPages, number, size. */
  @Test
  void testGetPublishedBlogsPagedIncludesPaginationMetadata() {
    // 3 published posts, page=0, size=2 → totalPages=2
    PagedResponse<BlogResponseDto> result = blogService.getPublishedBlogsPaged(0, 2);

    assertEquals(3L, result.getTotalElements());
    assertEquals(2, result.getTotalPages()); // ceil(3/2)=2
    assertEquals(0, result.getNumber());
    assertEquals(2, result.getSize());
  }

  /** AC-3: page >= totalPages returns empty content with valid metadata (not HTTP 400). */
  @Test
  void testGetPublishedBlogsPagedBeyondTotalPagesReturnsEmptyContentWithMetadata() {
    // 3 published posts, page=5, size=10 → way beyond last page
    PagedResponse<BlogResponseDto> result = blogService.getPublishedBlogsPaged(5, 10);

    assertTrue(result.getContent().isEmpty());
    assertEquals(3L, result.getTotalElements());
    assertEquals(1, result.getTotalPages()); // ceil(3/10)=1
    assertEquals(5, result.getNumber());
    assertEquals(10, result.getSize());
  }

  /** size=0 must be rejected (size must be between 1 and 100). */
  @Test
  void testGetPublishedBlogsPagedThrowsForZeroSize() {
    assertThrows(IllegalArgumentException.class, () -> blogService.getPublishedBlogsPaged(0, 0));
  }

  /** size=101 must be rejected. */
  @Test
  void testGetPublishedBlogsPagedThrowsForSizeOver100() {
    assertThrows(IllegalArgumentException.class, () -> blogService.getPublishedBlogsPaged(0, 101));
  }

  /** page=-1 must be rejected. */
  @Test
  void testGetPublishedBlogsPagedThrowsForNegativePage() {
    assertThrows(IllegalArgumentException.class, () -> blogService.getPublishedBlogsPaged(-1, 10));
  }

  // ---- Tests for createBlog (CR-36 acceptance criteria) ----

  private CreateBlogRequestDto buildValidRequest() {
    CreateBlogRequestDto.AuthorDto author = new CreateBlogRequestDto.AuthorDto("Test Author", null);
    return new CreateBlogRequestDto(
        "New Blog Title",
        "A short summary of the blog.",
        "Full body content of the new blog post.",
        Arrays.asList("tag1", "tag2"),
        author,
        null);
  }

  /**
   * AC: On successful submission, the new blog is persisted to the backend's data/data.json file.
   */
  @Test
  void testCreateBlogPersistsToDataJsonFile() throws Exception {
    String filePath = tempDir.resolve("data.json").toString();
    BlogService service = new BlogService(new ArrayList<>(testBlogs), filePath);

    service.createBlog(buildValidRequest());

    File file = new File(filePath);
    assertTrue(file.exists(), "data.json should be created after createBlog");

    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    List<Blog> persisted = mapper.readValue(file, new TypeReference<List<Blog>>() {});
    boolean found =
        persisted.stream().anyMatch(b -> "New Blog Title".equals(b.getTitle()) && b.isPublished());
    assertTrue(found, "Newly created blog should be persisted in data.json");
  }

  /** AC: The backend serves blogs from data/data.json (loading existing entries on startup). */
  @Test
  void testBlogServiceLoadsFromDataJsonOnStartup() throws Exception {
    String filePath = tempDir.resolve("data.json").toString();

    // First service writes seed data to the file
    BlogService firstService = new BlogService(null, filePath);
    int seedCount = firstService.getPublishedBlogs(0, 100).size();
    assertTrue(seedCount > 0, "Seed data should produce at least one blog");

    // Second service loads from the file
    BlogService secondService = new BlogService(null, filePath);
    List<Blog> loaded = secondService.getPublishedBlogs(0, 100);
    assertEquals(seedCount, loaded.size(), "Second service should load same blogs from data.json");
  }

  /**
   * AC: createBlog assigns max-id+1, sets published=true, sets publishedAt, and returns correct
   * DTO.
   */
  @Test
  void testCreateBlogAssignsCorrectIdAndFields() {
    // testBlogs max id is 5
    BlogResponseDto result = blogService.createBlog(buildValidRequest());

    assertNotNull(result);
    assertEquals(6L, result.getId(), "New id should be max(existing ids) + 1 = 5 + 1 = 6");
    assertEquals("New Blog Title", result.getTitle());
    assertEquals("A short summary of the blog.", result.getExcerpt());
    assertEquals("Full body content of the new blog post.", result.getBody());
    assertNotNull(result.getPublishedAt());
    assertNotNull(result.getAuthor());
    assertEquals("Test Author", result.getAuthor().getName());
  }

  /** AC: createBlog makes the new blog appear in subsequent getPublishedBlogsPaged calls. */
  @Test
  void testCreateBlogAppearsInPublishedList() {
    int before = blogService.getPublishedBlogsPaged(0, 100).getContent().size();
    blogService.createBlog(buildValidRequest());
    int after = blogService.getPublishedBlogsPaged(0, 100).getContent().size();

    assertEquals(before + 1, after, "Published list should grow by 1 after createBlog");
    boolean found =
        blogService.getPublishedBlogsPaged(0, 100).getContent().stream()
            .anyMatch(b -> "New Blog Title".equals(b.getTitle()));
    assertTrue(found, "New blog should be visible in the published listing");
  }

  /** AC: createBlog with missing title throws BadRequestException (validation). */
  @Test
  void testCreateBlogRejectsBlankTitle() {
    CreateBlogRequestDto.AuthorDto author = new CreateBlogRequestDto.AuthorDto("Test Author", null);
    CreateBlogRequestDto request =
        new CreateBlogRequestDto("", "excerpt", "body", Arrays.asList("tag"), author, null);
    assertThrows(BadRequestException.class, () -> blogService.createBlog(request));
  }

  /** AC: createBlog with missing author name throws BadRequestException (validation). */
  @Test
  void testCreateBlogRejectsMissingAuthorName() {
    CreateBlogRequestDto.AuthorDto author = new CreateBlogRequestDto.AuthorDto("", null);
    CreateBlogRequestDto request =
        new CreateBlogRequestDto("Title", "excerpt", "body", Arrays.asList("tag"), author, null);
    assertThrows(BadRequestException.class, () -> blogService.createBlog(request));
  }
}
