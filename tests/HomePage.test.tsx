import React from 'react';
import { render, screen, waitFor, fireEvent } from '@testing-library/react';
import '@testing-library/jest-dom';
import HomePage from '../src/pages/HomePage';
import * as blogService from '../src/services/blogService';

jest.mock('../src/services/blogService');

const mockFetchBlogs = blogService.fetchBlogs as jest.MockedFunction<typeof blogService.fetchBlogs>;

const mockBlogPost = {
  id: 1,
  title: 'Test Blog Post',
  excerpt: 'This is a test excerpt',
  coverImageUrl: 'https://example.com/cover.jpg',
  author: {
    name: 'John Doe',
    avatarUrl: 'https://example.com/avatar.jpg',
  },
  tags: ['tech', 'javascript'],
  publishedAt: '2024-01-15T10:00:00Z',
};

const mockPagedResponse = {
  content: [mockBlogPost],
  page: 0,
  size: 10,
  totalElements: 1,
  totalPages: 1,
  hasMore: false,
};

describe('HomePage', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  test('displays skeleton cards while loading', () => {
    mockFetchBlogs.mockImplementation(
      () => new Promise(() => {})
    );

    render(<HomePage />);

    const skeletonCards = screen.getAllByTestId('skeleton-card');
    expect(skeletonCards).toHaveLength(3);
  });

  test('displays blog posts after successful fetch', async () => {
    mockFetchBlogs.mockResolvedValue(mockPagedResponse);

    render(<HomePage />);

    await waitFor(() => {
      expect(screen.getByText('Test Blog Post')).toBeInTheDocument();
    });

    expect(screen.getByText('This is a test excerpt')).toBeInTheDocument();
    expect(screen.getByText('John Doe')).toBeInTheDocument();
  });

  test('displays error state when fetch fails', async () => {
    mockFetchBlogs.mockRejectedValue(new Error('Network error'));

    render(<HomePage />);

    await waitFor(() => {
      expect(screen.getByText('Something went wrong. Please try again.')).toBeInTheDocument();
    });

    expect(screen.getByRole('button', { name: /retry/i })).toBeInTheDocument();
  });

  test('displays empty state when no posts exist', async () => {
    mockFetchBlogs.mockResolvedValue({
      ...mockPagedResponse,
      content: [],
      totalElements: 0,
    });

    render(<HomePage />);

    await waitFor(() => {
      expect(screen.getByText('No posts yet. Check back soon.')).toBeInTheDocument();
    });
  });

  test('retry button calls fetchBlogs again', async () => {
    mockFetchBlogs.mockRejectedValueOnce(new Error('Network error'));
    mockFetchBlogs.mockResolvedValueOnce(mockPagedResponse);

    render(<HomePage />);

    await waitFor(() => {
      expect(screen.getByText('Something went wrong. Please try again.')).toBeInTheDocument();
    });

    const retryButton = screen.getByRole('button', { name: /retry/i });
    fireEvent.click(retryButton);

    await waitFor(() => {
      expect(screen.getByText('Test Blog Post')).toBeInTheDocument();
    });

    expect(mockFetchBlogs).toHaveBeenCalledTimes(2);
  });

  test('displays Load More button when hasMore is true', async () => {
    mockFetchBlogs.mockResolvedValue({
      ...mockPagedResponse,
      hasMore: true,
    });

    render(<HomePage />);

    await waitFor(() => {
      expect(screen.getByText('Test Blog Post')).toBeInTheDocument();
    });

    expect(screen.getByRole('button', { name: /load more/i })).toBeInTheDocument();
  });

  test('hides Load More button when hasMore is false', async () => {
    mockFetchBlogs.mockResolvedValue(mockPagedResponse);

    render(<HomePage />);

    await waitFor(() => {
      expect(screen.getByText('Test Blog Post')).toBeInTheDocument();
    });

    expect(screen.queryByRole('button', { name: /load more/i })).not.toBeInTheDocument();
  });

  test('Load More button fetches next page and appends posts', async () => {
    const secondPost = { ...mockBlogPost, id: 2, title: 'Second Post' };

    mockFetchBlogs.mockResolvedValueOnce({
      ...mockPagedResponse,
      hasMore: true,
    });

    mockFetchBlogs.mockResolvedValueOnce({
      content: [secondPost],
      page: 1,
      size: 10,
      totalElements: 2,
      totalPages: 1,
      hasMore: false,
    });

    render(<HomePage />);

    await waitFor(() => {
      expect(screen.getByText('Test Blog Post')).toBeInTheDocument();
    });

    const loadMoreButton = screen.getByRole('button', { name: /load more/i });
    fireEvent.click(loadMoreButton);

    await waitFor(() => {
      expect(screen.getByText('Second Post')).toBeInTheDocument();
    });

    expect(screen.getByText('Test Blog Post')).toBeInTheDocument();
    expect(mockFetchBlogs).toHaveBeenCalledWith(0, 10);
    expect(mockFetchBlogs).toHaveBeenCalledWith(1, 10);
  });

  test('Load More button shows loading state while fetching', async () => {
    mockFetchBlogs.mockResolvedValueOnce({
      ...mockPagedResponse,
      hasMore: true,
    });

    mockFetchBlogs.mockImplementation(
      () => new Promise((resolve) => setTimeout(() => resolve(mockPagedResponse), 100))
    );

    render(<HomePage />);

    await waitFor(() => {
      expect(screen.getByText('Test Blog Post')).toBeInTheDocument();
    });

    const loadMoreButton = screen.getByRole('button', { name: /load more/i });
    fireEvent.click(loadMoreButton);

    expect(screen.getByText(/loading/i)).toBeInTheDocument();
    expect(loadMoreButton).toBeDisabled();
  });

  test('displays multiple blog posts in grid', async () => {
    const posts = [
      mockBlogPost,
      { ...mockBlogPost, id: 2, title: 'Second Post' },
      { ...mockBlogPost, id: 3, title: 'Third Post' },
    ];

    mockFetchBlogs.mockResolvedValue({
      ...mockPagedResponse,
      content: posts,
      totalElements: 3,
    });

    render(<HomePage />);

    await waitFor(() => {
      expect(screen.getByText('Test Blog Post')).toBeInTheDocument();
      expect(screen.getByText('Second Post')).toBeInTheDocument();
      expect(screen.getByText('Third Post')).toBeInTheDocument();
    });
  });
});
