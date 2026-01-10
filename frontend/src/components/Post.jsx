import React, { useState } from 'react';
import { Heart, MessageCircle, Send, Bookmark, MoreHorizontal } from 'lucide-react';

const Post = ({ post }) => {
  const [isLiked, setIsLiked] = useState(false);


  const user = post.user || { name: 'User ' + (post.UserId || ''), avatar: '' };
  const imageUrl = post.urls && post.urls.length > 0 ? post.urls[0] : post.image;
  const likes = post.likeCount !== undefined ? post.likeCount : post.likes;
  const comments = post.commentCount !== undefined ? post.commentCount : post.comments;
  const content = post.content;

  return (
    <div className="bg-white border-b border-gray-200 md:border md:rounded-lg mb-4 md:mb-8">

      <div className="p-3 flex items-center justify-between">
        <div className="flex items-center space-x-3">
          <div className="w-8 h-8 rounded-full bg-gradient-to-tr from-yellow-400 to-pink-600 p-[2px]">
            <div className="w-full h-full rounded-full bg-white p-[2px]">
              <img
                src={user.avatar || `https://ui-avatars.com/api/?name=${user.name}&background=random`}
                alt={user.name}
                className="w-full h-full rounded-full object-cover"
              />
            </div>
          </div>
          <span className="font-semibold text-sm text-gray-900">{user.name}</span>
        </div>
        <button className="text-gray-900">
          <MoreHorizontal className="w-5 h-5" />
        </button>
      </div>


      <div className="w-full bg-black aspect-square flex items-center justify-center overflow-hidden">
        {imageUrl && (
          imageUrl.includes('video') || imageUrl.endsWith('.mp4') ? (
            <video src={imageUrl} controls className="w-full h-full object-contain" />
          ) : (
            <img
              src={imageUrl}
              alt="Post content"
              className="w-full h-full object-cover"
            />
          )
        )}
      </div>


      <div className="p-3">
        <div className="flex items-center justify-between mb-3">
          <div className="flex items-center space-x-4">
            <button
              onClick={() => setIsLiked(!isLiked)}
              className={`transition-transform active:scale-90 ${isLiked ? 'text-red-500' : 'text-gray-900 hover:text-gray-600'}`}
            >
              <Heart className={`w-7 h-7 ${isLiked ? 'fill-current' : ''}`} />
            </button>
            <button className="text-gray-900 hover:text-gray-600">
              <MessageCircle className="w-7 h-7" />
            </button>
            <button className="text-gray-900 hover:text-gray-600">
              <Send className="w-7 h-7" />
            </button>
          </div>
          <button className="text-gray-900 hover:text-gray-600">
            <Bookmark className="w-7 h-7" />
          </button>
        </div>


        <div className="font-semibold text-sm mb-2">
          {likes} likes
        </div>


        <div className="text-sm mb-2">
          <span className="font-semibold mr-2">{user.name}</span>
          <span className="text-gray-900">{content}</span>
        </div>


        {comments > 0 && (
          <button className="text-gray-500 text-sm mb-2">
            View all {comments} comments
          </button>
        )}


        {post.timeAgo && (
          <div className="text-xs text-gray-400 uppercase tracking-wide">
            {post.timeAgo}
          </div>
        )}
      </div>
    </div>
  );
};

export default Post;
