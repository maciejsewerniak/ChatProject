package com.example.chat_project.Communication.local.server.threads;


import com.example.chat_project.Communication.local.threads.ListenerThread;
import com.example.chat_project.Communication.local.threads.TransmitterThread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerThread implements Runnable {

    private ServerSocket serverSocket;

    public ServerThread(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    @Override
    public void run() {
        Socket socket;
        while (!Thread.currentThread().isInterrupted()) {

            try {
                System.out.println("Server is accepting connections.");
                socket = serverSocket.accept();
                System.out.println("Connection accepted.");

                TransmitterThread transmitterThread = new TransmitterThread(socket);
                new Thread(transmitterThread).start();
                ListenerThread listenerThread = new ListenerThread(socket);
                new Thread(listenerThread).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
