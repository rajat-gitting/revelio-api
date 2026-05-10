import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import '@testing-library/jest-dom';
import ErrorState from '../src/components/ErrorState';

describe('ErrorState', () => {
  test('renders error message', () => {
    const mockRetry = jest.fn();
    render(<ErrorState onRetry={mockRetry} />);

    expect(screen.getByText('Something went wrong. Please try again.')).toBeInTheDocument();
  });

  test('renders error icon', () => {
    const mockRetry = jest.fn();
    render(<ErrorState onRetry={mockRetry} />);

    expect(screen.getByText('⚠️')).toBeInTheDocument();
  });

  test('renders retry button', () => {
    const mockRetry = jest.fn();
    render(<ErrorState onRetry={mockRetry} />);

    expect(screen.getByRole('button', { name: /retry/i })).toBeInTheDocument();
  });

  test('calls onRetry when retry button is clicked', () => {
    const mockRetry = jest.fn();
    render(<ErrorState onRetry={mockRetry} />);

    const retryButton = screen.getByRole('button', { name: /retry/i });
    fireEvent.click(retryButton);

    expect(mockRetry).toHaveBeenCalledTimes(1);
  });

  test('has correct CSS classes', () => {
    const mockRetry = jest.fn();
    const { container } = render(<ErrorState onRetry={mockRetry} />);

    expect(container.querySelector('.error-state')).toBeInTheDocument();
    expect(container.querySelector('.error-state-icon')).toBeInTheDocument();
    expect(container.querySelector('.error-state-title')).toBeInTheDocument();
    expect(container.querySelector('.error-state-button')).toBeInTheDocument();
  });
});
