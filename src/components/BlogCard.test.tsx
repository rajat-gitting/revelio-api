import React from 'react';
import { render, screen } from '@testing-library/react';
import { BlogCard } from './BlogCard';
import { BlogPost } from '../services/blogService';

describe('BlogCard', () => {
  const mockPost: BlogPost = {
    id: 1,
    title: 'Test Blog Post',
    excerpt: 'This is a test excerpt for the blog post.',
    coverImageUrl: 'https://example.com/cover.jpg',
    author: {
      name: 'John Doe',
      avatarUrl: 'https://example.com/avatar.jpg',
    },
    tags: ['tech', 'java', 'spring'],
    publishedAt: '2024-01-15T10:00:00Z',
  };

  it('should render blog card', () => {
    render(<BlogCard post={mockPost} />);

    const blogCard = screen.getByTestId('blog-card');
    expect(blogCard).toBeInTheDocument();
  });

  it('should display post title', () => {
    render(<BlogCard post={mockPost} />);

    const title = screen.getByText('Test Blog Post');
    expect(title).toBeInTheDocument();
  });

  it('should display post excerpt', () => {
    render(<BlogCard post={mockPost} />);

    const excerpt = screen.getByText('This is a test excerpt for the blog post.');
    expect(excerpt).toBeInTheDocument();
  });

  it('should display cover image when coverImageUrl is provided', () => {
    render(<BlogCard post={mockPost} />);

    const image = screen.getByAltText('Test Blog Post');
    expect(image).toBeInTheDocument();
    expect(image).toHaveAttribute('src', 'https://example.com/cover.jpg');
  });

  it('should display placeholder when coverImageUrl is null', () => {
    const postWithoutCover = { ...mockPost, coverImageUrl: null };
    render(<BlogCard post={postWithoutCover} />);

    const placeholder = screen.getByTestId('blog-card-placeholder');
    expect(placeholder).toBeInTheDocument();
  });

  it('should display author name', () => {
    render(<BlogCard post={mockPost} />);

    const authorName = screen.getByText('John Doe');
    expect(authorName).toBeInTheDocument();
  });

  it('should display author avatar when avatarUrl is provided', () => {
    render(<BlogCard post={mockPost} />);

    const avatar = screen.getByAltText('John Doe');
    expect(avatar).toBeInTheDocument();
    expect(avatar).toHaveAttribute('src', 'https://example.com/avatar.jpg');
  });

  it('should display author initials when avatarUrl is null', () => {
    const postWithoutAvatar = {
      ...mockPost,
      author: { name: 'John Doe', avatarUrl: null },
    };
    render(<BlogCard post={postWithoutAvatar} />);

    const initials = screen.getByTestId('blog-card-initials');
    expect(initials).toBeInTheDocument();
    expect(initials).toHaveTextContent('JD');
  });

  it('should display first 3 tags', () => {
    render(<BlogCard post={mockPost} />);

    expect(screen.getByText('tech')).toBeInTheDocument();
    expect(screen.getByText('java')).toBeInTheDocument();
    expect(screen.getByText('spring')).toBeInTheDocument();
  });

  it('should display +N more indicator when more than 3 tags exist', () => {
    const postWithManyTags = {
      ...mockPost,
      tags: ['tech', 'java', 'spring', 'backend', 'api'],
    };
    render(<BlogCard post={postWithManyTags} />);

    const moreIndicator = screen.getByTestId('blog-card-tag-more');
    expect(moreIndicator).toBeInTheDocument();
    expect(moreIndicator).toHaveTextContent('+2 more');
  });

  it('should not display +N more indicator when 3 or fewer tags exist', () => {
    render(<BlogCard post={mockPost} />);

    const moreIndicator = screen.queryByTestId('blog-card-tag-more');
    expect(moreIndicator).not.toBeInTheDocument();
  });

  it('should display relative date for posts within 30 days', () => {
    const recentDate = new Date();
    recentDate.setDate(recentDate.getDate() - 5);
    const postWithRecentDate = {
      ...mockPost,
      publishedAt: recentDate.toISOString(),
    };
    render(<BlogCard post={postWithRecentDate} />);

    const date = screen.getByText('5 days ago');
    expect(date).toBeInTheDocument();
  });

  it('should display full date for posts older than 30 days', () => {
    const oldDate = new Date('2024-01-15T10:00:00Z');
    const postWithOldDate = {
      ...mockPost,
      publishedAt: oldDate.toISOString(),
    };
    render(<BlogCard post={postWithOldDate} />);

    const date = screen.getByText(/Jan 15, 2024/);
    expect(date).toBeInTheDocument();
  });

  it('should display "just now" for posts published today', () => {
    const today = new Date();
    const postPublishedToday = {
      ...mockPost,
      publishedAt: today.toISOString(),
    };
    render(<BlogCard post={postPublishedToday} />);

    const date = screen.getByText('just now');
    expect(date).toBeInTheDocument();
  });

  it('should link to blog detail page', () => {
    render(<BlogCard post={mockPost} />);

    const link = screen.getByTestId('blog-card');
    expect(link).toHaveAttribute('href', '/blog/1');
  });

  it('should handle single-word author name for initials', () => {
    const postWithSingleName = {
      ...mockPost,
      author: { name: 'John', avatarUrl: null },
    };
    render(<BlogCard post={postWithSingleName} />);

    const initials = screen.getByTestId('blog-card-initials');
    expect(initials).toHaveTextContent('J');
  });

  it('should handle empty author name for initials', () => {
    const postWithEmptyName = {
      ...mockPost,
      author: { name: '', avatarUrl: null },
    };
    render(<BlogCard post={postWithEmptyName} />);

    const initials = screen.getByTestId('blog-card-initials');
    expect(initials).toHaveTextContent('?');
  });

  it('should handle multi-word author name for initials', () => {
    const postWithMultiName = {
      ...mockPost,
      author: { name: 'John Michael Doe', avatarUrl: null },
    };
    render(<BlogCard post={postWithMultiName} />);

    const initials = screen.getByTestId('blog-card-initials');
    expect(initials).toHaveTextContent('JD');
  });

  it('should handle empty tags array', () => {
    const postWithoutTags = {
      ...mockPost,
      tags: [],
    };
    render(<BlogCard post={postWithoutTags} />);

    const blogCard = screen.getByTestId('blog-card');
    expect(blogCard).toBeInTheDocument();
  });

  it('should display "1 day ago" for posts published yesterday', () => {
    const yesterday = new Date();
    yesterday.setDate(yesterday.getDate() - 1);
    const postFromYesterday = {
      ...mockPost,
      publishedAt: yesterday.toISOString(),
    };
    render(<BlogCard post={postFromYesterday} />);

    const date = screen.getByText('1 day ago');
    expect(date).toBeInTheDocument();
  });
});
