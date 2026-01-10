import api from './api';

const followerService = {
  sendFollowRequest: async (targetUserId) => {
    const response = await api.post(`/follower/request/${targetUserId}`);
    return response.data;
  },

  acceptFollowRequest: async (requestId) => {
    const response = await api.post(`/follower/accept/${requestId}`);
    return response.data;
  },

  declineFollowRequest: async (requestId) => {
    const response = await api.delete(`/follower/request/${requestId}`);
    return response.data;
  },

  getPendingRequests: async () => {
    const response = await api.get('/follower/pending');
    return response.data;
  },

  getFollowers: async (userId) => {
    const response = await api.get(`/follower/${userId}/followers`);
    return response.data;
  },

  getFollowing: async (userId) => {
    const response = await api.get(`/follower/${userId}/following`);
    return response.data;
  },

  getMutualFollowers: async () => {
    const response = await api.get('/follower/mutual');
    return response.data;
  },
};

export default followerService;
