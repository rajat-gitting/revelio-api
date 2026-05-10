import React, { useState, useEffect } from 'react';
import { fetchBlogs } from '../services/blogService';
import BlogCard from '../components/BlogCard';
import SkeletonCard from '../components/SkeletonCard';
import EmptyState from '../components/EmptyState';
import ErrorState from '../components/ErrorState';
import './HomePage.css';

export interface BlogPost {
  id: number;
  title: string;
  excerpt: string;
  coverImageUrl: string | null;
  author: {
    name: string;
    avatarUrl: string | null;
  };
  tags: string[];
  publishedAt: string;
}

interface PagedResponse {
  content: BlogPost[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  hasMore: boolean;
}

const HomePage: React.FC = () => {
  const [posts, setPosts] = useState<BlogPost[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<boolean>(false);
  const [loadingMore, setLoadingMore] = useState<boolean>(false);
  const [currentPage, setCurrentPage] = useState<number>(0);
  const [hasMore, setHasMore] = useState<boolean>(false);
  const pageSize = 10;

  const loadPosts = async (page: number, append: boolean = false) => {
    try {
      if (append) {
        setLoadingMore(true);
      } else {
        setLoading(true);
      }
      setError(false);

      const response: PagedResponse = await fetchBlogs(page, pageSize);

      if (append) {
        setPosts((prevPosts) => [...prevPosts, ...response.content]);
      } else {
        setPosts(response.content);
      }

      setHasMore(response.hasMore);
      setCurrentPage(page);
    } catch (err) {
      setError(true);
    } finally {
      setLoading(false);
      setLoadingMore(false);
    }
  };

  useEffect(() => {
    loadPosts(0);
  }, []);

  const handleLoadMore = () => {
    loadPosts(currentPage + 1, true);
  };

  const handleRetry = () => {
    setPosts([]);
    setCurrentPage(0);
    loadPosts(0);
  };

  if (loading) {
    return (
      <div className="home-page">
        <div className="blog-grid">
          <SkeletonCard />
          <SkeletonCard />
          <SkeletonCard />
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="home-page">
        <ErrorState onRetry={handleRetry} />
      </div>
    );
  }

  if (posts.length === 0) {
    return (
      <div className="home-page">
        <EmptyState />
      </div>
    );
  }

  return (
    <div className="home-page">
      <div className="blog-grid">
        {posts.map((post) => (
          <BlogCard key={post.id} post={post} />
        ))}
      </div>
      {hasMore && (
        <div className="load-more-container">
          <button
            className="load-more-button"
            onClick={handleLoadMore}
            disabled={loadingMore}
          >
            {loadingMore ? (
              <>
                <span className="spinner"></span>
                Loading...
              </>
            ) : (
              'Load More'
            )}
          </button>
        </div>
      )}
    </div>
  );
};

export default HomePage;
