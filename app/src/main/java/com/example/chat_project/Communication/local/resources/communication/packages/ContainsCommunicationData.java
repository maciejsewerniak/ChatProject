package com.example.chat_project.Communication.local.resources.communication.packages;


import com.example.chat_project.Communication.local.resources.communication.data.CommunicationData;

interface ContainsCommunicationData {
    CommunicationData getCommunicationData();

    void setCommunicationData(CommunicationData communicationData);
}
