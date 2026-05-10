import React from 'react';
import './ErrorState.css';

export interface ErrorStateProps {
  message: string;
  onRetry: () => void;
}

export function ErrorState({ message, onRetry }: ErrorStateProps): JSX.Element {
  return (
    <div className="error-state" data-testid="error-state">
      <div className="error-state__content">
        <p className="error-state__message">{message}</p>
        <button
          className="error-state__retry-button"
          onClick={onRetry}
          data-testid="error-state-retry-button"
        >
          Retry
        </button>
      </div>
    </div>
  );
}
