package com.example.chat_project.Communication.local.queues;

import com.example.chat_project.Communication.local.resources.communication.packages.CommunicationPackage;

public interface TakeableQueue {
    CommunicationPackage take() throws InterruptedException;
}
