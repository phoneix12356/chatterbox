import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { MapPin, Link as LinkIcon, Calendar, Grid, Heart, X, User } from 'lucide-react';
import { useSelector } from 'react-redux';
import Button from '../components/Button';
import api from '../services/api';
import Post from '../components/Post';
import followerService from '../services/followerService';
import userService from '../services/userService';

import { uploadFile } from '../services/fileUploadService';

const UserListModal = ({ title, users, onClose, currentUserId }) => {
  const navigate = useNavigate();

  return (
    <div className="fixed inset-0 bg-black/50 z-50 flex items-center justify-center p-4">
      <div className="bg-white rounded-xl shadow-xl w-full max-w-md max-h-[80vh] flex flex-col">
        <div className="p-4 border-b border-gray-100 flex items-center justify-between">
          <h2 className="text-lg font-bold text-gray-900">{title}</h2>
          <button onClick={onClose} className="p-2 hover:bg-gray-100 rounded-full">
            <X className="w-5 h-5 text-gray-500" />
          </button>
        </div>

        <div className="overflow-y-auto p-4 space-y-4">
          {users.length === 0 ? (
            <p className="text-center text-gray-500 py-8">No users found</p>
          ) : (
            users.map(user => (
              <div
                key={user.id}
                className="flex items-center gap-3 p-2 hover:bg-gray-50 rounded-lg cursor-pointer"
                onClick={() => {
                  navigate(`/profile/${user.id}`);
                  onClose();
                }}
              >
                <div className="w-10 h-10 rounded-full bg-gray-200 flex items-center justify-center overflow-hidden">
                  {user.avatar ? (
                    <img src={user.avatar} alt={user.username} className="w-full h-full object-cover" />
                  ) : (
                    <User className="w-6 h-6 text-gray-400" />
                  )}
                </div>
                <div>
                  <h3 className="font-medium text-gray-900">{user.username}</h3>
                  {user.id === currentUserId && (
                    <span className="text-xs text-blue-600 bg-blue-50 px-2 py-0.5 rounded-full">You</span>
                  )}
                </div>
              </div>
            ))
          )}
        </div>
      </div>
    </div>
  );
};

const EditProfileModal = ({ user, onClose, onUpdate }) => {
  const [formData, setFormData] = useState({
    username: user.name,
    email: user.email,
  });
  const [selectedFile, setSelectedFile] = useState(null);
  const [isSubmitting, setIsSubmitting] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setIsSubmitting(true);
    try {
      let avatarUrl = user.avatar;
      if (selectedFile) {
        avatarUrl = await uploadFile(selectedFile);
      }
      await onUpdate({ ...formData, avatar: avatarUrl });
      onClose();
    } catch (error) {
      console.error("Failed to update profile", error);
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="fixed inset-0 bg-black/50 z-50 flex items-center justify-center p-4">
      <div className="bg-white rounded-xl shadow-xl w-full max-w-md flex flex-col">
        <div className="p-4 border-b border-gray-100 flex items-center justify-between">
          <h2 className="text-lg font-bold text-gray-900">Edit Profile</h2>
          <button onClick={onClose} className="p-2 hover:bg-gray-100 rounded-full">
            <X className="w-5 h-5 text-gray-500" />
          </button>
        </div>

        <form onSubmit={handleSubmit} className="p-6 space-y-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Username</label>
            <input
              type="text"
              required
              value={formData.username}
              onChange={(e) => setFormData({ ...formData, username: e.target.value })}
              className="w-full px-4 py-2 border border-gray-200 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-transparent outline-none transition-all"
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Email</label>
            <input
              type="email"
              required
              value={formData.email}
              onChange={(e) => setFormData({ ...formData, email: e.target.value })}
              className="w-full px-4 py-2 border border-gray-200 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-transparent outline-none transition-all"
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Profile Photo</label>
            <div className="mt-1 flex items-center gap-4">
              <div className="w-16 h-16 rounded-full overflow-hidden bg-gray-100 flex-shrink-0">
                {selectedFile ? (
                  <img src={URL.createObjectURL(selectedFile)} alt="Preview" className="w-full h-full object-cover" />
                ) : user.avatar ? (
                  <img src={user.avatar} alt="Current" className="w-full h-full object-cover" />
                ) : (
                  <User className="w-8 h-8 text-gray-400 m-4" />
                )}
              </div>
              <input
                type="file"
                accept="image/*"
                onChange={(e) => setSelectedFile(e.target.files[0])}
                className="block w-full text-sm text-gray-500 file:mr-4 file:py-2 file:px-4 file:rounded-full file:border-0 file:text-sm file:font-semibold file:bg-indigo-50 file:text-indigo-700 hover:file:bg-indigo-100 transition-all"
              />
            </div>
            <p className="text-xs text-gray-500 mt-2">Upload a new photo to change your avatar.</p>
          </div>

          <div className="flex gap-3 pt-4">
            <Button
              type="button"
              variant="outline"
              className="flex-1"
              onClick={onClose}
              disabled={isSubmitting}
            >
              Cancel
            </Button>
            <Button
              type="submit"
              className="flex-1"
              disabled={isSubmitting}
            >
              {isSubmitting ? 'Saving...' : 'Save Changes'}
            </Button>
          </div>
        </form>
      </div>
    </div>
  );
};

