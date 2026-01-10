import { Client } from '@stomp/stompjs';
import api from './api';

class ChatService {
  constructor() {
    this.client = null;
    this.connected = false;
    this.messageCallbacks = [];
  }

  async connect(onMessageReceived) {

    if (this.client) {
        await this.disconnect();
    }

    const token = localStorage.getItem('token');
    
    this.client = new Client({
      brokerURL: 'ws://localhost:8080/ws/websocket',
      connectHeaders: {
        Authorization: `Bearer ${token}`,
      },
      debug: function (str) {
        console.log(str);
      },
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
    });

    this.client.onConnect = (frame) => {
      console.log('Connected: ' + frame);
      this.connected = true;
      
      this.client.subscribe('/user/queue/chat', (message) => {
        if (message.body) {
            try {
                onMessageReceived(message.body);
            } catch (e) {
                console.error("Error parsing message", e);
            }
        }
      });
      

      if (this.onConnectCallback) {
          this.onConnectCallback();
      }
    };

    this.client.onStompError = (frame) => {
      console.error('Broker reported error: ' + frame.headers['message']);
      console.error('Additional details: ' + frame.body);
    };


    this.client.activate();
  }

  subscribeToPresence(callback) {
      if (this.client && this.connected) {
          return this.client.subscribe('/topic/presence', (message) => {
              if (message.body) {
                  try {
                       const statusUpdate = JSON.parse(message.body);
                       callback(statusUpdate);
                  } catch (e) {
                      console.error("Error parsing presence", e);
                  }
              }
          });
      }
      return null;
  }

  async disconnect() {
    if (this.client) {
      try {
          await this.client.deactivate();
      } catch (err) {
          console.error("Error deactivating client:", err);
      }
      this.client = null;
      this.connected = false;
    }
  }

  sendMessage(message) {

    if (this.client && this.connected) {
      this.client.publish({
        destination: '/app/send',
        body: JSON.stringify(message),
      });
    } else {
        console.error("Client not connected");
    }
  }

  async getChatHistory(userId) {
    const response = await api.get(`/chat/history/${userId}`);
    return response.data;
  }
}

export default new ChatService();
