import { fetchBlogs, BlogPost, BlogsResponse } from './blogService';

global.fetch = jest.fn();

const mockFetch = global.fetch as jest.MockedFunction<typeof fetch>;

describe('blogService', () => {
  beforeEach(() => {
    mockFetch.mockClear();
  });

  describe('fetchBlogs', () => {
    it('should fetch blogs with correct URL and query parameters', async () => {
      const mockResponse: BlogsResponse = {
        blogs: [
          {
            id: 1,
            title: 'Test Blog',
            excerpt: 'Test excerpt',
            coverImageUrl: 'https://example.com/cover.jpg',
            author: {
              name: 'John Doe',
              avatarUrl: 'https://example.com/avatar.jpg',
            },
            tags: ['tech', 'java'],
            publishedAt: '2024-01-15T10:00:00Z',
          },
        ],
        totalCount: 1,
        page: 0,
        size: 10,
      };

      mockFetch.mockResolvedValueOnce({
        ok: true,
        json: async () => mockResponse,
      } as Response);

      const result = await fetchBlogs(0, 10);

      expect(mockFetch).toHaveBeenCalledWith(
        expect.stringContaining('/api/blogs?page=0&size=10'),
        expect.objectContaining({
          method: 'GET',
          headers: {
            'Content-Type': 'application/json',
          },
        })
      );
      expect(result).toEqual(mockResponse);
    });

    it('should handle pagination with different page numbers', async () => {
      const mockResponse: BlogsResponse = {
        blogs: [],
        totalCount: 0,
        page: 2,
        size: 10,
      };

      mockFetch.mockResolvedValueOnce({
        ok: true,
        json: async () => mockResponse,
      } as Response);

      await fetchBlogs(2, 10);

      expect(mockFetch).toHaveBeenCalledWith(
        expect.stringContaining('page=2&size=10'),
        expect.any(Object)
      );
    });

    it('should handle different page sizes', async () => {
      const mockResponse: BlogsResponse = {
        blogs: [],
        totalCount: 0,
        page: 0,
        size: 5,
      };

      mockFetch.mockResolvedValueOnce({
        ok: true,
        json: async () => mockResponse,
      } as Response);

      await fetchBlogs(0, 5);

      expect(mockFetch).toHaveBeenCalledWith(
        expect.stringContaining('page=0&size=5'),
        expect.any(Object)
      );
    });

    it('should throw error for negative page number', async () => {
      await expect(fetchBlogs(-1, 10)).rejects.toThrow('Page number must be non-negative');
      expect(mockFetch).not.toHaveBeenCalled();
    });

    it('should throw error for zero page size', async () => {
      await expect(fetchBlogs(0, 0)).rejects.toThrow('Page size must be positive');
      expect(mockFetch).not.toHaveBeenCalled();
    });

    it('should throw error for negative page size', async () => {
      await expect(fetchBlogs(0, -5)).rejects.toThrow('Page size must be positive');
      expect(mockFetch).not.toHaveBeenCalled();
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

      await expect(fetchBlogs(0, 10)).rejects.toThrow('Failed to fetch blogs: 404 Not Found');
    });

    it('should handle network errors', async () => {
      mockFetch.mockRejectedValueOnce(new Error('Network error'));

      await expect(fetchBlogs(0, 10)).rejects.toThrow('Network error');
    });

    it('should handle blogs with null coverImageUrl', async () => {
      const mockResponse: BlogsResponse = {
        blogs: [
          {
            id: 1,
            title: 'Test Blog',
            excerpt: 'Test excerpt',
            coverImageUrl: null,
            author: {
              name: 'John Doe',
              avatarUrl: 'https://example.com/avatar.jpg',
            },
            tags: ['tech'],
            publishedAt: '2024-01-15T10:00:00Z',
          },
        ],
        totalCount: 1,
        page: 0,
        size: 10,
      };

      mockFetch.mockResolvedValueOnce({
        ok: true,
        json: async () => mockResponse,
      } as Response);

      const result = await fetchBlogs(0, 10);

      expect(result.blogs[0].coverImageUrl).toBeNull();
    });

    it('should handle author with null avatarUrl', async () => {
      const mockResponse: BlogsResponse = {
        blogs: [
          {
            id: 1,
            title: 'Test Blog',
            excerpt: 'Test excerpt',
            coverImageUrl: 'https://example.com/cover.jpg',
            author: {
              name: 'John Doe',
              avatarUrl: null,
            },
            tags: ['tech'],
            publishedAt: '2024-01-15T10:00:00Z',
          },
        ],
        totalCount: 1,
        page: 0,
        size: 10,
      };

      mockFetch.mockResolvedValueOnce({
        ok: true,
        json: async () => mockResponse,
      } as Response);

      const result = await fetchBlogs(0, 10);

      expect(result.blogs[0].author.avatarUrl).toBeNull();
    });

    it('should handle empty blogs array', async () => {
      const mockResponse: BlogsResponse = {
        blogs: [],
        totalCount: 0,
        page: 0,
        size: 10,
      };

      mockFetch.mockResolvedValueOnce({
        ok: true,
        json: async () => mockResponse,
      } as Response);

      const result = await fetchBlogs(0, 10);

      expect(result.blogs).toEqual([]);
      expect(result.totalCount).toBe(0);
    });

    it('should handle blogs with empty tags array', async () => {
      const mockResponse: BlogsResponse = {
        blogs: [
          {
            id: 1,
            title: 'Test Blog',
            excerpt: 'Test excerpt',
            coverImageUrl: 'https://example.com/cover.jpg',
            author: {
              name: 'John Doe',
              avatarUrl: 'https://example.com/avatar.jpg',
            },
            tags: [],
            publishedAt: '2024-01-15T10:00:00Z',
          },
        ],
        totalCount: 1,
        page: 0,
        size: 10,
      };

      mockFetch.mockResolvedValueOnce({
        ok: true,
        json: async () => mockResponse,
      } as Response);

      const result = await fetchBlogs(0, 10);

      expect(result.blogs[0].tags).toEqual([]);
    });

    it('should handle blogs with multiple tags', async () => {
      const mockResponse: BlogsResponse = {
        blogs: [
          {
            id: 1,
            title: 'Test Blog',
            excerpt: 'Test excerpt',
            coverImageUrl: 'https://example.com/cover.jpg',
            author: {
              name: 'John Doe',
              avatarUrl: 'https://example.com/avatar.jpg',
            },
            tags: ['tech', 'java', 'spring', 'kotlin', 'react'],
            publishedAt: '2024-01-15T10:00:00Z',
          },
        ],
        totalCount: 1,
        page: 0,
        size: 10,
      };

      mockFetch.mockResolvedValueOnce({
        ok: true,
        json: async () => mockResponse,
      } as Response);

      const result = await fetchBlogs(0, 10);

      expect(result.blogs[0].tags).toHaveLength(5);
    });

    it('should use API_BASE_URL from environment variable', async () => {
      const originalEnv = process.env.REACT_APP_API_BASE_URL;
      process.env.REACT_APP_API_BASE_URL = 'https://api.example.com';

      const mockResponse: BlogsResponse = {
        blogs: [],
        totalCount: 0,
        page: 0,
        size: 10,
      };

      mockFetch.mockResolvedValueOnce({
        ok: true,
        json: async () => mockResponse,
      } as Response);

      await fetchBlogs(0, 10);

      expect(mockFetch).toHaveBeenCalledWith(
        expect.stringContaining('https://api.example.com/api/blogs'),
        expect.any(Object)
      );

      process.env.REACT_APP_API_BASE_URL = originalEnv;
    });
  });
});