const Profile = () => {
  const { userId } = useParams();
  const navigate = useNavigate();
  const { user: currentUser } = useSelector((state) => state.auth);
  const [user, setUser] = useState(null);
  const [posts, setPosts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [activeTab, setActiveTab] = useState('posts');


  const [showFollowers, setShowFollowers] = useState(false);
  const [showFollowing, setShowFollowing] = useState(false);
  const [showEditProfile, setShowEditProfile] = useState(false);
  const [followerList, setFollowerList] = useState([]);
  const [followingList, setFollowingList] = useState([]);

  const profileUserId = userId ? parseInt(userId) : currentUser?.id;

  useEffect(() => {
    if (profileUserId) {
      fetchUserData();
      fetchUserPosts();
    }
  }, [profileUserId]);

  const fetchUserData = async () => {
    try {
      let userData;
      if (profileUserId === currentUser?.id) {

        userData = await userService.getCurrentUser();
      } else {
        userData = await userService.getUserById(profileUserId);
      }

      setUser({
        ...userData,
        name: userData.username,
        username: '@' + userData.username,
        joined: new Date(userData.createdAt).toLocaleDateString(),
        website: '',
        location: 'Unknown',
        bio: 'Welcome to my profile!'
      });
    } catch (error) {
      console.error('Failed to fetch user data:', error);
    }
  };

  const handleUpdateProfile = async (data) => {
    try {
      await userService.updateProfile(currentUser.id, data);

    } catch (error) {
      console.error("Failed to update profile", error);
      throw error;
    }
  };

  const fetchUserPosts = async () => {
    try {
      setLoading(true);
      const response = await api.get(`/posts/user/${profileUserId}`, {
        params: { pageNumber: 0, pageSize: 50 }
      });
      setPosts(response.data);
    } catch (error) {
      console.error('Failed to fetch user posts:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleShowFollowers = async () => {
    try {
      const users = await followerService.getFollowers(profileUserId);
      setFollowerList(users);
      setShowFollowers(true);
    } catch (error) {
      console.error("Failed to fetch followers", error);
    }
  };

  const handleShowFollowing = async () => {
    try {
      const users = await followerService.getFollowing(profileUserId);
      setFollowingList(users);
      setShowFollowing(true);
    } catch (error) {
      console.error("Failed to fetch following", error);
    }
  };

  if (!user) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="text-gray-500">Loading profile...</div>
      </div>
    );
  }

  const isOwnProfile = profileUserId === currentUser?.id;

  return (
    <div className="max-w-4xl mx-auto">
      <div className="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden mb-6">

        <div className="h-48 md:h-64 bg-gradient-to-r from-indigo-500 via-purple-500 to-pink-500 relative">
        </div>


        <div className="px-6 pb-6">
          <div className="relative flex justify-between items-end -mt-12 mb-6">
            <div className="w-32 h-32 rounded-full border-4 border-white overflow-hidden bg-white shadow-md">
              {user.avatar ? (
                <img src={user.avatar} alt={user.name} className="w-full h-full object-cover" />
              ) : (
                <div className="w-full h-full bg-gray-200 flex items-center justify-center">
                  <User className="w-12 h-12 text-gray-400" />
                </div>
              )}
            </div>
            {isOwnProfile && (
              <Button
                variant="outline"
                className="mb-2"
                onClick={() => setShowEditProfile(true)}
              >
                Edit Profile
              </Button>
            )}
          </div>

          <div className="space-y-4">
            <div>
              <h1 className="text-2xl font-bold text-gray-900">{user.name}</h1>
              <p className="text-gray-500 font-medium">{user.username}</p>
            </div>

            {user.bio && <p className="text-gray-800 max-w-2xl">{user.bio}</p>}

            <div className="flex flex-wrap gap-4 text-sm text-gray-500">
              {user.location && (
                <div className="flex items-center space-x-1">
                  <MapPin className="w-4 h-4" />
                  <span>{user.location}</span>
                </div>
              )}
              {user.website && (
                <div className="flex items-center space-x-1">
                  <LinkIcon className="w-4 h-4" />
                  <a href={`https://${user.website}`} className="text-indigo-600 hover:underline">{user.website}</a>
                </div>
              )}
              <div className="flex items-center space-x-1">
                <Calendar className="w-4 h-4" />
                <span>Joined {user.joined}</span>
              </div>
            </div>

            <div className="flex space-x-6 py-4 border-t border-b border-gray-100">
              <div className="flex space-x-1">
                <span className="font-bold text-gray-900">{posts.length}</span>
                <span className="text-gray-500">Posts</span>
              </div>
              <button onClick={handleShowFollowers} className="flex space-x-1 hover:bg-gray-50 rounded px-2 -ml-2 transition-colors">
                <span className="font-bold text-gray-900">{user.followerCount || 0}</span>
                <span className="text-gray-500">Followers</span>
              </button>
              <button onClick={handleShowFollowing} className="flex space-x-1 hover:bg-gray-50 rounded px-2 transition-colors">
                <span className="font-bold text-gray-900">{user.followingCount || 0}</span>
                <span className="text-gray-500">Following</span>
              </button>
            </div>


            <div className="flex border-b border-gray-200">
              <button
                onClick={() => setActiveTab('posts')}
                className={`flex items-center gap-2 px-4 py-3 font-medium text-sm ${activeTab === 'posts'
                  ? 'border-b-2 border-indigo-600 text-indigo-600'
                  : 'text-gray-500 hover:text-gray-700'
                  }`}
              >
                <Grid className="w-4 h-4" />
                Posts
              </button>
              <button
                onClick={() => setActiveTab('liked')}
                className={`flex items-center gap-2 px-4 py-3 font-medium text-sm ${activeTab === 'liked'
                  ? 'border-b-2 border-indigo-600 text-indigo-600'
                  : 'text-gray-500 hover:text-gray-700'
                  }`}
              >
                <Heart className="w-4 h-4" />
                Liked
              </button>
            </div>
          </div>
        </div>
      </div>


      <div>
        {loading ? (
          <div className="text-center py-12 text-gray-500">Loading posts...</div>
        ) : activeTab === 'posts' ? (
          posts.length > 0 ? (
            <div className="space-y-6">
              {posts.map((post) => (
                <Post key={post.id} post={post} />
              ))}
            </div>
          ) : (
            <div className="text-center py-12 bg-white rounded-xl border border-gray-100">
              <p className="text-gray-500">No posts yet</p>
            </div>
          )
        ) : (
          <div className="text-center py-12 bg-white rounded-xl border border-gray-100">
            <p className="text-gray-500">Liked posts feature coming soon</p>
          </div>
        )}
      </div>

      {showEditProfile && (
        <EditProfileModal
          user={user}
          onClose={() => setShowEditProfile(false)}
          onUpdate={handleUpdateProfile}
        />
      )}

      {showFollowers && (
        <UserListModal
          title="Followers"
          users={followerList}
          onClose={() => setShowFollowers(false)}
          currentUserId={currentUser?.id}
        />
      )}

      {showFollowing && (
        <UserListModal
          title="Following"
          users={followingList}
          onClose={() => setShowFollowing(false)}
          currentUserId={currentUser?.id}
        />
      )}
    </div>
  );
};

export default Profile;
