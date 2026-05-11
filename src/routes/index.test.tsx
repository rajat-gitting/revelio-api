import { describe, it, expect } from 'vitest';
import { render } from '@testing-library/react';
import { RouterProvider, createRouter, createRootRoute } from '@tanstack/react-router';
import { Route as IndexRoute } from './index';

const rootRoute = createRootRoute();

const routeTree = rootRoute.addChildren([IndexRoute]);

const router = createRouter({ routeTree });

describe('HomePage background color', () => {
  it('applies background color #836565 to the section element', () => {
    const { container } = render(<RouterProvider router={router} />);
    
    const section = container.querySelector('section');
    expect(section).toBeInTheDocument();
    
    const computedStyle = window.getComputedStyle(section!);
    expect(computedStyle.backgroundColor).toBe('rgb(131, 101, 101)');
  });

  it('section element has the correct CSS class', () => {
    const { container } = render(<RouterProvider router={router} />);
    
    const section = container.querySelector('section');
    expect(section).toHaveClass('section');
  });
});
