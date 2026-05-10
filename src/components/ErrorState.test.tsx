import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import { ErrorState } from './ErrorState';

describe('ErrorState', () => {
  it('should render error state with provided message', () => {
    const mockRetry = jest.fn();
    render(<ErrorState message="Something went wrong. Please try again." onRetry={mockRetry} />);

    const errorState = screen.getByTestId('error-state');
    expect(errorState).toBeInTheDocument();

    const message = screen.getByText('Something went wrong. Please try again.');
    expect(message).toBeInTheDocument();
  });

  it('should render retry button', () => {
    const mockRetry = jest.fn();
    render(<ErrorState message="Error message" onRetry={mockRetry} />);

    const retryButton = screen.getByTestId('error-state-retry-button');
    expect(retryButton).toBeInTheDocument();
    expect(retryButton).toHaveTextContent('Retry');
  });

  it('should call onRetry when retry button is clicked', () => {
    const mockRetry = jest.fn();
    render(<ErrorState message="Error message" onRetry={mockRetry} />);

    const retryButton = screen.getByTestId('error-state-retry-button');
    fireEvent.click(retryButton);

    expect(mockRetry).toHaveBeenCalledTimes(1);
  });

  it('should call onRetry multiple times when retry button is clicked multiple times', () => {
    const mockRetry = jest.fn();
    render(<ErrorState message="Error message" onRetry={mockRetry} />);

    const retryButton = screen.getByTestId('error-state-retry-button');
    fireEvent.click(retryButton);
    fireEvent.click(retryButton);
    fireEvent.click(retryButton);

    expect(mockRetry).toHaveBeenCalledTimes(3);
  });

  it('should render with custom message', () => {
    const mockRetry = jest.fn();
    render(<ErrorState message="Custom error message" onRetry={mockRetry} />);

    const message = screen.getByText('Custom error message');
    expect(message).toBeInTheDocument();
  });

  it('should have correct CSS class on root element', () => {
    const mockRetry = jest.fn();
    const { container } = render(<ErrorState message="Test message" onRetry={mockRetry} />);

    const errorState = container.querySelector('.error-state');
    expect(errorState).toBeInTheDocument();
  });

  it('should have correct CSS class on content element', () => {
    const mockRetry = jest.fn();
    const { container } = render(<ErrorState message="Test message" onRetry={mockRetry} />);

    const content = container.querySelector('.error-state__content');
    expect(content).toBeInTheDocument();
  });

  it('should have correct CSS class on message element', () => {
    const mockRetry = jest.fn();
    const { container } = render(<ErrorState message="Test message" onRetry={mockRetry} />);

    const message = container.querySelector('.error-state__message');
    expect(message).toBeInTheDocument();
  });

  it('should have correct CSS class on retry button', () => {
    const mockRetry = jest.fn();
    const { container } = render(<ErrorState message="Test message" onRetry={mockRetry} />);

    const retryButton = container.querySelector('.error-state__retry-button');
    expect(retryButton).toBeInTheDocument();
  });

  it('should render message as paragraph element', () => {
    const mockRetry = jest.fn();
    render(<ErrorState message="Test message" onRetry={mockRetry} />);

    const message = screen.getByText('Test message');
    expect(message.tagName).toBe('P');
  });

  it('should render retry button as button element', () => {
    const mockRetry = jest.fn();
    render(<ErrorState message="Test message" onRetry={mockRetry} />);

    const retryButton = screen.getByTestId('error-state-retry-button');
    expect(retryButton.tagName).toBe('BUTTON');
  });

  it('should handle empty string message', () => {
    const mockRetry = jest.fn();
    render(<ErrorState message="" onRetry={mockRetry} />);

    const errorState = screen.getByTestId('error-state');
    expect(errorState).toBeInTheDocument();
  });

  it('should handle long message text', () => {
    const mockRetry = jest.fn();
    const longMessage = 'This is a very long error message that should still render correctly in the error state component without breaking the layout or causing any issues.';
    render(<ErrorState message={longMessage} onRetry={mockRetry} />);

    const message = screen.getByText(longMessage);
    expect(message).toBeInTheDocument();
  });

  it('should render multiple error states independently', () => {
    const mockRetry1 = jest.fn();
    const mockRetry2 = jest.fn();
    const { container } = render(
      <>
        <ErrorState message="First error" onRetry={mockRetry1} />
        <ErrorState message="Second error" onRetry={mockRetry2} />
      </>
    );

    const errorStates = container.querySelectorAll('.error-state');
    expect(errorStates).toHaveLength(2);

    expect(screen.getByText('First error')).toBeInTheDocument();
    expect(screen.getByText('Second error')).toBeInTheDocument();
  });

  it('should display the exact message for API failure scenario', () => {
    const mockRetry = jest.fn();
    render(<ErrorState message="Something went wrong. Please try again." onRetry={mockRetry} />);

    const message = screen.getByText('Something went wrong. Please try again.');
    expect(message).toHaveClass('error-state__message');
  });

  it('should not call onRetry when component is rendered', () => {
    const mockRetry = jest.fn();
    render(<ErrorState message="Error message" onRetry={mockRetry} />);

    expect(mockRetry).not.toHaveBeenCalled();
  });

  it('should maintain retry button functionality after multiple renders', () => {
    const mockRetry = jest.fn();
    const { rerender } = render(<ErrorState message="Error 1" onRetry={mockRetry} />);

    const retryButton = screen.getByTestId('error-state-retry-button');
    fireEvent.click(retryButton);
    expect(mockRetry).toHaveBeenCalledTimes(1);

    rerender(<ErrorState message="Error 2" onRetry={mockRetry} />);
    fireEvent.click(retryButton);
    expect(mockRetry).toHaveBeenCalledTimes(2);
  });
});
