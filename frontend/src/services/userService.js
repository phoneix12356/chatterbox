import api from './api';

const userService = {
  updateProfile: async (userId, data) => {
    const response = await api.patch(`/users/${userId}`, data);
    return response.data;
  },

  getCurrentUser: async () => {
    const response = await api.get('/users/me');
    return response.data;
  },

  getUserById: async (userId) => {
    const response = await api.get(`/users/${userId}`);
    return response.data;
  },
};

export default userService;
