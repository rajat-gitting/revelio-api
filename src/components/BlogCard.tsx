import React from 'react';
import { BlogPost } from '../services/blogService';
import './BlogCard.css';

export interface BlogCardProps {
  post: BlogPost;
}

function getInitials(name: string): string {
  const parts = name.trim().split(/\s+/);
  if (parts.length === 0) return '?';
  if (parts.length === 1) return parts[0].charAt(0).toUpperCase();
  return (parts[0].charAt(0) + parts[parts.length - 1].charAt(0)).toUpperCase();
}

function formatDate(publishedAt: string): string {
  const date = new Date(publishedAt);
  const now = new Date();
  const diffMs = now.getTime() - date.getTime();
  const diffDays = Math.floor(diffMs / (1000 * 60 * 60 * 24));

  if (diffDays <= 30) {
    if (diffDays === 0) return 'just now';
    if (diffDays === 1) return '1 day ago';
    return `${diffDays} days ago`;
  }

  const months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
  return `${months[date.getMonth()]} ${date.getDate()}, ${date.getFullYear()}`;
}

export function BlogCard({ post }: BlogCardProps): JSX.Element {
  const displayTags = post.tags.slice(0, 3);
  const remainingTagsCount = post.tags.length - 3;

  return (
    <a href={`/blog/${post.id}`} className="blog-card" data-testid="blog-card">
      <div className="blog-card__image-container">
        {post.coverImageUrl ? (
          <img
            src={post.coverImageUrl}
            alt={post.title}
            className="blog-card__image"
          />
        ) : (
          <div className="blog-card__image-placeholder" data-testid="blog-card-placeholder">
            <svg
              width="48"
              height="48"
              viewBox="0 0 48 48"
              fill="none"
              xmlns="http://www.w3.org/2000/svg"
              aria-hidden="true"
            >
              <rect width="48" height="48" fill="#cccccc" />
              <text x="24" y="28" fontSize="16" fill="#666666" textAnchor="middle">
                R
              </text>
            </svg>
          </div>
        )}
      </div>
      <div className="blog-card__content">
        <h3 className="blog-card__title">{post.title}</h3>
        <p className="blog-card__excerpt">{post.excerpt}</p>
        <div className="blog-card__footer">
          <div className="blog-card__author">
            {post.author.avatarUrl ? (
              <img
                src={post.author.avatarUrl}
                alt={post.author.name}
                className="blog-card__avatar"
              />
            ) : (
              <div className="blog-card__avatar-initials" data-testid="blog-card-initials">
                {getInitials(post.author.name)}
              </div>
            )}
            <span className="blog-card__author-name">{post.author.name}</span>
          </div>
          <div className="blog-card__meta">
            <div className="blog-card__tags">
              {displayTags.map((tag, index) => (
                <span key={index} className="blog-card__tag">
                  {tag}
                </span>
              ))}
              {remainingTagsCount > 0 && (
                <span className="blog-card__tag-more" data-testid="blog-card-tag-more">
                  +{remainingTagsCount} more
                </span>
              )}
            </div>
            <span className="blog-card__date">{formatDate(post.publishedAt)}</span>
          </div>
        </div>
      </div>
    </a>
  );
}
