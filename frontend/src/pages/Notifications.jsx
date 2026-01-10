import React, { useEffect, useState } from 'react';
import { useSelector } from 'react-redux';
import { Client } from '@stomp/stompjs';
import { Bell, UserPlus } from 'lucide-react';
import api from '../services/api';
import followerService from '../services/followerService';
import NotificationItem from '../components/NotificationItem';
import FollowRequestItem from '../components/FollowRequestItem';

const Notifications = () => {
  const [notifications, setNotifications] = useState([]);
  const [followRequests, setFollowRequests] = useState([]);
  const [activeTab, setActiveTab] = useState('all'); // 'all' or 'requests'
  const { token } = useSelector((state) => state.auth);

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      const [notifRes, requestsRes] = await Promise.all([
        api.get('/notifications'),
        followerService.getPendingRequests()
      ]);
      setNotifications(notifRes.data);
      setFollowRequests(requestsRes);
    } catch (error) {
      console.error('Failed to fetch data', error);
    }
  };

  useEffect(() => {
    const client = new Client({
      brokerURL: 'ws://localhost:8080/ws/websocket',
      connectHeaders: { Authorization: `Bearer ${token}` },
      onConnect: () => {
        client.subscribe('/user/notification', (message) => {
          const newNotification = JSON.parse(message.body);
          setNotifications((prev) => [newNotification, ...prev]);
        });
      },
      onStompError: (frame) => {
        console.error('Broker reported error: ' + frame.headers['message']);
      },
    });

    client.activate();
    return () => client.deactivate();
  }, [token]);

  const handleRequestAction = (requestId, action) => {
    setFollowRequests((prev) => prev.filter((req) => req.id !== requestId));
  };

  return (
    <div className="max-w-2xl mx-auto space-y-6">
      <div className="flex items-center justify-between mb-6">
        <h2 className="text-2xl font-bold text-gray-900">Notifications</h2>
      </div>

      {/* Tabs */}
      <div className="flex space-x-4 border-b border-gray-200 mb-6">
        <button
          onClick={() => setActiveTab('all')}
          className={`pb-3 px-4 font-medium text-sm transition-colors relative ${activeTab === 'all'
              ? 'text-blue-600 border-b-2 border-blue-600'
              : 'text-gray-500 hover:text-gray-700'
            }`}
        >
          <div className="flex items-center gap-2">
            <Bell className="w-4 h-4" />
            General
            {notifications.length > 0 && (
              <span className="bg-blue-100 text-blue-600 text-xs px-2 py-0.5 rounded-full">
                {notifications.length}
              </span>
            )}
          </div>
        </button>
        <button
          onClick={() => setActiveTab('requests')}
          className={`pb-3 px-4 font-medium text-sm transition-colors relative ${activeTab === 'requests'
              ? 'text-blue-600 border-b-2 border-blue-600'
              : 'text-gray-500 hover:text-gray-700'
            }`}
        >
          <div className="flex items-center gap-2">
            <UserPlus className="w-4 h-4" />
            Follow Requests
            {followRequests.length > 0 && (
              <span className="bg-red-100 text-red-600 text-xs px-2 py-0.5 rounded-full">
                {followRequests.length}
              </span>
            )}
          </div>
        </button>
      </div>

      <div className="space-y-4">
        {activeTab === 'requests' && (
          <div className="space-y-4 animate-in fade-in slide-in-from-bottom-2 duration-300">
            {followRequests.length === 0 ? (
              <div className="text-center py-12 text-gray-500 bg-white rounded-xl border border-gray-100">
                <UserPlus className="w-12 h-12 mx-auto mb-3 text-gray-300" />
                <p>No pending follow requests</p>
              </div>
            ) : (
              followRequests.map((request) => (
                <FollowRequestItem
                  key={request.id}
                  request={request}
                  onActionComplete={handleRequestAction}
                />
              ))
            )}
          </div>
        )}

        {activeTab === 'all' && (
          <div className="space-y-4 animate-in fade-in slide-in-from-bottom-2 duration-300">
            {notifications.length === 0 ? (
              <div className="text-center py-12 text-gray-500 bg-white rounded-xl border border-gray-100">
                <Bell className="w-12 h-12 mx-auto mb-3 text-gray-300" />
                <p>No notifications yet</p>
              </div>
            ) : (
              notifications.map((notification) => (
                <NotificationItem key={notification.id} notification={notification} />
              ))
            )}
          </div>
        )}
      </div>
    </div>
  );
};

export default Notifications;
