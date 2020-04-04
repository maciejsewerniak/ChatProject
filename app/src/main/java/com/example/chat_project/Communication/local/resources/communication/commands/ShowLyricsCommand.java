package com.example.chat_project.Communication.local.resources.communication.commands;

public class ShowLyricsCommand extends CommunicationCommand implements CommandName {
    private static final String COMMAND_NAME = "SHOW_LYRICS";

    @Override
    public String getCommandName() {
        return COMMAND_NAME;
    }
}
