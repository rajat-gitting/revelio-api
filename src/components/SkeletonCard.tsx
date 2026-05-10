import React from 'react';
import './SkeletonCard.css';

const SkeletonCard: React.FC = () => {
  return (
    <div className="skeleton-card">
      <div className="skeleton-image"></div>
      <div className="skeleton-content">
        <div className="skeleton-title"></div>
        <div className="skeleton-excerpt"></div>
        <div className="skeleton-excerpt short"></div>
        <div className="skeleton-meta">
          <div className="skeleton-author">
            <div className="skeleton-avatar"></div>
            <div className="skeleton-author-name"></div>
          </div>
          <div className="skeleton-timestamp"></div>
        </div>
        <div className="skeleton-tags">
          <div className="skeleton-tag"></div>
          <div className="skeleton-tag"></div>
          <div className="skeleton-tag"></div>
        </div>
      </div>
    </div>
  );
};

export default SkeletonCard;
