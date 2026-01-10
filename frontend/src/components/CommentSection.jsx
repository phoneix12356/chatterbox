import React, { useState, useEffect } from 'react';
import commentService from '../services/commentService';
import { Send, Reply, X } from 'lucide-react';

const CommentSection = ({ postId }) => {
  const [comments, setComments] = useState([]);
  const [newComment, setNewComment] = useState('');
  const [replyTo, setReplyTo] = useState(null); // { id, username }
  const [loading, setLoading] = useState(true);
  const [isSubmitting, setIsSubmitting] = useState(false);

  useEffect(() => {
    console.log("DEBUG: CommentSection mounted with postId:", postId);
    fetchComments();
  }, [postId]);

  const fetchComments = async () => {
    try {
      console.log("DEBUG: Fetching comments for postId:", postId);
      const data = await commentService.getCommentsByPostId(postId);
      console.log("DEBUG: Fetched comments data:", data);
      setComments(data);
    } catch (error) {
      console.error("Failed to fetch comments", error);
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!newComment.trim()) return;

    if (!postId) {
      alert("Error: Post ID is missing. Cannot submit comment.");
      console.error("DEBUG: Post ID is missing");
      return;
    }

    console.log("DEBUG: Submitting comment:", { content: newComment, postId, parentCommentId: replyTo?.id });
    setIsSubmitting(true);

    try {
      await commentService.addComment({
        content: newComment,
        postId: postId,
        parentCommentId: replyTo ? replyTo.id : null
      });
      console.log("DEBUG: Comment submitted successfully");
      setNewComment('');
      setReplyTo(null);
      fetchComments(); // Refresh comments
    } catch (error) {
      console.error("Failed to post comment", error);
      alert("Failed to post comment. Please check console for details.");
    } finally {
      setIsSubmitting(false);
    }
  };

  // Helper to build comment tree
  const buildCommentTree = (comments) => {
    const commentMap = {};
    const roots = [];

    // First pass: create map
    comments.forEach(comment => {
      commentMap[comment.id] = { ...comment, replies: [] };
    });

    // Second pass: link children to parents
    comments.forEach(comment => {
      if (comment.parentCommentId && commentMap[comment.parentCommentId]) {
        commentMap[comment.parentCommentId].replies.push(commentMap[comment.id]);
      } else {
        // If parent not found (or is null), treat as root
        if (!comment.parentCommentId) {
             roots.push(commentMap[comment.id]);
        }
      }
    });

    return roots;
  };

  const CommentItem = ({ comment, depth = 0 }) => (
    <div className={`flex gap-3 mb-4 ${depth > 0 ? 'ml-8' : ''}`}>
      <div className="w-8 h-8 rounded-full bg-gray-200 flex items-center justify-center flex-shrink-0 text-gray-600 font-bold">
        {comment.username ? comment.username[0].toUpperCase() : 'U'}
      </div>
      <div className="flex-1">
        <div className="bg-gray-50 p-3 rounded-lg">
          <p className="font-semibold text-sm text-gray-900">{comment.username || 'Unknown User'}</p>
          <p className="text-gray-700 text-sm">{comment.content}</p>
        </div>
        <div className="flex items-center gap-4 mt-1 text-xs text-gray-500">
           <span>{comment.createdAt ? new Date(comment.createdAt).toLocaleDateString() : 'Just now'}</span>
           <button 
             onClick={() => setReplyTo({ id: comment.id, username: comment.username })}
             className="flex items-center gap-1 hover:text-blue-500 font-medium"
           >
             <Reply size={12} /> Reply
           </button>
        </div>
        
        {comment.replies && comment.replies.length > 0 && (
          <div className="mt-3">
            {comment.replies.map(reply => (
              <CommentItem key={reply.id} comment={reply} depth={depth + 1} />
            ))}
          </div>
        )}
      </div>
    </div>
  );

  const rootComments = buildCommentTree(comments);

  return (
    <div className="mt-4 pt-4 border-t border-gray-100">
      <h3 className="font-semibold mb-4 text-gray-800">Comments</h3>
      
      <div className="space-y-4 mb-4 max-h-96 overflow-y-auto pr-2 custom-scrollbar">
        {loading ? (
          <p className="text-center text-gray-500 text-sm">Loading comments...</p>
        ) : rootComments.length > 0 ? (
          rootComments.map(comment => (
            <CommentItem key={comment.id} comment={comment} />
          ))
        ) : (
          <p className="text-center text-gray-500 py-4 text-sm">No comments yet. Be the first!</p>
        )}
      </div>

      <form onSubmit={handleSubmit} className="relative">
        {replyTo && (
          <div className="flex items-center justify-between bg-blue-50 p-2 rounded-t-lg text-xs text-blue-600 border border-blue-100 border-b-0">
            <span>Replying to <span className="font-semibold">@{replyTo.username}</span></span>
            <button type="button" onClick={() => setReplyTo(null)} className="hover:bg-blue-100 rounded p-1">
              <X size={14} />
            </button>
          </div>
        )}
        <div className="flex gap-2">
          <input
            type="text"
            value={newComment}
            onChange={(e) => setNewComment(e.target.value)}
            placeholder={replyTo ? `Reply to @${replyTo.username}...` : "Write a comment..."}
            className={`flex-1 p-2.5 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 text-sm ${replyTo ? 'rounded-tl-none' : ''}`}
          />
          <button 
            type="submit"
            disabled={!newComment.trim() || isSubmitting}
            className="p-2.5 bg-blue-500 text-white rounded-lg hover:bg-blue-600 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
          >
            {isSubmitting ? <div className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin" /> : <Send size={18} />}
          </button>
        </div>
      </form>
    </div>
  );
};

export default CommentSection;
