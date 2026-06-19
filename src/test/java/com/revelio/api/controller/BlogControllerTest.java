package com.revelio.api.controller;

import static org.junit.jupiter.api.Assertions.*;

import com.revelio.api.dto.ApiResponse;
import com.revelio.api.dto.BlogResponseDto;
import com.revelio.api.dto.CreateBlogRequestDto;
import com.revelio.api.dto.PagedResponse;
import com.revelio.api.model.Blog;
import com.revelio.api.service.BlogService;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

class BlogControllerTest {

  private BlogController blogController;
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

    blogService = new BlogService(testBlogs);
    blogController = new BlogController(blogService);
  }

  private PagedResponse<BlogResponseDto> getPagedBlogs(
      BlogController controller, int page, int size) {
    ResponseEntity<ApiResponse<PagedResponse<BlogResponseDto>>> response =
        controller.getBlogs(page, size);
    assertNotNull(response.getBody());
    assertTrue(response.getBody().isSuccess());
    return response.getBody().getData();
  }

  @Test
  void testGetBlogsReturnsPublishedPostsOnly() {
    PagedResponse<BlogResponseDto> result = getPagedBlogs(blogController, 0, 10);

    assertEquals(2, result.getContent().size());
    assertEquals(3L, result.getContent().get(0).getId());
    assertEquals(1L, result.getContent().get(1).getId());
  }

  @Test
  void testGetBlogsReturnsSortedByPublishedAtDescending() {
    PagedResponse<BlogResponseDto> result = getPagedBlogs(blogController, 0, 10);

    assertEquals(
        Instant.parse("2024-01-25T10:00:00Z"), result.getContent().get(0).getPublishedAt());
    assertEquals(
        Instant.parse("2024-01-15T10:00:00Z"), result.getContent().get(1).getPublishedAt());
  }

  @Test
  void testGetBlogsWithPagination() {
    PagedResponse<BlogResponseDto> result = getPagedBlogs(blogController, 0, 1);

    assertEquals(1, result.getContent().size());
    assertEquals(3L, result.getContent().get(0).getId());
  }

  @Test
  void testGetBlogsReturnsEmptyListWhenNoPublishedPosts() {
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

    BlogService emptyService = new BlogService(unpublishedBlogs);
    BlogController emptyController = new BlogController(emptyService);

    PagedResponse<BlogResponseDto> result = getPagedBlogs(emptyController, 0, 10);

    assertTrue(result.getContent().isEmpty());
  }

  @Test
  void testGetBlogsConvertsToDto() {
    PagedResponse<BlogResponseDto> result = getPagedBlogs(blogController, 0, 10);

    BlogResponseDto firstDto = result.getContent().get(0);
    assertEquals("Third Post", firstDto.getTitle());
    assertEquals("Third excerpt", firstDto.getExcerpt());
    assertEquals("https://example.com/3.jpg", firstDto.getCoverImageUrl());
    assertNotNull(firstDto.getAuthor());
    assertEquals("John Doe", firstDto.getAuthor().getName());
    assertEquals("https://example.com/john.jpg", firstDto.getAuthor().getAvatarUrl());
    assertEquals(Arrays.asList("spring"), firstDto.getTags());
  }

  @Test
  void testGetBlogsWithNullCoverImageUrl() {
    List<Blog> blogsWithNullCover = new ArrayList<>();
    Blog.Author author = new Blog.Author("John Doe", "https://example.com/john.jpg");

    blogsWithNullCover.add(
        new Blog(
            1L,
            "Post Without Cover",
            "Excerpt",
            null,
            author,
            Arrays.asList("tech"),
            Instant.parse("2024-01-15T10:00:00Z"),
            true));

    BlogService serviceWithNullCover = new BlogService(blogsWithNullCover);
    BlogController controllerWithNullCover = new BlogController(serviceWithNullCover);

    PagedResponse<BlogResponseDto> result = getPagedBlogs(controllerWithNullCover, 0, 10);

    assertEquals(1, result.getContent().size());
    assertNull(result.getContent().get(0).getCoverImageUrl());
  }

  @Test
  void testGetBlogsResponseIncludesPaginationMetadata() {
    // 2 published posts; page=0, size=10
    PagedResponse<BlogResponseDto> result = getPagedBlogs(blogController, 0, 10);

    assertEquals(2, result.getTotalElements());
    assertEquals(1, result.getTotalPages()); // ceil(2/10) = 1
    assertEquals(0, result.getNumber());
    assertEquals(10, result.getSize());
    assertEquals(2, result.getContent().size());
  }

  @Test
  void testGetBlogsMetadataWithMultiplePages() {
    // 2 published posts; page=0, size=1 → totalPages=2
    PagedResponse<BlogResponseDto> page0 = getPagedBlogs(blogController, 0, 1);
    assertEquals(2, page0.getTotalElements());
    assertEquals(2, page0.getTotalPages());
    assertEquals(0, page0.getNumber());
    assertEquals(1, page0.getSize());
    assertEquals(1, page0.getContent().size());

    // page=1
    PagedResponse<BlogResponseDto> page1 = getPagedBlogs(blogController, 1, 1);
    assertEquals(2, page1.getTotalElements());
    assertEquals(2, page1.getTotalPages());
    assertEquals(1, page1.getNumber());
    assertEquals(1, page1.getSize());
    assertEquals(1, page1.getContent().size());
  }

  @Test
  void testGetBlogsWithPageBeyondAvailableDataReturnsEmptyContentWithMetadata() {
    // 2 published posts; page=5, size=10 → beyond totalPages
    PagedResponse<BlogResponseDto> result = getPagedBlogs(blogController, 5, 10);

    assertTrue(result.getContent().isEmpty());
    assertEquals(2, result.getTotalElements());
    assertEquals(1, result.getTotalPages());
    assertEquals(5, result.getNumber());
    assertEquals(10, result.getSize());
  }

  @Test
  void testGetBlogsThrowsExceptionForInvalidPage() {
    assertThrows(IllegalArgumentException.class, () -> blogController.getBlogs(-1, 10));
  }

  @Test
  void testGetBlogsThrowsExceptionForZeroSize() {
    assertThrows(IllegalArgumentException.class, () -> blogController.getBlogs(0, 0));
  }

  @Test
  void testGetBlogsThrowsExceptionForNegativeSize() {
    assertThrows(IllegalArgumentException.class, () -> blogController.getBlogs(0, -5));
  }

  @Test
  void testGetBlogsThrowsExceptionForSizeOver100() {
    assertThrows(IllegalArgumentException.class, () -> blogController.getBlogs(0, 101));
  }

  @Test
  void testGetBlogsWithDefaultConstructorIncludesAIBlogPost() {
    BlogService defaultService = new BlogService();
    BlogController defaultController = new BlogController(defaultService);

    PagedResponse<BlogResponseDto> result = getPagedBlogs(defaultController, 0, 10);

    BlogResponseDto aiBlog =
        result.getContent().stream()
            .filter(blog -> blog.getTitle().equals("Development in the era of AI"))
            .findFirst()
            .orElse(null);

    assertNotNull(aiBlog);
    assertEquals("Development in the era of AI", aiBlog.getTitle());
    assertEquals(
        "How AI tools are reshaping the way developers write, review, and ship code.",
        aiBlog.getExcerpt());
    assertEquals(Arrays.asList("ai", "development", "productivity"), aiBlog.getTags());
  }

  @Test
  void testGetBlogByIdReturnsPublishedPost() {
    List<Blog> blogs = new ArrayList<>();
    Blog.Author author = new Blog.Author("Alice Chen", null);
    blogs.add(
        new Blog(
            1L,
            "Test Post",
            "Test excerpt",
            null,
            author,
            Arrays.asList("java"),
            Instant.parse("2024-01-15T10:00:00Z"),
            true,
            "Full article body content for the test post."));
    BlogService service = new BlogService(blogs);
    BlogController controller = new BlogController(service);

    ResponseEntity<ApiResponse<BlogResponseDto>> response = controller.getBlogById(1L);

    assertEquals(200, response.getStatusCode().value());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().isSuccess());
    BlogResponseDto dto = response.getBody().getData();
    assertNotNull(dto);
    assertEquals(1L, dto.getId());
    assertEquals("Test Post", dto.getTitle());
    assertEquals("Full article body content for the test post.", dto.getBody());
  }

  @Test
  void testGetBlogByIdReturns404WhenNotFound() {
    BlogService service = new BlogService(new ArrayList<>());
    BlogController controller = new BlogController(service);

    ResponseEntity<ApiResponse<BlogResponseDto>> response = controller.getBlogById(999L);

    assertEquals(404, response.getStatusCode().value());
  }

  @Test
  void testGetBlogByIdReturns404ForUnpublishedPost() {
    List<Blog> blogs = new ArrayList<>();
    Blog.Author author = new Blog.Author("Bob Smith", null);
    blogs.add(
        new Blog(
            2L,
            "Unpublished Post",
            "Excerpt",
            null,
            author,
            Arrays.asList("draft"),
            Instant.parse("2024-01-15T10:00:00Z"),
            false,
            "Body content that should not be visible."));
    BlogService service = new BlogService(blogs);
    BlogController controller = new BlogController(service);

    ResponseEntity<ApiResponse<BlogResponseDto>> response = controller.getBlogById(2L);

    assertEquals(404, response.getStatusCode().value());
  }

  @Test
  void testGetBlogByIdResponseEnvelopeMatchesPattern() {
    List<Blog> blogs = new ArrayList<>();
    Blog.Author author = new Blog.Author("Alice Chen", null);
    blogs.add(
        new Blog(
            1L,
            "Envelope Test",
            "Excerpt",
            null,
            author,
            Arrays.asList("test"),
            Instant.parse("2024-06-01T10:00:00Z"),
            true,
            "Article body text."));
    BlogService service = new BlogService(blogs);
    BlogController controller = new BlogController(service);

    ResponseEntity<ApiResponse<BlogResponseDto>> response = controller.getBlogById(1L);

    assertNotNull(response.getBody());
    assertTrue(response.getBody().isSuccess());
    assertEquals("OK", response.getBody().getMessage());
    assertNotNull(response.getBody().getData());
    assertNotNull(response.getBody().getTimestamp());
  }

  @Test
  void testGetBlogByIdIncludesBodyField() {
    List<Blog> blogs = new ArrayList<>();
    Blog.Author author = new Blog.Author("Alice Chen", null);
    String expectedBody = "This is the full article body with multiple paragraphs of content.";
    blogs.add(
        new Blog(
            5L,
            "Body Test Post",
            "A short excerpt",
            null,
            author,
            Arrays.asList("java", "testing"),
            Instant.parse("2024-03-01T09:00:00Z"),
            true,
            expectedBody));
    BlogService service = new BlogService(blogs);
    BlogController controller = new BlogController(service);

    ResponseEntity<ApiResponse<BlogResponseDto>> response = controller.getBlogById(5L);

    assertEquals(200, response.getStatusCode().value());
    assertNotNull(response.getBody());
    assertEquals(expectedBody, response.getBody().getData().getBody());
  }

  @Test
  void testGetBlogsIncludesBodyFieldInResponseDtos() {
    List<Blog> blogs = new ArrayList<>();
    Blog.Author author = new Blog.Author("Alice Chen", null);
    blogs.add(
        new Blog(
            1L,
            "Post With Body",
            "Excerpt",
            null,
            author,
            Arrays.asList("java"),
            Instant.parse("2024-01-15T10:00:00Z"),
            true,
            "The full article body for regression testing."));
    BlogService service = new BlogService(blogs);
    BlogController controller = new BlogController(service);

    PagedResponse<BlogResponseDto> result = getPagedBlogs(controller, 0, 10);

    assertEquals(1, result.getContent().size());
    assertEquals(
        "The full article body for regression testing.", result.getContent().get(0).getBody());
  }

  @Test
  void testSeedDataAllPostsHaveNonEmptyBody() {
    BlogService service = new BlogService();
    BlogController controller = new BlogController(service);

    PagedResponse<BlogResponseDto> result = getPagedBlogs(controller, 0, 20);

    assertEquals(10, result.getContent().size());
    for (BlogResponseDto post : result.getContent()) {
      assertNotNull(post.getBody(), "Body should not be null for post: " + post.getTitle());
      assertFalse(
          post.getBody().isBlank(), "Body should not be blank for post: " + post.getTitle());
    }
  }

  private CreateBlogRequestDto buildCreateRequest() {
    CreateBlogRequestDto.AuthorDto author =
        new CreateBlogRequestDto.AuthorDto("Controller Author", null);
    return new CreateBlogRequestDto(
        "Controller Blog Title",
        "Controller blog summary.",
        "Controller blog body content.",
        Arrays.asList("java", "spring"),
        author,
        null);
  }

  @Test
  void testCreateBlogReturns201WithCreatedBlog() {
    ResponseEntity<ApiResponse<BlogResponseDto>> response =
        blogController.createBlog(buildCreateRequest());

    assertEquals(201, response.getStatusCode().value());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().isSuccess());
    BlogResponseDto dto = response.getBody().getData();
    assertNotNull(dto);
    assertEquals("Controller Blog Title", dto.getTitle());
    assertEquals("Controller blog summary.", dto.getExcerpt());
    assertEquals("Controller blog body content.", dto.getBody());
    assertNotNull(dto.getId());
    assertNotNull(dto.getPublishedAt());
    assertNotNull(dto.getAuthor());
    assertEquals("Controller Author", dto.getAuthor().getName());
  }

  @Test
  void testCreateBlogAppearsInBlogListing() {
    int before = getPagedBlogs(blogController, 0, 100).getContent().size();
    blogController.createBlog(buildCreateRequest());
    int after = getPagedBlogs(blogController, 0, 100).getContent().size();

    assertEquals(before + 1, after);
    boolean found =
        getPagedBlogs(blogController, 0, 100).getContent().stream()
            .anyMatch(b -> "Controller Blog Title".equals(b.getTitle()));
    assertTrue(found, "New blog should appear in listing after POST /blogs");
  }
}
