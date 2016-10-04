package com.toast.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

public class TestClient implements SocketListener
{
   public static void main(final String args[])
   {
      System.out.format("*** Crispy Sock ***\n");
      System.out.format("***    Client   ***\n\n");
      
      TestClient client = new  TestClient();
      
      SimpleSocket socket = new SimpleSocket();
      socket.addListener(client);
      socket.connect("10.1.11.150", 1025);
      
      System.out.print("Connecting ...");
      while (!socket.isConnected())
      {
         try
         {
            Thread.sleep(1000);
         }
         catch(InterruptedException e)
         {
            
         }
         
         System.out.print(".");
      }
      
      String buffer = "";
      BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
      
      while (!input.equals("quit"))
      {
         System.out.format("> ");
         
         try
         {
            buffer = input.readLine();
         }
         catch (IOException e)
         {
            
         }
         
         socket.write(buffer);
      }
      
      socket.disconnect();
   }
   
   TestClient()
   {
      // Nothing here.
   }

   @Override
   public void onConnected(SimpleSocket socket)
   {
      System.out.format(" connected to %s!\n\n", socket.getSocket().getRemoteSocketAddress().toString());
   }

   @Override
   public void onDisconnected(SimpleSocket socket)
   {
      System.out.format("Client dissconnected!");
      
   }

   @Override
   public void onConnectionFailed(SimpleSocket socket)
   {
      System.out.format("Client connection failed!");
   }

   @Override
   public void handleData(SimpleSocket socket, String buffer)
   {
      System.out.print(buffer);
   }
   
   private static BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
}
