package com.example.chat_project.Communication.local.resources.communication.packages;


import com.example.chat_project.Communication.local.resources.communication.commands.CommunicationCommand;

interface ContainsCommunicationCommand {
    CommunicationCommand getCommunicationCommand();

    void setCommunicationCommand(CommunicationCommand communicationCommand);
}
