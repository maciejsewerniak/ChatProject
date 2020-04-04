package com.example.chat_project.Communication.local.resources.communication.data;


public class Lyrics extends CommunicationData {
    static final long serialVersionUID = 1L;
    private String lyrics;
    private String title;

    public String getLyrics() {
        return this.lyrics;
    }

    public void setLyrics(String lyrics) {
        this.lyrics = lyrics;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
