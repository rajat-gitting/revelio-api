import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import { ErrorState } from './ErrorState';

describe('ErrorState', () => {
  it('should render error state with provided message', () => {
    const mockRetry = jest.fn();
    render(<ErrorState message="Something went wrong. Please try again." onRetry={mockRetry} />);

    const errorState = screen.getByTestId('error-state');
    expect(errorState).toBeInTheDocument();
  });

  it('should display the correct error message text', () => {
    const mockRetry = jest.fn();
    render(<ErrorState message="Something went wrong. Please try again." onRetry={mockRetry} />);

    const message = screen.getByText('Something went wrong. Please try again.');
    expect(message).toBeInTheDocument();
  });

  it('should render retry button', () => {
    const mockRetry = jest.fn();
    render(<ErrorState message="Something went wrong. Please try again." onRetry={mockRetry} />);

    const retryButton = screen.getByTestId('error-retry-button');
    expect(retryButton).toBeInTheDocument();
    expect(retryButton).toHaveTextContent('Try Again');
  });

  it('should call onRetry when retry button is clicked', () => {
    const mockRetry = jest.fn();
    render(<ErrorState message="Something went wrong. Please try again." onRetry={mockRetry} />);

    const retryButton = screen.getByTestId('error-retry-button');
    fireEvent.click(retryButton);

    expect(mockRetry).toHaveBeenCalledTimes(1);
  });

  it('should render icon element', () => {
    const mockRetry = jest.fn();
    const { container } = render(<ErrorState message="Something went wrong. Please try again." onRetry={mockRetry} />);

    const icon = container.querySelector('.error-state__icon');
    expect(icon).toBeInTheDocument();
  });

  it('should render message with correct class', () => {
    const mockRetry = jest.fn();
    const { container } = render(<ErrorState message="Something went wrong. Please try again." onRetry={mockRetry} />);

    const message = container.querySelector('.error-state__message');
    expect(message).toBeInTheDocument();
    expect(message).toHaveTextContent('Something went wrong. Please try again.');
  });

  it('should render with custom message', () => {
    const mockRetry = jest.fn();
    render(<ErrorState message="Custom error message" onRetry={mockRetry} />);

    const message = screen.getByText('Custom error message');
    expect(message).toBeInTheDocument();
  });

  it('should have proper structure with icon, message, and button', () => {
    const mockRetry = jest.fn();
    const { container } = render(<ErrorState message="Something went wrong. Please try again." onRetry={mockRetry} />);

    const errorState = container.querySelector('.error-state');
    const icon = container.querySelector('.error-state__icon');
    const message = container.querySelector('.error-state__message');
    const button = container.querySelector('.error-state__retry-button');

    expect(errorState).toContainElement(icon);
    expect(errorState).toContainElement(message);
    expect(errorState).toContainElement(button);
  });

  it('should render SVG icon with correct attributes', () => {
    const mockRetry = jest.fn();
    const { container } = render(<ErrorState message="Something went wrong. Please try again." onRetry={mockRetry} />);

    const svg = container.querySelector('svg');
    expect(svg).toBeInTheDocument();
    expect(svg).toHaveAttribute('width', '64');
    expect(svg).toHaveAttribute('height', '64');
    expect(svg).toHaveAttribute('aria-hidden', 'true');
  });

  it('should handle multiple retry button clicks', () => {
    const mockRetry = jest.fn();
    render(<ErrorState message="Something went wrong. Please try again." onRetry={mockRetry} />);

    const retryButton = screen.getByTestId('error-retry-button');
    fireEvent.click(retryButton);
    fireEvent.click(retryButton);
    fireEvent.click(retryButton);

    expect(mockRetry).toHaveBeenCalledTimes(3);
  });

  it('should handle long error message text', () => {
    const mockRetry = jest.fn();
    const longMessage = 'This is a very long error message that should still be displayed correctly in the error state component without breaking the layout or causing any issues.';
    render(<ErrorState message={longMessage} onRetry={mockRetry} />);

    const message = screen.getByText(longMessage);
    expect(message).toBeInTheDocument();
  });
});
