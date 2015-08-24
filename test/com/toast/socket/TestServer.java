package com.toast.socket;

import java.util.Scanner;

public class TestServer implements SocketListener
{
   public static void main(final String args[])
   {
      System.out.format("*** Server ***\n\n");
      
      TestServer server = new  TestServer();
      
      SimpleSocket socket = new SimpleSocket();
      socket.addListener(server);
      socket.listen(1025);
      
      Scanner scan = new Scanner(System.in);
      String input = "";
      
      while (!input.equals("quit"))
      {
         System.out.format("Enter text: ");
                  input = scan.next();
         
         socket.write(input);
      }
      
      socket.disconnect();
      scan.close();
   }
   
   TestServer()
   {
      // Nothing here.
   }

   @Override
   public void onConnected()
   {
      System.out.format("Server connected!");
   }

   @Override
   public void onDisconnected()
   {
      System.out.format("Server disconnected!");
      
   }

   @Override
   public void onConnectionFailed()
   {
      System.out.format("Server connection failed!");
   }

   @Override
   public void handleData(String buffer)
   {
      System.out.println(buffer);
   }
}
