import React, { useState, useEffect } from 'react';
import { fetchBlogs, BlogPost, BlogListResponse } from '../services/blogService';
import { BlogCard } from '../components/BlogCard';
import { SkeletonCard } from '../components/SkeletonCard';
import { EmptyState } from '../components/EmptyState';
import { ErrorState } from '../components/ErrorState';
import './HomePage.css';

type LoadingState = 'idle' | 'loading' | 'success' | 'error';

export function HomePage(): JSX.Element {
  const [posts, setPosts] = useState<BlogPost[]>([]);
  const [loadingState, setLoadingState] = useState<LoadingState>('loading');
  const [currentPage, setCurrentPage] = useState<number>(0);
  const [hasMore, setHasMore] = useState<boolean>(false);
  const [isLoadingMore, setIsLoadingMore] = useState<boolean>(false);

  const loadPosts = async (page: number, append: boolean = false) => {
    try {
      if (!append) {
        setLoadingState('loading');
      } else {
        setIsLoadingMore(true);
      }

      const response: BlogListResponse = await fetchBlogs(page, 10);

      if (append) {
        setPosts((prevPosts) => [...prevPosts, ...response.blogs]);
      } else {
        setPosts(response.blogs);
      }

      setHasMore(response.hasMore);
      setCurrentPage(page);
      setLoadingState('success');
    } catch (error) {
      setLoadingState('error');
    } finally {
      setIsLoadingMore(false);
    }
  };

  useEffect(() => {
    loadPosts(0);
  }, []);

  const handleRetry = () => {
    loadPosts(0);
  };

  const handleLoadMore = () => {
    loadPosts(currentPage + 1, true);
  };

  if (loadingState === 'loading') {
    return (
      <div className="home-page" data-testid="home-page">
        <div className="home-page__grid">
          <SkeletonCard />
          <SkeletonCard />
          <SkeletonCard />
        </div>
      </div>
    );
  }

  if (loadingState === 'error') {
    return (
      <div className="home-page" data-testid="home-page">
        <ErrorState
          message="Something went wrong. Please try again."
          onRetry={handleRetry}
        />
      </div>
    );
  }

  if (posts.length === 0) {
    return (
      <div className="home-page" data-testid="home-page">
        <EmptyState message="No posts yet. Check back soon." />
      </div>
    );
  }

  return (
    <div className="home-page" data-testid="home-page">
      <div className="home-page__grid">
        {posts.map((post) => (
          <BlogCard key={post.id} post={post} />
        ))}
      </div>
      {hasMore && (
        <div className="home-page__load-more">
          <button
            className="home-page__load-more-button"
            onClick={handleLoadMore}
            disabled={isLoadingMore}
            data-testid="load-more-button"
          >
            {isLoadingMore ? (
              <>
                <span className="home-page__spinner" data-testid="load-more-spinner" />
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
}
