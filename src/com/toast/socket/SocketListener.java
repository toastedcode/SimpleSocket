package com.toast.socket;

public interface SocketListener
{
   void onConnected();
   
   void onDisconnected();
   
   void onConnectionFailed();
   
   void handleData(String buffer);
}
