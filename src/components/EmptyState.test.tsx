import React from 'react';
import { render, screen } from '@testing-library/react';
import { EmptyState } from './EmptyState';

describe('EmptyState', () => {
  it('should render empty state with provided message', () => {
    render(<EmptyState message="No posts yet. Check back soon." />);

    const emptyState = screen.getByTestId('empty-state');
    expect(emptyState).toBeInTheDocument();
  });

  it('should display the correct message text', () => {
    render(<EmptyState message="No posts yet. Check back soon." />);

    const message = screen.getByText('No posts yet. Check back soon.');
    expect(message).toBeInTheDocument();
  });

  it('should render icon element', () => {
    const { container } = render(<EmptyState message="No posts yet. Check back soon." />);

    const icon = container.querySelector('.empty-state__icon');
    expect(icon).toBeInTheDocument();
  });

  it('should render message with correct class', () => {
    const { container } = render(<EmptyState message="No posts yet. Check back soon." />);

    const message = container.querySelector('.empty-state__message');
    expect(message).toBeInTheDocument();
    expect(message).toHaveTextContent('No posts yet. Check back soon.');
  });

  it('should render with custom message', () => {
    render(<EmptyState message="Custom empty message" />);

    const message = screen.getByText('Custom empty message');
    expect(message).toBeInTheDocument();
  });

  it('should have proper structure with icon and message', () => {
    const { container } = render(<EmptyState message="No posts yet. Check back soon." />);

    const emptyState = container.querySelector('.empty-state');
    const icon = container.querySelector('.empty-state__icon');
    const message = container.querySelector('.empty-state__message');

    expect(emptyState).toContainElement(icon);
    expect(emptyState).toContainElement(message);
  });

  it('should render SVG icon with correct attributes', () => {
    const { container } = render(<EmptyState message="No posts yet. Check back soon." />);

    const svg = container.querySelector('svg');
    expect(svg).toBeInTheDocument();
    expect(svg).toHaveAttribute('width', '64');
    expect(svg).toHaveAttribute('height', '64');
    expect(svg).toHaveAttribute('aria-hidden', 'true');
  });

  it('should handle empty string message', () => {
    render(<EmptyState message="" />);

    const emptyState = screen.getByTestId('empty-state');
    expect(emptyState).toBeInTheDocument();

    const message = screen.queryByText(/.+/);
    expect(message).not.toBeInTheDocument();
  });

  it('should handle long message text', () => {
    const longMessage = 'This is a very long message that should still be displayed correctly in the empty state component without breaking the layout or causing any issues.';
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
});
