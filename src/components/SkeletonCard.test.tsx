import React from 'react';
import { render, screen } from '@testing-library/react';
import { SkeletonCard } from './SkeletonCard';

describe('SkeletonCard', () => {
  it('should render skeleton card with correct structure', () => {
    render(<SkeletonCard />);

    const skeletonCard = screen.getByTestId('skeleton-card');
    expect(skeletonCard).toBeInTheDocument();
  });

  it('should render skeleton image placeholder', () => {
    const { container } = render(<SkeletonCard />);

    const imageElement = container.querySelector('.skeleton-card__image');
    expect(imageElement).toBeInTheDocument();
  });

  it('should render skeleton title placeholder', () => {
    const { container } = render(<SkeletonCard />);

    const titleElement = container.querySelector('.skeleton-card__title');
    expect(titleElement).toBeInTheDocument();
  });

  it('should render skeleton excerpt with three lines', () => {
    const { container } = render(<SkeletonCard />);

    const excerptLines = container.querySelectorAll('.skeleton-card__excerpt-line');
    expect(excerptLines).toHaveLength(3);
  });

  it('should render skeleton author section with avatar and name', () => {
    const { container } = render(<SkeletonCard />);

    const avatar = container.querySelector('.skeleton-card__avatar');
    const authorName = container.querySelector('.skeleton-card__author-name');

    expect(avatar).toBeInTheDocument();
    expect(authorName).toBeInTheDocument();
  });

  it('should render skeleton meta section with three tag placeholders', () => {
    const { container } = render(<SkeletonCard />);

    const tags = container.querySelectorAll('.skeleton-card__tag');
    expect(tags).toHaveLength(3);
  });

  it('should render skeleton footer with author and meta sections', () => {
    const { container } = render(<SkeletonCard />);

    const footer = container.querySelector('.skeleton-card__footer');
    const author = container.querySelector('.skeleton-card__author');
    const meta = container.querySelector('.skeleton-card__meta');

    expect(footer).toBeInTheDocument();
    expect(author).toBeInTheDocument();
    expect(meta).toBeInTheDocument();
  });

  it('should have correct CSS class on root element', () => {
    const { container } = render(<SkeletonCard />);

    const skeletonCard = container.querySelector('.skeleton-card');
    expect(skeletonCard).toBeInTheDocument();
  });

  it('should render content section', () => {
    const { container } = render(<SkeletonCard />);

    const content = container.querySelector('.skeleton-card__content');
    expect(content).toBeInTheDocument();
  });

  it('should render excerpt section', () => {
    const { container } = render(<SkeletonCard />);

    const excerpt = container.querySelector('.skeleton-card__excerpt');
    expect(excerpt).toBeInTheDocument();
  });

  it('should have short class on third excerpt line', () => {
    const { container } = render(<SkeletonCard />);

    const excerptLines = container.querySelectorAll('.skeleton-card__excerpt-line');
    const thirdLine = excerptLines[2];

    expect(thirdLine).toHaveClass('skeleton-card__excerpt-line--short');
  });

  it('should render multiple skeleton cards independently', () => {
    const { container } = render(
      <>
        <SkeletonCard />
        <SkeletonCard />
        <SkeletonCard />
      </>
    );

    const skeletonCards = container.querySelectorAll('.skeleton-card');
    expect(skeletonCards).toHaveLength(3);
  });
});
