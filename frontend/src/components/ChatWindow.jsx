import React, { useEffect, useRef, useState } from 'react';
import { useSelector } from 'react-redux';
import chatService from '../services/chatService';
import { Send, Image as ImageIcon, User, Smile } from 'lucide-react';

const ChatWindow = ({ selectedUser, receivedMessages, onlineUsers = {} }) => {
  const { user: currentUser } = useSelector((state) => state.auth);
  const [messages, setMessages] = useState([]);
  const [inputText, setInputText] = useState('');
  const messagesEndRef = useRef(null);


  const isOnline = selectedUser
    ? (onlineUsers[selectedUser.email] ?? selectedUser.isOnline ?? false)
    : false;

  useEffect(() => {
    if (selectedUser) {
      loadHistory();
    }
  }, [selectedUser]);

  useEffect(() => {
    if (receivedMessages && receivedMessages.length > 0) {
      const relevant = receivedMessages.filter(msg =>
        (msg.senderId == selectedUser.id && msg.receiverId == currentUser.id) ||
        (msg.senderId == currentUser.id && msg.receiverId == selectedUser.id)
      );


      if (relevant.length > 0) {
        setMessages(prev => {
          const newMsgs = relevant.filter(r => !prev.some(p => p.id === r.id));
          return [...prev, ...newMsgs];
        });
      }
    }
  }, [receivedMessages, selectedUser, currentUser]);

  const loadHistory = async () => {
    try {
      const history = await chatService.getChatHistory(selectedUser.id);

      setMessages(history.reverse());
    } catch (error) {
      console.error("Failed to load history", error);
    }
  };

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  const handleSend = () => {
    if (!inputText.trim()) return;


    const msg = {
      senderId: currentUser.id,
      receiverId: selectedUser.id,
      messsage: inputText,
      url: ""
    };

    const optimisticMsg = {
      ...msg,
      message: inputText,
      id: Date.now(),
      createdAt: new Date().toISOString()
    };
    setMessages(prev => [...prev, optimisticMsg]);

    chatService.sendMessage(msg);
    setInputText('');
  };

  const handleKeyPress = (e) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      handleSend();
    }
  };

  if (!selectedUser) {
    return (
      <div className="flex-1 flex flex-col items-center justify-center bg-gray-50 text-gray-500">
        <div className="w-16 h-16 bg-gray-200 rounded-full flex items-center justify-center mb-4">
          <User className="w-8 h-8 text-gray-400" />
        </div>
        <p className="text-lg">Select a conversation to start chatting</p>
      </div>
    );
  }

  return (
    <div className="flex-1 flex flex-col h-full bg-white">

      <div className="p-4 border-b border-gray-200 flex items-center justify-center bg-white shadow-sm z-10 sticky top-0">

        <div className="flex items-center gap-3 w-full">
          <div className="w-10 h-10 rounded-full bg-gray-200 overflow-hidden">
            {selectedUser.avatar ? (
              <img src={selectedUser.avatar} alt={selectedUser.username} className="w-full h-full object-cover" />
            ) : (
              <User className="w-6 h-6 text-gray-400 m-auto mt-2" />
            )}
          </div>
          <div>
            <h3 className="font-bold text-gray-900">{selectedUser.username}</h3>
            {isOnline ? (
              <span className="flex items-center gap-1.5 text-xs text-green-600 font-medium">
                <span className="w-2 h-2 rounded-full bg-green-600"></span>
                Online
              </span>
            ) : (
              <span className="flex items-center gap-1.5 text-xs text-gray-500 font-medium">
                <span className="w-2 h-2 rounded-full bg-gray-400"></span>
                Offline
              </span>
            )}
          </div>
        </div>
      </div>


      <div className="flex-1 overflow-y-auto p-4 space-y-4 bg-gray-50 scrollbar-thin scrollbar-thumb-gray-200">
        {messages.map((msg, idx) => {
          const isOwn = msg.senderId == currentUser.id;
          return (
            <div key={msg.id || idx} className={`flex ${isOwn ? 'justify-end' : 'justify-start'}`}>
              <div
                className={`max-w-[70%] px-4 py-2 rounded-2xl shadow-sm ${isOwn
                  ? 'bg-blue-600 text-white rounded-br-none'
                  : 'bg-white text-gray-800 border border-gray-100 rounded-bl-none'
                  }`}
              >

                <p className="whitespace-pre-wrap break-words">{msg.message || msg.messsage}</p>
                <span className={`text-[10px] mt-1 block ${isOwn ? 'text-blue-100' : 'text-gray-400'}`}>
                  {new Date(msg.createdAt).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                </span>
              </div>
            </div>
          );
        })}
        <div ref={messagesEndRef} />
      </div>


      <div className="p-4 bg-white border-t border-gray-200">
        <div className="flex items-end gap-2 bg-gray-50 border border-gray-200 rounded-2xl px-4 py-2 focus-within:border-blue-500 focus-within:ring-1 focus-within:ring-blue-500 transition-all">
          <button className="p-2 text-gray-400 hover:text-blue-500 transition-colors">
            <ImageIcon className="w-5 h-5" />
          </button>
          <textarea
            value={inputText}
            onChange={(e) => setInputText(e.target.value)}
            onKeyDown={handleKeyPress}
            placeholder="Type a message..."
            className="flex-1 bg-transparent border-none focus:ring-0 resize-none py-2 max-h-32 min-h-[40px] text-gray-800 placeholder-gray-400"
            rows="1"
          />
          <button
            onClick={handleSend}
            disabled={!inputText.trim()}
            className={`p-2 rounded-full transition-all ${inputText.trim()
              ? 'bg-blue-600 text-white shadow-md hover:bg-blue-700 transform hover:scale-105 active:scale-95'
              : 'bg-gray-200 text-gray-400 cursor-not-allowed'
              }`}
          >
            <Send className="w-5 h-5" />
          </button>
        </div>
      </div>
    </div>
  );
};

export default ChatWindow;
