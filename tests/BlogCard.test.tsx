import React from 'react';
import { render, screen } from '@testing-library/react';
import '@testing-library/jest-dom';
import BlogCard from '../src/components/BlogCard';
import { BlogPost } from '../src/pages/HomePage';

const mockPost: BlogPost = {
  id: 1,
  title: 'Test Blog Post',
  excerpt: 'This is a test excerpt for the blog post',
  coverImageUrl: 'https://example.com/cover.jpg',
  author: {
    name: 'John Doe',
    avatarUrl: 'https://example.com/avatar.jpg',
  },
  tags: ['tech', 'javascript', 'react'],
  publishedAt: '2024-01-15T10:00:00Z',
};

describe('BlogCard', () => {
  test('renders blog post with all fields', () => {
    render(<BlogCard post={mockPost} />);

    expect(screen.getByText('Test Blog Post')).toBeInTheDocument();
    expect(screen.getByText('This is a test excerpt for the blog post')).toBeInTheDocument();
    expect(screen.getByText('John Doe')).toBeInTheDocument();
    expect(screen.getByText('tech')).toBeInTheDocument();
    expect(screen.getByText('javascript')).toBeInTheDocument();
    expect(screen.getByText('react')).toBeInTheDocument();
  });

  test('renders cover image when coverImageUrl is provided', () => {
    render(<BlogCard post={mockPost} />);

    const image = screen.getByAltText('Test Blog Post');
    expect(image).toBeInTheDocument();
    expect(image).toHaveAttribute('src', 'https://example.com/cover.jpg');
  });

  test('renders placeholder when coverImageUrl is null', () => {
    const postWithoutCover = { ...mockPost, coverImageUrl: null };
    render(<BlogCard post={postWithoutCover} />);

    expect(screen.getByText('R')).toBeInTheDocument();
    expect(screen.queryByAltText('Test Blog Post')).not.toBeInTheDocument();
  });

  test('renders author avatar when avatarUrl is provided', () => {
    render(<BlogCard post={mockPost} />);

    const avatar = screen.getByAltText('John Doe');
    expect(avatar).toBeInTheDocument();
    expect(avatar).toHaveAttribute('src', 'https://example.com/avatar.jpg');
  });

  test('renders author initials when avatarUrl is null', () => {
    const postWithoutAvatar = {
      ...mockPost,
      author: { name: 'John Doe', avatarUrl: null },
    };
    render(<BlogCard post={postWithoutAvatar} />);

    expect(screen.getByText('JD')).toBeInTheDocument();
  });

  test('renders initials correctly for single name', () => {
    const postWithSingleName = {
      ...mockPost,
      author: { name: 'Madonna', avatarUrl: null },
    };
    render(<BlogCard post={postWithSingleName} />);

    expect(screen.getByText('MA')).toBeInTheDocument();
  });

  test('displays only first 3 tags', () => {
    const postWithManyTags = {
      ...mockPost,
      tags: ['tech', 'javascript', 'react', 'typescript', 'node'],
    };
    render(<BlogCard post={postWithManyTags} />);

    expect(screen.getByText('tech')).toBeInTheDocument();
    expect(screen.getByText('javascript')).toBeInTheDocument();
    expect(screen.getByText('react')).toBeInTheDocument();
    expect(screen.queryByText('typescript')).not.toBeInTheDocument();
    expect(screen.queryByText('node')).not.toBeInTheDocument();
  });

  test('displays +N more indicator when more than 3 tags', () => {
    const postWithManyTags = {
      ...mockPost,
      tags: ['tech', 'javascript', 'react', 'typescript', 'node'],
    };
    render(<BlogCard post={postWithManyTags} />);

    expect(screen.getByText('+2 more')).toBeInTheDocument();
  });

  test('does not display +N more when 3 or fewer tags', () => {
    render(<BlogCard post={mockPost} />);

    expect(screen.queryByText(/\+\d+ more/)).not.toBeInTheDocument();
  });

  test('renders no tags section when tags array is empty', () => {
    const postWithoutTags = { ...mockPost, tags: [] };
    const { container } = render(<BlogCard post={postWithoutTags} />);

    const tagsSection = container.querySelector('.blog-card-tags');
    expect(tagsSection).not.toBeInTheDocument();
  });

  test('formats timestamp as relative time for recent posts', () => {
    const recentDate = new Date();
    recentDate.setDate(recentDate.getDate() - 5);
    const recentPost = { ...mockPost, publishedAt: recentDate.toISOString() };

    render(<BlogCard post={recentPost} />);

    expect(screen.getByText(/days ago/i)).toBeInTheDocument();
  });

  test('formats timestamp as full date for old posts', () => {
    const oldDate = new Date();
    oldDate.setDate(oldDate.getDate() - 60);
    const oldPost = { ...mockPost, publishedAt: oldDate.toISOString() };

    render(<BlogCard post={oldPost} />);

    const timestamp = screen.getByText(/\w+ \d+, \d{4}/);
    expect(timestamp).toBeInTheDocument();
  });

  test('links to blog detail page', () => {
    const { container } = render(<BlogCard post={mockPost} />);

    const link = container.querySelector('a');
    expect(link).toHaveAttribute('href', '/blog/1');
  });

  test('handles very long titles gracefully', () => {
    const postWithLongTitle = {
      ...mockPost,
      title: 'This is a very long blog post title that should be truncated or wrapped properly to prevent layout breaking issues in the card component',
    };
    render(<BlogCard post={postWithLongTitle} />);

    expect(screen.getByText(/This is a very long blog post title/)).toBeInTheDocument();
  });

  test('handles very long author names gracefully', () => {
    const postWithLongAuthorName = {
      ...mockPost,
      author: {
        name: 'Christopher Alexander Montgomery Wellington III',
        avatarUrl: null,
      },
    };
    render(<BlogCard post={postWithLongAuthorName} />);

    expect(screen.getByText('Christopher Alexander Montgomery Wellington III')).toBeInTheDocument();
  });
});
