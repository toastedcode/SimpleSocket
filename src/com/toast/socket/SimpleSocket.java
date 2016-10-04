package com.toast.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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
   
   public SimpleSocket()
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
            while (true)
            {
               while ((clientSocket == null) || (clientSocket.isConnected() == false))
               {
                  try
                  {
                     clientSocket = new Socket(address, port);
                  }
                  catch (IOException e)
                  {
                     // Ignore and keep trying ...
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
               
               // Start listening for input.
               while (isConnected)
               {
                  read();
                  
                  //
                  // Blocks until EOL encountered.
                  //
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
               while (true)
               {
                  // Attempt to connect.
                  serverSocket = new ServerSocket(port);
                  clientSocket = serverSocket.accept();
                  
                  //
                  // Blocks until connection is made.
                  //
                  
                  isConnected = true;
                  
                  onConnected();
                  
                  // Start listening for input.
                  while (isConnected)
                  {
                     read();
                     
                     //
                     // Blocks until EOL encountered.
                     //
                  }
               }
            }
            catch (SecurityException | IllegalBlockingModeException | IOException e)
            {
               e.printStackTrace();
               
               isConnected = false;
               
               onConnectionFailed();
            }
            
         }  // end public void run()
      };  // end thread
      
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
            
            //socketThread.stop();  // TODO: Research.
            
            isConnected = false;
            
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
   
   public Socket getSocket()
   {
      return (clientSocket);
   }
   
   public void write(String buffer)
   {
      if (output != null)
      {
         output.write(buffer);
         output.write(EOL_CHARACTER);
         output.flush();
      }
   }
   
   // **************************************************************************
   //                             Private
   // **************************************************************************
   
   private void onConnected()
   {
      try
      {
         output = new PrintWriter(clientSocket.getOutputStream(), true);
         
         input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
      
      for (SocketListener listener : listeners)
      {
         listener.onConnected(this);         
      }
   }
   
   private void onConnectionFailed()
   {
      for (SocketListener listener : listeners)
      {
         listener.onConnectionFailed(this);         
      }
   }
   
   private void onDisconnected()
   {
      for (SocketListener listener : listeners)
      {
         listener.onDisconnected(this);         
      }
   }
   
   private void read()
   {
      try
      {
         String buffer = input.readLine();
         
         if (!buffer.isEmpty())
         {
            handleData(buffer);
         }
      }
      catch (Exception e)
      {
         disconnect();
      }
   }
   
   private void handleData(String buffer)
   {
      for (SocketListener listener : listeners)
      {
         listener.handleData(this, buffer);         
      }
   }
   
   private static final char EOL_CHARACTER = '\n';
   
   private Thread socketThread;
   
   private boolean isServer = false;
   
   private static Socket clientSocket;
   
   private static ServerSocket serverSocket;
   
   private boolean isConnected = false;
   
   PrintWriter output;
   
   BufferedReader input;
   
   List<SocketListener> listeners = new ArrayList<>(); 
}
