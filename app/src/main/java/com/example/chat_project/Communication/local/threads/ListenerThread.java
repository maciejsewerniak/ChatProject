package com.example.chat_project.Communication.local.threads;


import com.example.chat_project.Communication.local.queues.InputMessageQueue;
import com.example.chat_project.Communication.local.resources.communication.packages.CommunicationPackage;

import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;


public class ListenerThread implements Runnable {

    private final Socket socket;

    public ListenerThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            System.out.println(getClass().getName() + " Socket has been created. Ready for connection");
            sendOutNewMessages(socket);
        } catch (IOException | InterruptedException e1) {
            e1.printStackTrace();
        }

    }

    private void sendOutNewMessages(Socket socket) throws IOException, InterruptedException {
        InputStream inputStream = socket.getInputStream();
        ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(inputStream));
        BlockingQueue<CommunicationPackage> inputMessageQueueOld = InputMessageQueue.getInstance();
        while (!Thread.interrupted()) {
            try {
                CommunicationPackage communicationPackage = (CommunicationPackage) in.readObject();
                inputMessageQueueOld.put(communicationPackage);
            } catch (ClassNotFoundException |EOFException e){
                e.printStackTrace();
                return;
            }
        }
    }
}
