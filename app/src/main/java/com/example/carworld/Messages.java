package com.example.carworld;

public class Messages {

    public String data,from,message,time,type;

    public Messages(String data, String from, String message, String time, String type) {
        this.data = data;
        this.from = from;
        this.message = message;
        this.time = time;
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String date) {
        this.data = date;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Messages()
    {

    }
}
