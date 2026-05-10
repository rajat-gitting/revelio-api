import React from 'react';
import './EmptyState.css';

const EmptyState: React.FC = () => {
  return (
    <div className="empty-state">
      <div className="empty-state-icon">📝</div>
      <h2 className="empty-state-title">No posts yet. Check back soon.</h2>
    </div>
  );
};

export default EmptyState;
