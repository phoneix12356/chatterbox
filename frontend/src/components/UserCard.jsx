import React, { useState } from 'react';
import { User, UserPlus, UserCheck, Loader2 } from 'lucide-react';
import followerService from '../services/followerService';

const UserCard = ({ user, currentUserId, onFollowStatusChange }) => {
  const [isLoading, setIsLoading] = useState(false);
  const [followStatus, setFollowStatus] = useState('none'); // 'none', 'pending', 'following'

  const isCurrentUser = user.id === currentUserId;

  const handleFollow = async () => {
    if (isLoading || isCurrentUser) return;

    setIsLoading(true);
    try {
      await followerService.sendFollowRequest(user.id);
      setFollowStatus('pending');
      if (onFollowStatusChange) {
        onFollowStatusChange(user.id, 'pending');
      }
    } catch (error) {
      console.error('Failed to send follow request:', error);
      // Check if already following or request already sent
      if (error.response?.data?.includes('already')) {
        setFollowStatus('pending');
      }
    } finally {
      setIsLoading(false);
    }
  };

  const getButtonContent = () => {
    if (isCurrentUser) {
      return { text: 'You', icon: User, disabled: true, className: 'bg-gray-100 text-gray-500 cursor-not-allowed' };
    }
    if (isLoading) {
      return { text: 'Loading...', icon: Loader2, disabled: true, className: 'bg-gray-200 text-gray-500 cursor-wait' };
    }
    if (followStatus === 'pending') {
      return { text: 'Requested', icon: UserCheck, disabled: true, className: 'bg-yellow-100 text-yellow-700 cursor-default' };
    }
    if (followStatus === 'following') {
      return { text: 'Following', icon: UserCheck, disabled: true, className: 'bg-green-100 text-green-700 cursor-default' };
    }
    return { text: 'Follow', icon: UserPlus, disabled: false, className: 'bg-blue-600 text-white hover:bg-blue-700 cursor-pointer' };
  };

  const buttonConfig = getButtonContent();
  const ButtonIcon = buttonConfig.icon;

  return (
    <div className="flex items-center justify-between p-4 bg-white rounded-xl shadow-sm border border-gray-100 hover:shadow-md transition-shadow">
      <div className="flex items-center gap-3">
        <div className="w-12 h-12 rounded-full bg-gradient-to-br from-purple-400 to-pink-400 flex items-center justify-center overflow-hidden">
          {user.avatar ? (
            <img src={user.avatar} alt={user.username} className="w-full h-full object-cover" />
          ) : (
            <User className="w-6 h-6 text-white" />
          )}
        </div>
        <div>
          <h3 className="font-semibold text-gray-900">{user.username}</h3>
          {user.bio && <p className="text-sm text-gray-500 truncate max-w-[200px]">{user.bio}</p>}
        </div>
      </div>

      <button
        onClick={handleFollow}
        disabled={buttonConfig.disabled}
        className={`flex items-center gap-2 px-4 py-2 rounded-full text-sm font-medium transition-all ${buttonConfig.className}`}
      >
        <ButtonIcon className={`w-4 h-4 ${isLoading ? 'animate-spin' : ''}`} />
        {buttonConfig.text}
      </button>
    </div>
  );
};

export default UserCard;
