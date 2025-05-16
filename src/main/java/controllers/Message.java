package controllers;

import java.io.File;
import java.time.LocalTime;

public class Message {
    private final String textContent;
    private final String typeMessage;
    private final LocalTime time;
    private final Boolean sentByUser;
    private final File file;

    public Message(String textContent, LocalTime time, Boolean sentByUser) {
        this.textContent = textContent;
        this.typeMessage = "text";
        this.sentByUser = sentByUser;
        this.time = time;
        this.file = null;
    }

    public Message(File file, LocalTime time, Boolean sentByUser) {
        this.file = file;
        this.textContent = "";
        this.typeMessage = "file";
        this.sentByUser = sentByUser;
        this.time = time;
    }
    
    public String getTextContent() {
        return textContent;
    }

    public String getTypeMessage() {
        return typeMessage;
    }

    public Boolean isSentByUser() {
        return sentByUser;
    }

    public LocalTime getTime() {
        return time;
    }

    public File getFile() {
        return file;
    }
}
