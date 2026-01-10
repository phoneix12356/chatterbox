import React, { useState } from 'react';
import { User, Search } from 'lucide-react';

const ChatSidebar = ({ onSelectUser, selectedUserId, onlineUsers = {}, contacts = [], loadingContacts = true }) => {
  const [searchTerm, setSearchTerm] = useState('');

  const filteredUsers = contacts.filter(u =>
    u.username.toLowerCase().includes(searchTerm.toLowerCase())
  );

  return (
    <div className="w-80 border-r border-gray-200 bg-white flex flex-col h-full">
      <div className="p-4 border-b border-gray-100">
        <h2 className="text-xl font-bold mb-4 text-gray-800">Messages</h2>
        <div className="relative">
          <input
            type="text"
            placeholder="Search people..."
            className="w-full pl-10 pr-4 py-2 border border-gray-200 rounded-full bg-gray-50 focus:outline-none focus:border-blue-500 transition-colors"
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
          />
          <Search className="w-4 h-4 text-gray-400 absolute left-3 top-3" />
        </div>
      </div>

      <div className="flex-1 overflow-y-auto">
        {loadingContacts ? (
          <div className="p-4 text-center text-gray-500">Loading contacts...</div>
        ) : filteredUsers.length === 0 ? (
          <div className="p-4 text-center text-gray-500">No mutual followers found</div>
        ) : (
          filteredUsers.map((user) => {
            const isOnline = onlineUsers[user.email] ?? user.isOnline;

            return (
              <div
                key={user.id}
                onClick={() => onSelectUser(user)}
                className={`p-4 flex items-center gap-3 cursor-pointer hover:bg-gray-50 transition-colors ${selectedUserId === user.id ? 'bg-blue-50 border-r-4 border-blue-500' : ''
                  }`}
              >
                <div className="relative">
                  <div className="w-10 h-10 rounded-full bg-gray-200 flex items-center justify-center overflow-hidden">
                    {user.avatar ? (
                      <img src={user.avatar} alt={user.username} className="w-full h-full object-cover" />
                    ) : (
                      <User className="w-6 h-6 text-gray-400" />
                    )}
                  </div>
                  {isOnline && (
                    <span className="absolute bottom-0 right-0 block h-2.5 w-2.5 rounded-full ring-2 ring-white bg-green-500"></span>
                  )}
                </div>
                <div className="flex-1 min-w-0">
                  <h3 className="font-semibold text-gray-900 truncate">{user.username}</h3>
                  <p className="text-sm text-gray-500 truncate">{isOnline ? 'Online' : 'Tap to chat'}</p>
                </div>
              </div>
            );
          })
        )}
      </div>
    </div>
  );
};

export default ChatSidebar;
