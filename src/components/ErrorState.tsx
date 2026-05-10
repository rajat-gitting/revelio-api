import React from 'react';
import './ErrorState.css';

interface ErrorStateProps {
  onRetry: () => void;
}

const ErrorState: React.FC<ErrorStateProps> = ({ onRetry }) => {
  return (
    <div className="error-state">
      <div className="error-state-icon">⚠️</div>
      <h2 className="error-state-title">Something went wrong. Please try again.</h2>
      <button className="error-state-button" onClick={onRetry}>
        Retry
      </button>
    </div>
  );
};

export default ErrorState;
