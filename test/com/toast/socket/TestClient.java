package com.toast.socket;

import java.util.Scanner;

public class TestClient implements SocketListener
{
   public static void main(final String args[])
   {
      System.out.format("*** Client ***\n\n");
      
      TestClient client = new  TestClient();
      
      SimpleSocket socket = new SimpleSocket();
      socket.addListener(client);
      socket.connect("10.1.11.150", 1025);
      
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
   
   TestClient()
   {
      // Nothing here.
   }

   @Override
   public void onConnected()
   {
      System.out.format("Client connected!");
   }

   @Override
   public void onDisconnected()
   {
      System.out.format("Client dissconnected!");
      
   }

   @Override
   public void onConnectionFailed()
   {
      System.out.format("Client connection failed!");
   }

   @Override
   public void handleData(String buffer)
   {
      System.out.println(buffer);
   }
}
