package com.saad.youssif.alsalonalmalaky;

/**
 * Created by youssif on 05/02/18.
 */

public class Suggestion {

    String username,details,time;

    public Suggestion(String username, String details, String time) {
        this.username = username;
        this.details = details;
        this.time = time;
    }

    public Suggestion() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
