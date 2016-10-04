package com.toast.socket;

public interface SocketListener
{
   void onConnected(SimpleSocket socket);
   
   void onDisconnected(SimpleSocket socket);
   
   void onConnectionFailed(SimpleSocket socket);
   
   void handleData(SimpleSocket socket, String buffer);
}
