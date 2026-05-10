export interface Author {
  name: string;
  avatarUrl: string | null;
}

export interface BlogPost {
  id: number;
  title: string;
  excerpt: string;
  coverImageUrl: string | null;
  author: Author;
  tags: string[];
  publishedAt: string;
}

export interface BlogListResponse {
  blogs: BlogPost[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  hasMore: boolean;
}

const API_BASE_URL = process.env.REACT_APP_API_BASE_URL || 'http://localhost:8080';

export async function fetchBlogs(page: number, size: number): Promise<BlogListResponse> {
  const url = `${API_BASE_URL}/api/blogs?page=${page}&size=${size}`;
  
  const response = await fetch(url, {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json',
    },
  });

  if (!response.ok) {
    throw new Error(`Failed to fetch blogs: ${response.status} ${response.statusText}`);
  }

  const data: BlogListResponse = await response.json();
  return data;
}
