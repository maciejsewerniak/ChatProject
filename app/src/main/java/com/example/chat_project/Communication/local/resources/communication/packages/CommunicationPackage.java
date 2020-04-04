package com.example.chat_project.Communication.local.resources.communication.packages;


import com.example.chat_project.Communication.local.resources.communication.commands.CommunicationCommand;
import com.example.chat_project.Communication.local.resources.communication.data.CommunicationData;

import java.io.Serializable;

public class CommunicationPackage implements Serializable, ContainsCommunicationCommand, ContainsCommunicationData {
    private CommunicationCommand communicationCommand;
    private CommunicationData communicationData;

    @Override
    public CommunicationCommand getCommunicationCommand() {
        return communicationCommand;
    }

    @Override
    public void setCommunicationCommand(CommunicationCommand communicationCommand) {
        this.communicationCommand = communicationCommand;
    }

    @Override
    public CommunicationData getCommunicationData() {
        return this.communicationData;
    }

    @Override
    public void setCommunicationData(CommunicationData communicationData) {
        this.communicationData = communicationData;
    }
}