import React from 'react';
import { render, screen } from '@testing-library/react';
import '@testing-library/jest-dom';
import SkeletonCard from '../src/components/SkeletonCard';

describe('SkeletonCard', () => {
  test('renders skeleton card structure', () => {
    const { container } = render(<SkeletonCard />);

    expect(container.querySelector('.skeleton-card')).toBeInTheDocument();
    expect(container.querySelector('.skeleton-image')).toBeInTheDocument();
    expect(container.querySelector('.skeleton-content')).toBeInTheDocument();
  });

  test('renders skeleton title', () => {
    const { container } = render(<SkeletonCard />);

    expect(container.querySelector('.skeleton-title')).toBeInTheDocument();
  });

  test('renders skeleton excerpt lines', () => {
    const { container } = render(<SkeletonCard />);

    const excerpts = container.querySelectorAll('.skeleton-excerpt');
    expect(excerpts.length).toBeGreaterThan(0);
  });

  test('renders skeleton author section', () => {
    const { container } = render(<SkeletonCard />);

    expect(container.querySelector('.skeleton-author')).toBeInTheDocument();
    expect(container.querySelector('.skeleton-avatar')).toBeInTheDocument();
    expect(container.querySelector('.skeleton-author-name')).toBeInTheDocument();
  });

  test('renders skeleton timestamp', () => {
    const { container } = render(<SkeletonCard />);

    expect(container.querySelector('.skeleton-timestamp')).toBeInTheDocument();
  });

  test('renders skeleton tags', () => {
    const { container } = render(<SkeletonCard />);

    const tags = container.querySelectorAll('.skeleton-tag');
    expect(tags.length).toBe(3);
  });

  test('has correct CSS classes for animation', () => {
    const { container } = render(<SkeletonCard />);

    const image = container.querySelector('.skeleton-image');
    expect(image).toHaveClass('skeleton-image');
  });
});
