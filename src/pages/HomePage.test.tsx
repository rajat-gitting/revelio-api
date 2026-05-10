import React from 'react';
import { render, screen, waitFor, fireEvent } from '@testing-library/react';
import { HomePage } from './HomePage';
import * as blogService from '../services/blogService';
import { BlogListResponse, BlogPost } from '../services/blogService';

jest.mock('../services/blogService');

const mockFetchBlogs = blogService.fetchBlogs as jest.MockedFunction<typeof blogService.fetchBlogs>;

describe('HomePage', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render home page', () => {
    mockFetchBlogs.mockResolvedValueOnce({
      blogs: [],
      page: 0,
      size: 10,
      totalElements: 0,
      totalPages: 0,
      hasMore: false,
    });

    render(<HomePage />);

    const homePage = screen.getByTestId('home-page');
    expect(homePage).toBeInTheDocument();
  });

  it('should display 3 skeleton cards while loading', () => {
    mockFetchBlogs.mockImplementation(() => new Promise(() => {}));

    render(<HomePage />);

    const skeletonCards = screen.getAllByTestId('skeleton-card');
    expect(skeletonCards).toHaveLength(3);
  });

  it('should display blog cards when data loads successfully', async () => {
    const mockPosts: BlogPost[] = [
      {
        id: 1,
        title: 'Test Post 1',
        excerpt: 'Excerpt 1',
        coverImageUrl: 'https://example.com/cover1.jpg',
        author: { name: 'John Doe', avatarUrl: 'https://example.com/avatar.jpg' },
        tags: ['tech'],
        publishedAt: '2024-01-15T10:00:00Z',
      },
      {
        id: 2,
        title: 'Test Post 2',
        excerpt: 'Excerpt 2',
        coverImageUrl: 'https://example.com/cover2.jpg',
        author: { name: 'Jane Smith', avatarUrl: 'https://example.com/jane.jpg' },
        tags: ['design'],
        publishedAt: '2024-01-20T10:00:00Z',
      },
    ];

    const mockResponse: BlogListResponse = {
      blogs: mockPosts,
      page: 0,
      size: 10,
      totalElements: 2,
      totalPages: 1,
      hasMore: false,
    };

    mockFetchBlogs.mockResolvedValueOnce(mockResponse);

    render(<HomePage />);

    await waitFor(() => {
      expect(screen.getByText('Test Post 1')).toBeInTheDocument();
      expect(screen.getByText('Test Post 2')).toBeInTheDocument();
    });
  });

  it('should display empty state when no posts exist', async () => {
    const mockResponse: BlogListResponse = {
      blogs: [],
      page: 0,
      size: 10,
      totalElements: 0,
      totalPages: 0,
      hasMore: false,
    };

    mockFetchBlogs.mockResolvedValueOnce(mockResponse);

    render(<HomePage />);

    await waitFor(() => {
      expect(screen.getByTestId('empty-state')).toBeInTheDocument();
      expect(screen.getByText('No posts yet. Check back soon.')).toBeInTheDocument();
    });
  });

  it('should display error state when API call fails', async () => {
    mockFetchBlogs.mockRejectedValueOnce(new Error('Network error'));

    render(<HomePage />);

    await waitFor(() => {
      expect(screen.getByTestId('error-state')).toBeInTheDocument();
      expect(screen.getByText('Something went wrong. Please try again.')).toBeInTheDocument();
    });
  });

  it('should retry API call when retry button is clicked', async () => {
    mockFetchBlogs.mockRejectedValueOnce(new Error('Network error'));

    render(<HomePage />);

    await waitFor(() => {
      expect(screen.getByTestId('error-state')).toBeInTheDocument();
    });

    const mockResponse: BlogListResponse = {
      blogs: [],
      page: 0,
      size: 10,
      totalElements: 0,
      totalPages: 0,
      hasMore: false,
    };

    mockFetchBlogs.mockResolvedValueOnce(mockResponse);

    const retryButton = screen.getByTestId('error-retry-button');
    fireEvent.click(retryButton);

    await waitFor(() => {
      expect(mockFetchBlogs).toHaveBeenCalledTimes(2);
    });
  });

  it('should display Load More button when more posts exist', async () => {
    const mockPosts: BlogPost[] = [
      {
        id: 1,
        title: 'Test Post 1',
        excerpt: 'Excerpt 1',
        coverImageUrl: 'https://example.com/cover1.jpg',
        author: { name: 'John Doe', avatarUrl: 'https://example.com/avatar.jpg' },
        tags: ['tech'],
        publishedAt: '2024-01-15T10:00:00Z',
      },
    ];

    const mockResponse: BlogListResponse = {
      blogs: mockPosts,
      page: 0,
      size: 10,
      totalElements: 25,
      totalPages: 3,
      hasMore: true,
    };

    mockFetchBlogs.mockResolvedValueOnce(mockResponse);

    render(<HomePage />);

    await waitFor(() => {
      expect(screen.getByTestId('load-more-button')).toBeInTheDocument();
      expect(screen.getByText('Load More')).toBeInTheDocument();
    });
  });

  it('should hide Load More button when no more posts exist', async () => {
    const mockPosts: BlogPost[] = [
      {
        id: 1,
        title: 'Test Post 1',
        excerpt: 'Excerpt 1',
        coverImageUrl: 'https://example.com/cover1.jpg',
        author: { name: 'John Doe', avatarUrl: 'https://example.com/avatar.jpg' },
        tags: ['tech'],
        publishedAt: '2024-01-15T10:00:00Z',
      },
    ];

    const mockResponse: BlogListResponse = {
      blogs: mockPosts,
      page: 0,
      size: 10,
      totalElements: 1,
      totalPages: 1,
      hasMore: false,
    };

    mockFetchBlogs.mockResolvedValueOnce(mockResponse);

    render(<HomePage />);

    await waitFor(() => {
      expect(screen.getByText('Test Post 1')).toBeInTheDocument();
    });

    const loadMoreButton = screen.queryByTestId('load-more-button');
    expect(loadMoreButton).not.toBeInTheDocument();
  });

  it('should disable Load More button and show spinner while loading next batch', async () => {
    const mockPosts: BlogPost[] = [
      {
        id: 1,
        title: 'Test Post 1',
        excerpt: 'Excerpt 1',
        coverImageUrl: 'https://example.com/cover1.jpg',
        author: { name: 'John Doe', avatarUrl: 'https://example.com/avatar.jpg' },
        tags: ['tech'],
        publishedAt: '2024-01-15T10:00:00Z',
      },
    ];

    const mockResponse: BlogListResponse = {
      blogs: mockPosts,
      page: 0,
      size: 10,
      totalElements: 25,
      totalPages: 3,
      hasMore: true,
    };

    mockFetchBlogs.mockResolvedValueOnce(mockResponse);

    render(<HomePage />);

    await waitFor(() => {
      expect(screen.getByTestId('load-more-button')).toBeInTheDocument();
    });

    mockFetchBlogs.mockImplementation(() => new Promise(() => {}));

    const loadMoreButton = screen.getByTestId('load-more-button');
    fireEvent.click(loadMoreButton);

    await waitFor(() => {
      expect(loadMoreButton).toBeDisabled();
      expect(screen.getByTestId('load-more-spinner')).toBeInTheDocument();
      expect(screen.getByText('Loading...')).toBeInTheDocument();
    });
  });

  it('should append new posts when Load More is clicked', async () => {
    const firstBatchPosts: BlogPost[] = [
      {
        id: 1,
        title: 'Test Post 1',
        excerpt: 'Excerpt 1',
        coverImageUrl: 'https://example.com/cover1.jpg',
        author: { name: 'John Doe', avatarUrl: 'https://example.com/avatar.jpg' },
        tags: ['tech'],
        publishedAt: '2024-01-15T10:00:00Z',
      },
    ];

    const secondBatchPosts: BlogPost[] = [
      {
        id: 2,
        title: 'Test Post 2',
        excerpt: 'Excerpt 2',
        coverImageUrl: 'https://example.com/cover2.jpg',
        author: { name: 'Jane Smith', avatarUrl: 'https://example.com/jane.jpg' },
        tags: ['design'],
        publishedAt: '2024-01-20T10:00:00Z',
      },
    ];

    const firstResponse: BlogListResponse = {
      blogs: firstBatchPosts,
      page: 0,
      size: 10,
      totalElements: 25,
      totalPages: 3,
      hasMore: true,
    };

    const secondResponse: BlogListResponse = {
      blogs: secondBatchPosts,
      page: 1,
      size: 10,
      totalElements: 25,
      totalPages: 3,
      hasMore: true,
    };

    mockFetchBlogs.mockResolvedValueOnce(firstResponse);

    render(<HomePage />);

    await waitFor(() => {
      expect(screen.getByText('Test Post 1')).toBeInTheDocument();
    });

    mockFetchBlogs.mockResolvedValueOnce(secondResponse);

    const loadMoreButton = screen.getByTestId('load-more-button');
    fireEvent.click(loadMoreButton);

    await waitFor(() => {
      expect(screen.getByText('Test Post 1')).toBeInTheDocument();
      expect(screen.getByText('Test Post 2')).toBeInTheDocument();
    });
  });

  it('should call fetchBlogs with correct page and size on initial load', async () => {
    const mockResponse: BlogListResponse = {
      blogs: [],
      page: 0,
      size: 10,
      totalElements: 0,
      totalPages: 0,
      hasMore: false,
    };

    mockFetchBlogs.mockResolvedValueOnce(mockResponse);

    render(<HomePage />);

    await waitFor(() => {
      expect(mockFetchBlogs).toHaveBeenCalledWith(0, 10);
    });
  });

  it('should call fetchBlogs with incremented page when Load More is clicked', async () => {
    const mockPosts: BlogPost[] = [
      {
        id: 1,
        title: 'Test Post 1',
        excerpt: 'Excerpt 1',
        coverImageUrl: 'https://example.com/cover1.jpg',
        author: { name: 'John Doe', avatarUrl: 'https://example.com/avatar.jpg' },
        tags: ['tech'],
        publishedAt: '2024-01-15T10:00:00Z',
      },
    ];

    const mockResponse: BlogListResponse = {
      blogs: mockPosts,
      page: 0,
      size: 10,
      totalElements: 25,
      totalPages: 3,
      hasMore: true,
    };

    mockFetchBlogs.mockResolvedValueOnce(mockResponse);

    render(<HomePage />);

    await waitFor(() => {
      expect(screen.getByTestId('load-more-button')).toBeInTheDocument();
    });

    mockFetchBlogs.mockResolvedValueOnce({
      blogs: [],
      page: 1,
      size: 10,
      totalElements: 25,
      totalPages: 3,
      hasMore: true,
    });

    const loadMoreButton = screen.getByTestId('load-more-button');
    fireEvent.click(loadMoreButton);

    await waitFor(() => {
      expect(mockFetchBlogs).toHaveBeenCalledWith(1, 10);
    });
  });

  it('should render blog cards in a grid', async () => {
    const mockPosts: BlogPost[] = [
      {
        id: 1,
        title: 'Test Post 1',
        excerpt: 'Excerpt 1',
        coverImageUrl: 'https://example.com/cover1.jpg',
        author: { name: 'John Doe', avatarUrl: 'https://example.com/avatar.jpg' },
        tags: ['tech'],
        publishedAt: '2024-01-15T10:00:00Z',
      },
      {
        id: 2,
        title: 'Test Post 2',
        excerpt: 'Excerpt 2',
        coverImageUrl: 'https://example.com/cover2.jpg',
        author: { name: 'Jane Smith', avatarUrl: 'https://example.com/jane.jpg' },
        tags: ['design'],
        publishedAt: '2024-01-20T10:00:00Z',
      },
    ];

    const mockResponse: BlogListResponse = {
      blogs: mockPosts,
      page: 0,
      size: 10,
      totalElements: 2,
      totalPages: 1,
      hasMore: false,
    };

    mockFetchBlogs.mockResolvedValueOnce(mockResponse);

    const { container } = render(<HomePage />);

    await waitFor(() => {
      const grid = container.querySelector('.home-page__grid');
      expect(grid).toBeInTheDocument();
    });
  });

  it('should re-enable Load More button after next batch loads', async () => {
    const mockPosts: BlogPost[] = [
      {
        id: 1,
        title: 'Test Post 1',
        excerpt: 'Excerpt 1',
        coverImageUrl: 'https://example.com/cover1.jpg',
        author: { name: 'John Doe', avatarUrl: 'https://example.com/avatar.jpg' },
        tags: ['tech'],
        publishedAt: '2024-01-15T10:00:00Z',
      },
    ];

    const mockResponse: BlogListResponse = {
      blogs: mockPosts,
      page: 0,
      size: 10,
      totalElements: 25,
      totalPages: 3,
      hasMore: true,
    };

    mockFetchBlogs.mockResolvedValueOnce(mockResponse);

    render(<HomePage />);

    await waitFor(() => {
      expect(screen.getByTestId('load-more-button')).toBeInTheDocument();
    });

    mockFetchBlogs.mockResolvedValueOnce({
      blogs: [],
      page: 1,
      size: 10,
      totalElements: 25,
      totalPages: 3,
      hasMore: true,
    });

    const loadMoreButton = screen.getByTestId('load-more-button');
    fireEvent.click(loadMoreButton);

    await waitFor(() => {
      expect(loadMoreButton).not.toBeDisabled();
      expect(screen.queryByTestId('load-more-spinner')).not.toBeInTheDocument();
    });
  });
});
