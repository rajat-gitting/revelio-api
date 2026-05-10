import React from 'react';
import './ErrorState.css';

export interface ErrorStateProps {
  message: string;
  onRetry: () => void;
}

export function ErrorState({ message, onRetry }: ErrorStateProps): JSX.Element {
  return (
    <div className="error-state" data-testid="error-state">
      <div className="error-state__icon">
        <svg
          width="64"
          height="64"
          viewBox="0 0 64 64"
          fill="none"
          xmlns="http://www.w3.org/2000/svg"
          aria-hidden="true"
        >
          <circle cx="32" cy="32" r="30" stroke="#dc3545" strokeWidth="2" />
          <path
            d="M32 20v16M32 44v4"
            stroke="#dc3545"
            strokeWidth="2"
            strokeLinecap="round"
          />
        </svg>
      </div>
      <p className="error-state__message">{message}</p>
      <button
        className="error-state__retry-button"
        onClick={onRetry}
        data-testid="error-retry-button"
      >
        Try Again
      </button>
    </div>
  );
}
