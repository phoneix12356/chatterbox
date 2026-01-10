import React, { useEffect, useState } from 'react';
import ChatSidebar from '../components/ChatSidebar';
import ChatWindow from '../components/ChatWindow';
import chatService from '../services/chatService';
import followerService from '../services/followerService';

const Chat = () => {
  const [selectedUser, setSelectedUser] = useState(null);
  const [receivedMessages, setReceivedMessages] = useState([]);
  const [onlineUsers, setOnlineUsers] = useState({});
  const [contacts, setContacts] = useState([]);
  const [loadingContacts, setLoadingContacts] = useState(true);


  useEffect(() => {
    const fetchContacts = async () => {
      try {
        const mutuals = await followerService.getMutualFollowers();
        setContacts(mutuals);


        const initialOnlineStatus = {};
        mutuals.forEach(user => {
          if (user.email) {
            initialOnlineStatus[user.email] = user.isOnline ?? false;
          }
        });
        setOnlineUsers(initialOnlineStatus);
      } catch (error) {
        console.error("Failed to fetch contacts", error);
      } finally {
        setLoadingContacts(false);
      }
    };
    fetchContacts();
  }, []);

  useEffect(() => {
    chatService.onConnectCallback = () => {
      chatService.subscribeToPresence((statusUpdate) => {
        console.log("Presence update:", statusUpdate);
        setOnlineUsers(prev => ({
          ...prev,
          [statusUpdate.email]: statusUpdate.status === 'ONLINE'
        }));
      });
    };

    chatService.connect((messageBody) => {

      try {

        const parsed = typeof messageBody === 'string' ? JSON.parse(messageBody) : messageBody;
        setReceivedMessages(prev => [...prev, parsed]);
      } catch (e) {
        console.error("Failed to parse incoming message", e);
      }
    });

    return () => {
      chatService.disconnect();
    };
  }, []);

  const handleSelectUser = (user) => {
    setSelectedUser(user);

  };

  return (
    <div className="flex h-[calc(100vh-64px)] w-full bg-white overflow-hidden shadow-lg rounded-lg border border-gray-200 mt-4 max-w-7xl mx-auto">
      <ChatSidebar
        onSelectUser={handleSelectUser}
        selectedUserId={selectedUser?.id}
        onlineUsers={onlineUsers}
        contacts={contacts}
        loadingContacts={loadingContacts}
      />
      <ChatWindow
        selectedUser={selectedUser}
        receivedMessages={receivedMessages}
        onlineUsers={onlineUsers}
      />
    </div>
  );
};

export default Chat;
