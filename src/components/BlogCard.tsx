import React from 'react';
import { BlogPost } from '../pages/HomePage';
import { formatDistanceToNow, format } from 'date-fns';
import './BlogCard.css';

interface BlogCardProps {
  post: BlogPost;
}

const BlogCard: React.FC<BlogCardProps> = ({ post }) => {
  const getInitials = (name: string): string => {
    const parts = name.trim().split(/\s+/);
    if (parts.length === 1) {
      return parts[0].substring(0, 2).toUpperCase();
    }
    return (parts[0][0] + parts[parts.length - 1][0]).toUpperCase();
  };

  const formatTimestamp = (timestamp: string): string => {
    const date = new Date(timestamp);
    const now = new Date();
    const daysDiff = Math.floor((now.getTime() - date.getTime()) / (1000 * 60 * 60 * 24));

    if (daysDiff <= 30) {
      return formatDistanceToNow(date, { addSuffix: true });
    } else {
      return format(date, 'MMM d, yyyy');
    }
  };

  const displayTags = post.tags.slice(0, 3);
  const remainingTagsCount = post.tags.length - 3;

  return (
    <a href={`/blog/${post.id}`} className="blog-card-link">
      <article className="blog-card">
        <div className="blog-card-image">
          {post.coverImageUrl ? (
            <img src={post.coverImageUrl} alt={post.title} />
          ) : (
            <div className="blog-card-placeholder">
              <div className="placeholder-logo">R</div>
            </div>
          )}
        </div>
        <div className="blog-card-content">
          <h2 className="blog-card-title">{post.title}</h2>
          <p className="blog-card-excerpt">{post.excerpt}</p>
          <div className="blog-card-meta">
            <div className="blog-card-author">
              {post.author.avatarUrl ? (
                <img
                  src={post.author.avatarUrl}
                  alt={post.author.name}
                  className="author-avatar"
                />
              ) : (
                <div className="author-initials">
                  {getInitials(post.author.name)}
                </div>
              )}
              <span className="author-name">{post.author.name}</span>
            </div>
            <span className="blog-card-timestamp">{formatTimestamp(post.publishedAt)}</span>
          </div>
          {post.tags.length > 0 && (
            <div className="blog-card-tags">
              {displayTags.map((tag, index) => (
                <span key={index} className="blog-tag">
                  {tag}
                </span>
              ))}
              {remainingTagsCount > 0 && (
                <span className="blog-tag-more">+{remainingTagsCount} more</span>
              )}
            </div>
          )}
        </div>
      </article>
    </a>
  );
};

export default BlogCard;
