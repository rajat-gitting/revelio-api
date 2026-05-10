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

export interface BlogsResponse {
  blogs: BlogPost[];
  totalCount: number;
  page: number;
  size: number;
}

const API_BASE_URL = process.env.REACT_APP_API_BASE_URL || 'http://localhost:8080';

export async function fetchBlogs(page: number, size: number): Promise<BlogsResponse> {
  if (page < 0) {
    throw new Error('Page number must be non-negative');
  }
  if (size <= 0) {
    throw new Error('Page size must be positive');
  }

  const url = new URL(`${API_BASE_URL}/api/blogs`);
  url.searchParams.append('page', page.toString());
  url.searchParams.append('size', size.toString());

  const response = await fetch(url.toString(), {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json',
    },
  });

  if (!response.ok) {
    throw new Error(`Failed to fetch blogs: ${response.status} ${response.statusText}`);
  }

  const data = await response.json();
  return data;
}
