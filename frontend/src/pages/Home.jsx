import React, { useEffect, useState } from 'react';
import Post from '../components/Post';
import CreatePost from '../components/CreatePost';
import api from '../services/api';
import { Loader2 } from 'lucide-react';

const Home = () => {
  const [posts, setPosts] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);

  const fetchPosts = async () => {
    try {
      const response = await api.get('/posts');
      setPosts(response.data);
      setError(null);
    } catch (err) {
      console.error('Failed to fetch posts:', err);
      setError('Failed to load posts. Please try again later.');
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchPosts();
  }, []);

  return (
    <div className="max-w-xl mx-auto">

      <CreatePost onPostCreated={fetchPosts} />


      <div className="space-y-4">
        {isLoading ? (
          <div className="flex justify-center py-10">
            <Loader2 className="w-8 h-8 animate-spin text-gray-400" />
          </div>
        ) : error ? (
          <div className="text-center py-10 text-red-500">
            {error}
          </div>
        ) : posts.length === 0 ? (
          <div className="text-center py-10 text-gray-500">
            No posts yet. Be the first to share something!
          </div>
        ) : (
          posts.map(post => (
            <Post key={post.id} post={post} />
          ))
        )}
      </div>
    </div>
  );
};

export default Home;
