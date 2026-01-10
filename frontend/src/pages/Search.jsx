import React, { useState, useEffect } from 'react';
import { useSelector } from 'react-redux';
import { Search as SearchIcon, Users, Loader2 } from 'lucide-react';
import searchService from '../services/searchService';
import UserCard from '../components/UserCard';

const Search = () => {
  const { user: currentUser } = useSelector((state) => state.auth);
  const [query, setQuery] = useState('');
  const [results, setResults] = useState([]);
  const [isLoading, setIsLoading] = useState(false);
  const [hasSearched, setHasSearched] = useState(false);

  useEffect(() => {
    const delayDebounceFn = setTimeout(() => {
      if (query.trim()) {
        handleSearch();
      } else {
        setResults([]);
        setHasSearched(false);
      }
    }, 300);

    return () => clearTimeout(delayDebounceFn);
  }, [query]);

  const handleSearch = async () => {
    if (!query.trim()) return;

    setIsLoading(true);
    setHasSearched(true);
    try {
      const users = await searchService.searchUsers(query);
      setResults(users);
    } catch (error) {
      console.error('Search failed:', error);
      setResults([]);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="max-w-2xl mx-auto">
      {/* Header */}
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-gray-900 mb-2">Search</h1>
        <p className="text-gray-500">Find and connect with people</p>
      </div>

      {/* Search Input */}
      <div className="relative mb-8">
        <div className="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none">
          <SearchIcon className="w-5 h-5 text-gray-400" />
        </div>
        <input
          type="text"
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          placeholder="Search for users..."
          className="w-full pl-12 pr-4 py-4 bg-white border border-gray-200 rounded-2xl text-gray-900 placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent shadow-sm transition-all"
        />
        {isLoading && (
          <div className="absolute inset-y-0 right-0 pr-4 flex items-center">
            <Loader2 className="w-5 h-5 text-blue-500 animate-spin" />
          </div>
        )}
      </div>

      {/* Results */}
      <div className="space-y-4">
        {isLoading && results.length === 0 && (
          <div className="flex flex-col items-center justify-center py-12 text-gray-500">
            <Loader2 className="w-8 h-8 animate-spin mb-4" />
            <p>Searching...</p>
          </div>
        )}

        {!isLoading && hasSearched && results.length === 0 && (
          <div className="flex flex-col items-center justify-center py-12 text-gray-500">
            <Users className="w-16 h-16 mb-4 text-gray-300" />
            <p className="text-lg font-medium">No users found</p>
            <p className="text-sm">Try a different search term</p>
          </div>
        )}

        {!hasSearched && (
          <div className="flex flex-col items-center justify-center py-12 text-gray-400">
            <SearchIcon className="w-16 h-16 mb-4 text-gray-200" />
            <p className="text-lg">Start typing to search for users</p>
          </div>
        )}

        {results.map((user) => (
          <UserCard
            key={user.id}
            user={user}
            currentUserId={currentUser?.id}
          />
        ))}
      </div>
    </div>
  );
};

export default Search;
