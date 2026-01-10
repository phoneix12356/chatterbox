import React, { useState, useRef } from 'react';
import { Image, Send, X, Loader2, Video, Plus } from 'lucide-react';
import Button from './Button';
import { uploadFile } from '../services/fileUploadService';
import api from '../services/api';
import { useSelector } from 'react-redux';

const CreatePost = ({ onPostCreated }) => {
  const [content, setContent] = useState('');
  const [mediaFiles, setMediaFiles] = useState([]);
  const [previewUrls, setPreviewUrls] = useState([]);
  const [isUploading, setIsUploading] = useState(false);
  const fileInputRef = useRef(null);
  const { user } = useSelector((state) => state.auth);

  const handleFileSelect = (e) => {
    const files = Array.from(e.target.files);
    if (files.length > 0) {
      setMediaFiles(prev => [...prev, ...files]);
      const newPreviews = files.map(file => URL.createObjectURL(file));
      setPreviewUrls(prev => [...prev, ...newPreviews]);
    }
  };

  const removeFile = (index) => {
    setMediaFiles(prev => prev.filter((_, i) => i !== index));
    setPreviewUrls(prev => prev.filter((_, i) => i !== index));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!content && mediaFiles.length === 0) return;

    setIsUploading(true);
    try {
      const uploadPromises = mediaFiles.map(file => uploadFile(file));
      const uploadedUrls = await Promise.all(uploadPromises);

      const postData = {
        content: content,
        urls: uploadedUrls,
      };

      await api.post('/posts', postData);

      setContent('');
      setMediaFiles([]);
      setPreviewUrls([]);
      if (onPostCreated) onPostCreated();

    } catch (error) {
      console.error('Failed to create post:', error);
      alert('Failed to create post. Please try again.');
    } finally {
      setIsUploading(false);
    }
  };

  return (
    <div className="bg-white rounded-xl border border-gray-200 p-4 mb-6 shadow-sm">
      <form onSubmit={handleSubmit}>
        <div className="flex space-x-4">
          <div className="w-10 h-10 rounded-full bg-gray-200 flex-shrink-0 overflow-hidden">
            <img
              src={user?.avatar && user.avatar !== "null" ? user.avatar : `https://ui-avatars.com/api/?name=${user?.username || 'User'}&background=random`}
              alt="Avatar"
              className="w-full h-full object-cover"
            />
          </div>
          <div className="flex-1">
            <textarea
              value={content}
              onChange={(e) => setContent(e.target.value)}
              placeholder={`What's on your mind, ${user?.username?.split(' ')[0] || 'User'}?`}
              className="w-full border-none focus:ring-0 resize-none text-base placeholder-gray-400 min-h-[60px] p-0 bg-transparent"
            />


            {previewUrls.length > 0 && (
              <div className="grid grid-cols-2 gap-2 mt-3 mb-2">
                {previewUrls.map((url, index) => (
                  <div key={index} className="relative rounded-lg overflow-hidden bg-gray-100 aspect-square group">
                    <button
                      type="button"
                      onClick={() => removeFile(index)}
                      className="absolute top-2 right-2 bg-black/60 text-white p-1.5 rounded-full hover:bg-black/80 z-10 transition-colors opacity-0 group-hover:opacity-100"
                    >
                      <X className="w-3 h-3" />
                    </button>
                    {mediaFiles[index]?.type.startsWith('video') ? (
                      <video src={url} controls className="w-full h-full object-cover" />
                    ) : (
                      <img src={url} alt={`Preview ${index}`} className="w-full h-full object-cover" />
                    )}
                  </div>
                ))}
              </div>
            )}

            <div className="flex items-center justify-between pt-4 border-t border-gray-100 mt-2">
              <div className="flex space-x-1">
                <button
                  type="button"
                  onClick={() => fileInputRef.current?.click()}
                  className="text-gray-500 hover:text-green-600 hover:bg-green-50 p-2 rounded-full transition-all flex items-center space-x-2"
                >
                  <Image className="w-5 h-5" />
                  <span className="text-xs font-medium hidden sm:inline">Photo</span>
                </button>
                <button
                  type="button"
                  onClick={() => fileInputRef.current?.click()}
                  className="text-gray-500 hover:text-pink-600 hover:bg-pink-50 p-2 rounded-full transition-all flex items-center space-x-2"
                >
                  <Video className="w-5 h-5" />
                  <span className="text-xs font-medium hidden sm:inline">Video</span>
                </button>
                <input
                  type="file"
                  ref={fileInputRef}
                  onChange={handleFileSelect}
                  accept="image/*,video/*"
                  multiple
                  className="hidden"
                />
              </div>

              <Button
                type="submit"
                disabled={(!content.trim() && mediaFiles.length === 0) || isUploading}
                className={`rounded-full px-6 py-1.5 text-sm font-medium transition-all ${(!content.trim() && mediaFiles.length === 0) ? 'opacity-50 cursor-not-allowed' : 'hover:shadow-md'}`}
                isLoading={isUploading}
              >
                Post
              </Button>
            </div>
          </div>
        </div>
      </form>
    </div>
  );
};

export default CreatePost;
