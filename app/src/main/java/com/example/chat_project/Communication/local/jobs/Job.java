package com.example.chat_project.Communication.local.jobs;


import com.example.chat_project.Communication.local.resources.communication.data.CommunicationData;

public interface Job {
    void execute(CommunicationData inputData);
}
