package com.example.chat_project.Communication.local.resources.communication.commands;

public class Greeting extends CommunicationCommand implements CommandName {
    @Override
    public String getCommandName() {
        return "GREETING";
    }
}
