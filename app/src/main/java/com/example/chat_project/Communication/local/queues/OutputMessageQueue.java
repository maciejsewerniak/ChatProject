package com.example.chat_project.Communication.local.queues;


import com.example.chat_project.Communication.local.resources.communication.packages.CommunicationPackage;

import java.util.concurrent.LinkedTransferQueue;

public class OutputMessageQueue implements Queue {
    private static OutputMessageQueue instance;
    private LinkedTransferQueue<CommunicationPackage> queue;

    public OutputMessageQueue() {
        queue = new LinkedTransferQueue<>();
    }

    public synchronized static OutputMessageQueue getInstance() {
        if (instance == null) {
            instance = new OutputMessageQueue();
            return instance;
        } else {
            return instance;
        }
    }

    @Override
    public void put(CommunicationPackage object) {
        queue.put(object);
    }

    @Override
    public CommunicationPackage take() throws InterruptedException {
        return queue.take();
    }
}
