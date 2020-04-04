package com.example.chat_project.Communication.local.queues;

import com.example.chat_project.Communication.local.resources.communication.packages.CommunicationPackage;

public interface PuttableQueue {
    void put(CommunicationPackage object);
}
