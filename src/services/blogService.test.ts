import { fetchBlogs, BlogListResponse, BlogPost, Author } from './blogService';

global.fetch = jest.fn();

const mockFetch = global.fetch as jest.MockedFunction<typeof fetch>;

describe('blogService', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  describe('fetchBlogs', () => {
    it('should fetch blogs with correct URL and parameters', async () => {
      const mockResponse: BlogListResponse = {
        blogs: [],
        page: 0,
        size: 10,
        totalElements: 0,
        totalPages: 0,
        hasMore: false,
      };

      mockFetch.mockResolvedValueOnce({
        ok: true,
        json: async () => mockResponse,
      } as Response);

      const result = await fetchBlogs(0, 10);

      expect(mockFetch).toHaveBeenCalledWith(
        'http://localhost:8080/api/blogs?page=0&size=10',
        {
          method: 'GET',
          headers: {
            'Content-Type': 'application/json',
          },
        }
      );
      expect(result).toEqual(mockResponse);
    });

    it('should return blog posts with all required fields', async () => {
      const author: Author = {
        name: 'John Doe',
        avatarUrl: 'https://example.com/avatar.jpg',
      };

      const blogPost: BlogPost = {
        id: 1,
        title: 'Test Title',
        excerpt: 'Test excerpt',
        coverImageUrl: 'https://example.com/cover.jpg',
        author: author,
        tags: ['tech', 'java'],
        publishedAt: '2024-01-15T10:00:00Z',
      };

      const mockResponse: BlogListResponse = {
        blogs: [blogPost],
        page: 0,
        size: 10,
        totalElements: 1,
        totalPages: 1,
        hasMore: false,
      };

      mockFetch.mockResolvedValueOnce({
        ok: true,
        json: async () => mockResponse,
      } as Response);

      const result = await fetchBlogs(0, 10);

      expect(result.blogs).toHaveLength(1);
      expect(result.blogs[0].id).toBe(1);
      expect(result.blogs[0].title).toBe('Test Title');
      expect(result.blogs[0].excerpt).toBe('Test excerpt');
      expect(result.blogs[0].coverImageUrl).toBe('https://example.com/cover.jpg');
      expect(result.blogs[0].author.name).toBe('John Doe');
      expect(result.blogs[0].author.avatarUrl).toBe('https://example.com/avatar.jpg');
      expect(result.blogs[0].tags).toEqual(['tech', 'java']);
      expect(result.blogs[0].publishedAt).toBe('2024-01-15T10:00:00Z');
    });

    it('should handle null coverImageUrl', async () => {
      const author: Author = {
        name: 'Jane Smith',
        avatarUrl: 'https://example.com/jane.jpg',
      };

      const blogPost: BlogPost = {
        id: 2,
        title: 'Post without cover',
        excerpt: 'Excerpt',
        coverImageUrl: null,
        author: author,
        tags: ['design'],
        publishedAt: '2024-01-20T10:00:00Z',
      };

      const mockResponse: BlogListResponse = {
        blogs: [blogPost],
        page: 0,
        size: 10,
        totalElements: 1,
        totalPages: 1,
        hasMore: false,
      };

      mockFetch.mockResolvedValueOnce({
        ok: true,
        json: async () => mockResponse,
      } as Response);

      const result = await fetchBlogs(0, 10);

      expect(result.blogs[0].coverImageUrl).toBeNull();
    });

    it('should handle null avatarUrl', async () => {
      const author: Author = {
        name: 'Bob Wilson',
        avatarUrl: null,
      };

      const blogPost: BlogPost = {
        id: 3,
        title: 'Post with author without avatar',
        excerpt: 'Excerpt',
        coverImageUrl: 'https://example.com/cover.jpg',
        author: author,
        tags: ['tech'],
        publishedAt: '2024-01-25T10:00:00Z',
      };

      const mockResponse: BlogListResponse = {
        blogs: [blogPost],
        page: 0,
        size: 10,
        totalElements: 1,
        totalPages: 1,
        hasMore: false,
      };

      mockFetch.mockResolvedValueOnce({
        ok: true,
        json: async () => mockResponse,
      } as Response);

      const result = await fetchBlogs(0, 10);

      expect(result.blogs[0].author.avatarUrl).toBeNull();
    });

    it('should handle pagination with page parameter', async () => {
      const mockResponse: BlogListResponse = {
        blogs: [],
        page: 2,
        size: 10,
        totalElements: 25,
        totalPages: 3,
        hasMore: false,
      };

      mockFetch.mockResolvedValueOnce({
        ok: true,
        json: async () => mockResponse,
      } as Response);

      const result = await fetchBlogs(2, 10);

      expect(mockFetch).toHaveBeenCalledWith(
        'http://localhost:8080/api/blogs?page=2&size=10',
        expect.any(Object)
      );
      expect(result.page).toBe(2);
    });

    it('should handle custom page size', async () => {
      const mockResponse: BlogListResponse = {
        blogs: [],
        page: 0,
        size: 20,
        totalElements: 0,
        totalPages: 0,
        hasMore: false,
      };

      mockFetch.mockResolvedValueOnce({
        ok: true,
        json: async () => mockResponse,
      } as Response);

      const result = await fetchBlogs(0, 20);

      expect(mockFetch).toHaveBeenCalledWith(
        'http://localhost:8080/api/blogs?page=0&size=20',
        expect.any(Object)
      );
      expect(result.size).toBe(20);
    });

    it('should handle hasMore flag when more pages exist', async () => {
      const mockResponse: BlogListResponse = {
        blogs: [],
        page: 0,
        size: 10,
        totalElements: 25,
        totalPages: 3,
        hasMore: true,
      };

      mockFetch.mockResolvedValueOnce({
        ok: true,
        json: async () => mockResponse,
      } as Response);

      const result = await fetchBlogs(0, 10);

      expect(result.hasMore).toBe(true);
    });

    it('should handle hasMore flag when no more pages exist', async () => {
      const mockResponse: BlogListResponse = {
        blogs: [],
        page: 2,
        size: 10,
        totalElements: 25,
        totalPages: 3,
        hasMore: false,
      };

      mockFetch.mockResolvedValueOnce({
        ok: true,
        json: async () => mockResponse,
      } as Response);

      const result = await fetchBlogs(2, 10);

      expect(result.hasMore).toBe(false);
    });

    it('should throw error when API returns non-ok status', async () => {
      mockFetch.mockResolvedValueOnce({
        ok: false,
        status: 500,
        statusText: 'Internal Server Error',
      } as Response);

      await expect(fetchBlogs(0, 10)).rejects.toThrow(
        'Failed to fetch blogs: 500 Internal Server Error'
      );
    });

    it('should throw error when API returns 404', async () => {
      mockFetch.mockResolvedValueOnce({
        ok: false,
        status: 404,
        statusText: 'Not Found',
      } as Response);

      await expect(fetchBlogs(0, 10)).rejects.toThrow(
        'Failed to fetch blogs: 404 Not Found'
      );
    });

    it('should handle network errors', async () => {
      mockFetch.mockRejectedValueOnce(new Error('Network error'));

      await expect(fetchBlogs(0, 10)).rejects.toThrow('Network error');
    });

    it('should handle empty blog list', async () => {
      const mockResponse: BlogListResponse = {
        blogs: [],
        page: 0,
        size: 10,
        totalElements: 0,
        totalPages: 0,
        hasMore: false,
      };

      mockFetch.mockResolvedValueOnce({
        ok: true,
        json: async () => mockResponse,
      } as Response);

      const result = await fetchBlogs(0, 10);

      expect(result.blogs).toEqual([]);
      expect(result.totalElements).toBe(0);
    });

    it('should handle multiple tags', async () => {
      const author: Author = {
        name: 'John Doe',
        avatarUrl: 'https://example.com/avatar.jpg',
      };

      const blogPost: BlogPost = {
        id: 1,
        title: 'Test Title',
        excerpt: 'Test excerpt',
        coverImageUrl: 'https://example.com/cover.jpg',
        author: author,
        tags: ['tech', 'java', 'spring', 'backend'],
        publishedAt: '2024-01-15T10:00:00Z',
      };

      const mockResponse: BlogListResponse = {
        blogs: [blogPost],
        page: 0,
        size: 10,
        totalElements: 1,
        totalPages: 1,
        hasMore: false,
      };

      mockFetch.mockResolvedValueOnce({
        ok: true,
        json: async () => mockResponse,
      } as Response);

      const result = await fetchBlogs(0, 10);

      expect(result.blogs[0].tags).toEqual(['tech', 'java', 'spring', 'backend']);
    });

    it('should use environment variable for API base URL if set', async () => {
      const originalEnv = process.env.REACT_APP_API_BASE_URL;
      process.env.REACT_APP_API_BASE_URL = 'https://api.example.com';

      const mockResponse: BlogListResponse = {
        blogs: [],
        page: 0,
        size: 10,
        totalElements: 0,
        totalPages: 0,
        hasMore: false,
      };

      mockFetch.mockResolvedValueOnce({
        ok: true,
        json: async () => mockResponse,
      } as Response);

      await fetchBlogs(0, 10);

      expect(mockFetch).toHaveBeenCalledWith(
        'https://api.example.com/api/blogs?page=0&size=10',
        expect.any(Object)
      );

      process.env.REACT_APP_API_BASE_URL = originalEnv;
    });
  });
});
