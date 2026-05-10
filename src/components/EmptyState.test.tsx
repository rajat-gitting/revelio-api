import React from 'react';
import { render, screen } from '@testing-library/react';
import { EmptyState } from './EmptyState';

describe('EmptyState', () => {
  it('should render empty state with provided message', () => {
    render(<EmptyState message="No posts yet. Check back soon." />);

    const emptyState = screen.getByTestId('empty-state');
    expect(emptyState).toBeInTheDocument();

    const message = screen.getByText('No posts yet. Check back soon.');
    expect(message).toBeInTheDocument();
  });

  it('should render with custom message', () => {
    render(<EmptyState message="Custom empty message" />);

    const message = screen.getByText('Custom empty message');
    expect(message).toBeInTheDocument();
  });

  it('should have correct CSS class on root element', () => {
    const { container } = render(<EmptyState message="Test message" />);

    const emptyState = container.querySelector('.empty-state');
    expect(emptyState).toBeInTheDocument();
  });

  it('should have correct CSS class on content element', () => {
    const { container } = render(<EmptyState message="Test message" />);

    const content = container.querySelector('.empty-state__content');
    expect(content).toBeInTheDocument();
  });

  it('should have correct CSS class on message element', () => {
    const { container } = render(<EmptyState message="Test message" />);

    const message = container.querySelector('.empty-state__message');
    expect(message).toBeInTheDocument();
  });

  it('should render message as paragraph element', () => {
    render(<EmptyState message="Test message" />);

    const message = screen.getByText('Test message');
    expect(message.tagName).toBe('P');
  });

  it('should handle empty string message', () => {
    render(<EmptyState message="" />);

    const emptyState = screen.getByTestId('empty-state');
    expect(emptyState).toBeInTheDocument();
  });

  it('should handle long message text', () => {
    const longMessage = 'This is a very long message that should still render correctly in the empty state component without breaking the layout or causing any issues.';
    render(<EmptyState message={longMessage} />);

    const message = screen.getByText(longMessage);
    expect(message).toBeInTheDocument();
  });

  it('should render multiple empty states independently', () => {
    const { container } = render(
      <>
        <EmptyState message="First message" />
        <EmptyState message="Second message" />
      </>
    );

    const emptyStates = container.querySelectorAll('.empty-state');
    expect(emptyStates).toHaveLength(2);

    expect(screen.getByText('First message')).toBeInTheDocument();
    expect(screen.getByText('Second message')).toBeInTheDocument();
  });

  it('should display the exact message for no posts scenario', () => {
    render(<EmptyState message="No posts yet. Check back soon." />);

    const message = screen.getByText('No posts yet. Check back soon.');
    expect(message).toHaveClass('empty-state__message');
  });
});
