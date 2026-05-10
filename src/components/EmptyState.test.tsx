import React from 'react';
import { render, screen } from '@testing-library/react';
import '@testing-library/jest-dom';
import EmptyState from '../src/components/EmptyState';

describe('EmptyState', () => {
  test('renders empty state message', () => {
    render(<EmptyState />);

    expect(screen.getByText('No posts yet. Check back soon.')).toBeInTheDocument();
  });

  test('renders empty state icon', () => {
    render(<EmptyState />);

    expect(screen.getByText('📝')).toBeInTheDocument();
  });

  test('has correct CSS classes', () => {
    const { container } = render(<EmptyState />);

    expect(container.querySelector('.empty-state')).toBeInTheDocument();
    expect(container.querySelector('.empty-state-icon')).toBeInTheDocument();
    expect(container.querySelector('.empty-state-title')).toBeInTheDocument();
  });
});
