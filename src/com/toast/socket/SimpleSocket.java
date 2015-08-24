package com.toast.socket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.IllegalBlockingModeException;

public class SimpleSocket
{
   // **************************************************************************
   //                             Public
   // **************************************************************************
   
   SimpleSocket()
   {
   }
   
   public void addListener(SocketListener listener)
   {
      if (!listeners.contains(listener))
      {
         listeners.add(listener);
      }
   }
   
   public void removeListener(SocketListener listener)
   {
      listeners.remove(listener);
   }
   
   public void connect(final String address, final int port)
   {
      if (isConnected() == true)
      {
         disconnect();
      }
      
      isServer = false;
      
      // Create the socket listener on a separate thread.
      socketThread = new Thread()
      {
         @Override
         public void run()
         {
            while ((clientSocket == null) || (clientSocket.isConnected() == false))
            {
               try
               {
                  clientSocket = new Socket(address, port);
               }
               catch (IOException e)
               {
                  e.printStackTrace();
               }
               
               try
               {
                  Thread.sleep(250);
               } 
               catch (InterruptedException e)
               {
                  e.printStackTrace();
               }
               
            }  // end while (clientSocket.isConnected() == false)
            
            
            isConnected = true;
            
            onConnected();
            
            //
            // Now start listening for input.
            //
            
            while (isConnected)
            {
               try
               {
                  String buffer = input.readLine();
                  handleData(buffer);
               }
               catch (IOException e)
               {
                  disconnect();
               }
            }

         }  // end public void run()
      };  // end thread
      
      // Go!
      socketThread.start(); 
   }
   
   public void listen(final int port)
   {
      if (isConnected() == true)
      {
         disconnect();
      }
      
      isServer = true;
      
      // Create the socket listener on a separate thread.
      socketThread = new Thread()
      {
         @Override
         public void run()
         {
            try
            {
               serverSocket = new ServerSocket(port);
               clientSocket = serverSocket.accept();
               
               //
               // Blocks until connection is made.
               //
               
               isConnected = true;
               
               onConnected();
               
               //
               // Now start listening for input.
               //
               
               while (isConnected)
               {
                  try
                  {
                     String buffer = input.readLine();
                     handleData(buffer);
                  }
                  catch (IOException e)
                  {
                     disconnect();
                  }
               }
            }
            catch (SecurityException | IllegalBlockingModeException | IOException e)
            {
               e.printStackTrace();
               
               isConnected = false;
               
               onConnectionFailed();
            }
         }
      };
      
      // Go!
      socketThread.start();  
   }
   
   public void disconnect()
   {
      if (isConnected)
      {
         try
         {
            if (isServer)
            {
               serverSocket.close();
            }
            
            clientSocket.close();
            
            socketThread.stop();  // TODO: Research.
            
            onDisconnected();
         }
         catch (IOException e)
         {
            e.printStackTrace();
         }
      }
   }
   
   public boolean isConnected()
   {
      return (isConnected);
   }
   
   public void write(String buffer)
   {
      if (output != null)
      {
         try
         {
            output.writeChars(buffer);
            output.writeChar('\n');
         }
         catch (IOException e)
         {
            e.printStackTrace();
         }
      }
   }
   
   // **************************************************************************
   //                             Private
   // **************************************************************************
   
   private void onConnected()
   {
      System.out.format("Connected to %s\n",  clientSocket.getInetAddress());
      
      try
      {
         input = new DataInputStream(clientSocket.getInputStream());
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
      
      try
      {
         output = new DataOutputStream(clientSocket.getOutputStream());
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
      
      for (SocketListener listener : listeners)
      {
         listener.onConnected();         
      }
   }
   
   private void onConnectionFailed()
   {
      for (SocketListener listener : listeners)
      {
         listener.onConnectionFailed();         
      }
   }
   
   private void onDisconnected()
   {
      for (SocketListener listener : listeners)
      {
         listener.onDisconnected();         
      }
   }
   
   private void handleData(String buffer)
   {
      for (SocketListener listener : listeners)
      {
         listener.handleData(buffer);         
      }
   }
   
   private Thread socketThread;
   
   private boolean isServer = false;
   
   private static Socket clientSocket;
   
   private static ServerSocket serverSocket;
   
   private boolean isConnected = false;
   
   DataInputStream input;
   
   DataOutputStream output;
   
   List<SocketListener> listeners = new ArrayList<>(); 
}
