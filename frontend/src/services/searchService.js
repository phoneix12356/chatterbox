import api from './api';

/**
 * Search API Service
 */
const searchService = {
  /**
   * Search users by username
   * @param {string} query - Search query
   * @returns {Promise<Array>} List of users matching query
   */
  searchUsers: async (query) => {
    const response = await api.get('/search/users', { params: { query } });
    return response.data;
  },
};

export default searchService;
