import api from './api';


const commentService = {
  addComment: async (commentData) => {
    const response = await api.post('/comment/add', {
      content: commentData.content,
      postRef: commentData.postId,
      parentCommentRef: commentData.parentCommentId
    });
    return response.data;
  },

  getCommentsByPostId: async (postId) => {
    const response = await api.get(`/comment/getallcommentonapost?postId=${postId}`);
    return response.data;
  },

  getCommentById: async (id) => {
    const response = await api.get(`/comment/${id}`);
    return response.data;
  }
};

export default commentService;
