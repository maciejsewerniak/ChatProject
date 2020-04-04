package com.example.chat_project.Communication.local.resources.communication.data;

import java.util.ArrayList;

public class LyricsPicturesData extends CommunicationData {
    private Lyrics lyrics;
    private ArrayList<Picture> picturesCollection;

    public LyricsPicturesData() {
        this.picturesCollection = new ArrayList<>();
    }

    public Lyrics getLyricsData() {
        return this.lyrics;
    }

    public void setLyrics(Lyrics lyrics) {
        this.lyrics = lyrics;
    }

    public ArrayList<Picture> getPictures() {
        return this.picturesCollection;
    }

    public void pushPicture(Picture picture) {
        this.picturesCollection.add(picture);
    }
}
