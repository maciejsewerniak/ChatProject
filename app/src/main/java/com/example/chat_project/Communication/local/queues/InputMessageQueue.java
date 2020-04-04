package com.example.chat_project.Communication.local.queues;


import com.example.chat_project.Communication.local.resources.communication.packages.CommunicationPackage;

import java.util.concurrent.LinkedTransferQueue;

public class InputMessageQueue extends LinkedTransferQueue {
    private static LinkedTransferQueue<CommunicationPackage> instance;

    private InputMessageQueue() {

    }

    public synchronized static LinkedTransferQueue<CommunicationPackage> getInstance() {
        if (instance == null) {
            instance = new LinkedTransferQueue<>();
            return instance;
        } else {
            return instance;
        }
    }
}
