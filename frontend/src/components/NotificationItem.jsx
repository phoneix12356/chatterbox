import React from 'react';
import { UserPlus, Heart, MessageCircle } from 'lucide-react';

const NotificationItem = ({ notification }) => {
  const getIcon = () => {
    switch (notification.type) {
      case 'NEW_FOLLOWER':
        return <UserPlus className="w-5 h-5 text-white" />;
      case 'LIKE':
        return <Heart className="w-5 h-5 text-white" />;
      case 'COMMENT':
        return <MessageCircle className="w-5 h-5 text-white" />;
      default:
        return <UserPlus className="w-5 h-5 text-white" />;
    }
  };

  const getBgColor = () => {
    switch (notification.type) {
      case 'NEW_FOLLOWER':
        return 'bg-indigo-500';
      case 'LIKE':
        return 'bg-pink-500';
      case 'COMMENT':
        return 'bg-blue-500';
      default:
        return 'bg-gray-500';
    }
  };

  return (
    <div className={`p-4 rounded-xl border ${notification.read ? 'bg-white border-gray-100' : 'bg-indigo-50 border-indigo-100'} transition-colors`}>
      <div className="flex items-start space-x-4">
        <div className={`${getBgColor()} p-2 rounded-full flex-shrink-0`}>
          {getIcon()}
        </div>
        <div className="flex-1">
          <p className="text-gray-900 font-medium">
            {notification.message}
          </p>
          <p className="text-xs text-gray-500 mt-1">
            {new Date(notification.createdAt).toLocaleDateString()}
          </p>
        </div>
        {!notification.read && (
          <div className="w-2 h-2 bg-indigo-600 rounded-full mt-2"></div>
        )}
      </div>
    </div>
  );
};

export default NotificationItem;
