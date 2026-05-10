import React from 'react';
import './EmptyState.css';

export interface EmptyStateProps {
  message: string;
}

export function EmptyState({ message }: EmptyStateProps): JSX.Element {
  return (
    <div className="empty-state" data-testid="empty-state">
      <div className="empty-state__icon">
        <svg
          width="64"
          height="64"
          viewBox="0 0 64 64"
          fill="none"
          xmlns="http://www.w3.org/2000/svg"
          aria-hidden="true"
        >
          <circle cx="32" cy="32" r="30" stroke="#e0e0e0" strokeWidth="2" />
          <path
            d="M32 20v24M20 32h24"
            stroke="#e0e0e0"
            strokeWidth="2"
            strokeLinecap="round"
          />
        </svg>
      </div>
      <p className="empty-state__message">{message}</p>
    </div>
  );
}
