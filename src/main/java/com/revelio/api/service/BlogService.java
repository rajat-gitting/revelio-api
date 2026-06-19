package com.revelio.api.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.revelio.api.dto.BlogResponseDto;
import com.revelio.api.dto.CreateBlogRequestDto;
import com.revelio.api.dto.PagedResponse;
import com.revelio.api.dto.PostFiltersDto;
import com.revelio.api.dto.PostFiltersDto.AuthorSummaryDto;
import com.revelio.api.dto.PostSearchResultDto;
import com.revelio.api.dto.PostSearchResultDto.AppliedFiltersDto;
import com.revelio.api.model.Blog;
import com.revelio.api.model.Blog.Author;
import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class BlogService {

  private static final Logger log = LoggerFactory.getLogger(BlogService.class);
  private static final String DATA_DIR = "data";
  private static final String DATA_FILE = "data/data.json";

  private final List<Blog> blogRepository;
  private final ObjectMapper objectMapper;
  private final String dataFilePath;

  public BlogService() {
    this.objectMapper = buildObjectMapper();
    this.dataFilePath = DATA_FILE;
    this.blogRepository = loadOrSeed(this.dataFilePath);
  }

  /** Constructor for tests — injects a fixed list; no file I/O. */
  public BlogService(List<Blog> blogRepository) {
    this.objectMapper = buildObjectMapper();
    this.dataFilePath = null;
    this.blogRepository =
        blogRepository != null ? new ArrayList<>(blogRepository) : new ArrayList<>();
  }

  /** Package-private constructor for tests that want file I/O with a custom path. */
  BlogService(List<Blog> blogRepository, String dataFilePath) {
    this.objectMapper = buildObjectMapper();
    this.dataFilePath = dataFilePath;
    if (blogRepository != null) {
      this.blogRepository = new ArrayList<>(blogRepository);
    } else {
      this.blogRepository = loadOrSeed(dataFilePath);
    }
  }

  private static ObjectMapper buildObjectMapper() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    return mapper;
  }

  private List<Blog> loadOrSeed(String filePath) {
    File file = new File(filePath);
    if (file.exists()) {
      try {
        List<Blog> loaded = objectMapper.readValue(file, new TypeReference<List<Blog>>() {});
        log.info("Loaded {} blogs from {}", loaded.size(), filePath);
        return new ArrayList<>(loaded);
      } catch (IOException e) {
        log.warn("Failed to read {}, falling back to seed data: {}", filePath, e.getMessage());
      }
    }
    // File does not exist (or failed to parse) — seed from hardcoded data and write it
    List<Blog> seeded = new ArrayList<>(seedData());
    persistToFile(seeded, filePath);
    return seeded;
  }

  private void persistToFile(List<Blog> blogs, String filePath) {
    if (filePath == null) {
      return;
    }
    try {
      File file = new File(filePath);
      file.getParentFile().mkdirs();
      objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, blogs);
      log.info("Persisted {} blogs to {}", blogs.size(), filePath);
    } catch (IOException e) {
      log.error("Failed to persist blogs to {}: {}", filePath, e.getMessage());
    }
  }

  /**
   * Creates a new blog from the given request DTO, assigns a new id, sets publishedAt and
   * published, appends to the in-memory list, persists to data/data.json, and returns the DTO.
   */
  public BlogResponseDto createBlog(CreateBlogRequestDto request) {
    if (request == null) {
      throw new IllegalArgumentException("Request must not be null");
    }
    if (request.getTitle() == null || request.getTitle().isBlank()) {
      throw new IllegalArgumentException("Title is required");
    }
    if (request.getExcerpt() == null || request.getExcerpt().isBlank()) {
      throw new IllegalArgumentException("Excerpt is required");
    }
    if (request.getBody() == null || request.getBody().isBlank()) {
      throw new IllegalArgumentException("Body is required");
    }
    if (request.getAuthor() == null
        || request.getAuthor().getName() == null
        || request.getAuthor().getName().isBlank()) {
      throw new IllegalArgumentException("Author name is required");
    }

    long newId =
        blogRepository.stream()
                .map(Blog::getId)
                .filter(id -> id != null)
                .mapToLong(Long::longValue)
                .max()
                .orElse(0L)
            + 1L;

    Author author = new Author(request.getAuthor().getName(), request.getAuthor().getAvatarUrl());

    Blog blog =
        new Blog(
            newId,
            request.getTitle(),
            request.getExcerpt(),
            request.getCoverImageUrl(),
            author,
            request.getTags() != null ? request.getTags() : new ArrayList<>(),
            Instant.now(),
            true,
            request.getBody());

    blogRepository.add(blog);
    persistToFile(blogRepository, dataFilePath);

    return BlogResponseDto.fromBlog(blog);
  }

  public List<Blog> getPublishedBlogs(int page, int size) {
    if (page < 0) throw new IllegalArgumentException("Page number must be non-negative");
    if (size <= 0) throw new IllegalArgumentException("Page size must be positive");

    List<Blog> sorted =
        blogRepository.stream()
            .filter(Blog::isPublished)
            .sorted(Comparator.comparing(Blog::getPublishedAt).reversed())
            .collect(Collectors.toList());

    int start = page * size;
    if (start >= sorted.size()) return new ArrayList<>();
    return new ArrayList<>(sorted.subList(start, Math.min(start + size, sorted.size())));
  }

  /**
   * Returns a {@link PagedResponse} of {@link BlogResponseDto} for the given page and size,
   * including pagination metadata (totalElements, totalPages, number, size).
   *
   * <p>Validation rules (also enforced here so the controller stays thin):
   *
   * <ul>
   *   <li>{@code page} must be &gt;= 0
   *   <li>{@code size} must be between 1 and 100 (inclusive)
   * </ul>
   *
   * <p>If {@code page} &gt;= {@code totalPages}, an empty {@code content} is returned with valid
   * metadata (AC-3 default behaviour: empty content, not HTTP 400).
   */
  public PagedResponse<BlogResponseDto> getPublishedBlogsPaged(int page, int size) {
    if (page < 0) throw new IllegalArgumentException("Page number must be non-negative");
    if (size < 1 || size > 100)
      throw new IllegalArgumentException("Page size must be between 1 and 100");

    List<Blog> sorted =
        blogRepository.stream()
            .filter(Blog::isPublished)
            .sorted(Comparator.comparing(Blog::getPublishedAt).reversed())
            .collect(Collectors.toList());

    long totalElements = sorted.size();
    int start = page * size;
    List<BlogResponseDto> content;
    if (start >= sorted.size()) {
      content = new ArrayList<>();
    } else {
      content =
          sorted.subList(start, Math.min(start + size, sorted.size())).stream()
              .map(BlogResponseDto::fromBlog)
              .collect(Collectors.toList());
    }

    return PagedResponse.of(content, totalElements, page, size);
  }

  public List<Blog> filterPublishedPosts(List<Blog> blogs) {
    if (blogs == null) return new ArrayList<>();
    return blogs.stream().filter(Blog::isPublished).collect(Collectors.toList());
  }

  /**
   * Search and filter published blog posts.
   *
   * <p>The {@code q} parameter is matched case-insensitively against the post title, excerpt
   * (body), and tags. {@code categories} is a list of tag values; a post matches if it has at least
   * one of the requested tags. {@code authors} is a list of author names; a post matches if its
   * author name is among those requested. All active constraints are ANDed together.
   */
  public PostSearchResultDto searchPosts(
      String q, List<String> categories, List<String> authors, int page, int size) {
    if (page < 0) throw new IllegalArgumentException("Page number must be non-negative");
    if (size <= 0) throw new IllegalArgumentException("Page size must be positive");

    String normalizedQ = (q == null) ? "" : q.trim().toLowerCase(Locale.ROOT);
    List<String> normalizedCategories =
        (categories == null)
            ? new ArrayList<>()
            : categories.stream()
                .filter(c -> c != null && !c.isBlank())
                .map(c -> c.toLowerCase(Locale.ROOT))
                .collect(Collectors.toList());
    List<String> normalizedAuthors =
        (authors == null)
            ? new ArrayList<>()
            : authors.stream()
                .filter(a -> a != null && !a.isBlank())
                .map(a -> a.toLowerCase(Locale.ROOT))
                .collect(Collectors.toList());

    List<Blog> filtered =
        blogRepository.stream()
            .filter(Blog::isPublished)
            .filter(blog -> matchesQuery(blog, normalizedQ))
            .filter(blog -> matchesCategories(blog, normalizedCategories))
            .filter(blog -> matchesAuthors(blog, normalizedAuthors))
            .sorted(Comparator.comparing(Blog::getPublishedAt).reversed())
            .collect(Collectors.toList());

    long total = filtered.size();
    int start = page * size;
    List<Blog> pageSlice;
    if (start >= filtered.size()) {
      pageSlice = new ArrayList<>();
    } else {
      pageSlice = new ArrayList<>(filtered.subList(start, Math.min(start + size, filtered.size())));
    }

    List<com.revelio.api.dto.BlogResponseDto> results =
        pageSlice.stream()
            .map(com.revelio.api.dto.BlogResponseDto::fromBlog)
            .collect(Collectors.toList());

    // AC-6: Echo the active constraints back so the UI can render chip/badge indicators for each
    // applied filter or search term, allowing users to see — at a glance — what is active and
    // remove individual constraints or use 'Clear all'.
    AppliedFiltersDto appliedFilters =
        new AppliedFiltersDto(
            (q == null || q.isBlank()) ? null : q.trim(),
            categories == null ? new ArrayList<>() : new ArrayList<>(categories),
            authors == null ? new ArrayList<>() : new ArrayList<>(authors));

    return new PostSearchResultDto(total, page, size, results, appliedFilters);
  }

  /** Returns the distinct set of tags (categories) and author names across all published posts. */
  public PostFiltersDto getAvailableFilters() {
    List<String> categories =
        blogRepository.stream()
            .filter(Blog::isPublished)
            .flatMap(
                blog ->
                    blog.getTags() == null
                        ? java.util.stream.Stream.empty()
                        : blog.getTags().stream())
            .filter(tag -> tag != null && !tag.isBlank())
            .distinct()
            .sorted()
            .collect(Collectors.toList());

    List<AuthorSummaryDto> authors =
        blogRepository.stream()
            .filter(Blog::isPublished)
            .map(Blog::getAuthor)
            .filter(
                author -> author != null && author.getName() != null && !author.getName().isBlank())
            .map(author -> author.getName())
            .distinct()
            .sorted()
            .map(AuthorSummaryDto::new)
            .collect(Collectors.toList());

    return new PostFiltersDto(categories, authors);
  }

  /**
   * Returns the published blog post with the given {@code id} as a {@link BlogResponseDto}, or an
   * empty {@link Optional} if no published post exists with that id.
   */
  public Optional<BlogResponseDto> getBlogById(Long id) {
    return blogRepository.stream()
        .filter(Blog::isPublished)
        .filter(blog -> blog.getId() != null && blog.getId().equals(id))
        .findFirst()
        .map(BlogResponseDto::fromBlog);
  }

  // -------------------------------------------------------------------------
  // private helpers
  // -------------------------------------------------------------------------

  private boolean matchesQuery(Blog blog, String normalizedQ) {
    if (normalizedQ.isEmpty()) return true;
    if (blog.getTitle() != null && blog.getTitle().toLowerCase(Locale.ROOT).contains(normalizedQ)) {
      return true;
    }
    if (blog.getExcerpt() != null
        && blog.getExcerpt().toLowerCase(Locale.ROOT).contains(normalizedQ)) {
      return true;
    }
    if (blog.getTags() != null) {
      for (String tag : blog.getTags()) {
        if (tag != null && tag.toLowerCase(Locale.ROOT).contains(normalizedQ)) {
          return true;
        }
      }
    }
    return false;
  }

  private boolean matchesCategories(Blog blog, List<String> normalizedCategories) {
    if (normalizedCategories.isEmpty()) return true;
    if (blog.getTags() == null) return false;
    for (String tag : blog.getTags()) {
      if (tag != null && normalizedCategories.contains(tag.toLowerCase(Locale.ROOT))) {
        return true;
      }
    }
    return false;
  }

  private boolean matchesAuthors(Blog blog, List<String> normalizedAuthors) {
    if (normalizedAuthors.isEmpty()) return true;
    if (blog.getAuthor() == null || blog.getAuthor().getName() == null) return false;
    return normalizedAuthors.contains(blog.getAuthor().getName().toLowerCase(Locale.ROOT));
  }

  private static List<Blog> seedData() {
    Author alice = new Author("Alice Chen", null);
    Author bob = new Author("Bob Smith", null);
    Instant now = Instant.now();
    return Arrays.asList(
        new Blog(
            1L,
            "Getting Started with Spring Boot",
            "A beginner-friendly guide to building REST APIs with Spring Boot and Gradle.",
            null,
            alice,
            Arrays.asList("java", "spring", "tutorial"),
            now.minus(1, ChronoUnit.DAYS),
            true,
            "Spring Boot has transformed the way Java developers build and ship web applications. By"
                + " providing sensible defaults and an opinionated project structure, it removes much"
                + " of the boilerplate configuration that traditionally accompanied a Spring"
                + " application.\n\nTo get started, navigate to start.spring.io and generate a new"
                + " project with the 'Spring Web' and 'Spring Boot DevTools' dependencies. Choose"
                + " Gradle as your build tool and Java 17 or later as your language version. Once"
                + " you import the project into your IDE, you will find a single entry-point class"
                + " annotated with @SpringBootApplication.\n\nCreating your first REST endpoint is"
                + " straightforward. Annotate a class with @RestController and define a method"
                + " annotated with @GetMapping. Spring Boot automatically registers the route and"
                + " serialises the return value to JSON using Jackson. For example, returning a"
                + " plain Java record or POJO from a controller method will produce a well-formed"
                + " JSON response with no additional configuration.\n\nGradle makes dependency"
                + " management simple. Add a dependency in the dependencies block of build.gradle"
                + " and run ./gradlew dependencies to inspect the resolved classpath. The"
                + " spring-boot-starter-web artifact brings in an embedded Tomcat server, Jackson,"
                + " and Spring MVC in a single line.\n\nAs your application grows, consider"
                + " structuring it around feature packages rather than layer packages. Grouping"
                + " controller, service, and repository classes for a single domain concept together"
                + " makes the codebase easier to navigate and reduces the surface area of changes"
                + " when requirements evolve. Spring Boot's component scanning will discover beans"
                + " in any sub-package of the main application class, so reorganising packages does"
                + " not require any additional configuration.\n\nFinally, remember to externalise"
                + " configuration into application.properties or application.yml. Values such as"
                + " server port, database URLs, and feature flags should never be hard-coded."
                + " Spring's @Value annotation and @ConfigurationProperties binding make it easy to"
                + " inject typed configuration objects into your beans at startup."),
        new Blog(
            2L,
            "React Query vs SWR",
            "Comparing the two most popular data-fetching libraries for React in 2024.",
            null,
            bob,
            Arrays.asList("react", "frontend"),
            now.minus(3, ChronoUnit.DAYS),
            true,
            "Data fetching in React applications has evolved significantly since the days of"
                + " raw fetch calls inside useEffect hooks. Two libraries now dominate the"
                + " landscape: TanStack Query (formerly React Query) and SWR. Both solve the"
                + " same core problem — caching remote data, deduplicating requests, and"
                + " synchronising server state with the UI — but they make different"
                + " trade-offs.\n\nSWR, developed by Vercel, embraces simplicity. Its API surface"
                + " is small: a single useSWR hook covers most use cases. The library is"
                + " lightweight and integrates naturally into Next.js applications. For teams"
                + " that value minimal abstractions and a gentle learning curve, SWR is hard to"
                + " beat.\n\nTanStack Query offers a richer feature set. It provides fine-grained"
                + " cache invalidation, optimistic updates, infinite queries, and a powerful"
                + " devtools panel that shows every cached entry and its staleness state."
                + " Mutations are first-class citizens with dedicated hooks, and the library"
                + " ships with robust TypeScript generics out of the box.\n\nWhen choosing between"
                + " the two, consider your team's needs. If you are building a content-heavy site"
                + " where most data is read-only and you value bundle size, SWR is a great fit."
                + " If your application has complex write workflows — optimistic UI, dependent"
                + " queries, background refetching on window focus — TanStack Query's richer API"
                + " will pay dividends.\n\nBoth libraries support React Suspense, though TanStack"
                + " Query's Suspense integration is more mature and battle-tested in production."
                + " Either way, adopting a dedicated data-fetching library will dramatically"
                + " reduce the boilerplate in your components and centralise caching logic in one"
                + " place, making your frontend easier to maintain as it grows."),
        new Blog(
            3L,
            "Designing RESTful APIs",
            "Best practices for endpoint naming, pagination, and error responses.",
            null,
            alice,
            Arrays.asList("api", "design"),
            now.minus(7, ChronoUnit.DAYS),
            true,
            "A well-designed REST API is a joy to consume. It is predictable, self-documenting,"
                + " and resilient to change. Achieving this requires discipline around naming,"
                + " status codes, pagination, and error responses from the very first endpoint"
                + " you build.\n\nEndpoint naming should be noun-based and resource-oriented."
                + " Prefer /api/articles over /api/getArticles. Use plural nouns for collections"
                + " (/api/users) and singular path variables for specific resources"
                + " (/api/users/{id}). Nest sub-resources only one level deep; deeper nesting"
                + " becomes unwieldy and couples the URL structure to your data model.\n\nHTTP"
                + " status codes carry semantic meaning. Use 200 OK for successful reads, 201"
                + " Created when a resource is successfully created (and return the created"
                + " resource in the body), 204 No Content for successful deletes, 400 Bad Request"
                + " for validation failures, 401 Unauthorised when credentials are missing or"
                + " invalid, 403 Forbidden when the authenticated user lacks permission, and 404"
                + " Not Found when the requested resource does not exist.\n\nPagination is"
                + " essential for collection endpoints. Cursor-based pagination scales better"
                + " than offset-based for large datasets, but offset pagination is simpler to"
                + " implement and sufficient for most applications. Return metadata alongside"
                + " the content array: total element count, current page, page size, and total"
                + " pages give clients everything they need to render pagination controls.\n\nError"
                + " responses should follow a consistent envelope. A structure like"
                + " {\"success\": false, \"message\": \"Validation failed\", \"errors\": [...]} lets"
                + " clients handle errors uniformly without inspecting raw HTTP status codes."
                + " Include a machine-readable error code alongside the human-readable message so"
                + " that client error handling logic can branch without string matching.\n\nVersion"
                + " your API from day one. A /api/v1/ prefix costs nothing to add early and saves"
                + " a painful migration later when breaking changes become necessary."),
        new Blog(
            4L,
            "CSS Grid Layouts",
            "Mastering two-dimensional layouts with CSS Grid including responsive breakpoints.",
            null,
            bob,
            Arrays.asList("css", "frontend", "responsive"),
            now.minus(10, ChronoUnit.DAYS),
            true,
            "CSS Grid is the most powerful layout system available in CSS today. Unlike Flexbox,"
                + " which operates along a single axis, Grid lets you control both rows and"
                + " columns simultaneously, making complex two-dimensional layouts achievable"
                + " with a handful of CSS properties.\n\nThe grid container is defined by setting"
                + " display: grid on an element. You then define columns with grid-template-columns"
                + " and rows with grid-template-rows. The repeat() function and fr unit make it"
                + " concise: grid-template-columns: repeat(3, 1fr) creates three equal-width"
                + " columns that share the available space proportionally.\n\nGrid placement is"
                + " explicit or automatic. By default, grid items are placed automatically in"
                + " document order, filling cells left-to-right and top-to-bottom. For custom"
                + " layouts, use grid-column and grid-row properties to span items across multiple"
                + " tracks: grid-column: 1 / 3 places an item from column line 1 to line 3,"
                + " spanning two columns.\n\nNamed template areas take readability further. Assign"
                + " string names to regions with grid-template-areas and then reference those names"
                + " in child elements with grid-area. This approach makes the layout intent"
                + " self-documenting in the CSS source.\n\nResponsive grids can be achieved with"
                + " media queries or the built-in auto-fill and auto-fit keywords. Using"
                + " grid-template-columns: repeat(auto-fit, minmax(280px, 1fr)) creates a"
                + " naturally responsive card grid that reflows without any media queries — cards"
                + " grow to fill available space and wrap to new rows when they would become"
                + " narrower than 280px.\n\nThe gap property (formerly grid-gap) controls gutters"
                + " between tracks. Use gap: 1rem to add consistent spacing between all cells, or"
                + " separate row-gap and column-gap for asymmetric spacing. Combined with CSS"
                + " custom properties, Grid makes maintaining a consistent spacing scale across"
                + " an entire design system straightforward."),
        new Blog(
            5L,
            "JUnit 5 Tips and Tricks",
            "Lesser-known JUnit 5 features that will make your test suite cleaner.",
            null,
            alice,
            Arrays.asList("java", "testing"),
            now.minus(14, ChronoUnit.DAYS),
            true,
            "JUnit 5 introduced a modular architecture and a wealth of features that go beyond"
                + " what its predecessors offered. While most developers are comfortable with"
                + " @Test, @BeforeEach, and basic assertions, the platform has much more to"
                + " offer.\n\nParameterised tests eliminate repetitive test methods. The"
                + " @ParameterizedTest annotation combined with @ValueSource, @CsvSource, or"
                + " @MethodSource lets you run the same test logic against multiple inputs. This"
                + " keeps the test class lean and makes coverage gaps immediately obvious when"
                + " you read the argument list.\n\nDynamic tests, registered via @TestFactory,"
                + " allow you to generate test cases at runtime. This is invaluable when the"
                + " set of inputs is computed rather than statically enumerated — for example,"
                + " loading test fixtures from a directory of JSON files.\n\nAssumptions let"
                + " tests abort gracefully rather than fail when a precondition is not met."
                + " Assumptions.assumeTrue(condition) causes the test to be skipped rather than"
                + " marked red, keeping the signal-to-noise ratio high in CI.\n\nNested test"
                + " classes, annotated with @Nested, group related tests under a shared setup"
                + " without requiring a separate top-level class. This is particularly useful for"
                + " testing a single class that has multiple modes of operation: you can create a"
                + " @Nested class per scenario with its own @BeforeEach.\n\nThe @ExtendWith"
                + " mechanism replaces JUnit 4's @RunWith. Extensions can inject custom parameters,"
                + " wrap test execution, and register lifecycle callbacks. Spring Boot's"
                + " @SpringBootTest leverages this mechanism to bootstrap the application context"
                + " once and reuse it across all tests in a class.\n\nFinally, @TempDir injects a"
                + " temporary directory that is automatically cleaned up after each test, removing"
                + " a common source of test pollution when working with file system operations."),
        new Blog(
            6L,
            "Development in the era of AI",
            "How AI tools are reshaping the way developers write, review, and ship code.",
            null,
            alice,
            Arrays.asList("ai", "development", "productivity"),
            now.minus(2, ChronoUnit.DAYS),
            true,
            "Artificial intelligence has moved from a research curiosity to an everyday tool in"
                + " the software development workflow. AI-assisted code completion, automated"
                + " code review, and natural-language query interfaces are no longer futuristic"
                + " — they are features developers use in every sprint.\n\nLarge language models"
                + " have become particularly impactful for boilerplate generation. Tasks that"
                + " once took an experienced developer an hour — scaffolding a CRUD controller,"
                + " writing migration scripts, or translating a Figma design to HTML — can now"
                + " be drafted in seconds. The key shift is that developers spend less time on"
                + " synthesis and more time on validation, architecture, and problem framing.\n\nCode"
                + " review assistance is another high-value application. AI tools can flag common"
                + " security anti-patterns, suggest idiomatic alternatives, and catch off-by-one"
                + " errors before a human reviewer sees the pull request. This raises the baseline"
                + " quality of code that reaches review and frees reviewers to focus on higher-order"
                + " concerns such as design consistency and business logic correctness.\n\nHowever,"
                + " AI-generated code demands rigorous scepticism. Models confidently produce"
                + " plausible-looking but subtly wrong implementations, particularly for edge"
                + " cases, security-sensitive code, and domain-specific logic. A developer who"
                + " accepts suggestions without reading them is accumulating hidden debt.\n\nThe"
                + " most productive teams treat AI as a junior pair-programming partner: always"
                + " present, infinitely patient, occasionally brilliant, but requiring supervision."
                + " Cultivating strong code-reading skills, robust test suites, and a healthy"
                + " culture of review ensures that AI accelerates delivery without eroding"
                + " quality.\n\nLooking ahead, the teams that will benefit most are those that"
                + " invest in clear specifications, well-structured codebases, and comprehensive"
                + " automated testing — all of which make AI assistance more reliable and"
                + " easier to verify."),
        new Blog(
            7L,
            "Mastering Code Reviews",
            "Best practices for giving and receiving feedback that improves code quality and team culture.",
            null,
            bob,
            Arrays.asList("code-review", "collaboration", "best-practices"),
            now.minus(4, ChronoUnit.DAYS),
            true,
            "Code review is one of the highest-leverage activities in software development. A"
                + " thoughtful review catches bugs before they reach production, spreads knowledge"
                + " across the team, and enforces architectural consistency. Yet poor review"
                + " culture can slow delivery, demoralise contributors, and become a bottleneck"
                + " rather than a quality gate.\n\nThe first step to effective reviews is sizing."
                + " Research consistently shows that the defect detection rate drops sharply for"
                + " pull requests over 400 lines. Keep changes small and focused: one feature,"
                + " one fix, one refactoring per PR. If a change cannot be kept small, break it"
                + " into a chain of dependent PRs.\n\nWriting a good PR description is as"
                + " important as writing good code. Explain the problem being solved, the"
                + " approach chosen, and any alternatives considered. Attach screenshots for UI"
                + " changes. Link to the relevant ticket. A reviewer who understands the context"
                + " gives better feedback and takes less of your time.\n\nFeedback should be"
                + " specific, kind, and actionable. Avoid vague comments like 'this is unclear';"
                + " instead, explain what is unclear and suggest an improvement. Distinguish"
                + " between blocking issues and non-blocking suggestions using prefixes like"
                + " 'Nit:' or 'Blocking:' so the author knows what must change before merge.\n\nAs"
                + " a reviewer, it is your responsibility to approve changes you genuinely"
                + " understand and believe are correct. Rubber-stamp approvals undermine the"
                + " entire practice. If a change is too complex to review confidently, ask for"
                + " a pair walkthrough.\n\nAs an author, receive feedback with curiosity rather"
                + " than defensiveness. Every comment is an opportunity to learn or to clarify"
                + " intent. When you disagree, explain your reasoning — but be willing to defer"
                + " to the reviewer's experience on matters of style and convention."),
        new Blog(
            8L,
            "The Rise of Edge Computing",
            "Why processing data closer to the user is changing how we build modern applications.",
            null,
            alice,
            Arrays.asList("edge-computing", "architecture", "performance"),
            now.minus(5, ChronoUnit.DAYS),
            true,
            "For decades, the web architecture story was simple: clients make requests, servers"
                + " in a centralised data centre respond. Content delivery networks brought static"
                + " assets closer to users, but dynamic computation remained centralised. Edge"
                + " computing changes this fundamentally by moving compute to the network"
                + " periphery — servers distributed globally, physically close to end users.\n\nThe"
                + " primary benefit is latency. A round trip from Sydney to a data centre in"
                + " Virginia adds 200 ms or more to every request. An edge node in Sydney can"
                + " respond in under 10 ms. For applications where perceived speed determines"
                + " conversion and retention — e-commerce, real-time collaboration, gaming — this"
                + " difference is significant.\n\nEdge runtimes such as Cloudflare Workers,"
                + " Vercel Edge Functions, and Fastly Compute enable JavaScript and WebAssembly"
                + " code to run at the edge. These environments are intentionally constrained:"
                + " no filesystem, limited memory, short execution windows. These constraints"
                + " force architectural discipline: handlers must be stateless and fast.\n\nPer-user"
                + " personalisation is a compelling edge use case. Instead of serving a cached"
                + " HTML shell and then fetching personalisation data client-side, an edge worker"
                + " can rewrite the response in flight — injecting the user's name, locale, or"
                + " A/B test bucket before the browser even parses the document.\n\nThe trade-offs"
                + " are real. Debugging distributed edge deployments is harder than debugging a"
                + " monolith. Data residency requirements may conflict with running compute in"
                + " arbitrary regions. Cold-start latency, while lower than serverless functions,"
                + " still exists. And not every workload benefits: batch processing, complex"
                + " queries, and stateful operations remain better suited to traditional servers.\n\nThe"
                + " teams that will get the most from edge computing are those that design"
                + " clear boundaries between edge-suitable lightweight handlers and origin-side"
                + " heavy lifting, routing only the work that genuinely benefits from proximity."),
        new Blog(
            9L,
            "Securing Your CI/CD Pipeline",
            "Practical steps to protect your build and deployment workflows from common vulnerabilities.",
            null,
            bob,
            Arrays.asList("security", "ci-cd", "devops"),
            now.minus(6, ChronoUnit.DAYS),
            true,
            "The CI/CD pipeline is the nervous system of a modern software team. It builds,"
                + " tests, and deploys code dozens or hundreds of times a day. It also"
                + " represents a high-value target for attackers: a compromised pipeline can"
                + " inject malicious code into production, exfiltrate secrets, or tamper with"
                + " release artefacts.\n\nSecret management is the most critical control. Never"
                + " store secrets — API keys, database passwords, signing certificates — in"
                + " source code or pipeline configuration files. Use the secret storage provided"
                + " by your CI platform (GitHub Actions Secrets, GitLab CI Variables marked as"
                + " masked) or integrate with a dedicated vault such as HashiCorp Vault or AWS"
                + " Secrets Manager. Rotate secrets regularly and audit access logs.\n\nPin"
                + " third-party actions and Docker images to specific commit SHAs rather than"
                + " mutable tags. A tag like latest or v2 can be silently updated to point to"
                + " malicious code. Pinning to a SHA ensures you run exactly the code you"
                + " reviewed. Dependabot or Renovate can automate the process of updating"
                + " pinned versions while preserving auditability.\n\nApply least-privilege to"
                + " pipeline tokens. A job that only needs to read test results should not hold"
                + " a token with write access to production infrastructure. GitHub Actions'"
                + " fine-grained permissions model and OpenID Connect-based cloud authentication"
                + " make it straightforward to grant each job only what it needs.\n\nArtifact"
                + " integrity is often overlooked. Sign build artefacts with a tool like"
                + " Sigstore/cosign and verify signatures before deployment. This creates an"
                + " auditable chain of custody from source commit to running container,making it"
                + " possible to detect tampering that occurs outside the pipeline.\n\nFinally,"
                + " audit your pipeline configuration itself. Treat pipeline YAML files with the"
                + " same rigour as application code: require review on changes, scan for"
                + " common misconfigurations with tools like Semgrep or Checkov, and alert on"
                + " unexpected changes to pipeline definitions."),
        new Blog(
            10L,
            "Writing Documentation Developers Actually Read",
            "Tips for creating clear, concise docs that reduce support tickets and onboarding time.",
            null,
            alice,
            Arrays.asList("documentation", "writing", "developer-experience"),
            now.minus(8, ChronoUnit.DAYS),
            true,
            "Documentation is the one artefact that serves every stakeholder in a software"
                + " project: new engineers onboarding, senior engineers debugging an unfamiliar"
                + " subsystem, product managers understanding capability boundaries, and external"
                + " developers integrating with your API. Yet documentation is also the most"
                + " chronically neglected part of most codebases.\n\nThe root cause is usually"
                + " incentive misalignment. Writing features earns visible credit; writing docs"
                + " does not. The fix is cultural: treat documentation as a first-class"
                + " deliverable, include it in the definition of done, and review it with the"
                + " same rigour as code.\n\nStructure documentation around the reader's goal, not"
                + " the system's structure. A developer who wants to add a new endpoint does not"
                + " need a comprehensive description of the routing layer; they need a short"
                + " tutorial that shows exactly what to add and where. The Diátaxis framework"
                + " offers a useful taxonomy: tutorials (learning-oriented), how-to guides"
                + " (task-oriented), reference (information-oriented), and explanations"
                + " (understanding-oriented). Keeping these types separate prevents the common"
                + " failure mode of mixing tutorial and reference content in a way that serves"
                + " neither goal.\n\nCode examples are the most valuable element of any developer"
                + " documentation. A working, copy-pasteable snippet communicates more than three"
                + " paragraphs of prose. Keep examples minimal: strip everything that is not"
                + " essential to illustrating the concept, and add comments for any non-obvious"
                + " lines.\n\nKeep documentation close to the code it describes. README files at"
                + " the package level, inline JSDoc or Javadoc comments, and ADRs (Architecture"
                + " Decision Records) checked into the repository age far better than an"
                + " out-of-band wiki that drifts from reality.\n\nAutomate what you can: generate"
                + " API reference from annotations (OpenAPI, Javadoc), run documentation tests"
                + " in CI to catch broken examples, and use tools like Vale or Markdownlint to"
                + " enforce style consistency. Automation turns documentation from a"
                + " single-author burden into a team-maintained asset."));
  }
}
