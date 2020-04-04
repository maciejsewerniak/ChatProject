package com.example.chat_project.Communication.local.threads;


import com.example.chat_project.Communication.local.queues.OutputMessageQueueCollection;
import com.example.chat_project.Communication.local.queues.TakeableQueue;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;


public class TransmitterThread implements Runnable {
    private final Socket socket;

    public TransmitterThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            System.out.println(getClass().getName() + " Socket has been created. Ready for connection");
            this.sendOutNewMessages(socket);
        } catch (IOException | InterruptedException e1) {
            e1.printStackTrace();
        }

    }

    private void sendOutNewMessages(Socket socket) throws IOException, InterruptedException {
        ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        out.flush();
        TakeableQueue outputMessageQueue = OutputMessageQueueCollection.getInstance().getQueue();
        while (!Thread.interrupted()) {
            out.writeObject(outputMessageQueue.take());
            out.flush();
        }
    }
}
