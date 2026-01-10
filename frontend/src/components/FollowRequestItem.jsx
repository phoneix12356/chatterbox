import React, { useState } from 'react';
import { Check, X, User } from 'lucide-react';
import followerService from '../services/followerService';

const FollowRequestItem = ({ request, onActionComplete }) => {
  const [loading, setLoading] = useState(false);

  const handleAccept = async () => {
    setLoading(true);
    try {
      await followerService.acceptFollowRequest(request.id);
      if (onActionComplete) onActionComplete(request.id, 'accepted');
    } catch (error) {
      console.error('Failed to accept request:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleDecline = async () => {
    setLoading(true);
    try {
      await followerService.declineFollowRequest(request.id);
      if (onActionComplete) onActionComplete(request.id, 'declined');
    } catch (error) {
      console.error('Failed to decline request:', error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="flex items-center justify-between p-4 bg-white rounded-xl shadow-sm border border-gray-100 mb-3">
      <div className="flex items-center gap-3">
        <div className="w-12 h-12 rounded-full bg-gradient-to-br from-purple-400 to-pink-400 flex items-center justify-center overflow-hidden">
          {request.requester.avatar ? (
            <img
              src={request.requester.avatar}
              alt={request.requester.username}
              className="w-full h-full object-cover"
            />
          ) : (
            <User className="w-6 h-6 text-white" />
          )}
        </div>
        <div>
          <h3 className="font-semibold text-gray-900">{request.requester.username}</h3>
          <p className="text-sm text-gray-500">wants to follow you</p>
        </div>
      </div>

      <div className="flex gap-2">
        <button
          onClick={handleAccept}
          disabled={loading}
          className="p-2 rounded-full bg-blue-100 text-blue-600 hover:bg-blue-200 transition-colors"
          title="Accept"
        >
          <Check className="w-5 h-5" />
        </button>
        <button
          onClick={handleDecline}
          disabled={loading}
          className="p-2 rounded-full bg-red-100 text-red-600 hover:bg-red-200 transition-colors"
          title="Decline"
        >
          <X className="w-5 h-5" />
        </button>
      </div>
    </div>
  );
};

export default FollowRequestItem;
