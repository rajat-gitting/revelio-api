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

export interface PagedBlogResponse {
  content: BlogPost[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  hasMore: boolean;
}

export async function fetchBlogs(page: number, size: number): Promise<PagedBlogResponse> {
  const response = await fetch(`/api/blogs?page=${page}&size=${size}`);

  if (!response.ok) {
    throw new Error('Failed to fetch blogs');
  }

  return response.json();
}
