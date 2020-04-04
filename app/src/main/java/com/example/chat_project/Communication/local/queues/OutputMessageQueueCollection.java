package com.example.chat_project.Communication.local.queues;

import com.example.chat_project.Communication.local.resources.communication.packages.CommunicationPackage;

import java.util.ConcurrentModificationException;
import java.util.LinkedList;

public class OutputMessageQueueCollection implements PuttableQueue, WithQueue {
    private static OutputMessageQueueCollection instance;
    private LinkedList<OutputMessageQueue> outputMessageQueues;


    private OutputMessageQueueCollection() {
        outputMessageQueues = new LinkedList<>();
    }

    public static OutputMessageQueueCollection getInstance() {
        if (instance == null) {
            instance = new OutputMessageQueueCollection();
            return instance;
        } else {
            return instance;
        }
    }

    @Override
    public void put(CommunicationPackage object) {
        try {
            for (OutputMessageQueue messageQueue : outputMessageQueues) {
                messageQueue.put(object);
            }
        } catch (ConcurrentModificationException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public Queue getQueue() {
        OutputMessageQueue outputMessageQueueInstance = new OutputMessageQueue();
        outputMessageQueues.push(outputMessageQueueInstance);
        return outputMessageQueueInstance;
    }
}
